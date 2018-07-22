package org.gracilianomp.arithmetic;

import java.util.Arrays;

final public class MathObjectCollection<V extends MathValue> extends MathObject<V> {

    private final ArithmeticUnit<V> arithmeticUnit;
    private final V[] values ;

    public MathObjectCollection(ArithmeticUnit<V> arithmeticUnit, V... values) {
        this.arithmeticUnit = arithmeticUnit;
        this.values = values;
    }

    @Override
    public boolean isSingleton() {
        return values.length == 1 ;
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

    @Override
    public V getValue() {
        if ( !isSingleton() ) throw new ArithmeticException("Can't call getValue() of non singleton MathObject: "+ this) ;
        return values[0];
    }

    @Override
    public V getValue(int idx) {
        return values[idx] ;
    }

    @Override
    public int size() {
        return values.length ;
    }

    public V[] getValues() {
        return values;
    }

    @Override
    public double distance(MathObject<V> o) {
        if ( o.isSingleton() ) {
            return o.distance(this);
        }
        else {
            int sz1 = this.size();
            int sz2 = o.size();

            if (sz1 != sz2) return sz1-sz2 ;

            double distances = 0 ;

            for (int i = 0; i < sz1; i++) {
                V v1 = this.getValue(i);
                V v2 = o.getValue(i);

                double d = v1.distance(v2);
                distances += d*d ;
            }

            distances = distances / sz1 ;
            return distances ;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MathObjectCollection<?> that = (MathObjectCollection<?>) o;
        return arithmeticUnit.equals(that.arithmeticUnit) && Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        int result = arithmeticUnit.hashCode();
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public String toString() {
        return "#"+ values.length + Arrays.toString(values) ;
    }

}
