package org.gracilianomp.function.simulation;

import org.gracilianomp.arithmetic.ArithmeticOperation;
import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.arithmetic.MathObjectSingleton;
import org.gracilianomp.arithmetic.fast.ArithmeticUnitFast;
import org.gracilianomp.arithmetic.fast.MathValueFast;
import org.gracilianomp.function.FunctionOperation;
import org.gracilianomp.function.MathFunction;
import org.gracilianomp.function.MathStack;
import org.gracilianomp.function.StackType;
import org.gracilianomp.utils.PrimeUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class FunctionSimulationTestSqrtInt {

    static final private ArithmeticUnitFast au = ArithmeticUnitFast.instance;

    @Test
    public void test1() {

        MathStack<MathValueFast> globalStack = new MathStack<>(StackType.GLOBAL, true);

        for (int p = 2; p <= 100; p += PrimeUtils.nextPrime(p)) {
            globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(p)) );
        }

        MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(25)) );

        MathObject targetObject = new MathObjectSingleton(au, new MathValueFast.MathValueFastInteger(5));

        FunctionOperation[] operations = new FunctionOperation[] {
        };

        long mainInput = inputStack.getValue(0, 0).getValueInteger();


        MathFunction<MathValueFast> mathFunction = new MathFunction<>(globalStack, inputStack, operations);

        FunctionSimulation<MathValueFast> functionSimulation = new FunctionSimulation<>(mathFunction, targetObject, 0.0, 4, 0.0);

        functionSimulation.disableOperation(ArithmeticOperation.ROOT);
        functionSimulation.disableOperation(ArithmeticOperation.POWER);
        functionSimulation.disableOperation(ArithmeticOperation.LOG);
        functionSimulation.disableOperation(ArithmeticOperation.EXP);
        functionSimulation.disableOperation(ArithmeticOperation.COS);
        functionSimulation.disableOperation(ArithmeticOperation.COLLECTION_AVERAGE);
        functionSimulation.disableOperation(ArithmeticOperation.COLLECTION_SUM);

        setExtraTargets(functionSimulation);

        MathFunction<MathValueFast> targetFunction = functionSimulation.findFunction();

        if ( targetFunction == null ) {
            targetFunction = functionSimulation.getBestFoundFunction() ;
        }

        MathObject<MathValueFast> targetResult = targetFunction.getResult();

        System.out.println(targetFunction);

        System.out.println("TARGET:");
        System.out.println(targetObject);

        System.out.println("MAX DISTANCE:");
        System.out.println( functionSimulation.getBestFoundFunctionDistance() );

        Assert.assertEquals( targetObject , targetResult );

    }

    static private int sqrtInt(int a) {
        int s = (int) Math.sqrt(a);
        return s ;
    }

    private void setExtraTargets(FunctionSimulation<MathValueFast> functionSimulation) {
        for (int i = 100; i < 100000; i++) {
            MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
            inputStack.add(new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger(i)));
            MathObjectSingleton<MathValueFast> target = new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger( sqrtInt(i) ));
            functionSimulation.addExtraTarget(inputStack, target);
        }
    }


}
