package org.gracilianomp.function;

import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.arithmetic.MathValue;

import java.util.Arrays;

final public class MathFunction<V extends MathValue> implements Comparable<MathFunction<V>> {

    ///////////////////////////////////////////////////

    final private MathStack<V> globalStack ;
    final private MathStack<V> inputStack ;
    final private MathStack<V> runtimeStack ;
    final private FunctionOperation[] operations ;
    final private FunctionOperation operationsExtra ;

    final private int operationsSize ;

    public MathFunction(MathStack<V> globalStack, MathStack<V> inputStack, FunctionOperation... operations) {
        if ( !globalStack.isImmutable() ) throw new IllegalArgumentException("GlobalStack should be immutable!") ;
        if ( !inputStack.isImmutable() ) throw new IllegalArgumentException("InputStack should be immutable!") ;

        this.globalStack = globalStack;
        this.inputStack = inputStack;
        this.runtimeStack = new MathStack<>( StackType.RUNTIME.name() );
        this.operations = operations ;
        this.operationsExtra = null ;
        this.operationsSize = calcOperationsSize();

        this.globalStack.lockStack();
        this.inputStack.lockStack();
    }

    private MathFunction(MathStack<V> globalStack, MathStack<V> inputStack, MathStack<V> runtimeStack, int executedOperations, FunctionOperation[] operations, FunctionOperation operationsExtra ) {
        this.globalStack = globalStack;
        this.inputStack = inputStack;
        this.runtimeStack = runtimeStack;
        this.executedOperations = executedOperations;
        this.operations = operations ;
        this.operationsExtra = operationsExtra ;
        this.operationsSize = calcOperationsSize();
    }

    public MathFunction<V> copy() {
        MathFunction<V> copy = new MathFunction<>(globalStack, inputStack, runtimeStack.copy(), executedOperations, getAllOperations(), null);
        return copy;
    }

    public MathFunction<V> copy(MathStack<V> inputStack) {
        MathFunction<V> copy = new MathFunction<>(globalStack, inputStack, new MathStack<>( StackType.RUNTIME.name() , false, this.getOperationsSize()), 0, this.operations, this.operationsExtra);
        return copy;
    }

    public MathFunction<V> copy(FunctionOperation[] operations) {
        MathFunction<V> copy = new MathFunction<>(globalStack, inputStack, runtimeStack.copy(), executedOperations, operations, null);
        return copy;
    }

    public MathFunction<V> copy(FunctionOperation extraOperation) {
        MathFunction<V> copy = new MathFunction<V>(globalStack, inputStack, runtimeStack.copy(), executedOperations, getAllOperations(), extraOperation);
        return copy;
    }

    public MathFunction<V> copy(FunctionOperation extraOperation, TemplateOperations templateOperations) {
        if ( templateOperations.minRuntimeStackIdx >= runtimeStack.size() ) {
            return copy(extraOperation) ;
        }

        return copy(extraOperation, templateOperations.operations);
    }

    public MathFunction<V> copy(FunctionOperation extraOperation, FunctionOperation[] templateOperations) {
        FunctionOperation[] validTemplateOps = filterValidOperations(templateOperations, extraOperation);

        if ( validTemplateOps.length == 0 ) {
            return copy(extraOperation) ;
        }

        return copyWithTemplateOperations(extraOperation, validTemplateOps);
    }

    private MathFunction<V> copyWithTemplateOperations(FunctionOperation extraOperation, FunctionOperation[] validTemplateOps) {
        FunctionOperation[] allOperations = getAllOperations();

        int operationsCopySize = allOperations.length + validTemplateOps.length ;
        FunctionOperation[] operationsCopy ;

        if (extraOperation != null) {
            operationsCopySize++ ;
            operationsCopy = new FunctionOperation[operationsCopySize] ;

            System.arraycopy(allOperations, 0, operationsCopy, 0, allOperations.length);
            operationsCopy[allOperations.length] = extraOperation ;
            System.arraycopy(validTemplateOps, 0, operationsCopy, allOperations.length+1, validTemplateOps.length);
        }
        else {
            operationsCopy = new FunctionOperation[operationsCopySize] ;

            System.arraycopy(allOperations, 0, operationsCopy, 0, allOperations.length);
            System.arraycopy(validTemplateOps, 0, operationsCopy, allOperations.length, validTemplateOps.length);
        }

        MathFunction<V> copy = new MathFunction<V>(globalStack, inputStack, runtimeStack.copy(), executedOperations, operationsCopy, null);
        return copy;
    }

    public FunctionOperation[] filterValidOperations(FunctionOperation[] operations) {
        return filterValidOperations(operations , null);
    }

    public FunctionOperation[] filterValidOperations(FunctionOperation[] operations, FunctionOperation extraOperation) {
        FunctionOperation[] operationsOk = new FunctionOperation[ operations.length ] ;
        int operationsOkSz = 0 ;

        for (int i = 0; i < operations.length; i++) {
            FunctionOperation op = operations[i];
            if ( existsOperationStackValues(op, extraOperation) ) {
                operationsOk[operationsOkSz++] = op ;
            }
        }

        if ( operationsOkSz < operationsOk.length ) {
            operationsOk = Arrays.copyOf(operationsOk, operationsOkSz) ;
        }

        return operationsOk ;
    }

    public boolean existsOperationStackValues(FunctionOperation operation, FunctionOperation extraOperation) {
        StackValue valueA = operation.getValueA();

        if ( !existsStackValue(valueA, extraOperation) ) return false ;

        StackValue valueB = operation.getValueB();
        return valueB == null || existsStackValue(valueB, extraOperation) ;
    }

    public boolean existsOperationStackValues(FunctionOperation operation) {
        StackValue valueA = operation.getValueA();

        if ( !existsStackValue(valueA) ) return false ;

        StackValue valueB = operation.getValueB();
        return valueB == null || existsStackValue(valueB) ;
    }

    public boolean existsStackValue(StackValue stackValue, FunctionOperation extraOperation) {
        if ( existsStackValue(stackValue) ) return true ;

        if (extraOperation == null) return false ;

        StackType stackType = stackValue.getStackType();
        if ( stackType != StackType.RUNTIME ) return false ;

        MathStack<V> runtimeStack = this.getRuntimeStack();

        int stackIndex = stackValue.getStackIndex();

        if ( stackIndex == runtimeStack.size() ) {
            int valueIndex = stackValue.getValueIndex();
            if (valueIndex <= 0) return true ;

            return valueIndex < extraOperation.calcOutputSize(this) ;
        }

        return false ;
    }

    public boolean existsStackValue(StackValue stackValue) {
        StackType stackType = stackValue.getStackType();
        MathStack<V> stack = stackType.getStack(this);

        int stackIndex = stackValue.getStackIndex();
        if ( stackIndex >= stack.size() ) return false ;

        int valueIndex = stackValue.getValueIndex();
        if (valueIndex <= 0) return true ;

        MathObject<V> mathObject = stack.get(stackIndex);
        return valueIndex < mathObject.size()  ;
    }

    public FunctionOperation[] getOperations() {
        return getAllOperations().clone();
    }

    private FunctionOperation[] allOperations ;

    private FunctionOperation[] getAllOperations() {
        if (allOperations == null) {
            allOperations = getAllOperationsImpl() ;
        }
        return allOperations ;
    }

    private FunctionOperation[] getAllOperationsImpl() {
        if (this.operationsExtra == null) {
            return this.operations.clone();
        }
        else {
            FunctionOperation[] all = Arrays.copyOf(this.operations, this.operations.length+1) ;
            all[ operations.length ] = operationsExtra ;
            return all ;
        }
    }

    public MathStack<V> getGlobalStack() {
        return globalStack;
    }

    public MathStack<V> getInputStack() {
        return inputStack;
    }

    public MathStack<V> getRuntimeStack() {
        return runtimeStack;
    }

    public boolean hasSameStackSignature(MathFunction<V> o) {
        if ( this == o ) return true ;

        return this.runtimeStack.hasSameSignature(o.runtimeStack) &&
                this.globalStack.hasSameSignature(o.globalStack) &&
                this.inputStack.hasSameSignature(o.inputStack)
                ;
    }

    public int getOperationsSize() {
        return operationsSize ;
    }

    private int calcOperationsSize() {
        return operationsExtra != null ? operations.length+1 : operations.length ;
    }

    public boolean hasOperations() {
        return operationsExtra != null || operations.length > 0 ;
    }

    public FunctionOperation getOperation(int idx) {
        if (idx < 0) return operations[ operations.length+idx ] ;

        if (idx == operations.length && operationsExtra != null) return operationsExtra ;

        return operations[idx];
    }

    private long operationsComplexity ;

    public long getOperationsComplexity() {
        if ( operationsComplexity == 0 ) {
            operationsComplexity = calcOperationsComplexity() ;
        }
        return operationsComplexity ;
    }

    private long calcOperationsComplexity() {
        long globalComplexity = 0 ;

        for (int i = operations.length-1 ; i >= 0; i--) {
            FunctionOperation operation = operations[i];
            int complexity = operation.getArithmeticOperation().getComplexity();
            globalComplexity += complexity ;
        }

        if ( operationsExtra != null ) {
            int complexity = operationsExtra.getArithmeticOperation().getComplexity();
            globalComplexity += complexity ;
        }

        return globalComplexity ;
    }

    private MathObject<V> result ;

    public MathObject<V> getResult() {
        if (result == null && hasOperations()) execute();
        return result;
    }

    public MathObject<V> getResultNoException() {
        if (result == null && hasOperations()) executeNoException();
        return result;
    }

    public boolean hasResult() {
        return result != null ;
    }

    public boolean tryContinueExecution() {
        try {
            continueExecution();
            return true ;
        }
        catch (ArithmeticException e) {
            return false ;
        }
    }

    public boolean tryExecute() {
        try {
            execute();
            return true ;
        }
        catch (ArithmeticException e) {
            return false ;
        }
    }

    private int executedOperations ;

    public MathObject<V> continueExecution() {
        if ( executedOperations <= 0 ) {
            return execute() ;
        }

        MathObject<V> finalResult = result ;

        int opsCount = executedOperations ;

        for (int i = opsCount; i < operations.length; i++) {
            FunctionOperation operation = operations[i];
            MathObject<V> res = executeOperation(operation);
            this.runtimeStack.add(res);
            finalResult = res ;
            opsCount++ ;
        }

        if (operationsExtra != null && opsCount == operations.length) {
            MathObject<V> res = executeOperation(operationsExtra);
            this.runtimeStack.add(res);
            finalResult = res ;

            opsCount++ ;
        }

        if (opsCount > this.executedOperations) {
            this.executedOperations = opsCount ;
            this.result = finalResult ;

            return finalResult ;
        }
        else {
            if (result != null && executedOperations == getOperationsSize()) {
                return result;
            }

            return execute();
        }
    }

    public MathObject<V> execute() {
        try {
            MathObject<V> result = executeImpl();
            return result;
        }
        catch (ArithmeticException e) {
            System.out.println("Error executing function:");
            System.err.println(this);
            System.err.println("- Error message: "+ e.getMessage());
            throw e ;
        }
    }

    public MathObject<V> executeNoException() {
        try {
            MathObject<V> result = executeImpl();
            return result;
        }
        catch (ArithmeticException e) {
            return null ;
        }
    }

    private MathObject<V> executeImpl() {
        if ( this.runtimeStack.size() > 0 ) {
            this.runtimeStack.clear();
        }

        MathObject<V> finalResult = null ;

        for (FunctionOperation operation : operations) {
            MathObject<V> res = executeOperation(operation);
            this.runtimeStack.add(res);
            finalResult = res ;
        }

        int opsCount = operations.length ;

        if (operationsExtra != null) {
            MathObject<V> res = executeOperation(operationsExtra);
            this.runtimeStack.add(res);
            finalResult = res ;

            opsCount++ ;
        }

        this.executedOperations = opsCount ;
        this.result = finalResult ;

        return finalResult ;
    }

    public MathObject<V> executeOperation(FunctionOperation operation) {
        MathObject<V> vA = operation.getStackValueA(this) ;
        MathObject<V> vB = operation.getStackValueB(this) ;

        MathObject<V> res = vA.calc(operation.arithmeticOperation, vB);
        return res ;
    }

    public MathObject<V> getStackValue(StackValue stackValue) {
        return stackValue.getStackValue(this) ;
        /*
        MathStack<V> stack = getStack(stackValue.getStackType());
        return stack.getValueObject( stackValue.getStackIndex() , stackValue.getValueIndex() ) ;
        */
    }

    public MathStack<V> getStack(StackType stackType) {
        return stackType.getStack(this);
    }

    public double distance(MathObject<V> target) {
        if ( !hasOperations() ) {
            throw new IllegalStateException();
        }

        MathObject<V> r1 = getResult();
        double d1 = target.distance(r1);
        if (d1 < 0) d1 = -d1;
        return d1 ;
    }

    public double distanceNoException(MathObject<V> target) {
        if ( !hasOperations() ) return Double.NaN ;

        MathObject<V> r1 = getResultNoException();
        if (r1 == null) return Double.NaN ;

        double d1 = target.distance(r1);
        if (d1 < 0) d1 = -d1;
        return d1 ;
    }

    public boolean isUsedOperation(int opIdx) {
        int operationsSize = getOperationsSize();

        if (opIdx >= operationsSize) throw new IllegalArgumentException("Invalid operator index: opIdx:"+ opIdx +" > operationsSize:"+ operationsSize);

        if (opIdx == operationsSize-1) return true ;

        if ( operationsSize < 64 ) {
            long usedOperations = calcUsedOperationsBits();
            long opBit = 1L << opIdx ;
            long mask = usedOperations & opBit;
            return mask != 0 ;
        }
        else {
            boolean[] usedOperations = calcUsedOperationsArray();
            return usedOperations[opIdx] ;
        }
    }

    public boolean hasUnusedOperation() {
        int operationsSize = getOperationsSize();
        if ( operationsSize < 64 ) {
            long usedOperations = calcUsedOperationsBits();
            long expectedBits = (1L << (operationsSize-1)) -1 ;
            return usedOperations != expectedBits ;
        }
        else {
            boolean[] usedOperations = calcUsedOperationsArray();

            for (int i = usedOperations.length-2 ; i >= 0 ; i--) {
                if ( !usedOperations[i] ) return true ;
            }

            return false ;
        }
    }

    private long calcUsedOperationsBits() {
        long usedOperations = 0 ;

        for (int i = operations.length-1; i >= 0; i--) {
            FunctionOperation operation = operations[i];

            StackValue valueA = operation.getValueA();
            StackValue valueB = operation.getValueB();

            if ( valueA.getStackType() == StackType.RUNTIME ) {
                usedOperations|= 1L << valueA.getStackIndex() ;
            }

            if ( valueB != null && valueB.getStackType() == StackType.RUNTIME ) {
                usedOperations|= 1L << valueB.getStackIndex() ;
            }
        }

        if (operationsExtra != null) {
            FunctionOperation operation = this.operationsExtra ;

            StackValue valueA = operation.getValueA();
            StackValue valueB = operation.getValueB();

            if ( valueA.getStackType() == StackType.RUNTIME ) {
                usedOperations|= 1L << valueA.getStackIndex() ;
            }

            if ( valueB != null && valueB.getStackType() == StackType.RUNTIME ) {
                usedOperations|= 1L << valueB.getStackIndex() ;
            }
        }


        return usedOperations ;
    }

    private boolean[] calcUsedOperationsArray() {
        boolean[] usedOperations = new boolean[ getOperationsSize()-1 ] ;

        for (int i = operations.length-1; i >= 0; i--) {
            FunctionOperation operation = operations[i];

            StackValue valueA = operation.getValueA();
            StackValue valueB = operation.getValueB();

            if ( valueA.getStackType() == StackType.RUNTIME ) {
                usedOperations[valueA.getStackIndex()] = true ;
            }

            if ( valueB != null && valueB.getStackType() == StackType.RUNTIME ) {
                usedOperations[valueB.getStackIndex()] = true ;
            }
        }

        if (operationsExtra != null) {
            FunctionOperation operation = this.operationsExtra ;

            StackValue valueA = operation.getValueA();
            StackValue valueB = operation.getValueB();

            if ( valueA.getStackType() == StackType.RUNTIME ) {
                usedOperations[valueA.getStackIndex()] = true ;
            }

            if ( valueB != null && valueB.getStackType() == StackType.RUNTIME ) {
                usedOperations[valueB.getStackIndex()] = true ;
            }
        }

        return usedOperations ;
    }

    public String infos() {
        StringBuilder s = new StringBuilder();

        s.append(globalInfos()) ;
        s.append("\n") ;
        s.append(inputsInfos()) ;
        s.append("\n") ;
        s.append(runtimeInfos()) ;
        s.append("\n") ;
        s.append(operationsInfos()) ;

        return s.toString();
    }


    public String operationsInfos() {
        StringBuilder s = new StringBuilder("Operations:\n");

        for (int i = 0; i < operations.length; i++) {
            FunctionOperation operation = operations[i];

            s.append("[");
            s.append(i);
            s.append("] ");

            s.append( operation.toString(this) );

            if (!isUsedOperation(i)) s.append(" !");

            s.append("\n");
        }

        if (operationsExtra != null) {
            s.append("[");
            s.append(operations.length);
            s.append("] ");

            s.append( operationsExtra.toString(this) );
            s.append("\n");
        }

        return s.toString();
    }

    public String globalInfos() {
        return "GlobalStack:\n"+globalStack.toStringFull()+"\n";
    }

    public String inputsInfos() {
        return "InputStack:\n"+inputStack.toStringFull()+"\n";
    }

    public String runtimeInfos() {
        return "RuntimeStack:\n"+runtimeStack.toStringFull()+"\n";
    }

    @Override
    public String toString() {
        return "MathFunction{\n\n"+ infos() +"\n}";
    }

    @Override
    public int compareTo(MathFunction<V> o) {
        if (o == null) return -1 ;

        int s1 = this.operationsSize ;
        int s2 = o.operationsSize ;

        int cmp = Integer.compare(s1,s2) ;

        if (cmp == 0) {
            cmp = Long.compare( this.getOperationsComplexity() , o.getOperationsComplexity() ) ;
        }

        return cmp ;
    }

    public boolean isBetter(MathFunction<V> o) {
        if (o == null) return true ;

        int s1 = this.operationsSize ;
        int s2 = o.operationsSize ;

        if (s1 < s2) return true ;

        if (s1 == s2) {
            return this.getOperationsComplexity() < o.getOperationsComplexity() ;
        }
        else {
            return false ;
        }
    }


}
