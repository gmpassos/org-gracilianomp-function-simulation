package org.gracilianomp.function.simulation;

import org.gracilianomp.arithmetic.ArithmeticOperation;
import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.arithmetic.MathObjectSingleton;
import org.gracilianomp.arithmetic.fast.ArithmeticUnitFast;
import org.gracilianomp.arithmetic.fast.MathValueFast;
import org.gracilianomp.function.*;
import org.gracilianomp.utils.PrimeUtils;
import org.gracilianomp.utils.Statistics;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

@Ignore
public class FunctionSimulationTestPrimes {

    static final private double EXP_2 = Math.exp(2);

    static final private ArithmeticUnitFast au = ArithmeticUnitFast.instance;

    static final private int[][] PRIMES_COUNT_GENERATED = generatePrimesCount(100000) ;
    static final private boolean GENERATE_NON_PRIMES = false ;

    static final private int[][] PRIMES_COUNT = PRIMES_COUNT_GENERATED;

    private static int[][] generatePrimesCount(int limit) {
        ArrayList<int[]> list = new ArrayList<>();

        int prevCount = 0 ;
        int prevP = 2 ;

        int primesCount = 0 ;
        for (int p = 2; p < limit; p = PrimeUtils.nextPrime(p)) {

            if (GENERATE_NON_PRIMES) {
                for (int n = prevP + 1; n < p; n++) {
                    list.add(new int[]{n, prevCount});
                }
            }

            primesCount++ ;

            int count = primesCount ;//PrimeUtils.countPrimesInRange(2,p) ;

            list.add( new int[] { p , count } ) ;

            prevCount = count ;
            prevP = p ;
        }

        System.out.println("---------------------------------------------");
        System.out.println("PRIMES_COUNT: "+ list.size());

        int[][] allGMPErrors = new int[6][list.size()] ;

        for (int i = 0; i < list.size(); i++) {
            int[] ab = list.get(i) ;

            int n = ab[0];
            int c = ab[1];

            int c1 = calcPrimesCountClassic(n);

            double[] cGMP = new double[] {
                    calcPrimesCountGMP1(n),
                    calcPrimesCountGMP3(n),
                    calcPrimesCountGMP4(n),
                    calcPrimesCountGMP5(n),
                    calcPrimesCountGMP6(n),
                    calcPrimesCountGMP7(n),
                    //calcPrimesCountGMPFix(n),
            } ;

            int err1 = c1 - c ;

            int[] errGMP = new int[cGMP.length];

            for (int j = 0; j < errGMP.length; j++) {
                int err = ((int) cGMP[j]) - c;
                errGMP[j] = err;

                allGMPErrors[j][i] = err ;
            }

            if ( PrimeUtils.isPrime(n) ) {
                System.out.println(Arrays.toString(ab) + " > classic: " + c1 + " ; gmp: " + Arrays.toString(toInts(cGMP)) + " > err: " + err1 + " ; errGMP: " + Arrays.toString(errGMP));
            }
        }

        System.out.println("---------------------------------------------");

        calcPrimesCountGMP4(887);

        for (int i = 0; i < allGMPErrors.length; i++) {
            int[] allGMPError = allGMPErrors[i];
            int min = Statistics.min(allGMPError);
            int max = Statistics.max(allGMPError);
            double mean = Statistics.mean(allGMPError);
            System.out.println("allGMPErrors["+i+"]: "+ mean +" ; "+ min +" .. "+ max +" = "+ (max-min)) ;

        }

        //System.exit(0);

        return list.toArray( new int[ list.size() ][] );
    }

    static private int[] toInts(double[] ds) {
        int[] ns = new int[ds.length];
        for (int i = 0; i < ns.length; i++) {
            ns[i] = (int) ds[i];
        }
        return ns ;
    }

    private static int calcPrimesCountClassic(int n) {
        return (int) (n / Math.log(n));
    }


    private static double calcPrimesCountGMPFix(int n) {
        double c1 = calcPrimesCountGMP1(n);
        double c2 = calcPrimesCountGMP3(n);
        double c3 = calcPrimesCountGMP4(n);

        double cMin = Math.min(c2,c3) ;
        double cMax = Math.max(c2,c3) ;

        double cFix = ((cMax-2)+(cMin+2))/2;

        return cFix ;
    }

    /*
a [0] {LOG, (INPUT, 0)}
b [1] {DIVIDE, (INPUT, 0), (RUNTIME, 0)}
  [2] {EXP, (GLOBAL, 0)}
c [3] {DIVIDE, (RUNTIME, 1), (RUNTIME, 2)}
d [4] {SUM, (RUNTIME, 1), (RUNTIME, 3)}
     */

    private static double calcPrimesCountGMP1(int n) {
        double a = n / Math.log(n) ;
        double b = a / EXP_2 ;
        double c = a + b;
        return c;
    }

    /*
Operations:
a [0] {LOG, (INPUT, 0)}
b [1] {SUM, (RUNTIME, 0), (RUNTIME, 0)}
c [2] {ROOT, (RUNTIME, 0), (RUNTIME, 1)}
d [3] {MULTIPLY, (INPUT, 0), (RUNTIME, 2)}
e [4] {DIVIDE, (RUNTIME, 3), (RUNTIME, 0)}
    */

    private static int calcPrimesCountGMP2(int n) {
        double a = Math.log(n) ;
        double b = a * 2 ;
        double c = Math.pow(a, 1/b) ;
        double d = a * c ;
        double e = d / a ;
        return (int) e;
    }

    /*
a [0] {LOG, (INPUT, 0)}
b [1] {ROOT, (RUNTIME, 0), (GLOBAL, 0)}
c [2] {ROOT, (RUNTIME, 1), (RUNTIME, 0)}
d [3] {MULTIPLY, (INPUT, 0), (RUNTIME, 2)}
e [4] {DIVIDE, (RUNTIME, 3), (RUNTIME, 0)}
    */

    private static double calcPrimesCountGMP3(int n) {
        double a = Math.log(n) ;
        double b = Math.sqrt(a) ;
        double c = Math.pow(b, 1/a) ;
        double d = n * c ;
        double e = d / a ;
        return e;
    }

    /*
Operations:
a [0] {LOG, (INPUT, 0)}
b [1] {ROOT, (RUNTIME, 0), (GLOBAL, 0)}
c [2] {ROOT, (RUNTIME, 1), (RUNTIME, 0)}
d [3] {DIVIDE, (INPUT, 0), (RUNTIME, 0)}
e [4] {MULTIPLY, (RUNTIME, 2), (RUNTIME, 3)}
f [5] {SUM, (RUNTIME, 1), (RUNTIME, 4)}
     */

    private static double calcPrimesCountGMP4(int n) {
        double a = Math.log(n) ;
        double b = Math.sqrt(a) ;
        double c = Math.pow(b, 1/a) ;
        double d = n / a ;
        double e = c * d ;
        double f = b + e ;
        return f;
    }

    /*
a [0] {LOG, (INPUT, 0)}
b [1] {POWER, (INPUT, 0), (RUNTIME, 0)}
c [2] {MULTIPLY, (RUNTIME, 1), (GLOBAL, 4)}
d [3] {ROOT, (RUNTIME, 2), (RUNTIME, 0)}
e [4] {DIVIDE, (RUNTIME, 3), (RUNTIME, 0)}
     */

    private static double calcPrimesCountGMP5(int n) {
        double a = Math.log(n) ;
        double b = Math.pow(n, a) ;
        double c = b * 3 ;
        double d = Math.pow(c, 1/a) ;
        double e = d/a ;
        return e+3;
    }

    /*
a [0] {LOG, (INPUT, 0)}
b [1] {POWER, (INPUT, 0), (RUNTIME, 0)}
c [2] {MULTIPLY, (RUNTIME, 1), (GLOBAL, 4)}
d [3] {ROOT, (RUNTIME, 2), (RUNTIME, 0)}
e [4] {DIVIDE, (RUNTIME, 3), (RUNTIME, 0)}
f [5] {SUM, (RUNTIME, 0), (RUNTIME, 4)}
     */

    private static double calcPrimesCountGMP6(int n) {
        double a = Math.log(n) ;
        double b = Math.pow(n, a) ;
        double c = b * 3 ;
        double d = Math.pow(c, 1/a) ;
        double e = d/a ;
        double f = a+e ;
        return f;
    }

    /*
a [0] {LOG, (INPUT, 0)}
b [1] {ROOT, (RUNTIME, 0), (GLOBAL, 1)}
c [2] {POWER, (INPUT, 0), (RUNTIME, 0)}
d [3] {ROOT, (GLOBAL, 3), (RUNTIME, 1)}
e [4] {SUBTRACT, (RUNTIME, 0), (RUNTIME, 3)}
f [5] {DIVIDE, (INPUT, 0), (RUNTIME, 4)}
     */

    private static double calcPrimesCountGMP7(int n) {
        double a = Math.log(n) ;
        double b = Math.sqrt(a) ;
        //double c = Math.pow(n, a) ;
        double d = Math.pow(Math.E/2, 1/b) ;
        double e = a-d ;
        double f = n/e ;
        return f;
    }


    @Test
    public void test1() {

        MathStack<MathValueFast> globalStack = new MathStack<>(StackType.GLOBAL, true);
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(1)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(2)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(5)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(3)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.E/2)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.PI)) );
        //globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.PI)) );
        //globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.PI/2)) );
        //globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.E)) );


        MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(887)) );

        FunctionOperation[] operations = new FunctionOperation[] {
                new FunctionOperation( ArithmeticOperation.LOG , new StackValue(StackType.INPUT, 0) , null ) ,
                //new FunctionOperation( ArithmeticOperation.DIVIDE , new StackValue(StackType.INPUT, 0) , new StackValue(StackType.RUNTIME, 0) ) ,

                //new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 0) , new StackValue(StackType.GLOBAL, 1) ) ,
                //new FunctionOperation( ArithmeticOperation.COS , new StackValue(StackType.INPUT, 0) , null ) ,
                //new FunctionOperation( ArithmeticOperation.POWER , new StackValue(StackType.INPUT, 0) , new StackValue(StackType.RUNTIME, 0) ) ,
                //new FunctionOperation( ArithmeticOperation.MULTIPLY , new StackValue(StackType.RUNTIME, 1) , new StackValue(StackType.GLOBAL, 4) ) ,

                //new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 0) , new StackValue(StackType.GLOBAL, 0) ) ,
                //new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 1) , new StackValue(StackType.RUNTIME, 0) ) ,

                //new FunctionOperation( ArithmeticOperation.DIVIDE , new StackValue(StackType.INPUT, 0) , new StackValue(StackType.GLOBAL, 0) ) ,
                //new FunctionOperation( ArithmeticOperation.SUBTRACT , new StackValue(StackType.GLOBAL, 1) , new StackValue(StackType.GLOBAL, 2) ) ,
        };

        long mainInput = inputStack.getValue(0, 0).getValueInteger();

        int target = PrimeUtils.countPrimesInRange(2, (int) mainInput) ;
        MathObject targetObject = new MathObjectSingleton(au, new MathValueFast.MathValueFastInteger(target));

        MathFunction<MathValueFast> mathFunction = new MathFunction<>(globalStack, inputStack, operations);

        FunctionSimulation<MathValueFast> functionSimulation = new FunctionSimulation<>(mathFunction, targetObject, 0.0, 6, 0.0);

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

    private void setExtraTargets(FunctionSimulation<MathValueFast> functionSimulation) {
        for (int i = 0; i < PRIMES_COUNT_GENERATED.length; i++) {
            int[] pattern = PRIMES_COUNT_GENERATED[i];

            MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
            inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(pattern[0])) );

            MathObjectSingleton<MathValueFast> target = new MathObjectSingleton<>(au, new MathValueFast.MathValueFastInteger(pattern[1]));

            functionSimulation.addExtraTarget(inputStack, target);
        }
    }


}
