package org.gracilianomp.function;

public class TemplateOperations {
    final FunctionOperation[] operations;
    final int minRuntimeStackIdx ;

    public TemplateOperations(FunctionOperation[] operations) {
        this.operations = operations;

        int minRuntimeStackIdx = Integer.MAX_VALUE ;

        for (int i = 0; i < operations.length; i++) {
            FunctionOperation operation = operations[i];

            StackValue valueA = operation.getValueA();
            if ( valueA.getStackType() == StackType.RUNTIME ) {
                int valueAIdx = valueA.getStackIndex();
                if ( valueAIdx < minRuntimeStackIdx ) minRuntimeStackIdx = valueAIdx;
            }

            StackValue valueB = operation.getValueA();
            if ( valueB != null ) {
                if ( valueB.getStackType() == StackType.RUNTIME ) {
                    int valueBIdx = valueB.getStackIndex();
                    if ( valueBIdx < minRuntimeStackIdx ) minRuntimeStackIdx = valueBIdx;
                }
            }
        }

        if ( minRuntimeStackIdx == Integer.MAX_VALUE ) {
            minRuntimeStackIdx = -1 ;
        }

        this.minRuntimeStackIdx = minRuntimeStackIdx ;
    }

    public FunctionOperation[] getOperations() {
        return operations;
    }

    public int getMinRuntimeStackIdx() {
        return minRuntimeStackIdx;
    }
    
}
