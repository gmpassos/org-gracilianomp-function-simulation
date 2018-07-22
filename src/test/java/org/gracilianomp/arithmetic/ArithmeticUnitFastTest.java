package org.gracilianomp.arithmetic;

import org.gracilianomp.arithmetic.fast.ArithmeticUnitFast;
import org.gracilianomp.arithmetic.fast.MathValueFast;
import org.junit.Assert;
import org.junit.Test;

public class ArithmeticUnitFastTest {

    static final private ArithmeticUnitFast au = ArithmeticUnitFast.instance;

    @Test
    public void test1() {

        assertEquals(2, au.sum(MathValueFast.ONE, MathValueFast.ONE));
        assertEquals(0, au.subtract(MathValueFast.ONE, MathValueFast.ONE));
        assertEquals(1, au.multiply(MathValueFast.ONE, MathValueFast.ONE));
        assertEquals(1, au.divide(MathValueFast.ONE, MathValueFast.ONE));

        assertEquals(1, au.sum(MathValueFast.ONE, MathValueFast.ZERO));
        assertEquals(1, au.sum(MathValueFast.ZERO, MathValueFast.ONE));

        assertEquals(1, au.subtract(MathValueFast.ONE, MathValueFast.ZERO));
        assertEquals(-1, au.subtract(MathValueFast.ZERO, MathValueFast.ONE));

        assertEquals(0, au.multiply(MathValueFast.ONE, MathValueFast.ZERO));
        assertEquals(0, au.multiply(MathValueFast.ZERO, MathValueFast.ONE));

        assertEquals(0, au.divide(MathValueFast.ZERO, MathValueFast.ONE));

        assertEquals(0, au.power(MathValueFast.ZERO, MathValueFast.ONE));
        assertEquals(1, au.power(MathValueFast.ONE, MathValueFast.ZERO));

        assertEquals(1, au.root(MathValueFast.ONE, MathValueFast.ONE));
        assertEquals(0, au.root(MathValueFast.ZERO, MathValueFast.ONE));

        assertEquals(Math.log(0), au.log(MathValueFast.ZERO));
        assertEquals(Math.log(1), au.log(MathValueFast.ONE));
        assertEquals(Math.log(2), au.log(MathValueFast.TWO));
        assertEquals(Math.log(3), au.log(MathValueFast.THREE));

        assertEquals(Math.exp(0), au.exp(MathValueFast.ZERO));
        assertEquals(Math.exp(1), au.exp(MathValueFast.ONE));
        assertEquals(Math.exp(-1), au.exp(MathValueFast.MINUS_ONE));
        assertEquals(Math.exp(2), au.exp(MathValueFast.TWO));
        assertEquals(Math.exp(3), au.exp(MathValueFast.THREE));

    }

    @Test
    public void test2() {

        assertEquals(4, au.sum(MathValueFast.TWO, MathValueFast.TWO));
        assertEquals(3, au.sum(MathValueFast.TWO, MathValueFast.ONE));
        assertEquals(3, au.sum(MathValueFast.ONE, MathValueFast.TWO));
        assertEquals(2, au.sum(MathValueFast.TWO, MathValueFast.ZERO));
        assertEquals(2, au.sum(MathValueFast.ZERO, MathValueFast.TWO));

        assertEquals(0, au.subtract(MathValueFast.TWO, MathValueFast.TWO));
        assertEquals(1, au.subtract(MathValueFast.TWO, MathValueFast.ONE));
        assertEquals(-1, au.subtract(MathValueFast.ONE, MathValueFast.TWO));

        assertEquals(4, au.multiply(MathValueFast.TWO, MathValueFast.TWO));
        assertEquals(2, au.multiply(MathValueFast.TWO, MathValueFast.ONE));
        assertEquals(2, au.multiply(MathValueFast.ONE, MathValueFast.TWO));
        assertEquals(0, au.multiply(MathValueFast.TWO, MathValueFast.ZERO));
        assertEquals(0, au.multiply(MathValueFast.ZERO, MathValueFast.TWO));

        assertEquals(-2, au.multiply(MathValueFast.TWO, MathValueFast.MINUS_ONE));
        assertEquals(-1, au.multiply(MathValueFast.ONE, MathValueFast.MINUS_ONE));
        assertEquals(1, au.multiply(MathValueFast.MINUS_ONE, MathValueFast.MINUS_ONE));
        assertEquals(0, au.multiply(MathValueFast.ZERO, MathValueFast.MINUS_ONE));

        assertEquals(1, au.divide(MathValueFast.TWO, MathValueFast.TWO));
        assertEquals(2, au.divide(MathValueFast.TWO, MathValueFast.ONE));
        assertEquals(0, 0.5, au.divide(MathValueFast.ONE, MathValueFast.TWO));
        assertEquals(0, au.divide(MathValueFast.ZERO, MathValueFast.TWO));

        assertEquals(4, au.power(MathValueFast.TWO, MathValueFast.TWO));
        assertEquals(2, au.power(MathValueFast.TWO, MathValueFast.ONE));
        assertEquals(1, au.power(MathValueFast.ONE, MathValueFast.TWO));
        assertEquals(1, au.power(MathValueFast.TWO, MathValueFast.ZERO));
        assertEquals(0, au.power(MathValueFast.ZERO, MathValueFast.TWO));

        assertEquals(2, au.root(MathValueFast.FOUR, MathValueFast.TWO));
        assertEquals(4, au.root(MathValueFast.FOUR, MathValueFast.ONE));

    }

    @Test
    public void test3() {

        assertEquals(6, au.sum(MathValueFast.THREE, MathValueFast.THREE));
        assertEquals(5, au.sum(MathValueFast.THREE, MathValueFast.TWO));
        assertEquals(4, au.sum(MathValueFast.THREE, MathValueFast.ONE));
        assertEquals(5, au.sum(MathValueFast.TWO, MathValueFast.THREE));
        assertEquals(4, au.sum(MathValueFast.ONE, MathValueFast.THREE));
        assertEquals(3, au.sum(MathValueFast.THREE, MathValueFast.ZERO));
        assertEquals(3, au.sum(MathValueFast.ZERO, MathValueFast.THREE));

        assertEquals(0, au.subtract(MathValueFast.THREE, MathValueFast.THREE));
        assertEquals(1, au.subtract(MathValueFast.THREE, MathValueFast.TWO));
        assertEquals(2, au.subtract(MathValueFast.THREE, MathValueFast.ONE));
        assertEquals(-1, au.subtract(MathValueFast.TWO, MathValueFast.THREE));
        assertEquals(-2, au.subtract(MathValueFast.ONE, MathValueFast.THREE));

        assertEquals(9, au.multiply(MathValueFast.THREE, MathValueFast.THREE));
        assertEquals(6, au.multiply(MathValueFast.THREE, MathValueFast.TWO));
        assertEquals(3, au.multiply(MathValueFast.THREE, MathValueFast.ONE));
        assertEquals(6, au.multiply(MathValueFast.TWO, MathValueFast.THREE));
        assertEquals(3, au.multiply(MathValueFast.ONE, MathValueFast.THREE));
        assertEquals(0, au.multiply(MathValueFast.THREE, MathValueFast.ZERO));
        assertEquals(0, au.multiply(MathValueFast.ZERO, MathValueFast.THREE));

        assertEquals(1, au.divide(MathValueFast.THREE, MathValueFast.THREE));
        assertEquals(1, 1.5, au.divide(MathValueFast.THREE, MathValueFast.TWO));
        assertEquals(3, au.divide(MathValueFast.THREE, MathValueFast.ONE));
        assertEquals(0, 0.6666666666666666, au.divide(MathValueFast.TWO, MathValueFast.THREE));
        assertEquals(0, 0.3333333333333333, au.divide(MathValueFast.ONE, MathValueFast.THREE));
        assertEquals(0, au.divide(MathValueFast.ZERO, MathValueFast.THREE));

    }

    @Test(expected = java.lang.ArithmeticException.class)
    public void test1Error1() {
        au.divide(MathValueFast.ONE, MathValueFast.ZERO);
    }

    @Test(expected = java.lang.ArithmeticException.class)
    public void test2Error1() {
        au.divide(MathValueFast.TWO, MathValueFast.ZERO);
    }

    @Test(expected = java.lang.ArithmeticException.class)
    public void test3Error1() {
        au.divide(MathValueFast.THREE, MathValueFast.ZERO);
    }

    @Test(expected = java.lang.ArithmeticException.class)
    public void test4Error1() {
        au.log(MathValueFast.MINUS_ONE);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void assertEquals(long expcectedInt, MathValueFast val) {
        assertEquals(expcectedInt , expcectedInt, val) ;
    }

    private void assertEquals(double expcectedDec, MathValueFast val) {
        assertEquals( (long)expcectedDec, expcectedDec, val );
    }

    private void assertEquals(long expcectedInt, double expcectedDec, MathValueFast val) {
        Assert.assertEquals( expcectedInt , val.getValueInteger() );
        Assert.assertEquals( expcectedDec , val.getValueDecimal() , 0.0000001 );

        if (val.isInteger() ) {
            Assert.assertEquals( new MathValueFast.MathValueFastInteger(expcectedInt) , val );
        }
        else {
            Assert.assertEquals( new MathValueFast.MathValueFastDecimal(expcectedDec) , val );
        }

    }

}
