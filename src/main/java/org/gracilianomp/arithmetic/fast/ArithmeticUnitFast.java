package org.gracilianomp.arithmetic.fast;

import org.gracilianomp.arithmetic.ArithmeticUnit;
import org.gracilianomp.arithmetic.MathObjectSingleton;
import org.gracilianomp.arithmetic.MathValue;

import java.util.regex.Pattern;

final public class ArithmeticUnitFast extends ArithmeticUnit<MathValueFast> {

    static final public ArithmeticUnitFast instance = new ArithmeticUnitFast();

    static final public MathObjectSingleton<MathValueFast> ZERO = new MathObjectSingleton(instance, MathValueFast.ZERO);
    static final public MathObjectSingleton<MathValueFast> ONE = new MathObjectSingleton(instance, MathValueFast.ONE);
    static final public MathObjectSingleton<MathValueFast> TWO = new MathObjectSingleton(instance, MathValueFast.TWO);
    static final public MathObjectSingleton<MathValueFast> THREE = new MathObjectSingleton(instance, MathValueFast.THREE);
    static final public MathObjectSingleton<MathValueFast> FOUR = new MathObjectSingleton(instance, MathValueFast.FOUR);
    static final public MathObjectSingleton<MathValueFast> MINUS_ONE = new MathObjectSingleton(instance, MathValueFast.MINUS_ONE);
    static final public MathObjectSingleton<MathValueFast> PI = new MathObjectSingleton(instance, MathValueFast.PI);
    static final public MathObjectSingleton<MathValueFast> E = new MathObjectSingleton(instance, MathValueFast.E);

    //////////////////////////////////////////////////////////////////////////////////

    private ArithmeticUnitFast() {
    }

    @Override
    public MathValueFast ZERO() { return MathValueFast.ZERO ;}

    @Override
    public MathValueFast ONE() { return MathValueFast.ONE ;}

    @Override
    public MathValueFast PI() { return MathValueFast.PI ;}

    @Override
    public MathValueFast E() { return MathValueFast.E ;}

    @Override
    public boolean isCompatible(MathValue o) {
        return o instanceof MathValueFast ;
    }

    @Override
    public MathValueFast[] newValuesArray(int size) {
        return new MathValueFast[size] ;
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
            return new MathValueFast.MathValueFastDecimal(n.doubleValue()) ;
        }
        else {
            return new MathValueFast.MathValueFastInteger(n.longValue()) ;
        }
    }

    @Override
    public MathValueFast sum(MathValueFast a, MathValueFast b) {
        if ( a.isInteger() && b.isInteger() ) {
            return new MathValueFast.MathValueFastInteger(a.getValueInteger() + b.getValueInteger());
        }
        else {
            return new MathValueFast.MathValueFastDecimal(a.getValueDecimal() + b.getValueDecimal());
        }
    }

    @Override
    public MathValueFast subtract(MathValueFast a, MathValueFast b) {
        if ( a.isInteger() && b.isInteger() ) {
            return new MathValueFast.MathValueFastInteger(a.getValueInteger() - b.getValueInteger());
        }
        else {
            return new MathValueFast.MathValueFastDecimal(a.getValueDecimal() - b.getValueDecimal());
        }
    }

    public MathValueFast multiply(MathValueFast a, MathValueFast b) {
        if ( a.isInteger() && b.isInteger() ) {
            return new MathValueFast.MathValueFastInteger(a.getValueInteger() * b.getValueInteger());
        }
        else {
            return new MathValueFast.MathValueFastDecimal(a.getValueDecimal() * b.getValueDecimal());
        }
    }

    @Override
    public MathValueFast divide(MathValueFast a, MathValueFast b) {
        if ( b.isZero() ) {
            throw new ArithmeticException("Can't divide by zero: "+ a +" / 0") ;
        }

        boolean aInteger = a.isInteger();
        boolean bInteger = b.isInteger();

        if ( aInteger && bInteger) {
            long v1 = a.getValueInteger();
            long v2 = b.getValueInteger();

            if (v1 % v2 == 0) {
                return new MathValueFast.MathValueFastInteger(a.getValueInteger() / b.getValueInteger());
            }
            else {
                return new MathValueFast.MathValueFastDecimal(a.getValueDecimal() / b.getValueDecimal());
            }
        }
        else {
            return new MathValueFast.MathValueFastDecimal(a.getValueDecimal() / b.getValueDecimal());
        }
    }

    @Override
    public MathValueFast power(MathValueFast a, MathValueFast b) {
        boolean aZero = a.isZero();
        boolean bZero = b.isZero();

        if ( aZero && bZero) {
            throw new ArithmeticException("Can't power 0 to 0!") ;
        }

        double v1 = a.getValueDecimal();
        double v2 = b.getValueDecimal();
        double val = Math.pow(v1,v2) ;

        return new MathValueFast.MathValueFastDecimal(val);
    }

    @Override
    public MathValueFast root(MathValueFast a, MathValueFast b) {
        if ( b.isZero() ) {
            throw new ArithmeticException("Can't power to 0!") ;
        }
        else if ( a.isNegative() && b.isEven() ) {
            throw new ArithmeticException("Can't power negative number to even: "+ a +"ˆ"+ b) ;
        }

        double v1 = a.getValueDecimal();
        double v2 = b.getValueDecimal();

        double val ;
        if (v2 == 2d) {
            val = Math.sqrt(v1) ;
        }
        else if (v2 == 3d) {
            val = Math.cbrt(v1) ;
        }
        else {
            val = Math.pow(v1 , 1d/v2) ;
        }

        return new MathValueFast.MathValueFastDecimal(val);
    }

    @Override
    public MathValueFast log(MathValueFast a) {
        if ( a.isOne() ) {
            return MathValueFast.ZERO;
        }
        else if ( a.isNegative() ) {
            throw new ArithmeticException("Can't log(x) negative number: "+ a) ;
        }

        double v1 = a.getValueDecimal();
        double val = Math.log(v1) ;

        return new MathValueFast.MathValueFastDecimal(val);
    }

    @Override
    public MathValueFast exp(MathValueFast a) {
        if ( a.isZero() ) {
            return MathValueFast.ONE;
        }
        else if ( a.isOne() ) {
            return MathValueFast.E;
        }

        double v1 = a.getValueDecimal();
        double val = Math.exp(v1) ;

        return new MathValueFast.MathValueFastDecimal(val);
    }

    @Override
    public MathValueFast cos(MathValueFast a) {
        double v1 = a.getValueDecimal();
        double val = Math.cos(v1) ;
        return new MathValueFast.MathValueFastDecimal(val);
    }

    @Override
    public MathValueFast collectionSum(MathValueFast[] a) {
        int sumInt = 0 ;

        double sumDec = 0 ;
        boolean hasDecimal = false ;

        for (int i = 0; i < a.length; i++) {
            MathValueFast v = a[i];

            if ( v.isInteger() ) {
                sumInt += v.getValueInteger() ;
            }
            else {
                sumDec += v.getValueDecimal() ;
                hasDecimal = true ;
            }
        }

        if ( hasDecimal ) {
            double total = sumInt + sumDec;
            return new MathValueFast.MathValueFastDecimal( total );
        }
        else {
            return new MathValueFast.MathValueFastInteger( sumInt );
        }
    }

    @Override
    public MathValueFast collectionAverage(MathValueFast[] a) {
        int sumInt = 0 ;
        int sumIntSz = 0 ;

        double sumDec = 0 ;
        int sumDecSz = 0 ;

        for (int i = 0; i < a.length; i++) {
            MathValueFast v = a[i];

            if ( v.isInteger() ) {
                sumInt += v.getValueInteger() ;
                sumIntSz++ ;
            }
            else {
                sumDec += v.getValueDecimal() ;
                sumDecSz++ ;
            }
        }

        if ( sumDecSz > 0 ) {
            double total = sumInt + sumDec;
            int samples = sumIntSz + sumDecSz ;
            return new MathValueFast.MathValueFastDecimal( total/samples );
        }
        else {
            if ( sumInt % sumIntSz == 0 ) {
                return new MathValueFast.MathValueFastInteger( sumInt/sumIntSz );
            }
            else {
                return new MathValueFast.MathValueFastDecimal( sumInt/((double)sumIntSz) );
            }
        }
    }

}
