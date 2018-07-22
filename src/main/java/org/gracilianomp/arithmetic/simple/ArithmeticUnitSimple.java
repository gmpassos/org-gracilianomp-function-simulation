package org.gracilianomp.arithmetic.simple;

import org.gracilianomp.arithmetic.ArithmeticUnit;
import org.gracilianomp.arithmetic.MathObjectSingleton;
import org.gracilianomp.arithmetic.MathValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class ArithmeticUnitSimple extends ArithmeticUnit<MathValueSimple> {

    static final public ArithmeticUnitSimple instance = new ArithmeticUnitSimple();

    static final public MathObjectSingleton<MathValueSimple> ZERO = new MathObjectSingleton(instance, MathValueSimple.ZERO);
    static final public MathObjectSingleton<MathValueSimple> ONE = new MathObjectSingleton(instance, MathValueSimple.ONE);
    static final public MathObjectSingleton<MathValueSimple> TWO = new MathObjectSingleton(instance, MathValueSimple.TWO);
    static final public MathObjectSingleton<MathValueSimple> THREE = new MathObjectSingleton(instance, MathValueSimple.THREE);
    static final public MathObjectSingleton<MathValueSimple> FOUR = new MathObjectSingleton(instance, MathValueSimple.FOUR);
    static final public MathObjectSingleton<MathValueSimple> MINUS_ONE = new MathObjectSingleton(instance, MathValueSimple.MINUS_ONE);

    //////////////////////////////////////////////////////////////////////////////////


    private ArithmeticUnitSimple() {
    }

    @Override
    public MathValueSimple ZERO() { return MathValueSimple.ZERO ;}

    @Override
    public MathValueSimple ONE() { return MathValueSimple.ONE ;}

    @Override
    public MathValueSimple PI() { return MathValueSimple.PI ;}

    @Override
    public MathValueSimple E() { return MathValueSimple.E ;}

    @Override
    public boolean isCompatible(MathValue o) {
        return o instanceof MathValueSimple;
    }

    @Override
    public MathValueSimple[] newValuesArray(int size) {
        return new MathValueSimple[size] ;
    }

    private static final Pattern PATTERN_DIGITS = Pattern.compile("\\d+");

    @Override
    public MathValue newValue(Object o) {
        Number n ;

        if (o instanceof String) {
            String s = o.toString() ;

            if (s.contains(".")) {
                n = Double.parseDouble(s) ;
            }
            else if ( PATTERN_DIGITS.matcher(s).matches() ) {
                n = Long.parseLong(s);
            }
            else {
                n = Double.parseDouble(s) ;
            }
        }
        else if (o instanceof Number) {
            n = (Number) o;
        }
        else {
            throw new IllegalStateException("Can't convert to Number: "+ o) ;
        }

        if ( ( n instanceof Double ) || ( n instanceof Float) ) {
            return new MathValueSimple.MathValueSimpleDecimal(n.doubleValue()) ;
        }
        else {
            return new MathValueSimple.MathValueSimpleInteger(n.longValue()) ;
        }
    }

    @Override
    public MathValueSimple sum(MathValueSimple a, MathValueSimple b) {
        if ( a.isZero() ) {
            return b;
        }
        else if ( b.isZero() ) {
            return a;
        }

        if ( a.isInteger() && b.isInteger() ) {
            return new MathValueSimple.MathValueSimpleInteger(a.getValueInteger() + b.getValueInteger());
        }
        else {
            return new MathValueSimple.MathValueSimpleDecimal(a.getValueDecimal() + b.getValueDecimal());
        }
    }

    @Override
    public MathValueSimple subtract(MathValueSimple a, MathValueSimple b) {
        if ( a.isZero() ) {
            return b;
        }
        else if ( b.isZero() ) {
            return a;
        }

        if ( a.isInteger() && b.isInteger() ) {
            return new MathValueSimple.MathValueSimpleInteger(a.getValueInteger() - b.getValueInteger());
        }
        else {
            return new MathValueSimple.MathValueSimpleDecimal(a.getValueDecimal() - b.getValueDecimal());
        }
    }

    public MathValueSimple multiply(MathValueSimple a, MathValueSimple b) {
        if ( a.isZero() || b.isZero() ) {
            return MathValueSimple.ZERO;
        }

        boolean aOne = a.isOne();
        boolean bOne = b.isOne();

        if ( aOne && bOne ) {
            return MathValueSimple.ONE ;
        }
        else if ( aOne ) {
            return b ;
        }
        else if ( bOne ) {
            return a ;
        }

        boolean aMinusOne = a.isMinusOne();
        boolean bMinusOne = b.isMinusOne();

        if ( aMinusOne && bMinusOne ) {
            return MathValueSimple.ONE ;
        }
        else if ( aMinusOne ) {
            return bOne ? MathValueSimple.MINUS_ONE : b.minus() ;
        }
        else if ( bMinusOne ) {
            return aOne ? MathValueSimple.MINUS_ONE : a.minus() ;
        }

        if ( a.isInteger() && b.isInteger() ) {
            return new MathValueSimple.MathValueSimpleInteger(a.getValueInteger() * b.getValueInteger());
        }
        else {
            return new MathValueSimple.MathValueSimpleDecimal(a.getValueDecimal() * b.getValueDecimal());
        }
    }

    @Override
    public MathValueSimple divide(MathValueSimple a, MathValueSimple b) {
        if ( a.isZero() ) {
            return MathValueSimple.ZERO;
        }
        else if ( b.isZero() ) {
            throw new ArithmeticException("Can't divide by zero: "+ a +" / 0") ;
        }

        boolean aOne = a.isOne();
        boolean bOne = b.isOne();

        if ( aOne && bOne ) {
            return MathValueSimple.ONE ;
        }
        else if ( bOne ) {
            return a ;
        }

        boolean aMinusOne = a.isMinusOne();
        boolean bMinusOne = b.isMinusOne();

        if ( aMinusOne && bMinusOne ) {
            return MathValueSimple.ONE ;
        }
        else if ( bMinusOne ) {
            return aOne ? MathValueSimple.MINUS_ONE : a.minus() ;
        }

        boolean aInteger = a.isInteger();
        boolean bInteger = b.isInteger();

        if ( aInteger && bInteger) {
            long v1 = a.getValueInteger();
            long v2 = b.getValueInteger();

            if (v1 % v2 == 0) {
                return new MathValueSimple.MathValueSimpleInteger(a.getValueInteger() / b.getValueInteger());
            }
            else {
                return new MathValueSimple.MathValueSimpleDecimal(a.getValueDecimal() / b.getValueDecimal());
            }
        }
        else {
            return new MathValueSimple.MathValueSimpleDecimal(a.getValueDecimal() / b.getValueDecimal());
        }
    }

    @Override
    public MathValueSimple power(MathValueSimple a, MathValueSimple b) {
        boolean aZero = a.isZero();
        boolean bZero = b.isZero();

        if ( aZero && bZero) {
            throw new ArithmeticException("Can't power 0 to 0!") ;
        }
        else if ( aZero ) {
            return MathValueSimple.ZERO;
        }
        else if ( bZero ) {
            return MathValueSimple.ONE;
        }

        boolean aInteger = a.isInteger();
        boolean bInteger = b.isInteger();

        if ( aInteger && bInteger) {
            long v1 = a.getValueInteger();
            long v2 = b.getValueInteger();
            double val = Math.pow(v1,v2) ;
            return new MathValueSimple.MathValueSimpleDecimal(val);
        }
        else {
            double v1 = a.getValueDecimal();
            double v2 = b.getValueDecimal();
            double val = Math.pow(v1,v2) ;
            return new MathValueSimple.MathValueSimpleDecimal(val);
        }
    }

    @Override
    public MathValueSimple root(MathValueSimple a, MathValueSimple b) {
        if ( b.isZero() ) {
            throw new ArithmeticException("Can't power to 0!") ;
        }
        else if ( b.isEven() && a.isNegative() ) {
            throw new ArithmeticException("Can't power negative number to even: "+ a +"ˆ"+ b) ;
        }

        if ( a.isZero() ) {
            return MathValueSimple.ZERO;
        }
        else if ( a.isOne() ) {
            return MathValueSimple.ONE;
        }

        boolean bInteger = b.isInteger();

        double v1 = a.getValueDecimal();

        double val ;
        if (bInteger) {
            long v2 = b.getValueInteger();

            if (v2 == 2L) {
                val = Math.sqrt(v1) ;
            }
            else if (v2 == 3L) {
                val = Math.cbrt(v1) ;
            }
            else {
                val = Math.pow(v1 , 1d/v2) ;
            }
        }
        else {
            double v2 = b.getValueDecimal();

            if (v2 == 2d) {
                val = Math.sqrt(v1) ;
            }
            else if (v2 == 3d) {
                val = Math.cbrt(v1) ;
            }
            else {
                val = Math.pow(v1 , 1d/v2) ;
            }
        }

        return new MathValueSimple.MathValueSimpleDecimal(val);
    }

    @Override
    public MathValueSimple log(MathValueSimple a) {
        if ( a.isOne() ) {
            return MathValueSimple.ZERO;
        }
        else if ( a.isNegative() ) {
            throw new ArithmeticException("Can't log(x) negative number: "+ a) ;
        }

        double v1 = a.getValueDecimal();
        double val = Math.log(v1) ;

        return new MathValueSimple.MathValueSimpleDecimal(val);
    }

    @Override
    public MathValueSimple exp(MathValueSimple a) {
        if ( a.isZero() ) {
            return MathValueSimple.ONE;
        }
        else if ( a.isOne() ) {
            return MathValueSimple.E;
        }

        double v1 = a.getValueDecimal();
        double val = Math.exp(v1) ;

        return new MathValueSimple.MathValueSimpleDecimal(val);
    }

    @Override
    public MathValueSimple cos(MathValueSimple a) {
        double v1 = a.getValueDecimal();
        double val = Math.cos(v1) ;
        return new MathValueSimple.MathValueSimpleDecimal(val);
    }

    @Override
    public MathValueSimple collectionSum(MathValueSimple[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathValueSimple collectionAverage(MathValueSimple[] a) {
        throw new UnsupportedOperationException();
    }
}
