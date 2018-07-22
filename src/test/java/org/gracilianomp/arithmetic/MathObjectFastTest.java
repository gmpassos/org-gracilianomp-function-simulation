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

    private void assertEquals(long[] expcectedInts, MathObject<MathValueFast> val) {
        double[]expcectedDecs = new double[expcectedInts.length] ;

        for (int i = 0; i < expcectedDecs.length; i++) {
            expcectedDecs[i] = expcectedInts[i] ;
        }

        assertEquals(expcectedInts, expcectedDecs, val);
    }

    private void assertEquals(long[] expcectedInts, double[] expcectedDecs, MathObject<MathValueFast> val) {
        if (val.isSingleton()) {
            assertEquals(true , expcectedInts[0], expcectedDecs[0], val) ;
            return ;
        }

        MathObjectCollection<MathValueFast> valCol = (MathObjectCollection<MathValueFast>) val;

        MathValueFast[] values = valCol.getValues();

        Assert.assertEquals( expcectedInts.length , expcectedDecs.length );

        Assert.assertEquals( expcectedInts.length , values.length );

        for (int i = 0; i < values.length; i++) {
            MathValueFast value = values[i];

            long expcectedInt = expcectedInts[i] ;
            double expcectedDec = expcectedDecs[i] ;

            Assert.assertEquals( expcectedInt , value.getValueInteger() );
            Assert.assertEquals( expcectedDec , value.getValueDecimal() , 0.0000001 );
        }

    }

}
