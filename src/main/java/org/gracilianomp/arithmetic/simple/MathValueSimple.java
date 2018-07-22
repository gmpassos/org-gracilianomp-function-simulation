package org.gracilianomp.arithmetic.simple;

import org.gracilianomp.arithmetic.ArithmeticUnit;
import org.gracilianomp.arithmetic.MathValue;
import org.gracilianomp.arithmetic.MathValueDecimal;
import org.gracilianomp.arithmetic.MathValueInteger;

public interface MathValueSimple extends MathValue {

    MathValueSimpleInteger ZERO = new MathValueSimpleInteger(0);
    MathValueSimpleInteger ONE = new MathValueSimpleInteger(1);
    MathValueSimpleInteger TWO = new MathValueSimpleInteger(2);
    MathValueSimpleInteger THREE = new MathValueSimpleInteger(3);
    MathValueSimpleInteger FOUR = new MathValueSimpleInteger(4);
    MathValueSimpleInteger MINUS_ONE = new MathValueSimpleInteger(-1);
    MathValueSimpleDecimal PI = new MathValueSimpleDecimal(Math.PI);
    MathValueSimpleDecimal E = new MathValueSimpleDecimal(Math.E);

    static MathValueSimpleDecimal[] toDecimals(double... values) {
        int sz = values.length;
        MathValueSimpleDecimal[] vals = new MathValueSimpleDecimal[sz] ;
        for (int i = 0; i < vals.length; i++) {
            vals[i] = new MathValueSimpleDecimal( values[i] ) ;
        }
        return vals ;
    }

    static MathValueSimpleInteger[] toIntegers(long... values) {
        int sz = values.length;
        MathValueSimpleInteger[] vals = new MathValueSimpleInteger[sz] ;
        for (int i = 0; i < vals.length; i++) {
            vals[i] = new MathValueSimpleInteger( values[i] ) ;
        }
        return vals ;
    }

    static long[] getValuesIntegers(MathValueSimple... values) {
        int sz = values.length;
        long[] vs = new long[sz] ;

        for (int i = sz-1; i >= 0; i--) {
            vs[i] = values[i].getValueInteger() ;
        }

        return vs ;
    }

    static double[] getValuesDecimals(MathValueSimple... values) {
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
    MathValueSimple minus();

    long getValueInteger() ;
    double getValueDecimal() ;

    default ArithmeticUnit getArithmeticUnit() {
        return ArithmeticUnitSimple.instance ;
    }

    final class MathValueSimpleInteger implements MathValueSimple, MathValueInteger {
        final private long value ;

        public MathValueSimpleInteger(long value) {
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
        public MathValueSimpleInteger minus() {
            if (value == 0) return ZERO;
            if (value == 1) return MINUS_ONE;
            return new MathValueSimpleInteger(-value) ;
        }

        @Override
        public double distance(MathValue o) {
            MathValueSimple v = (MathValueSimple) o;

            if (v.isInteger()) {
                return value - v.getValueInteger() ;
            }
            else {
                return value - v.getValueDecimal() ;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;

            if (o.getClass() == MathValueSimpleInteger.class) {
                MathValueSimpleInteger v = (MathValueSimpleInteger) o;
                return value == v.value ;
            }
            else if (o.getClass() == MathValueSimpleDecimal.class) {
                MathValueSimpleDecimal v = (MathValueSimpleDecimal) o;
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

    final class MathValueSimpleDecimal implements MathValueSimple, MathValueDecimal {
        final private double value ;

        public MathValueSimpleDecimal(double value) {
            this.value = value;
        }

        public double getValueDecimal() {
            return value;
        }

        @Override
        public long getValueInteger() {
            return (long) value;
        }

        @Override
        public double getValue() {
            return value;
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
        public MathValueSimple minus() {
            if (value == 0) return ZERO;
            if (value == 1) return MINUS_ONE;
            return new MathValueSimpleDecimal(-value) ;
        }

        @Override
        public double distance(MathValue o) {
            MathValueSimple v = (MathValueSimple) o;

            if (v.isInteger()) {
                return value - v.getValueInteger() ;
            }
            else {
                return value - v.getValueDecimal() ;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;

            if ( !(o instanceof MathValueSimple) ) return false;
            MathValueSimple v = (MathValueSimple) o;

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
