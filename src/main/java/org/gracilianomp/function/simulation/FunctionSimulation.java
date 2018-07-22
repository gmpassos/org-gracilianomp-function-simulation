package org.gracilianomp.function.simulation;

import org.gracilianomp.arithmetic.ArithmeticOperation;
import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.arithmetic.MathValue;
import org.gracilianomp.function.*;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

final public class FunctionSimulation<V extends MathValue> {

    final private MathFunction<V> function ;
    final private MathObject<V> target ;

    final private double distanceMaxError ;
    final private int maxOperations;
    final private float skipOperationsRatio;

    public FunctionSimulation(MathFunction<V> function, MathObject<V> target, double distanceMaxError, int maxOperations, double skipOperationsRatio) {
        this.function = prepareFunction(function);
        this.target = target;
        this.distanceMaxError = distanceMaxError;
        this.maxOperations = maxOperations;
        this.skipOperationsRatio = (float) skipOperationsRatio;
    }

    private MathFunction<V> prepareFunction(MathFunction<V> function) {
        FunctionOperation[] operationsWithIDs = getGeneratedOperationsWithIDs(function);
        MathFunction<V> copy = function.copy(operationsWithIDs);
        return copy;
    }

    public MathFunction<V> getFunction() {
        return function;
    }

    public MathObject<V> getTarget() {
        return target;
    }

    public double getDistanceMaxError() {
        return distanceMaxError;
    }

    public int getMaxOperations() {
        return maxOperations;
    }

    public float getSkipOperationsRatio() {
        return skipOperationsRatio;
    }

    public MathFunction<V> findFunctionWithRetries(int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            MathFunction<V> function = findFunction();
            if (function != null) return function ;
        }
        return null ;
    }

    private MathFunction<V> bestFoundFunction ;
    private double bestFoundFunctionDistance ;

    public MathFunction<V> getBestFoundFunction() {
        return bestFoundFunction;
    }

    public double getBestFoundFunctionDistance() {
        return bestFoundFunctionDistance;
    }

    public MathFunction<V> findFunction() {
        FunctionFinder<V> functionFinder = new FunctionFinder<>(this);

        functionFinder.start(4);

        function.getResult();
        generateOperations(functionFinder, function);

        functionFinder.waitQueueFullyConsumed();
        functionFinder.stop();

        System.out.println("-------------------------------------------------------------------------------------");

        MathFunction<V> bestFunction = functionFinder.getBestFunction();
        double bestFunctionDistance = functionFinder.getBestFunctionDistance();

        this.bestFoundFunction = bestFunction ;
        this.bestFoundFunctionDistance = bestFunctionDistance ;

        System.out.println(bestFunction);
        System.out.println();
        System.out.println("-- Result: "+ bestFunction.getResult());
        System.out.println("-- Target: "+ target);
        System.out.println("-- Distance: "+ bestFunctionDistance);
        
        MathFunction<V>[] extraFunctions = getExtraFunctions(bestFunction, false);

        if (extraFunctions.length > 0) {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("EXTRA FUNCTIONS:");

            for (int i = 0; i < extraFunctions.length; i++) {
                MathFunction<V> extraFunction = extraFunctions[i];
                MathObject<V> extraTarget = extraTargets[i];

                double distance2 = extraFunction.distance(extraTarget);

                System.out.println("--------------------------");
                System.out.println(extraFunction.inputsInfos());
                System.out.println(extraFunction.runtimeInfos());
                System.out.println("-- Result: "+ extraFunction.getResult());
                System.out.println("-- Target: "+ extraTarget);
                System.out.println("-- Distance: "+ distance2);
            }
        }

        System.out.println("-------------------------------------------------------------------------------------");

        System.out.println(bestFunction);
        System.out.println();
        System.out.println("-- Result: "+ bestFunction.getResult());
        System.out.println("-- Target: "+ target);
        System.out.println("-- Distance: "+ bestFunctionDistance);

        System.out.println("-------------------------------------------------------------------------------------");

        if (isValidDistance(bestFunctionDistance)) return bestFunction ;

        return null ;
    }

    private MathStack<V>[] extraInputs ;
    private MathObject<V>[] extraTargets ;
    private int extraTargetsSize = 0 ;

    public int getExtraTargetsSize() {
        return extraTargetsSize;
    }

    public MathStack<V>[] getExtraInputs() {
        return extraInputs;
    }

    public MathObject<V>[] getExtraTargets() {
        return extraTargets;
    }

    public void setExtraTargets(MathStack<V>[] extraInputs, MathObject<V>[] extraTargets) {
        if (extraInputs == null) throw new IllegalArgumentException("Null extraInputs") ;
        if (extraTargets == null) throw new IllegalArgumentException("Null extraTargets") ;
        if (extraInputs.length != extraTargets.length) throw new IllegalArgumentException("Inputs and targets should have the same size: "+ extraInputs.length +" != "+ extraTargets.length) ;

        this.extraInputs = extraInputs ;
        this.extraTargets = extraTargets ;
        this.extraTargetsSize = extraTargets.length ;
    }

    public void addExtraTarget(MathStack<V> extraInput, MathObject<V> extraTarget) {
        if (extraInput == null) throw new IllegalArgumentException("Null extraInput") ;
        if (extraTarget == null) throw new IllegalArgumentException("Null extraTarget") ;

        if (this.extraTargets == null || this.extraTargets.length == 0) {
            this.extraInputs = new MathStack[4];
            this.extraTargets = new MathObject[4];

            this.extraInputs[0] = extraInput ;
            this.extraTargets[0] = extraTarget ;
            this.extraTargetsSize = 1 ;
        }
        else {
            if (this.extraTargetsSize == this.extraTargets.length) {
                int newSize = this.extraTargets.length *2 ;
                this.extraInputs = Arrays.copyOf(this.extraInputs, newSize) ;
                this.extraTargets = Arrays.copyOf(this.extraTargets, newSize) ;
            }

            int sz = this.extraTargetsSize ;
            this.extraInputs[sz] = extraInput ;
            this.extraTargets[sz] = extraTarget ;
            this.extraTargetsSize++ ;
        }
    }

    public void clearExtraTargets() {
        this.extraInputs = null ;
        this.extraTargets = null ;
        this.extraTargetsSize = 0 ;
    }

    public boolean hasExtraTargets() {
        return extraTargetsSize > 0 ;
    }

    private double evaluateFunctionDistance(MathFunction<V> function, double bestFunctionDistance) {
        double distance = function.distanceNoException(target);
        if ( Double.isNaN(distance) ) return Double.NaN ;
        if ( distance > bestFunctionDistance ) return distance ;

        if ( !hasExtraTargets() ) {
            return distance ;
        }

        int resumeIndex = evaluateFunctionDistance_extraTargets_lastIndexBreak ;

        double distance2 = evaluateFunctionDistance_extraTargets_distance(function, resumeIndex);
        if ( Double.isNaN(distance2) ) return Double.NaN ;
        if ( distance2 > bestFunctionDistance ) return distance2 ;

        double maxDistance = distance > distance2 ? distance : distance2 ;

        maxDistance = evaluateFunctionDistance_extraTargets(function, bestFunctionDistance, maxDistance, 0, resumeIndex);
        maxDistance = evaluateFunctionDistance_extraTargets(function, bestFunctionDistance, maxDistance, resumeIndex+1, this.extraTargetsSize);

        return maxDistance ;
    }

    private volatile int evaluateFunctionDistance_extraTargets_lastIndexBreak = 0 ;

    private double evaluateFunctionDistance_extraTargets(MathFunction<V> function, double bestFunctionDistance, double maxDistance, int offset, int limit) {
        if ( Double.isNaN(maxDistance) ) return Double.NaN ;

        for (int i = offset; i < limit; i++) {
            double distance2 = evaluateFunctionDistance_extraTargets_distance(function, i);

            if ( Double.isNaN(distance2) ) {
                evaluateFunctionDistance_extraTargets_lastIndexBreak = i ;
                return Double.NaN ;
            }

            if (distance2 > maxDistance) maxDistance = distance2 ;

            if (distance2 > bestFunctionDistance ) {
                evaluateFunctionDistance_extraTargets_lastIndexBreak = i ;
                return maxDistance ;
            }
        }

        evaluateFunctionDistance_extraTargets_lastIndexBreak = 0 ;

        return maxDistance ;
    }

    private double evaluateFunctionDistance_extraTargets_distance(MathFunction<V> function, int i) {
        MathStack<V> extraInput = extraInputs[i];
        MathObject<V> extraTarget = extraTargets[i];
        MathFunction<V> function2 = function.copy(extraInput) ;
        return function2.distanceNoException(extraTarget);
    }

    private MathFunction<V>[] getExtraFunctions(MathFunction<V> function, boolean includeMainFunction) {
        if ( !hasExtraTargets() ) {
            return includeMainFunction ? new MathFunction[] {function} : new MathFunction[0] ;
        }

        int sz = this.extraTargetsSize ;

        MathFunction<V>[] list ;
        int listOff ;

        if (includeMainFunction) {
            list = new MathFunction[sz+1];
            list[0] = function ;
            listOff = 1 ;
        }
        else {
            list = new MathFunction[sz];
            listOff = 0 ;
        }

        for (int i = 0; i < sz; i++) {
            MathStack<V> extraInput = extraInputs[i];
            list[listOff+i] = function.copy(extraInput) ;
        }

        return list ;
    }

    private boolean isValidDistance(double bestFunctionDistance) {
        return bestFunctionDistance <= distanceMaxError;
    }

    public boolean canGenerateOperations(MathFunction<V> function) {
        return function.getOperationsSize() < maxOperations ;
    }

    public void generateOperations(FunctionFinder<V> functionFinder, MathFunction<V> function) {
        if ( !canGenerateOperations(function) ) return ;

        if ( !function.hasResult() && function.hasOperations() ) throw new IllegalStateException("Result not computed yet! "+ function);

        Random random = new Random();

        HashMap<FunctionOperation, Boolean> uniqueOperations = new HashMap<>();

        long maxImmutableID = 0 ;

        for (FunctionOperation functionOperation : function.getOperations()) {
            uniqueOperations.put(functionOperation, Boolean.TRUE);
            long generationID = functionOperation.getGenerationID();
            if ( generationID > 0 && generationID > maxImmutableID ) {
                maxImmutableID = generationID;
            }
        }

        long immutableGenerationID = 0 ;
        long runtimeGenerationID = 0 ;

        for (StackType stackType1 : StackType.values()) {
            MathStack<V> stack1 = function.getStack(stackType1);
            int size1 = stack1.size();

            for (StackType stackType2 : StackType.values()) {
                MathStack<V> stack2 = function.getStack(stackType2);

                boolean immutableValues = stack1.isImmutable() && stack2.isImmutable() ;

                int size2 = stack2.size();

                for (int sIdx1 = 0; sIdx1 < size1; sIdx1++) {
                    MathObject<V> o1 = stack1.get(sIdx1);
                    int oSize1 = o1.size() ;
                    if (oSize1 == 1) oSize1 = 0 ;

                    for (int oIdx1 = -1; oIdx1 < oSize1; oIdx1++) {
                        StackValue value1 = new StackValue(stackType1, sIdx1, oIdx1);

                        for (int sIdx2 = 0; sIdx2 < size2; sIdx2++) {
                            MathObject<V> o2 = stack2.get(sIdx2);
                            int oSize2 = o2.size() ;
                            if (oSize2 == 1) oSize2 = 0 ;

                            for (int oIdx2 = -1; oIdx2 < oSize2; oIdx2++) {
                                StackValue value2 = new StackValue(stackType2, sIdx2, oIdx2);

                                LOOP_ARITH_OPS: for (ArithmeticOperation arithmeticOperation : ArithmeticOperation.values()) {
                                    if ( arithmeticOperation.isCollection() && arithmeticOperation.isCollectionSingletonUseless() ) {
                                        if ( oSize1 == 0 ) {
                                            continue LOOP_ARITH_OPS ;
                                        }
                                    }

                                    long generationID = immutableValues ? ++immutableGenerationID : --runtimeGenerationID;

                                    if (immutableValues && maxImmutableID >= generationID) {
                                        continue LOOP_ARITH_OPS ;
                                    }

                                    FunctionOperation functionOperation = new FunctionOperation(generationID, arithmeticOperation, value1, value2);

                                    if ( uniqueOperations.put(functionOperation, Boolean.TRUE) == null ) {
                                        if ( random.nextFloat() < skipOperationsRatio ) continue;

                                        MathFunction<V> function2 = function.copy(functionOperation);
                                        functionFinder.addToEvaluationQueue(function2);
                                    }
                                }
                            }

                        }
                    }
                }

            }
        }
    }


    private FunctionOperation[] getGeneratedOperationsWithIDs(MathFunction<V> function) {

        FunctionOperation[] operations = function.getOperations();

        long immutableGenerationID = 0 ;

        HashMap<FunctionOperation, Long> opsIDs = new HashMap<>();

        for (StackType stackType1 : StackType.values()) {
            MathStack<V> stack1 = function.getStack(stackType1);
            if ( !stack1.isImmutable() ) continue;

            int size1 = stack1.size();

            for (StackType stackType2 : StackType.values()) {
                MathStack<V> stack2 = function.getStack(stackType2);
                if ( !stack2.isImmutable() ) continue;

                int size2 = stack2.size();

                for (int sIdx1 = 0; sIdx1 < size1; sIdx1++) {
                    MathObject<V> o1 = stack1.get(sIdx1);
                    int oSize1 = o1.size() ;
                    if (oSize1 == 1) oSize1 = 0 ;

                    for (int oIdx1 = -1; oIdx1 < oSize1; oIdx1++) {
                        StackValue value1 = new StackValue(stackType1, sIdx1, oIdx1);

                        for (int sIdx2 = 0; sIdx2 < size2; sIdx2++) {
                            MathObject<V> o2 = stack2.get(sIdx2);
                            int oSize2 = o2.size() ;
                            if (oSize2 == 1) oSize2 = 0 ;

                            for (int oIdx2 = -1; oIdx2 < oSize2; oIdx2++) {
                                StackValue value2 = new StackValue(stackType2, sIdx2, oIdx2);

                                for (ArithmeticOperation arithmeticOperation : ArithmeticOperation.values()) {
                                    long generationID = ++immutableGenerationID ;

                                    FunctionOperation functionOperation = new FunctionOperation(generationID, arithmeticOperation, value1, value2);

                                    for (FunctionOperation operation : operations) {
                                        if ( operation.equals(functionOperation) ) {
                                            opsIDs.put(operation, generationID) ;
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

        FunctionOperation[] operations2 = operations.clone() ;

        for (int i = 0; i < operations2.length; i++) {
            FunctionOperation op = operations2[i];
            Long id = opsIDs.get(op);

            if (id != null) {
                operations2[i] = op.copy(id);
            }
        }

        return operations2 ;
    }

    static private class FunctionFinder<V extends MathValue> {
        static final private int VERBOSE_INTERVAL = 100000 ;
        static final private int MAX_QUEUE_SIZE = 1000 ;

        final private FunctionSimulation<V> functionSimulation ;
        final private MathObject<V> target ;
        final private int maxOperations;
        final private int maxOperationsM1;

        final private ArrayDeque<MathFunction<V>> evaluationQueue = new ArrayDeque<>();
        final private ArrayDeque<MathFunction<V>>[] generationQueue ;

        public FunctionFinder(FunctionSimulation<V> functionSimulation) {
            this.functionSimulation = functionSimulation ;
            this.target = functionSimulation.getTarget();

            this.maxOperations = functionSimulation.getMaxOperations();
            this.maxOperationsM1 = this.maxOperations - 1;


            this.generationQueue = new ArrayDeque[maxOperations] ;

            for (int i = 0; i < maxOperations; i++) {
                this.generationQueue[i] = new ArrayDeque<>();
            }
        }

        private MathFunction<V> bestFunction ;
        private volatile double bestFunctionDistance = Double.POSITIVE_INFINITY ;
        private volatile boolean foundValidFunction = false ;

        synchronized public MathFunction<V> getBestFunction() {
            return bestFunction;
        }

        synchronized public double getBestFunctionDistance() {
            return bestFunctionDistance;
        }

        synchronized public boolean haveFoundValidFunction() {
            return foundValidFunction ;
        }

        synchronized public void setBestFunction(MathFunction<V> function, double distance) {
            if (
                    distance < bestFunctionDistance ||
                    ( distance == bestFunctionDistance && function.compareTo(bestFunction) < 0 )
                ) {

                bestFunction = function;
                bestFunctionDistance = distance ;

                System.out.println("bestFunctionDistance: "+ distance);
                System.out.println(function);

                if ( functionSimulation.isValidDistance(distance) && !function.hasUnusedOperation() ) {
                    foundValidFunction = true ;
                }
            }
        }

        final private AtomicInteger evaluationCounter = new AtomicInteger();

        public void evaluateFunction(MathFunction<V> function) {
            int count = evaluationCounter.incrementAndGet();

            if (count % VERBOSE_INTERVAL == 0) {
                System.out.println("-- evaluateFunction: "+ formatNumber(count) +" > function ops: "+ function.getOperationsSize() +"/"+ functionSimulation.getMaxOperations());
            }

            if ( !function.tryContinueExecution() ) return ;

            try {
                double bestDistance = this.bestFunctionDistance ;

                double distance = functionSimulation.evaluateFunctionDistance(function, bestDistance);

                if ( !Double.isNaN(distance) ) {
                    setBestFunction(function, distance);

                    if ( functionSimulation.canGenerateOperations(function) ) {
                        addToGenerationQueue(function);
                    }
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void waitQueueFullyConsumed() {
            int waitCount = 0 ;
            synchronized (evaluationQueue) {
                while ( !evaluationQueue.isEmpty() || !generationQueueEmpty() ) {
                    waitCount++;
                    evaluationQueue.notifyAll();

                    if (waitCount % (VERBOSE_INTERVAL/10) == 0) {
                        System.out.println("-- waitQueueFullyConsumed[evaluation]: eval: " + formatNumber(evaluationQueue.size()) + " ; gen: " + formatNumber(generationQueueSize()) +" = "+ Arrays.toString(generationQueueSizes()) +" > bestFunctionDistance: "+ bestFunctionDistance );

                    }

                    try {
                        long sleep = evaluationQueue.isEmpty() ? 1000L : 1000L * 10 ;
                        evaluationQueue.wait(sleep);
                    }
                    catch (InterruptedException e) {}
                }
            }
        }


        public void addToEvaluationQueue(MathFunction<V> function) {
            if ( foundValidFunction ) return ;

            synchronized (evaluationQueue) {
                while ( evaluationQueue.size() > MAX_QUEUE_SIZE ) {
                    try {
                        evaluationQueue.wait(1000L);
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }

                evaluationQueue.add(function);
                evaluationQueue.notifyAll();
            }
        }

        private int generationQueueSize() {
            int size = 0 ;
            for (int i = maxOperationsM1; i >= 0; i--) {
                size += generationQueue[i].size() ;
            }
            return size;
        }

        private int[] generationQueueSizes() {
            int[] sizes = new int[maxOperations] ;
            for (int i = maxOperationsM1; i >= 0; i--) {
                sizes[i] = generationQueue[i].size() ;
            }
            return sizes;
        }

        private boolean generationQueueEmpty() {
            for (int i = maxOperationsM1; i >= 0; i--) {
                if ( !generationQueue[i].isEmpty() ) return false ;
            }
            return true ;
        }

        public void addToGenerationQueue(MathFunction<V> function) {
            if ( foundValidFunction ) return ;

            int opsIdx = function.getOperationsSize() ;

            synchronized (evaluationQueue) {
                generationQueue[opsIdx].add(function);
                evaluationQueue.notifyAll();
            }
        }

        private volatile boolean running ;

        public void start(int threads) {
            if (threads < 1) threads = 1;

            running = true ;

            for (int i = 0; i < threads; i++) {
                dispatchEvaluationProcessor();
                dispatchGenerationProcessor();
            }
        }

        public void stop() {
            synchronized (evaluationQueue) {
                running = false;
                evaluationQueue.notifyAll();
            }

            waitQueueFullyConsumed();
        }

        private void dispatchEvaluationProcessor() {
            new Thread(() -> processEvaluationQueueLoop() , "EvaluationProcessor").start();
        }

        private void dispatchGenerationProcessor() {
            new Thread(() -> processGenerationQueueLoop() , "GenerationProcessor").start();
        }

        private void processEvaluationQueueLoop() {
            while (true) {
                MathFunction<V> function = consumeEvaluationQueue();

                if (function != null) {
                    evaluateFunction(function);
                }
                else {
                    synchronized (evaluationQueue) {
                        if (evaluationQueue.isEmpty() && !running) break;
                    }
                }
            }
        }

        private void processGenerationQueueLoop() {
            while (true) {
                MathFunction<V> function = consumeGenerationQueue();

                if (function != null) {
                    functionSimulation.generateOperations(this, function);
                }
                else {
                    synchronized (evaluationQueue) {
                        if (generationQueueEmpty() && !running) break;
                    }
                }
            }
        }

        private MathFunction<V> consumeEvaluationQueue() {
            synchronized (evaluationQueue) {
                while (evaluationQueue.isEmpty() && running) {
                    try {
                        evaluationQueue.wait(1000L*10);
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }

                if (evaluationQueue.isEmpty()) return null;

                if (evaluationQueue.size() < MAX_QUEUE_SIZE) evaluationQueue.notifyAll();

                return evaluationQueue.poll();
            }
        }

        private MathFunction<V> consumeGenerationQueue() {
            synchronized (evaluationQueue) {
                while (generationQueueEmpty() && running) {
                    try {
                        evaluationQueue.wait(1000L*10);
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }

                for (int i = maxOperationsM1; i >= 0; i--) {
                    ArrayDeque<MathFunction<V>> queue = generationQueue[i];
                    if ( !queue.isEmpty() ) {
                        return queue.poll();
                    }
                }

                return null ;
            }
        }

    }


    static public String formatNumber(long n) {
        String s = String.valueOf(n) ;

        String f = "";
        int fSz = 0 ;

        char[] chars = s.toCharArray();

        for (int i = chars.length-1; i >= 0; i--) {
            char c = chars[i];
            if (fSz > 0 && fSz % 3 == 0) f = ","+f ;
            f = c+f ;
            fSz++ ;
        }

        return f ;
    }

    public static void main(String[] args) throws IOException {

        if ( args.length < 1 ) {
            System.out.println("USAGE:");
            System.out.println("  $> java "+ FunctionSimulation.class.getName() +" %functionSimulationJSONFile");
            System.exit(0);
        }

        FunctionSimulationJSONCodec.main(args);

    }

}
