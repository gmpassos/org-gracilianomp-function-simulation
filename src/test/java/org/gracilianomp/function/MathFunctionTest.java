package org.gracilianomp.function;

import org.gracilianomp.arithmetic.ArithmeticOperation;
import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.arithmetic.MathObjectCollection;
import org.gracilianomp.arithmetic.MathObjectSingleton;
import org.gracilianomp.arithmetic.fast.ArithmeticUnitFast;
import org.gracilianomp.arithmetic.fast.MathValueFast;
import org.junit.Assert;
import org.junit.Test;

public class MathFunctionTest {

    static final private ArithmeticUnitFast au = ArithmeticUnitFast.instance;

    @Test
    public void test1() {

        MathStack<MathValueFast> globalStack = new MathStack<>(StackType.GLOBAL, true);
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(2)) );

        MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(3)) );
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(4)) );

        FunctionOperation[] operations = new FunctionOperation[] {
            new FunctionOperation( ArithmeticOperation.MULTIPLY , new StackValue(StackType.INPUT, 0) , new StackValue(StackType.INPUT, 0) ) ,
            new FunctionOperation( ArithmeticOperation.MULTIPLY , new StackValue(StackType.INPUT, 1) , new StackValue(StackType.INPUT, 1) ) ,
            new FunctionOperation( ArithmeticOperation.SUM , new StackValue(StackType.RUNTIME, 0) , new StackValue(StackType.RUNTIME, 1) ) ,
            new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 2) , new StackValue(StackType.GLOBAL, 0) ) ,
        };

        MathFunction<MathValueFast> mathFunction = new MathFunction<>(globalStack, inputStack, operations);

        MathObject<MathValueFast> result = mathFunction.execute();

        Assert.assertEquals( 5L , result.getValue().getValueInteger() );

    }

    @Test
    public void test2() {

        MathStack<MathValueFast> globalStack = new MathStack<>(StackType.GLOBAL, true);
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(2)) );

        MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
        inputStack.add( new MathObjectCollection<>( au , new MathValueFast.MathValueFastInteger(3), new MathValueFast.MathValueFastInteger(6)) );
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(4)) );

        FunctionOperation[] operations = new FunctionOperation[] {
                new FunctionOperation( ArithmeticOperation.MULTIPLY , new StackValue(StackType.INPUT, 0) , new StackValue(StackType.INPUT, 0) ) ,
                new FunctionOperation( ArithmeticOperation.MULTIPLY , new StackValue(StackType.INPUT, 1) , new StackValue(StackType.INPUT, 1) ) ,
                new FunctionOperation( ArithmeticOperation.SUM , new StackValue(StackType.RUNTIME, 0) , new StackValue(StackType.RUNTIME, 1) ) ,
                new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 2) , new StackValue(StackType.GLOBAL, 0) ) ,
        };

        MathFunction<MathValueFast> mathFunction = new MathFunction<>(globalStack, inputStack, operations);

        MathObjectCollection<MathValueFast> result = (MathObjectCollection<MathValueFast>) mathFunction.execute();

        System.out.println(mathFunction);

        Assert.assertArrayEquals( new double[] {5.0, 5.830951894845301, 5.830951894845301, 7.211102550927978} , MathValueFast.getValuesDecimals(result.getValues()) , 0.00001);

    }

    @Test
    public void test3() {

        MathStack<MathValueFast> globalStack = new MathStack<>(StackType.GLOBAL, true);
        globalStack.add( new MathObjectCollection<>( au , new MathValueFast.MathValueFastInteger(2), new MathValueFast.MathValueFastInteger(3)) );

        MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(3)) );
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(4)) );

        FunctionOperation[] operations = new FunctionOperation[] {
                new FunctionOperation( ArithmeticOperation.POWER , new StackValue(StackType.INPUT, 0) , new StackValue(StackType.GLOBAL, 0) ) ,
                new FunctionOperation( ArithmeticOperation.POWER , new StackValue(StackType.INPUT, 1) , new StackValue(StackType.GLOBAL, 0) ) ,
                new FunctionOperation( ArithmeticOperation.SUM , new StackValue(StackType.RUNTIME, 0) , new StackValue(StackType.RUNTIME, 1) ) ,
                new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 2) , new StackValue(StackType.GLOBAL, 0) ) ,
        };

        MathFunction<MathValueFast> mathFunction = new MathFunction<>(globalStack, inputStack, operations);

        MathObjectCollection<MathValueFast> result = (MathObjectCollection<MathValueFast>) mathFunction.execute();

        System.out.println(mathFunction);

        Assert.assertArrayEquals( new double[] {5.0, 2.924017738212866, 8.54400374531753, 4.179339196381232, 6.557438524302, 3.5033980603867243, 9.539392014169456, 4.497941445275415} , MathValueFast.getValuesDecimals(result.getValues()) , 0.00001);

    }

}
