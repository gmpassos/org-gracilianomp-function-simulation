package org.gracilianomp.function;

import org.gracilianomp.arithmetic.MathValue;

public enum StackType {
    INPUT(new GetterInput()),
    RUNTIME(new GetterRuntime()),
    GLOBAL(new GetterGlobal())
    ;

    private final Getter getter ;

    StackType(Getter getter) {
        this.getter = getter;
    }

    final public MathStack getStack(MathFunction function) {
        return getter.getStack(function) ;
    }

    abstract static class Getter {
        abstract public <V extends MathValue> MathStack<V> getStack(MathFunction<V> function) ;
    }

    final static class GetterInput extends Getter {
        @Override
        public <V extends MathValue> MathStack<V> getStack(MathFunction<V> function) {
            return function.getInputStack();
        }
    }

    final static class GetterRuntime extends Getter {
        @Override
        public <V extends MathValue> MathStack<V> getStack(MathFunction<V> function) {
            return function.getRuntimeStack() ;
        }
    }

    final static class GetterGlobal extends Getter {
        @Override
        public <V extends MathValue> MathStack<V> getStack(MathFunction<V> function) {
            return function.getGlobalStack();
        }
    }

}
