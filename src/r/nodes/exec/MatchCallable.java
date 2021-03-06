package r.nodes.exec;

import r.*;
import r.builtins.*;
import r.data.*;
import r.data.RFunction.*;
import r.errors.*;
import r.nodes.ast.*;
import r.runtime.*;

// TODO: re-visit this with eval in mind

// this class is indeed very similar to ReadVariable
// if there was a way to re-factor without incurring performance overhead, it might be worth trying (but unlikely, R has distinct code as well)

public abstract class MatchCallable extends BaseR {

    final RSymbol symbol;
    public MatchCallable(ASTNode ast, RSymbol symbol) {
        super(ast);
        this.symbol = symbol;
    }

    public static RCallable matchNonVariable(ASTNode ast, RSymbol symbol) { // TODO: get rid of this (it is now in EnvironmentImpl.match)
        // builtins
        RBuiltIn builtIn = Primitives.getBuiltIn(symbol, null);
        if (builtIn != null) {
            return builtIn;
        } else {
            throw RError.getUnknownFunction(ast, symbol);
        }
    }

    // called from lapply
    public static RCallable matchGeneric(ASTNode ast, Frame frame, RSymbol symbol) {
        RCallable res = Frame.match(frame, symbol);
        if (res != null) {
            return res;
        }
        throw RError.getUnknownObjectMode(ast, symbol, "function");
    }


    public static MatchCallable getUninitialized(ASTNode ast, RSymbol sym) {
        return new MatchCallable(ast, sym) {

            private Object replaceAndExecute(MatchCallable node, String reason, Frame frame) {
                replace(node, reason);
                return node.execute(frame);
            }

            @Override
            public final Object execute(Frame frame) {

                try {
                    throw new SpecializationException(null);
                } catch (SpecializationException e) {

                    if (frame == null) {
                        return replaceAndExecute(getMatchOnlyFromTopLevel(ast, symbol), "installMatchOnlyFromTopLevel", frame);
                    }

                    int slot = frame.findVariable(symbol);
                    if (slot != -1) {
                        return replaceAndExecute(getMatchLocal(ast, symbol, slot), "installMatchLocal", frame);
                    }

                    EnclosingSlot rse = frame.readSetEntry(symbol);
                    if (rse == null) {
                        return replaceAndExecute(getMatchTopLevel(ast, symbol), "installMatchTopLevel", frame);
                    } else {
                        return replaceAndExecute(getMatchEnclosing(ast, symbol, rse.hops, rse.slot), "installMatchEnclosing", frame);
                    }
                }
            }
        };
    }

    public static MatchCallable getMatchLocal(ASTNode ast, RSymbol symbol, final int slot) {
        return new MatchCallable(ast, symbol) {

            @Override
            public final Object execute(Frame frame) {
                RAny val = frame.matchViaWriteSet(slot, symbol);
                if (val == null) {
                    throw RError.getUnknownFunction(ast, symbol);
                }
                return val;
            }
        };
    }

    public static MatchCallable getMatchEnclosing(ASTNode ast, RSymbol symbol, final int hops, final int slot) {
        return new MatchCallable(ast, symbol) {

            @Override
            public final Object execute(Frame frame) {
                RAny val = frame.matchViaReadSet(hops, slot, symbol);
                if (val == null) {
                    throw RError.getUnknownFunction(ast, symbol);
                }
                return val;
            }
        };
    }

    public static MatchCallable getMatchTopLevel(ASTNode ast, RSymbol symbol) {
        return new MatchCallable(ast, symbol) {


            @Override
            public final Object execute(Frame frame) {
                Object val;

                if (symbol.getVersion() != 0) { // NOTE: this could be made more efficient, see comments in ReadVariable
                    val = frame.matchFromExtensionEntry(symbol);
                    if (val == null) {
                        val = symbol.getValue();
                    }
                } else {
                    val = symbol.getValue();
                }
                if (val == null || !(val instanceof RCallable)) {
                    if (Primitives.STATIC_LOOKUP) {
                        throw RError.getUnknownFunction(ast, symbol);
                    } else {
                        return matchNonVariable(ast, symbol);
                    }
                }
                return val;
            }
        };
    }

    public static MatchCallable getMatchOnlyFromTopLevel(ASTNode ast, RSymbol symbol) {
        return new MatchCallable(ast, symbol) {

            @Override
            public final Object execute(Frame frame) {
                assert Utils.check(frame == null);
                Object val = symbol.getValue();
                if (val == null || !(val instanceof RCallable)) {
                    if (Primitives.STATIC_LOOKUP) {
                        throw RError.getUnknownFunction(ast, symbol);
                    } else {
                        return matchNonVariable(ast, symbol);
                    }
                }
                return val;
            }
        };
    }
}
