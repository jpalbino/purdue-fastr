package r.nodes.exec;

import r.*;
import r.data.*;
import r.nodes.ast.*;
import r.runtime.*;

// TODO: support optimizations that avoid copying - that is, "ref" values if they are being duplicated by the update
//       (like in UpdateVector)

// FIXME: could get some performance for local variable updates (non-super assignment), e.g. like in UpdateVector

// Note that the semantics is not exactly as of GNU-R. In GNU-R, the variable is locally copied into a temporary variable *tmp*.
// We don't do this. However, we have the forcePromiseNode to read the variable to force a promise to get closer to that behavior.
// This indeed leads to double look-up on the variable, which is not great for performance.

// the call passed must have SimpleAccessVariable of "var" as its first argument
// and RememberLast (the value) as its last argument
public class ReplacementCall extends BaseR {
    @Child RNode forcePromiseNode;
    @Child AbstractCall callNode;
    @Child RNode assign;

    final boolean isSuper;

    Object newContent;

    public ReplacementCall(ASTNode ast, boolean isSuper, RSymbol var, AbstractCall callNode) {
        super(ast);
        this.isSuper = isSuper;
        this.callNode = adoptChild(callNode);

        RNode[] argExprs = callNode.argExprs;
        assert Utils.check(argExprs.length >= 1);
        assert Utils.check(argExprs[argExprs.length - 1] instanceof RememberLast);

        RNode node = new BaseR(ast) {
            @Override
            public final Object execute(Frame frame) {
                return newContent;
            }
        };
        if (isSuper) {
            this.assign = adoptChild(SuperWriteVariable.getUninitialized(ast, var, node));
        } else {
            this.assign = adoptChild(WriteVariable.getUninitialized(ast, var, node));
        }

        this.forcePromiseNode = adoptChild(ReadVariable.getUninitialized(ast, var));
    }

    @Override
    public Object execute(Frame frame) {
        forcePromiseNode.execute(frame);
        newContent = callNode.execute(frame);
        assign.execute(frame);
        RNode[] argExprs = callNode.argExprs;
        return ((RememberLast) argExprs[argExprs.length - 1]).lastValue();
    }

    @Override
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (forcePromiseNode == oldNode) {
            forcePromiseNode = newNode;
            return adoptInternal(newNode);
        }
        if (callNode == oldNode) {
            callNode = (AbstractCall) newNode;
            return adoptInternal(newNode);
        }
        if (assign == oldNode) {
            assign = newNode;
            return adoptInternal(newNode);
        }
        return super.replaceChild(oldNode, newNode);
    }

    public static final class RememberLast extends BaseR {
        @Child RNode node;
        Object lastValue;

        public RememberLast(ASTNode ast, RNode node) {
            super(ast);
            this.node = insert(node, "inserting RememberLast");
        }

        @Override
        public Object execute(Frame frame) {
            lastValue = node.execute(frame);
            return lastValue;
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (node == oldNode) {
                node = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

        public Object lastValue() {
            return lastValue;
        }
    }
}
