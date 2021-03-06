package org.gracilianomp.function;

import org.gracilianomp.arithmetic.ArithmeticOperation;
import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.arithmetic.MathValue;

import java.util.Objects;

final public class FunctionOperation {
    final private long generationID;
    final protected ArithmeticOperation arithmeticOperation ;
    final private StackValue valueA ;
    final private StackValue valueB ;

    public FunctionOperation(ArithmeticOperation arithmeticOperation, StackValue valueA, StackValue valueB) {
        this(0, arithmeticOperation, valueA, valueB);
    }

    public FunctionOperation(long generationID, ArithmeticOperation arithmeticOperation, StackValue valueA, StackValue valueB) {
        this.generationID = generationID ;
        this.arithmeticOperation = arithmeticOperation;

        if ( arithmeticOperation.isSingleValue() ) {
            if ( valueA == null ) {
                valueA = valueB ;
            }
            else {
                valueB = null ;
            }
        }
        else if (arithmeticOperation.isMirrored()) {
            if ( valueA.compareTo(valueB) > 0 ) {
                StackValue tmp = valueA ;
                valueA = valueB ;
                valueB = tmp ;
            }
        }

        this.valueA = valueA;
        this.valueB = valueB;
    }

    public FunctionOperation copy(long generationID) {
        return new FunctionOperation(generationID, this.arithmeticOperation, this.valueA, this.valueB) ;
    }

    public long getGenerationID() {
        return generationID;
    }

    public ArithmeticOperation getArithmeticOperation() {
        return arithmeticOperation;
    }

    public <V extends MathValue> MathObject<V> getStackValueA(MathFunction<V> function) {
        return valueA.getStackValue(function) ;
    }

    public <V extends MathValue> MathObject<V> getStackValueB(MathFunction<V> function) {
        if (valueB == null) return null ;

        return valueB.getStackValue(function) ;
    }

    public StackValue getValueA() {
        return valueA;
    }

    public StackValue getValueB() {
        return valueB;
    }

    public boolean hasValueB() {
        return valueB != null ;
    }

    public boolean hasValueFromStackType(StackType stackType) {
        if ( valueA.getStackType() == stackType ) return true ;
        if ( hasValueB() && valueB.getStackType() == stackType ) return true ;
        return false ;
    }

    public <V extends MathValue> int calcOutputSize(MathFunction<V> function) {
        MathObject<V> valueA = getStackValueA(function);

        if ( hasValueB() ) {
            MathObject<V> valueB = getStackValueB(function);
            return valueA.calcOutputSize( this.arithmeticOperation , valueB ) ;
        }
        else {
            return valueA.calcOutputSizeSingleValue( this.arithmeticOperation , valueA ) ;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionOperation that = (FunctionOperation) o;
        return arithmeticOperation == that.arithmeticOperation &&
                Objects.equals(valueA, that.valueA) &&
                Objects.equals(valueB, that.valueB);
    }

    @Override
    public int hashCode() {
        int result = 31  + arithmeticOperation.hashCode() ;
        result = 31 * result + valueA.hashCode() ;
        result = 31 * result + ( valueB != null ? valueB.hashCode() : 0 ) ;
        return result;
    }

    @Override
    public String toString() {
        if ( arithmeticOperation.isSingleValue() ) {
            return "{" +
                    arithmeticOperation +
                    ", " + valueA +
                    '}';
        }
        else {
            return "{" +
                    arithmeticOperation +
                    ", " + valueA +
                    ", " + valueB +
                    '}';
        }
    }

    public <V extends MathValue> String toString( MathFunction<V> function ) {
        if ( arithmeticOperation.isSingleValue() ) {
            return "{" +
                    arithmeticOperation +
                    ", " + valueA.toString(function) +
                    '}';
        }
        else {
            return "{" +
                    arithmeticOperation +
                    ", " + valueA.toString(function) +
                    ", " + valueB.toString(function) +
                    '}';
        }
    }


}
