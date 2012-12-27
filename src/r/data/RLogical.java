package r.data;

import java.util.*;

import r.*;
import r.Convert.ConversionStatus;
import r.data.internal.*;

public interface RLogical extends RArray { // FIXME: should extend Number instead?

    String TYPE_STRING = "logical";
    int TRUE = 1;
    int FALSE = 0;
    int NA = Integer.MIN_VALUE;

    ScalarLogicalImpl BOXED_TRUE = RLogicalFactory.getScalar(TRUE);
    ScalarLogicalImpl BOXED_FALSE = RLogicalFactory.getScalar(FALSE);
    ScalarLogicalImpl BOXED_NA = RLogicalFactory.getScalar(NA);

    LogicalImpl EMPTY = (LogicalImpl) RLogicalFactory.getUninitializedArray(0);

    int getLogical(int il);
    RLogical set(int i, int val);
    RLogical materialize();

    public class RLogicalUtils {
        public static int truesInRange(RLogical l, int from, int to) {
            int ntrue = 0;
            for (int i = from; i < to; i++) {
                if (l.getLogical(i) == TRUE) {
                   ntrue++;
                }
            }
            return ntrue;
        }
        public static int nonFalsesInRange(RLogical l, int from, int to) {
            int nnonfalse = 0;
            for (int i = from; i < to; i++) {
                if (l.getLogical(i) != FALSE) {
                   nnonfalse++;
                }
            }
            return nnonfalse;
        }
        public static RRaw logicalToRaw(RLogical value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            byte[] content = new byte[size];
            for (int i = 0; i < size; i++) {
                int lval = value.getLogical(i);
                content[i] = Convert.logical2raw(lval, warn);
            }
            return RRaw.RRawFactory.getFor(content, value.dimensions());
        }
    }

    public class RLogicalFactory {
        public static ScalarLogicalImpl getScalar(int value) {
            return new ScalarLogicalImpl(value);
        }
        public static RLogical getScalar(int value, int[] dimensions) {
            if (dimensions == null) {
                return new ScalarLogicalImpl(value);
            } else {
                return getFor(new int[] {value}, dimensions);
            }
        }
        public static RLogical getArray(int... values) {
            if (values.length == 1) {
                return new ScalarLogicalImpl(values[0]);
            }
            return new LogicalImpl(values);
        }
        public static RLogical getArray(int[] values, int[] dimensions) {
            if (values.length == 1) {
                return new ScalarLogicalImpl(values[0]);
            }
            return new LogicalImpl(values, dimensions);
        }
        public static RLogical getUninitializedArray(int size) {
            if (size == 1) {
                return new ScalarLogicalImpl(0);
            }
            return new LogicalImpl(size);
        }
        public static RLogical getUninitializedArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return new ScalarLogicalImpl(0);
            }
            return new LogicalImpl(new int[size], dimensions, null, false);
        }
        public static RLogical getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RLogical getNAArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return BOXED_NA;
            }
            int[] content = new int[size];
            Arrays.fill(content, NA);
            return new LogicalImpl(content, dimensions, null, false);
        }
        public static LogicalImpl getMatrixFor(int[] values, int m, int n) {
            return new LogicalImpl(values, new int[] {m, n}, null, false);
        }
        public static RLogical copy(RLogical l) {
            if (l.size() == 1 && l.dimensions() == null && l.names() == null) {
                return new ScalarLogicalImpl(l.getLogical(0));
            }
            return new LogicalImpl(l, false);
        }
        public static RLogical strip(RLogical v) {
            if (v.size() == 1) {
                return new ScalarLogicalImpl(v.getLogical(0));
            }
            return new LogicalImpl(v, true);
        }
        public static RLogical getFor(int[] values) {  // re-uses values!
            return getFor(values, null);
        }
        public static RLogical getFor(int[] values, int[] dimensions) {  // re-uses values!
            if (values.length == 1 && dimensions == null) {
                return new ScalarLogicalImpl(values[0]);
            }
            return new LogicalImpl(values, dimensions, null, false);
        }
        public static RLogical exclude(int excludeIndex, RLogical orig) {
            return new RLogicalExclusion(excludeIndex, orig);
        }
        public static RLogical subset(RLogical value, RInt index) {
            return new RLogicalSubset(value, index);
        }
    }

    public static class RStringView extends View.RStringProxy<RLogical> implements RString {

        public RStringView(RLogical orig) {
            super(orig);
        }

        @Override
        public RLogical asLogical() {
            return orig;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig;
        }

        @Override
        public String getString(int i) {
            int v = orig.getLogical(i);
            return Convert.logical2string(v);
        }

    }

    public static class RComplexView extends View.RComplexProxy<RLogical> implements RComplex {

        public RComplexView(RLogical orig) {
            super(orig);
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig;
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public double getReal(int i) {
            int ll = orig.getLogical(i);
            return Convert.logical2double(ll);
        }

        @Override
        public double getImag(int i) {
            return 0;
        }
    }

    public static class RDoubleView extends View.RDoubleProxy<RLogical> implements RDouble {

        public RDoubleView(RLogical orig) {
            super(orig);
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig;
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public double getDouble(int i) {
            int ll = orig.getLogical(i);
            return Convert.logical2double(ll);
        }
    }

    public static class RIntView extends View.RIntProxy<RLogical> implements RInt {

        public RIntView(RLogical orig) {
            super(orig);
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RLogical asLogical() {
            return orig;
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public int getInt(int i) {
            return Convert.logical2int(orig.getLogical(i));
        }
    }

    public static class RRawView extends View.RRawProxy<RLogical> implements RRaw {

        public RRawView(RLogical orig) {
            super(orig);
        }

        @Override
        public byte getRaw(int i) {
            int ll = orig.getLogical(i);
            return Convert.logical2raw(ll);
        }
    }

    public static class RLogicalExclusion extends View.RLogicalView implements RLogical {

        final RLogical orig;
        final int excludeIndex;
        final int size;

        public RLogicalExclusion(int excludeIndex, RLogical orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int getLogical(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getLogical(i);
            } else {
                return orig.getLogical(i + 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return orig.isShared();
        }

        @Override
        public void ref() {
            orig.ref();
        }
    }

    // indexes must all be positive
    //   but can be out of bounds ==> NA's are returned in that case
    public static class RLogicalSubset extends View.RLogicalView implements RLogical {

        final RLogical value;
        final int vsize;
        final RInt index;
        final int isize;

        public RLogicalSubset(RLogical value, RInt index) {
            this.value = value;
            this.index = index;
            this.isize = index.size();
            this.vsize = value.size();
        }

        @Override
        public int size() {
            return isize;
        }

        @Override
        public int getLogical(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RLogical.NA;
            } else {
                return value.getLogical(j - 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return value.isShared() || index.isShared();
        }

        @Override
        public void ref() {
            value.ref();
            index.ref();
        }
    }
}
