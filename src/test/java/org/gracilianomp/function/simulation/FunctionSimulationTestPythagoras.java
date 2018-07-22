package org.gracilianomp.function.simulation;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class FunctionSimulationTestPythagoras {

    static final private ArithmeticUnitFast au = ArithmeticUnitFast.instance;

    static final private int[][] PYTHAGOREAN_TRIPLES_GENERATED = generatePythagoreanTriples(1000, true) ;

    static final private int[][] PYTHAGOREAN_TRIPLES = PYTHAGOREAN_TRIPLES_GENERATED ;

    private static int[][] generatePythagoreanTriples(int limit, boolean onlyPrimes) {
        ArrayList<int[]> list = new ArrayList<>();

        for (int a = 2; a <= limit; a++) {
            for (int b = a+1; b <= limit; b++) {
                double c = Math.sqrt((a * a) + (b * b));
                int cInt = (int) c;

                if ( cInt*1d == c ) {
                    if (onlyPrimes) {
                        int minimalDivisor = PrimeUtils.minimalDivisor(a, b);
                        //System.out.println(">> "+ a +" + "+ b +" = "+ cInt +" > "+ minimalDivisor);

                        if (minimalDivisor == 0) {
                            list.add( new int[] {a,b,cInt} ) ;
                        }
                    }
                    else {
                        list.add( new int[] {a,b,cInt} ) ;
                    }

                }
            }
        }

        System.out.println("---------------------------------------------");
        System.out.println("PYTHAGOREAN_TRIPLES: "+ list.size());

        for (int[] abc : list) {
            System.out.println(Arrays.toString(abc));
        }

        System.out.println("---------------------------------------------");

        return list.toArray( new int[ list.size() ][] );
    }

    @Test
    public void test1() {

        MathStack<MathValueFast> globalStack = new MathStack<>(StackType.GLOBAL, true);
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(2)) );

        MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(3)) );
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(4)) );

        FunctionOperation[] operations = new FunctionOperation[] {
                //new FunctionOperation( ArithmeticOperation.POWER , new StackValue(StackType.INPUT, 0) , new StackValue(StackType.GLOBAL, 0) ) ,
                //new FunctionOperation( ArithmeticOperation.POWER , new StackValue(StackType.INPUT, 1) , new StackValue(StackType.GLOBAL, 0) ) ,
                //new FunctionOperation( ArithmeticOperation.SUM , new StackValue(StackType.RUNTIME, 0) , new StackValue(StackType.RUNTIME, 1) ) ,
        };

        double[] targets = calcTargetsRoot(inputStack) ;
        Assert.assertEquals( 5 , targets[0] , 0.000001 );

        MathObject targetObject = new MathObjectSingleton(au, new MathValueFast.MathValueFastDecimal(targets[0]));

        MathFunction<MathValueFast> mathFunction = new MathFunction<>(globalStack, inputStack, operations);

        FunctionSimulation<MathValueFast> functionSimulation = new FunctionSimulation<>(mathFunction, targetObject, 0.0, 4, 0.0);

        setExtraTargets(functionSimulation);

        MathFunction<MathValueFast> targetFunction = functionSimulation.findFunction();

        MathObject<MathValueFast> targetResult = targetFunction.getResult();

        System.out.println(targetFunction);

        System.out.println("TARGET:");
        System.out.println(targetObject);

        Assert.assertEquals( targetObject , targetResult );

    }

    private void setExtraTargets(FunctionSimulation<MathValueFast> functionSimulation) {
        for (int i = 0; i < PYTHAGOREAN_TRIPLES.length; i++) {
            int[] triple = PYTHAGOREAN_TRIPLES[i];

            MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
            inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(triple[0])) );
            inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(triple[1])) );

            MathObjectSingleton<MathValueFast> target = new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger(triple[2]));

            functionSimulation.addExtraTarget(inputStack, target);
        }
    }

    private double[] calcTargetsRoot(MathStack<MathValueFast> inputStack) {
        MathObject<MathValueFast> val1 = inputStack.get(0);
        MathObject<MathValueFast> vals2 = inputStack.get(1);

        long v1 = val1.getValue().getValueInteger();

        double[] results = new double[ vals2.size() ] ;
        int resultsSz = 0 ;

        MathValueFast[] values = vals2.getValues();

        for (int i = 0; i < values.length; i++) {
            MathValueFast val2a = values[i];
            long v2 = val2a.getValueInteger();
            results[resultsSz++] = Math.sqrt((v1 * v1) + (v2 * v2));
        }

        return results;
    }



}
