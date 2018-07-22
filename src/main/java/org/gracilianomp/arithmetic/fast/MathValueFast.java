package org.gracilianomp.arithmetic.fast;

import org.gracilianomp.arithmetic.ArithmeticUnit;
import org.gracilianomp.arithmetic.MathValue;
import org.gracilianomp.arithmetic.MathValueDecimal;
import org.gracilianomp.arithmetic.MathValueInteger;

public interface MathValueFast extends MathValue {

    MathValueFastInteger ZERO = new MathValueFastInteger(0);
    MathValueFastInteger ONE = new MathValueFastInteger(1);
    MathValueFastInteger TWO = new MathValueFastInteger(2);
    MathValueFastInteger THREE = new MathValueFastInteger(3);
    MathValueFastInteger FOUR = new MathValueFastInteger(4);
    MathValueFastInteger MINUS_ONE = new MathValueFastInteger(-1);
    MathValueFastDecimal PI = new MathValueFastDecimal(Math.PI);
    MathValueFastDecimal E = new MathValueFastDecimal(Math.E);

    static MathValueFastDecimal[] toDecimals(double... values) {
        int sz = values.length;
        MathValueFastDecimal[] vals = new MathValueFastDecimal[sz] ;
        for (int i = 0; i < vals.length; i++) {
            vals[i] = new MathValueFastDecimal( values[i] ) ;
        }
        return vals ;
    }

    static MathValueFastInteger[] toIntegers(long... values) {
        int sz = values.length;
        MathValueFastInteger[] vals = new MathValueFastInteger[sz] ;
        for (int i = 0; i < vals.length; i++) {
            vals[i] = new MathValueFastInteger( values[i] ) ;
        }
        return vals ;
    }

    static long[] getValuesIntegers(MathValueFast... values) {
        int sz = values.length;
        long[] vs = new long[sz] ;

        for (int i = sz-1; i >= 0; i--) {
            vs[i] = values[i].getValueInteger() ;
        }

        return vs ;
    }

    static double[] getValuesDecimals(MathValueFast... values) {
        int sz = values.length;
        double[] vs = new double[sz] ;

        for (int i = sz-1; i >= 0; i--) {
            vs[i] = values[i].getValueDecimal();
        }

        return vs ;
    }

    ////////////////////////////////////////////////////////////


    @Override
    default boolean isEven() {
        return isInteger() && getValueInteger() % 2 == 0;
    }

    @Override
    MathValueFast minus();

    long getValueInteger() ;
    double getValueDecimal() ;

    default ArithmeticUnit getArithmeticUnit() {
        return ArithmeticUnitFast.instance ;
    }

    final class MathValueFastInteger implements MathValueFast , MathValueInteger {
        final private long value ;

        public MathValueFastInteger(long value) {
            this.value = value;
        }

        public long getValueInteger() {
            return value;
        }

        @Override
        public double getValueDecimal() {
            return value;
        }

        @Override
        public double getValue() {
            return value;
        }

        @Override
        public boolean isZero() {
            return value == 0;
        }

        @Override
        public boolean isOne() {
            return value == 1;
        }

        @Override
        public boolean isMinusOne() {
            return value == -1;
        }

        @Override
        public boolean isNegative() {
            return value < 0 ;
        }

        @Override
        public boolean isPositive() {
            return value > 0 ;
        }

        @Override
        public MathValueFastInteger minus() {
            if (value == 0) return ZERO;
            if (value == 1) return MINUS_ONE;
            return new MathValueFastInteger(-value) ;
        }

        @Override
        public double distance(MathValue o) {
            return value - o.getValue();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;

            if (o.getClass() == MathValueFastInteger.class) {
                MathValueFastInteger v = (MathValueFastInteger) o;
                return value == v.value ;
            }
            else if (o.getClass() == MathValueFastDecimal.class) {
                MathValueFastDecimal v = (MathValueFastDecimal) o;
                double valueDecimal = v.getValueDecimal();
                return Double.compare(valueDecimal , value) == 0 ;
            }
            else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Long.hashCode(value);
        }

        @Override
        public String toString() {
            return String.valueOf(value) ;
        }
    }

    final class MathValueFastDecimal implements MathValueFast , MathValueDecimal {
        final private double value ;

        public MathValueFastDecimal(double value) {
            this.value = value;
        }

        public double getValueDecimal() {
            return value;
        }

        @Override
        public double getValue() {
            return value;
        }

        @Override
        public long getValueInteger() {
            return (long) value;
        }

        @Override
        public boolean isZero() {
            return value == 0 ;
        }

        @Override
        public boolean isOne() {
            return value == 1 ;
        }

        @Override
        public boolean isMinusOne() {
            return value == -1;
        }

        @Override
        public boolean isNegative() {
            return value < 0 ;
        }

        @Override
        public boolean isPositive() {
            return value > 0 ;
        }

        @Override
        public MathValueFast minus() {
            if (value == 0) return ZERO;
            if (value == 1) return MINUS_ONE;
            return new MathValueFastDecimal(-value) ;
        }

        @Override
        public double distance(MathValue o) {
            return value - o.getValue();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;

            if ( !(o instanceof  MathValueFast) ) return false;
            MathValueFast v = (MathValueFast) o;

            return Double.compare(v.getValueDecimal() , value) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(value);
        }

        @Override
        public String toString() {
            return String.valueOf(value) ;
        }
    }

}
