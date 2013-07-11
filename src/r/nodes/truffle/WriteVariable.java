package r.nodes.truffle;

import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.nodes.*;

import com.oracle.truffle.api.frame.*;

// FIXME: we could get some performance by specializing on whether an update (writing the same value) is likely ; this is so when the assignment is used
// in update operations (vector update, replacement functions) ; we could use unconditional ref in other cases
public abstract class WriteVariable extends BaseR {

    // TODO: All BaseRNode are useless EXCEPT for the uninitialized version (since Truffle keeps track of the original)
    public final RSymbol symbol;
    @Child RNode expr;

    private static final boolean DEBUG_W = false;


    private WriteVariable(ASTNode orig, RSymbol symbol, RNode expr) {
        super(orig);
        setExpr(expr);
        this.symbol = symbol;
    }

    public void setExpr(RNode expr) {
        this.expr = adoptChild(expr);
    }

    public RNode getExpr() {
        return expr;
    }

    public static WriteVariable getUninitialized(ASTNode orig, RSymbol sym, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override public final Object execute(Frame frame) {

                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    WriteVariable node;
                    String reason;

                    if (frame == null) {
                        node = getWriteTopLevel(getAST(), symbol, expr);
                        reason = "installWriteTopLevelNode";
                    } else {
                        FrameSlot slot = RFrameHeader.findVariable(frame, symbol);
                        if (slot != null) {
                            node = getWriteLocal(getAST(), symbol, slot, expr);
                            reason = "installWriteLocalNode";
                        } else {
                            // this is only reachable with dynamic invocation (e.g. through eval)
                            node = getWriteExtension(getAST(), symbol, expr);
                            reason = "installWriteExtensionNode";
                        }
                    }
                    if (DEBUG_W) {
                        Utils.debug("write - " + symbol.pretty() + " uninitialized rewritten: " + reason);
                    }
                    return replace(node, reason).execute(frame);
                }
            }
        };
    }

    public static class Local extends WriteVariable {
        public final FrameSlot slot;

        public Local(ASTNode orig, RSymbol symbol, RNode rhs, FrameSlot slot) {
            super(orig, symbol, rhs);
            this.slot = slot;
        }

        @Override public final Object execute(Frame frame) {
            RAny val = Utils.cast(expr.execute(frame));
            RFrameHeader.writeAtCondRef(frame, slot, val);
            if (DEBUG_W) {
                Utils.debug("write - " + symbol.pretty() + " local-ws, wrote " + val + " (" + val.pretty() + ") to slot " + slot);
            }
            return val;
        }
    }

    public static class TopLevel extends WriteVariable {

        private TopLevel(ASTNode orig, RSymbol symbol, RNode expr) {
            super(orig, symbol, expr);
        }

        @Override public final Object execute(Frame frame) {
            RAny val = Utils.cast(expr.execute(frame));
            RFrameHeader.writeToTopLevelCondRef(symbol, val);
            if (DEBUG_W) {
                Utils.debug("write - " + symbol.pretty() + " toplevel, wrote " + val + " (" + val.pretty() + ")");
            }
            return val;
        }
    }

    public static class Extension extends WriteVariable {

        private Extension(ASTNode orig, RSymbol symbol, RNode expr) {
            super(orig, symbol, expr);
        }

        @Override public final Object execute(Frame frame) {
            RAny val = Utils.cast(expr.execute(frame));
            RFrameHeader.writeToExtension(frame, symbol, val);
            return val;
        }
    }

    public static class Frameless extends WriteVariable {

        final RAny[] locals;
        final int index;

        public Frameless(ASTNode orig, RSymbol symbol, RNode expr, RAny[] locals, int index) {
            super(orig, symbol, expr);
            this.locals = locals;
            this.index = index;
        }

        @Override
        public Object execute(Frame frame) {
            RAny val = Utils.cast(expr.execute(frame));
            locals[index] = val;
            return val;
        }
    }

    // TODO This is likely not graal friendly, creating a new frameslot and "renaming" the variable is likely the
    // better way to do.

    /** Reads the variable from the frame extension when its slot index is known. This is used for inlined functions to
     * access their arguments.
      */
    public static class KnownExtension extends WriteVariable {
        private final int extensionIndex;

        public KnownExtension(ASTNode orig, RSymbol symbol, RNode expr, int extensionIndex) {
            super(orig, symbol, expr);
            this.extensionIndex = extensionIndex;
        }

        @Override
        public Object execute(Frame frame) {
            RAny val = Utils.cast(expr.execute(frame));
            RFrameHeader.writeToKnownExtension(frame, val, extensionIndex);
            return val;
        }
    }


    public static WriteVariable getWriteLocal(ASTNode orig, RSymbol sym, final FrameSlot slot, RNode rhs) {
        return new Local(orig, sym, rhs, slot);
    }

    public static WriteVariable getWriteTopLevel(ASTNode orig, RSymbol sym, RNode rhs) {
        return new TopLevel(orig, sym, rhs);
    }

    public static WriteVariable getWriteExtension(ASTNode orig, RSymbol sym, RNode rhs) {
        return new Extension(orig, sym, rhs);
    }
}
