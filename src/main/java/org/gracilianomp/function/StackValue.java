package org.gracilianomp.function;

import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.arithmetic.MathValue;

final public class StackValue implements Comparable<StackValue> {
    final private StackType stackType ;
    final private int stackIndex ;
    final private int valueIndex ;

    public StackValue(StackType stackType, int stackIndex) {
        this(stackType, stackIndex, -1);
    }

    public StackValue(StackType stackType, int stackIndex, int valueIndex) {
        this.stackType = stackType;
        this.stackIndex = stackIndex;
        this.valueIndex = valueIndex;
    }

    public StackType getStackType() {
        return stackType;
    }

    public int getStackIndex() {
        return stackIndex;
    }

    public int getValueIndex() {
        return valueIndex;
    }

    public <V extends MathValue> MathObject<V> getStackValue(MathFunction<V> function) {
        MathStack<V> stack = function.getStack(stackType);
        return stack.getValueObject( stackIndex, valueIndex ) ;
    }

    public <V extends MathValue> MathObject<V> getStackValue(MathStack<V> stack) {
        return stack.getValueObject( stackIndex, valueIndex ) ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StackValue that = (StackValue) o;
        return stackIndex == that.stackIndex &&
                valueIndex == that.valueIndex &&
                stackType == that.stackType;
    }

    @Override
    public int hashCode() {
        int result = 31  + stackType.hashCode() ;
        result = 31 * result + stackIndex ;
        result = 31 * result + valueIndex ;
        return result;
    }

    @Override
    public int compareTo(StackValue o) {
        int cmp = Integer.compare( this.stackType.ordinal() , o.stackType.ordinal() ) ;

        if (cmp == 0) {
            cmp = Integer.compare( this.stackIndex , o.stackIndex ) ;

            if (cmp == 0) {
                cmp = Integer.compare( this.valueIndex , o.valueIndex ) ;
            }
        }

        return cmp ;
    }

    @Override
    public String toString() {
        return "(" +
                stackType +
                ", " + stackIndex +

                (valueIndex < 0 ? "" : ", " + valueIndex ) +


                ')';
    }
}
