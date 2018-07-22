package org.gracilianomp.arithmetic;

abstract public class MathObject<V extends MathValue> {

    abstract public ArithmeticUnit<V> getArithmeticUnit() ;

    public boolean isCompatible(MathObject o) {
        return getArithmeticUnit().isCompatible(o) ;
    }

    public void checkCompatible(MathObject o) {
        checkCompatible(this, o);
    }

    public void checkCompatible(MathObject a, MathObject b) {
        if ( !a.isCompatible(b) ) {
            throw new ArithmeticException("Not compatible instances of MathObject: "+ a +" != "+ b) ;
        }
    }

    abstract public boolean isSingleton() ;

    public boolean isCollection() {
        return !isSingleton();
    }

    abstract public V getValue() ;

    abstract public V[] getValues() ;

    abstract public V getValue(int idx) ;

    abstract public int size();

    abstract public double distance(MathObject<V> o);

    abstract public String toString() ;

    ///////////////////////////////////////////////////////////////

    public MathObject<V> sum(MathObject<V> o) {
        return calc(ArithmeticOperation.SUM, o) ;
    }

    public MathObject<V> subtract(MathObject<V> o) {
        return calc(ArithmeticOperation.SUBTRACT, o) ;
    }

    public MathObject<V> multiply(MathObject<V> o) {
        return calc(ArithmeticOperation.MULTIPLY, o) ;
    }

    public MathObject<V> divide(MathObject<V> o) {
        return calc(ArithmeticOperation.DIVIDE, o) ;
    }

    public MathObject<V> power(MathObject<V> o) {
        return calc(ArithmeticOperation.POWER, o) ;
    }

    public MathObject<V> root(MathObject<V> o) {
        return calc(ArithmeticOperation.ROOT, o) ;
    }

    ///////////////////////////////////////////////////////////////

    public MathObject<V> sum(V b) {
        return this.calc(ArithmeticOperation.SUM, new MathObjectSingleton<>(getArithmeticUnit(), b)) ;
    }

    public MathObject<V> subtract(V b) {
        return this.calc(ArithmeticOperation.SUBTRACT, new MathObjectSingleton<>(getArithmeticUnit(), b)) ;
    }

    public MathObject<V> multiply(V b) {
        return this.calc(ArithmeticOperation.MULTIPLY, new MathObjectSingleton<>(getArithmeticUnit(), b)) ;
    }

    public MathObject<V> divide(V b) {
        return this.calc(ArithmeticOperation.DIVIDE, new MathObjectSingleton<>(getArithmeticUnit(), b)) ;
    }

    public MathObject<V> power(V b) {
        return this.calc(ArithmeticOperation.POWER, new MathObjectSingleton<>(getArithmeticUnit(), b)) ;
    }

    public MathObject<V> root(V b) {
        return this.calc(ArithmeticOperation.ROOT, new MathObjectSingleton<>(getArithmeticUnit(), b)) ;
    }

    public MathObject<V> log() {
        return this.calc(ArithmeticOperation.LOG, new MathObjectSingleton<>(getArithmeticUnit(), getArithmeticUnit().ONE())) ;
    }

    public MathObject<V> exp() {
        return this.calc(ArithmeticOperation.EXP, new MathObjectSingleton<>(getArithmeticUnit(), getArithmeticUnit().ONE())) ;
    }

    ///////////////////////////////////////////////////////////////

    public MathObject<V> collectionSum() {
        return calc(ArithmeticOperation.COLLECTION_SUM, null) ;
    }

    public MathObject<V> collectionAverage() {
        return calc(ArithmeticOperation.COLLECTION_AVERAGE, null) ;
    }

    ///////////////////////////////////////////////////////////////

    public V calc(ArithmeticOperation op, V a, V b) {
        return getArithmeticUnit().calc(op, a, b);
    }

    public MathObject<V> calc(ArithmeticOperation op, MathObject<V> o) {
        if (op.isSingleValue()) {
            return calcSingleValue(op, this);
        }

        return calcImpl(op, o);
    }

    protected MathObject<V> calcImpl(ArithmeticOperation op, MathObject<V> o) {
        boolean thisSingleton = this.isSingleton();
        boolean oSingleton = o.isSingleton();

        if ( thisSingleton && oSingleton ) {
            return calc(op, (MathObjectSingleton<V>)this, (MathObjectSingleton<V>)o );
        }
        else if ( thisSingleton ) {
            return calc(op, (MathObjectSingleton<V>)this, (MathObjectCollection<V>)o );
        }
        else if ( oSingleton ) {
            return calc(op, (MathObjectCollection<V>)this, (MathObjectSingleton<V>)o);
        }
        else {
            return calc(op, (MathObjectCollection<V>)this, (MathObjectCollection<V>)o );
        }
    }

    public MathObject<V> calcSingleValue(ArithmeticOperation op, MathObject<V> a) {
        if (op.isCollection()) {
            return op.calc(a, null);
        }
        else {
            ArithmeticUnit<V> au = getArithmeticUnit();
            V aValue = a.getValue();
            V res = op.calc(au, aValue, null);
            return new MathObjectSingleton<>(au, res) ;
        }
    }

    protected MathObject<V> calc(ArithmeticOperation op, MathObjectSingleton<V> a, MathObjectSingleton<V> b) {
        //checkCompatible(a,b);
        ArithmeticUnit<V> au = getArithmeticUnit();

        V aValue = a.getValue();
        V bValue = b.getValue();

        V res = op.calc(au, aValue, bValue);

        if ( aValue == res ) return a ;
        if ( bValue == res ) return b ;

        return new MathObjectSingleton<>(au, res) ;
    }

    protected MathObject<V> calc(ArithmeticOperation op, MathObjectSingleton<V> a, MathObjectCollection<V> b) {
        //checkCompatible(a,b);
        ArithmeticUnit<V> au = getArithmeticUnit();

        V aValue = a.getValue();

        V[] values = b.getValues();
        V[] resValues = au.newValuesArray(values.length);

        for (int i = 0; i < values.length; i++) {
            V v = values[i];
            V res = op.calc(au, aValue, v);
            resValues[i] = res ;
        }

        return new MathObjectCollection<>(au, resValues) ;
    }

    protected MathObject<V> calc(ArithmeticOperation op, MathObjectCollection<V> a, MathObjectSingleton<V> b) {
        //checkCompatible(a,b);
        ArithmeticUnit<V> au = getArithmeticUnit();

        V[] aValues = a.getValues();
        V bValue = b.getValue();

        V[] resValues = au.newValuesArray(aValues.length);

        for (int i = 0; i < aValues.length; i++) {
            V v = aValues[i];
            V res = op.calc(au, v, bValue);
            resValues[i] = res ;
        }

        return new MathObjectCollection<>(au, resValues) ;
    }

    protected MathObject<V> calc(ArithmeticOperation op, MathObjectCollection<V> a, MathObjectCollection<V> b) {
        //checkCompatible(a,b);
        ArithmeticUnit<V> au = getArithmeticUnit();

        V[] aValues = a.getValues();
        V[] bValues = b.getValues();

        V[] resValues = au.newValuesArray( aValues.length * bValues.length );
        int resValuesSz = 0 ;

        for (V aValue : aValues) {
            for (V bValue : bValues) {
                V res = op.calc(au, aValue, bValue);
                resValues[resValuesSz++] = res ;
            }
        }

        return new MathObjectCollection<>(au, resValues) ;
    }

    ///////////////////////////////////////////////////////////////


    @Override
    abstract public int hashCode() ;

    @Override
    abstract public boolean equals(Object obj) ;


}
