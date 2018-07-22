package org.gracilianomp.arithmetic;

final public class MathObjectSingleton<V extends MathValue> extends MathObject<V> {

    private final ArithmeticUnit<V> arithmeticUnit;
    private final V value ;

    public MathObjectSingleton(ArithmeticUnit<V> arithmeticUnit, V value) {
        this.arithmeticUnit = arithmeticUnit;
        this.value = value;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public ArithmeticUnit<V> getArithmeticUnit() {
        return arithmeticUnit;
    }

    @Override
    protected MathObject<V> calcImpl(ArithmeticOperation op, MathObject<V> o) {
        return o.isSingleton()
                ?
                calc(op, this, (MathObjectSingleton<V>)o )
                :
                calc(op, this, (MathObjectCollection<V>)o )
                ;
    }

    public V getValue() {
        return value;
    }

    @Override
    public V[] getValues() {
        V[] a = arithmeticUnit.newValuesArray(1);
        a[0] = value;
        return a ;
    }

    @Override
    public V getValue(int idx) {
        if (idx != 0) {
            throw new ArithmeticException("Can't access index != 0: "+ idx);
        }
        return value;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public double distance(MathObject<V> o) {
        if ( o.isSingleton() ) {
            return value.distance(o.getValue()) ;
        }
        else {
            int sz = o.size();

            double distances = 0 ;

            for (int i = sz-1; i >= 0; i--) {
                V v = o.getValue(i);
                double d = value.distance(v);
                distances += d*d ;
            }

            distances = distances/sz ;

            return distances ;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if ( !(o instanceof MathObject) ) return false;
        MathObject<?> obj = (MathObject<?>) o;

        if ( !obj.isSingleton() ) return false ;
        if ( !arithmeticUnit.equals( obj.getArithmeticUnit() ) ) return false ;

        if ( !value.equals( obj.getValue() ) ) return false ;

        return true ;
    }

    @Override
    public int hashCode() {
        int result = arithmeticUnit.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return value.toString() ;
    }

}
