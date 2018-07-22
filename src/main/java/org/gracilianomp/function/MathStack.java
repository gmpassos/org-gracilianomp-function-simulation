package org.gracilianomp.function;

import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.arithmetic.MathObjectSingleton;
import org.gracilianomp.arithmetic.MathValue;

import java.util.Arrays;

final public class MathStack<V extends MathValue> implements Cloneable {

    private final String name ;
    private final boolean immutable ;

    private MathObject<V>[] values ;
    private int valuesSize ;
    private MathObject<V> valuesExtra ;


    public MathStack(StackType stackType) {
        this(stackType, false);
    }

    public MathStack(String name) {
        this(name, false);
    }

    public MathStack(StackType stackType, boolean immutable) {
        this( stackType.name() , immutable ) ;
    }

    public MathStack(String name, boolean immutable) {
        this(name, immutable, 1);
    }

    public MathStack(String name, boolean immutable, int capacity) {
        this.name = name;
        this.immutable = immutable;
        this.values = new MathObject[capacity] ;
        this.valuesSize = 0 ;
        this.valuesExtra = null ;
    }

    private MathStack(String name, boolean immutable, MathObject<V>[] values) {
        this.name = name;
        this.immutable = immutable;
        this.values = values ;
        this.valuesSize = values.length ;
        this.valuesExtra = null ;
    }

    private MathObject<V>[] allValues ;
    private MathObject<V>[] getAllValues() {
        if (allValues == null) {
            allValues = getAllValuesImpl() ;
        }
        return allValues ;
    }

    private MathObject<V>[] getAllValuesImpl() {
        if (valuesExtra == null) {
            return this.values.length == valuesSize ? this.values : Arrays.copyOf(this.values, valuesSize) ;
        }
        else {
            MathObject<V>[] all = Arrays.copyOf(this.values, valuesSize+1) ;
            all[valuesSize] = valuesExtra ;
            return all ;
        }
    }

    public MathStack<V> copy() {
        MathObject<V>[] allValues = getAllValues();
        return new MathStack<>(name, immutable, allValues);
    }

    public String getName() {
        return name;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public int size() {
        return valuesExtra != null ? valuesSize+1 : valuesSize ;
    }

    public boolean hasSameSignature(MathStack<V> o) {
        if ( this == o ) return true ;

        int sz = this.size() ;

        if ( sz != o.size() ) return false ;

        for (int i = 0; i < sz; i++) {
            MathObject<V> v1 = this.get(i) ;
            MathObject<V> v2 = o.get(i);

            if (v1.size() != v2.size()) return false ;
        }

        return true ;
    }

    public MathObject<V> get(int idx) {
        int valuesLength = values.length;
        if (idx >= valuesLength) {
            if (idx == valuesLength && valuesExtra != null) {
                return valuesExtra ;
            }

            throw new ArrayIndexOutOfBoundsException("Invalid values index: "+ idx +"/"+ size());
        }

        return values[idx] ;
    }

    public V getValue(int stackIndex) {
        return getValue(stackIndex, 0);
    }

    public V getValue(int stackIndex, int valueIndex) {
        MathObject<V> o = get(stackIndex);
        return o.getValue(valueIndex) ;
    }

    public MathObject<V> getValueObject(int stackIndex, int valueIndex) {
        MathObject<V> mathObject = get(stackIndex);

        if ( valueIndex <= 0 ) {
            if ( mathObject.isSingleton() || valueIndex < 0 ) {
                return mathObject ;
            }
            else {
                V value = mathObject.getValue(valueIndex);
                return new MathObjectSingleton<>(mathObject.getArithmeticUnit(), value);
            }
        }
        else {
            V value = mathObject.getValue(valueIndex);
            return new MathObjectSingleton<>(mathObject.getArithmeticUnit(), value);
        }
    }

    private boolean lockStack ;

    public boolean isLockedStack() {
        return lockStack;
    }

    public void lockStack() {
        if (!immutable) throw new IllegalStateException("Can't lock non immutable stack: "+ name) ;
        lockStack = true ;
    }

    public void add(MathObject<V> value) {
        if ( immutable && lockStack ) throw new IllegalStateException("Can't modify immutable stack: "+ name) ;

        boolean valuesFull = valuesSize == values.length;

        if (valuesFull) {
            if ( valuesExtra == null ) {
                valuesExtra = value ;
                allValues = null ;
                return ;
            }
            else {
                values = Arrays.copyOf(values, (valuesSize+1) * 2);
                values[valuesSize] = valuesExtra ;
                valuesSize++ ;
                valuesExtra = null ;
            }
        }

        values[valuesSize] = value;
        valuesSize++;

        allValues = null ;
    }

    public void clear() {
        if ( immutable && lockStack ) throw new IllegalStateException("Can't modify immutable stack: "+ name) ;

        for (int i = this.valuesSize-1; i >= 0; i--) {
            this.values[i] = null ;
        }

        this.valuesSize = 0 ;
        this.valuesExtra = null ;
        this.allValues = null ;
    }

    private StringBuilder toStringValues() {
        StringBuilder strValues = new StringBuilder("<");

        int sz = size() ;

        for (int i = 0; i < sz; i++) {
            if (i > 0) strValues.append(", ");
            MathObject<V> value = get(i);
            strValues.append(value);
        }

        strValues.append(">");

        return strValues;
    }

    @Override
    public String toString() {
        String str = "MathStack{" +
                "name='" + name + '\'' +
                ", immutable='" + immutable + '\'' +
                ", lockStack='" + lockStack + '\'' +
                ", size='" + size() + '\'' +
                '}';

        if ( size() <= 4 ) {
            str += toStringValues() ;
        }

        return str ;
    }

    private StringBuilder toStringValuesMultiline() {
        StringBuilder strValues = new StringBuilder();

        int sz = size() ;

        for (int i = 0; i < sz; i++) {
            MathObject<V> value = get(i);
            strValues.append("[");
            strValues.append(i);
            strValues.append("] ");
            strValues.append(value);
            strValues.append("\n");
        }
        return strValues;
    }

    public String toStringFull() {
        StringBuilder strValues = toStringValuesMultiline();

        return "MathStack{" +
                "name='" + name + '\'' +
                ", immutable='" + immutable + '\'' +
                ", lockStack='" + lockStack + '\'' +
                ", size='" + size() + '\'' +
                "}<\n"+ strValues +">";
    }

}
