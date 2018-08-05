package org.gracilianomp.arithmetic;

import org.gracilianomp.arithmetic.fast.ArithmeticUnitFast;
import org.gracilianomp.arithmetic.fast.MathValueFast;
import org.junit.Assert;
import org.junit.Test;

public class MathObjectFastTest {

    static final private ArithmeticUnitFast au = ArithmeticUnitFast.instance;

    @Test
    public void testSingleton1() {

        assertEquals(true, 3 , ArithmeticUnitFast.TWO.sum(ArithmeticUnitFast.ONE)) ;
        assertEquals(true, 2 , ArithmeticUnitFast.ONE.sum(ArithmeticUnitFast.ONE)) ;

        assertEquals(true, 2 , ArithmeticUnitFast.TWO.sum(ArithmeticUnitFast.ZERO)) ;
        assertEquals(true, 1 , ArithmeticUnitFast.ONE.sum(ArithmeticUnitFast.ZERO)) ;

        assertEquals(true, 0 , ArithmeticUnitFast.TWO.subtract(ArithmeticUnitFast.TWO)) ;
        assertEquals(true, 1 , ArithmeticUnitFast.TWO.subtract(ArithmeticUnitFast.ONE)) ;
        assertEquals(true, 2 , ArithmeticUnitFast.TWO.subtract(ArithmeticUnitFast.ZERO)) ;

        assertEquals(true, 4 , ArithmeticUnitFast.TWO.multiply(ArithmeticUnitFast.TWO)) ;
        assertEquals(true, 2 , ArithmeticUnitFast.TWO.multiply(ArithmeticUnitFast.ONE)) ;
        assertEquals(true, 0 , ArithmeticUnitFast.TWO.multiply(ArithmeticUnitFast.ZERO)) ;

        assertEquals(true, 1 , ArithmeticUnitFast.TWO.divide(ArithmeticUnitFast.TWO)) ;
        assertEquals(true, 2 , ArithmeticUnitFast.TWO.divide(ArithmeticUnitFast.ONE)) ;
        assertEquals(true, -2 , ArithmeticUnitFast.TWO.divide(ArithmeticUnitFast.MINUS_ONE)) ;

        assertEquals(true, 4 , ArithmeticUnitFast.TWO.power(ArithmeticUnitFast.TWO)) ;
        assertEquals(true, 3 , ArithmeticUnitFast.THREE.power(ArithmeticUnitFast.ONE)) ;
        assertEquals(true, 1 , ArithmeticUnitFast.THREE.power(ArithmeticUnitFast.ZERO)) ;
        assertEquals(true, 0 , ArithmeticUnitFast.ZERO.power(ArithmeticUnitFast.TWO)) ;

        assertEquals(true, 2 , ArithmeticUnitFast.FOUR.root(ArithmeticUnitFast.TWO)) ;
        assertEquals(true, 4 , ArithmeticUnitFast.FOUR.root(ArithmeticUnitFast.ONE)) ;

    }

    @Test
    public void testCollection1() {

        MathObjectCollection<MathValueFast> obj_One_Two = new MathObjectCollection<>(au, MathValueFast.ONE, MathValueFast.TWO);

        assertEquals(new long[] {2,3} , obj_One_Two.sum(ArithmeticUnitFast.ONE)) ;
        assertEquals(new long[] {0,1} , obj_One_Two.sum(ArithmeticUnitFast.MINUS_ONE)) ;
        assertEquals(new long[] {3,4} , obj_One_Two.sum(ArithmeticUnitFast.TWO)) ;
        assertEquals(new long[] {0,1} , obj_One_Two.sum(ArithmeticUnitFast.MINUS_ONE)) ;

        assertEquals(new long[] {0,1} , obj_One_Two.subtract(ArithmeticUnitFast.ONE)) ;
        assertEquals(new long[] {2,3} , obj_One_Two.subtract(ArithmeticUnitFast.MINUS_ONE)) ;
        assertEquals(new long[] {-1,0} , obj_One_Two.subtract(ArithmeticUnitFast.TWO)) ;
        assertEquals(new long[] {-2,-1} , obj_One_Two.subtract(ArithmeticUnitFast.THREE)) ;

        assertEquals(new long[] {1,2} , obj_One_Two.multiply(ArithmeticUnitFast.ONE)) ;
        assertEquals(new long[] {-1,-2} , obj_One_Two.multiply(ArithmeticUnitFast.MINUS_ONE)) ;
        assertEquals(new long[] {2,4} , obj_One_Two.multiply(ArithmeticUnitFast.TWO)) ;
        assertEquals(new long[] {3,6} , obj_One_Two.multiply(ArithmeticUnitFast.THREE)) ;

        assertEquals(new long[] {1,2} , obj_One_Two.divide(ArithmeticUnitFast.ONE)) ;
        assertEquals(new long[] {-1,-2} , obj_One_Two.divide(ArithmeticUnitFast.MINUS_ONE)) ;
        assertEquals(new long[] {0,1}, new double[] {0.5,1}  , obj_One_Two.divide(ArithmeticUnitFast.TWO)) ;

        assertEquals(true, 3 , obj_One_Two.collectionSum()) ;
        assertEquals(true, 1, 1.5 , obj_One_Two.collectionAverage()) ;

    }

    @Test
    public void testCollection2() {

        MathObjectCollection<MathValueFast> obj_One_Two = new MathObjectCollection<>(au, MathValueFast.ONE, MathValueFast.TWO);
        MathObjectCollection<MathValueFast> obj_Two_Three = new MathObjectCollection<>(au, MathValueFast.TWO, MathValueFast.THREE);

        assertEquals(new long[] {2, 3, 3, 4} , obj_One_Two.sum(obj_One_Two)) ;
        assertEquals(new long[] {3, 4, 4, 5} , obj_One_Two.sum(obj_Two_Three)) ;

        assertEquals(new long[] {1, 2, 2, 4} , obj_One_Two.multiply(obj_One_Two)) ;
        assertEquals(new long[] {2, 3, 4, 6} , obj_One_Two.multiply(obj_Two_Three)) ;

    }

    @Test
    public void testCollection3() {

        MathObjectCollection<MathValueFast> obj_One_Two = new MathObjectCollection<>(au, MathValueFast.ONE, MathValueFast.TWO);
        MathObjectCollection<MathValueFast> obj_Two_Three = new MathObjectCollection<>(au, MathValueFast.TWO, MathValueFast.THREE);

        assertEquals(new long[] {3, 4} , ArithmeticUnitFast.TWO.sum(obj_One_Two)) ;
        assertEquals(new long[] {4, 5} , ArithmeticUnitFast.TWO.sum(obj_Two_Three)) ;

        assertEquals(new long[] {2, 4} , ArithmeticUnitFast.TWO.multiply(obj_One_Two)) ;
        assertEquals(new long[] {4, 6} , ArithmeticUnitFast.TWO.multiply(obj_Two_Three)) ;

        assertEquals(new long[] { (long)Math.log(2) }, new double[] { Math.log(2) } , ArithmeticUnitFast.TWO.log()) ;

        assertEquals(new long[] { (long)Math.log(2) , (long)Math.log(3) }, new double[] { Math.log(2) , Math.log(3) } , obj_Two_Three.log()) ;

    }

    @Test
    public void testCalcOutputSize() {

        MathObjectCollection<MathValueFast> obj_One_Two = new MathObjectCollection<>(au, MathValueFast.ONE, MathValueFast.TWO);
        MathObjectSingleton<MathValueFast> obj_Two = new MathObjectSingleton<>(au, MathValueFast.TWO);

        for (ArithmeticOperation op : ArithmeticOperation.values()) {
            if ( op.isSingleValue() ) continue;

            Assert.assertEquals(2 , obj_One_Two.calcOutputSize(op, obj_Two) );
            Assert.assertEquals(4 , obj_One_Two.calcOutputSize(op, obj_One_Two) );

            Assert.assertEquals(1 , obj_Two.calcOutputSize(op, obj_Two) );
            Assert.assertEquals(2 , obj_Two.calcOutputSize(op, obj_One_Two) );
        }

        for (ArithmeticOperation op : ArithmeticOperation.values()) {
            if ( !op.isSingleValue() || op.isCollection() ) continue;

            Assert.assertEquals(2 , obj_One_Two.calcOutputSize(op, null) );
            Assert.assertEquals(1 , obj_Two.calcOutputSize(op, null) );
        }

        for (ArithmeticOperation op : ArithmeticOperation.values()) {
            if ( !op.isCollection() ) continue;

            Assert.assertEquals(1 , obj_One_Two.calcOutputSize(op, null) );
            Assert.assertEquals(1 , obj_Two.calcOutputSize(op, null) );
        }

    }

    private void assertEquals(boolean singleton, long expcectedInt, MathObject<MathValueFast> val) {
        assertEquals(singleton, expcectedInt , expcectedInt, val) ;
    }

    private void assertEquals(boolean singleton, long expcectedInt, double expcectedDec, MathObject<MathValueFast> val) {
        Assert.assertEquals( singleton , val.isSingleton() );

        MathValueFast mathValue = val.getValue();

        Assert.assertEquals( expcectedInt , mathValue.getValueInteger() );
        Assert.assertEquals( expcectedDec , mathValue.getValueDecimal() , 0.0000001 );

        MathValueFast mathValueCheck ;

        if (mathValue.isInteger() ) {
            mathValueCheck = new MathValueFast.MathValueFastInteger(expcectedInt) ;
        }
        else {
            mathValueCheck = new MathValueFast.MathValueFastDecimal(expcectedDec) ;
        }

        Assert.assertEquals( new MathObjectSingleton( val.getArithmeticUnit(), mathValueCheck) , val );

    }

    private void assertEquals(long[] expectedInts, MathObject<MathValueFast> val) {
        double[]expcectedDecs = new double[expectedInts.length] ;

        for (int i = 0; i < expcectedDecs.length; i++) {
            expcectedDecs[i] = expectedInts[i] ;
        }

        assertEquals(expectedInts, expcectedDecs, val);
    }

    private void assertEquals(long[] expectedInts, double[] expectedDecs, MathObject<MathValueFast> val) {
        if (val.isSingleton()) {
            assertEquals(true , expectedInts[0], expectedDecs[0], val) ;
            return ;
        }

        MathObjectCollection<MathValueFast> valCol = (MathObjectCollection<MathValueFast>) val;

        MathValueFast[] values = valCol.getValues();

        Assert.assertEquals( expectedInts.length , expectedDecs.length );

        Assert.assertEquals( expectedInts.length , values.length );

        for (int i = 0; i < values.length; i++) {
            MathValueFast value = values[i];

            long expectedInt = expectedInts[i] ;
            double expectedDec = expectedDecs[i] ;

            Assert.assertEquals(expectedInt, value.getValueInteger() );
            Assert.assertEquals(expectedDec, value.getValueDecimal() , 0.0000001 );
        }

    }

}
