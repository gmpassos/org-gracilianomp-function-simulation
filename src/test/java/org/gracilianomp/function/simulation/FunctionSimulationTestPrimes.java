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
import java.util.Random;

@Ignore
public class FunctionSimulationTestPrimes {

    static final private double EXP_2 = Math.exp(2);

    static final private ArithmeticUnitFast au = ArithmeticUnitFast.instance;

    static final private int[][] PRIMES_COUNT_GENERATED = generatePrimesCount(100000000, 0.80) ;
    static final private boolean GENERATE_NON_PRIMES = false ;

    static final private int[][] PRIMES_COUNT = PRIMES_COUNT_GENERATED;

    private static int[][] generatePrimesCount(int limit, double skipRatio) {

        PrimeUtils.expandBasicPrimes( limit ) ;

        System.out.println("-- Generating primes count samples...");

        ArrayList<int[]> list = new ArrayList<>();

        int prevCount = 0 ;
        int prevP = 2 ;

        int primesCount = 0 ;

        Random random = new Random(123);

        int skipCount = 0 ;

        for (int p = 2; p < limit; p = PrimeUtils.nextPrime(p)) {

            if (GENERATE_NON_PRIMES) {
                for (int n = prevP + 1; n < p; n++) {
                    if ( p < 100 || random.nextFloat() > skipRatio ) {
                        list.add(new int[]{n, prevCount});
                    }
                    else {
                        skipCount++;
                    }
                }
            }

            primesCount++ ;

            int count = primesCount ;

            if ( p < 100 || random.nextFloat() > skipRatio ) {
                list.add(new int[]{p, count});
            }
            else {
                skipCount++;
            }

            prevCount = count ;
            prevP = p ;
        }

        System.out.println("---------------------------------------------");

        int totalSize = list.size() + skipCount ;

        System.out.println("PRIMES_COUNT: size: "+ list.size() +"/"+ totalSize +" ; skipCount: "+ skipCount +" ; "+ ( (skipCount*1d) / totalSize ));

        int[][] allGMPErrors = new int[9][list.size()] ;

        int printCount = 0 ;

        for (int i = 0; i < list.size(); i++) {
            int[] ab = list.get(i) ;

            int n = ab[0];
            int c = ab[1];

            int c1 = calcPrimesCountClassic(n);

            double[] cGMP = new double[] {
                    calcPrimesCountClassic(n),
                    calcPrimesCountGMP1(n),
                    calcPrimesCountGMP3(n),
                    calcPrimesCountGMP4(n),
                    calcPrimesCountGMP5(n),
                    calcPrimesCountGMP6(n),
                    calcPrimesCountGMP7(n),
                    calcPrimesCountGMP8(n),
                    calcPrimesCountGMP10(n),
                    //calcPrimesCountGMPFix(n),
            } ;

            int err1 = c1 - c ;

            int[] errGMP = new int[cGMP.length];

            for (int j = 0; j < errGMP.length; j++) {
                int err = ((int) cGMP[j]) - c;
                errGMP[j] = err;

                allGMPErrors[j][i] = err ;
            }

            if ( printCount < 1000 && PrimeUtils.isPrime(n) ) {
                printCount++ ;
                System.out.println(Arrays.toString(ab) + " > classic: " + c1 + " ; gmp: " + Arrays.toString(toInts(cGMP)) + " > err: " + err1 + " ; errGMP: " + Arrays.toString(errGMP));
            }
        }

        System.out.println("---------------------------------------------");

        System.out.println("PRIMES_COUNT: size: "+ list.size() +"/"+ totalSize +" ; skipCount: "+ skipCount +" ; "+ (1-((skipCount*1d)/totalSize)) );

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


    private static double calcPrimesCountGMP8(int n) {
        double a = Math.log(n) ;
        double b = Math.pow( Math.PI , 1/a) ;
        double c = a-b ;
        double f = n/c ;
        return f;
    }


    /*
--[1000] waitQueueFullyConsumed[simulationThreads: 16]: eval: 102,464 ; gen: 120,324 = [0, 0, 2355, 103404, 13671, 894] > bestFunctionDistance: 696.0233919275925

Operations:
a [0] {LOG, (INPUT#0: 887)}
b [1] {LOG, (RUNTIME#0: 6.787844982309579)}
c [2] {ROOT, (RUNTIME#1: 1.9151335100914084), (RUNTIME#0: 6.787844982309579)}
d [3] {POWER, (RUNTIME#2: 1.1004597815423836), (RUNTIME#2: 1.1004597815423836)}
e [4] {SUBTRACT, (RUNTIME#0: 6.787844982309579), (RUNTIME#3: 1.1110937574776176)}
f [5] {DIVIDE, (INPUT#0: 887), (RUNTIME#4: 5.676751224831961)}

-- evaluateFunction: -1,559,000,000 > function ops: 6/6
     */
    private static double calcPrimesCountGMP10(long n) {
        double a = Math.log(n) ;
        double b = Math.log(a) ;
        double c = Math.pow( b , 1/a ) ;
        double d = Math.pow( c , c ) ;
        double e = a-d ;
        double f = n/e;
        double g = f * 0.0002 ;
        double h = f-g-2;
        return h;
    }



    @Test
    public void test1() {

        MathStack<MathValueFast> globalStack = new MathStack<>(StackType.GLOBAL, true);
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal( Math.PI )) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal( Math.sqrt(2))) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal( Math.sqrt(3))) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal( Math.sqrt(5))) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal( Math.sqrt(7))) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal( 1.61803398874989484820458683436563811772030917980576286213544862270526046281890 )) ); // Golden ratio
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal( 0.577215664901532860606512090082 )) ); //gamma or the Euler-Mascheroni constant
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal( 2.6854520010653064453097148354817956938203822939 )) ); // Khinchin's constant K
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal( 1.282427129100622636875342568869791727767688927325001192 )) ); // Glaisher-Kinkelin constant A
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(1)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(2)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(3)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(4)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(5)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(7)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.E/2)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.E)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.E*2)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.PI/2)) );
        globalStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastDecimal(Math.PI*2)) );


        MathStack<MathValueFast> inputStack = new MathStack<>(StackType.INPUT, true);
        inputStack.add( new MathObjectSingleton<>( au , new MathValueFast.MathValueFastInteger(887)) );

        FunctionOperation[] operations = new FunctionOperation[] {
                new FunctionOperation( ArithmeticOperation.LOG , new StackValue(StackType.INPUT, 0) , null ) ,
                new FunctionOperation( ArithmeticOperation.LOG , new StackValue(StackType.RUNTIME, 0) , null ) ,

                //new FunctionOperation( ArithmeticOperation.ROOT , new StackValue(StackType.RUNTIME, 1) , new StackValue(StackType.RUNTIME, 0) ) ,
                //new FunctionOperation( ArithmeticOperation.POWER , new StackValue(StackType.RUNTIME, 2) , new StackValue(StackType.RUNTIME, 2) ) ,

                //new FunctionOperation( ArithmeticOperation.SUBTRACT , new StackValue(StackType.RUNTIME, 0) , new StackValue(StackType.RUNTIME, 1) ) ,
                //new FunctionOperation( ArithmeticOperation.DIVIDE , new StackValue(StackType.INPUT, 0) , new StackValue(StackType.RUNTIME, 2) ) ,


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

        functionSimulation.setAlwaysGenerateWithTemplateOperations(true);
        functionSimulation.setTemplateOperations(
                new FunctionOperation( ArithmeticOperation.DIVIDE , new StackValue(StackType.INPUT, 0) , new StackValue(StackType.RUNTIME, 4) )
        );

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
