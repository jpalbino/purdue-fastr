package r.data.internal;

import r.*;
import r.data.*;
import r.data.RInt.*;

public class ListImpl extends ArrayImpl implements RList {

    RAny[] content;

    public ListImpl(RAny[] values, boolean doCopy) {
        if (doCopy) {
            content = new RAny[values.length];
            System.arraycopy(values, 0, content, 0, values.length);
        } else {
            content = values;
        }
    }

    public ListImpl(RAny[] values) {
        this(values, true);
    }

    public ListImpl(int size) {
        content = new RAny[size];
    }

    public ListImpl(RList v) {
        content = new RAny[v.size()];
        for (int i = 0; i < content.length; i++) {
            RAny e = v.getRAny(i);
            if (e instanceof RNull) {
                content[i] = e;
            } else if (e instanceof RArray) {
                content[i] = Utils.copyArray((RArray) e);
            } else {
                Utils.nyi("unsupported type");
            }
        }
    }

    @Override
    public int size() {
        return content.length;
    }

    @Override
    public Object get(int i) {
        return content[i];  // FIXME: should remove the R box ?
    }

    @Override
    public RAny boxedGet(int i) {
        return RListFactory.getScalar(getRAny(i));
    }

    @Override
    public RAny getRAny(int i) {
        return content[i];
    }

    @Override
    public RArray set(int i, Object val) {
        content[i] = (RAny) val; //FIXME a better conversion
        return this;
    }

    @Override
    public RArray set(int i, RAny val) {
        content[i] = val;
        return this;
    }

    private static final StringBuilder emptyString = new StringBuilder();
    public String pretty() {
        return pretty(emptyString);
    }

    public String pretty(StringBuilder indexPrefix) {
        if (content.length == 0) {
            return RList.TYPE_STRING + "()";
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < content.length; i++) {
                if (i >= 1) {
                    str.append("\n\n");
                }
                StringBuilder nprefix = new StringBuilder();
                nprefix.append(indexPrefix);
                nprefix.append("[[");
                nprefix.append(i + 1);
                nprefix.append("]]");
                str.append(nprefix);
                str.append("\n");

                RAny v = content[i];
                if (v instanceof ListImpl) {
                    str.append(((ListImpl) v).pretty(nprefix));
                } else {
                    str.append(v.pretty());
                }
            }
            return str.toString();
        }
    }

    @Override
    public RInt asInt() {
        Utils.nyi();
        return null;
    }

    @Override
    public RLogical asLogical() {
        Utils.nyi();
        return null;
    }

    @Override
    public RDouble asDouble() {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray subset(RInt index) {
        return RList.RListFactory.subset(this, index);
    }
}