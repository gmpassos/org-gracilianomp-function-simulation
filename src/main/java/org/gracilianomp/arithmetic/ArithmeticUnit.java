package org.gracilianomp.arithmetic;

import org.gracilianomp.arithmetic.fast.ArithmeticUnitFast;
import org.gracilianomp.arithmetic.simple.ArithmeticUnitSimple;

abstract public class ArithmeticUnit<V extends MathValue> {

    static public ArithmeticUnit toArithmeticUnit(String type) {
        type = type.toLowerCase().trim() ;

        if ( type.equals("arithmeticunitfast") || type.contains("fast") ) return ArithmeticUnitFast.instance ;
        if ( type.equals("arithmeticunitsimple") || type.contains("simple") ) return ArithmeticUnitSimple.instance ;

        throw new IllegalStateException("Invalid ArithmeticUnit type: "+ type) ;
    }


    public boolean isCompatible(ArithmeticUnit au) {
        return this == au || this.getClass() == au.getClass() ;
    }

    public boolean isCompatible(MathObject o) {
        return o.getArithmeticUnit().isCompatible(o.getArithmeticUnit()) ;
    }

    abstract public boolean isCompatible(MathValue o) ;

    public void checkCompatible(MathObject o) {
        if ( !isCompatible(o) ) {
            throw new ArithmeticException("Not compatible MathObject: "+ o) ;
        }
    }

    public void checkCompatible(MathValue v) {
        if ( !isCompatible(v) ) {
            throw new ArithmeticException("Not compatible MathValue: "+ v) ;
        }
    }

    abstract public V[] newValuesArray(int size) ;

    abstract public MathValue newValue(Object o) ;

    abstract public V ZERO() ;
    abstract public V ONE() ;
    abstract public V PI() ;
    abstract public V E() ;

    public V calc(ArithmeticOperation op, V a, V b) {
        checkCompatible(a);
        checkCompatible(b);
        return op.calc(this, a, b);
    }

    abstract public V sum(V a, V b) ;
    abstract public V subtract(V a, V b) ;
    abstract public V multiply(V a, V b) ;
    abstract public V divide(V a, V b) ;

    abstract public V power(V a, V b) ;
    abstract public V root(V a, V b) ;

    abstract public V log(V a) ;
    abstract public V exp(V a) ;
    abstract public V cos(V a) ;

    abstract public V collectionSum(V[] a) ;
    abstract public V collectionAverage(V[] a) ;

    public String toString() {
        return this.getClass().getName() ;
    }

}
