package org.gracilianomp.arithmetic;

public interface MathValue {

    ArithmeticUnit getArithmeticUnit() ;

    default boolean isCompatibleArithmeticUnit(ArithmeticUnit au) {
        return getArithmeticUnit().isCompatible(au) ;
    }

    boolean isDecimal() ;
    boolean isInteger() ;

    boolean isZero() ;
    boolean isOne() ;
    boolean isMinusOne() ;

    boolean isNegative() ;
    boolean isPositive() ;

    boolean isEven() ;
    default boolean isOdd() {
        return !isEven();
    }

    MathValue minus() ;

    double distance(MathValue o) ;

    double getValue() ;

    default double getValueDecimal() {
        return getValue();
    }

    default long getValueInteger() {
        return (long) getValue();
    }

    int hashCode() ;
    boolean equals(Object o) ;

    String toString() ;

}
