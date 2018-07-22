package org.gracilianomp.arithmetic;

public enum ArithmeticOperation {

    SUM(false, true, false, false, 1, new CallerSum()),
    SUBTRACT(false, false, false, false, 1, new CallerSubtract()),
    MULTIPLY(false, true, false, false, 10, new CallerMultiply()),
    DIVIDE(false, false, false, false, 11, new CallerDivide()),
    POWER(false, false, false, false, 100, new CallerPower()),
    ROOT(false, false, false, false, 120, new CallerRoot()),
    LOG(true, false, false, false, 200, new CallerLog()),
    EXP(true, false, false, false, 300, new CallerExp()),
    COS(true, false, false, false, 100, new CallerCos()),
    COLLECTION_SUM(true, false, true, true, 50, new CallerCollectionSum()),
    COLLECTION_AVERAGE(true, false, true, true, 100, new CallerCollectionAverage()),
    ;

    final private boolean singleValue ;
    final private boolean mirrored ;
    final private boolean collection ;
    final private boolean collectionSingletonUseless ;
    final private int complexity;
    final private Caller caller ;

    ArithmeticOperation(boolean singleValue, boolean mirrored, boolean collection, boolean collectionSingletonUseless, int complexity, Caller caller) {
        this.singleValue = singleValue ;
        this.mirrored = mirrored;
        this.collection = collection;
        this.collectionSingletonUseless = collectionSingletonUseless;
        this.complexity = complexity;
        this.caller = caller;
    }

    public boolean isSingleValue() {
        return singleValue;
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public boolean isCollection() {
        return collection;
    }

    public boolean isCollectionSingletonUseless() {
        return collectionSingletonUseless;
    }

    public int getComplexity() {
        return complexity;
    }

    public <V extends MathValue> V calc(ArithmeticUnit<V> au, V a, V b) {
        return caller.call(au, a, b);
    }

    public <V extends MathValue> MathObject<V> calc(MathObject<V> a, MathObject<V> b) {
        return caller.call(a, b);
    }

    /////////////////////////////////////////////////////////

    abstract static private class Caller {
        abstract <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) ;
        abstract <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) ;
    }

    final static class CallerSum extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            return au.sum(a,b);
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            return a.calc(SUM, b) ;
        }
    }

    final static class CallerSubtract extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            return au.subtract(a,b);
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            return a.calc(SUBTRACT, b) ;
        }
    }

    final static class CallerMultiply extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            return au.multiply(a,b);
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            return a.calc(MULTIPLY, b) ;
        }
    }

    final static class CallerDivide extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            return au.divide(a,b);
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            return a.calc(DIVIDE, b) ;
        }
    }

    final static class CallerPower extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            return au.power(a,b);
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            return a.calc(POWER, b) ;
        }
    }

    final static class CallerRoot extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            return au.root(a,b);
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            return a.calc(ROOT, b) ;
        }
    }

    final static class CallerLog extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            return au.log(a);
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            return a.calc(LOG, b) ;
        }
    }

    final static class CallerExp extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            return au.exp(a);
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            return a.calc(EXP, b) ;
        }
    }

    final static class CallerCos extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            return au.cos(a);
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            return a.calc(COS, b) ;
        }
    }

    final static class CallerCollectionSum extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            throw new UnsupportedOperationException() ;
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            ArithmeticUnit<V> au = a.getArithmeticUnit();
            V val = au.collectionSum(a.getValues());
            return new MathObjectSingleton<V>(au, val);
        }
    }

    final static class CallerCollectionAverage extends Caller {
        @Override
        <V extends MathValue> V call(ArithmeticUnit<V> au, V a, V b) {
            throw new UnsupportedOperationException() ;
        }

        @Override
        <V extends MathValue> MathObject<V> call(MathObject<V> a, MathObject<V> b) {
            ArithmeticUnit<V> au = a.getArithmeticUnit();
            V val = au.collectionAverage(a.getValues());
            return new MathObjectSingleton<V>(au, val);
        }
    }

}
