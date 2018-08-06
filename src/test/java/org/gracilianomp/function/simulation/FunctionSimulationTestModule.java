package org.gracilianomp.function.simulation;

import org.gracilianomp.arithmetic.ArithmeticOperation;
import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.arithmetic.MathObjectSingleton;
import org.gracilianomp.arithmetic.fast.ArithmeticUnitFast;
import org.gracilianomp.arithmetic.fast.MathValueFast;
import org.gracilianomp.function.*;
import org.junit.Assert;
import org.junit.Test;

public class FunctionSimulationTestModule {

    static final private ArithmeticUnitFast au = ArithmeticUnitFast.instance;

    @Test
    public void test1() {

        MathStack<MathValueFast> globalStack = new MathStack<>(StackType.GLOBAL, true);
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(2)) );

        MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(10)) );
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(15)) );

        MathObject targetObject = new MathObjectSingleton(au, new MathValueFast.MathValueFastInteger(5));

        FunctionOperation[] operations = new FunctionOperation[] {
        };

        long mainInput = inputStack.getValue(0, 0).getValueInteger();


        MathFunction<MathValueFast> mathFunction = new MathFunction<>(globalStack, inputStack, operations);

        FunctionSimulation<MathValueFast> functionSimulation = new FunctionSimulation<>(mathFunction, targetObject, 0.0, 4, 0.0);

        setExtraTargets(functionSimulation);

        MathFunction<MathValueFast> targetFunction = functionSimulation.findFunction();

        if ( targetFunction == null ) {
            targetFunction = functionSimulation.getBestFoundFunction() ;
        }

        MathObject<MathValueFast> targetResult = targetFunction.getResult();

        System.out.println(targetFunction);

        System.out.println("TARGET:");
        System.out.println(targetObject);

        Assert.assertEquals( targetObject , targetResult );

    }

    @Test
    public void test2A() {
        test2Impl( true, false, new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 0) , new StackValue(StackType.GLOBAL, 0) ) ) ;
    }

    @Test
    public void test2AErr() {
        test2Impl( false, true, new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 0) , new StackValue(StackType.GLOBAL, 0) ) ) ;
    }

    @Test
    public void test2B() {
        test2Impl( true, false, new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 1) , new StackValue(StackType.GLOBAL, 0) ) ) ;
    }

    @Test
    public void test2C() {
        test2Impl( true, true, new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 1) , new StackValue(StackType.GLOBAL, 0) ) ) ;
    }

    @Test
    public void test2D() {
        test2Impl( true, true,
                new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 1) , new StackValue(StackType.GLOBAL, 0) ) ,
                new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 2) , new StackValue(StackType.GLOBAL, 0) )
        ) ;
    }

    @Test
    public void test2E() {
        test2Impl( true, false, new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 3) , new StackValue(StackType.GLOBAL, 0) ) ) ;
    }

    private void test2Impl(boolean shouldFindFunction, boolean alwaysGenerateWithTemplateOperations, FunctionOperation... templateOperations ) {

        MathStack<MathValueFast> globalStack = new MathStack<>(StackType.GLOBAL, true);
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(2)) );

        MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(10)) );
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(15)) );

        MathObject targetObject = new MathObjectSingleton(au, new MathValueFast.MathValueFastInteger(5));

        FunctionOperation[] operations = new FunctionOperation[] {
        };

        long mainInput = inputStack.getValue(0, 0).getValueInteger();

        MathFunction<MathValueFast> mathFunction = new MathFunction<>(globalStack, inputStack, operations);

        FunctionSimulation<MathValueFast> functionSimulation = new FunctionSimulation<>(mathFunction, targetObject, 0.0, 3, 0.0);

        functionSimulation.setAlwaysGenerateWithTemplateOperations(alwaysGenerateWithTemplateOperations);
        functionSimulation.setTemplateOperations(templateOperations) ;

        setExtraTargets(functionSimulation);

        MathFunction<MathValueFast> targetFunction = functionSimulation.findFunction();

        boolean foundFunction = targetFunction != null;

        if ( targetFunction == null ) {
            targetFunction = functionSimulation.getBestFoundFunction() ;
        }

        MathObject<MathValueFast> targetResult = targetFunction.getResult();

        System.out.println(targetFunction);

        System.out.println("TARGET:");
        System.out.println(targetObject);

        Assert.assertEquals( targetObject , targetResult );

        if (shouldFindFunction) {
            Assert.assertTrue(foundFunction);
        }
        else {
            Assert.assertFalse(foundFunction);
        }

    }

    static private int diff(int a, int b) {
        int d1 = diff1(a, b);
        int d2 = diff2(a, b);

        if ( d1 != d2 ) throw new IllegalStateException();

        return d1 ;
    }

    static private int diff1(int a, int b) {
        int d = a-b ;
        if (d < 0) d = -d ;
        return d;
    }

    static private int diff2(int a, int b) {
        int d = a-b ;
        int e = d*d ;
        int f = (int) Math.sqrt(e);
        return f;
    }

    private void setExtraTargets(FunctionSimulation<MathValueFast> functionSimulation) {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                {
                    MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
                    inputStack.add(new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger(i)));
                    inputStack.add(new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger(j)));
                    MathObjectSingleton<MathValueFast> target = new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger( diff(i,j) ));
                    functionSimulation.addExtraTarget(inputStack, target);
                }
                {
                    MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
                    inputStack.add(new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger(j)));
                    inputStack.add(new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger(i)));
                    MathObjectSingleton<MathValueFast> target = new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger( diff(j,i) ));
                    functionSimulation.addExtraTarget(inputStack, target);
                }
            }
        }
    }


}
