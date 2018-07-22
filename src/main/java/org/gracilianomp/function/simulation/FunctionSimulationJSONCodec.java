package org.gracilianomp.function.simulation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gracilianomp.arithmetic.*;
import org.gracilianomp.function.MathFunction;
import org.gracilianomp.function.MathStack;
import org.gracilianomp.function.StackType;
import org.gracilianomp.utils.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class FunctionSimulationJSONCodec {

    static final private ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public FunctionSimulation read(File jsonFile) throws IOException {
        return read( StreamUtils.read(jsonFile) );
    }

    public FunctionSimulation read(InputStream in) throws IOException {
        return read( StreamUtils.read(in) );
    }

    private ArithmeticUnit arithmeticUnit ;

    public FunctionSimulation read(String json) throws IOException {

        arithmeticUnit = null ;

        JsonNode jsonNode = OBJECT_MAPPER.readTree(json);

        ObjectNode objRoot = asObject(jsonNode);

        String arithmeticUnitType = get(objRoot, "arithmeticUnit").asText();

        this.arithmeticUnit = ArithmeticUnit.toArithmeticUnit(arithmeticUnitType);

        if (this.arithmeticUnit == null) throw new IllegalStateException("Can't parse ArithmeticUnit") ;

        JsonNode globalStackNode = get(objRoot, "globalStack");

        MathStack globalStack = toStack( asArray(globalStackNode), StackType.GLOBAL);

        ArrayNode patternsNode = asArray( get(objRoot, "patterns") ) ;

        int size = patternsNode.size() ;

        MathStack[] inputs = new MathStack[size] ;
        MathObject[] outputs = new MathObject[size] ;

        for (int i = 0; i < size; i++) {
            ArrayNode entry = asArray( patternsNode.get(i) ) ;

            MathStack input = toStack(entry.get(0), StackType.INPUT);
            MathObject output = toMathObject(entry.get(1));

            inputs[i] = input ;
            outputs[i] = output ;
        }

        MathStack stackInput = inputs[0] ;
        MathObject targetObject = outputs[0] ;

        MathFunction mathFunction = new MathFunction<>(globalStack, stackInput);

        double distanceMaxError = getDouble(objRoot,"distanceMaxError", 0) ;
        int maxOperations = getInt(objRoot,"maxOperations", 4) ;
        double skipOperationsRatio = getDouble(objRoot,"skipOperationsRatio", 0) ;

        FunctionSimulation functionSimulation = new FunctionSimulation<>(mathFunction, targetObject, distanceMaxError, maxOperations, skipOperationsRatio) ;

        for (int i = 1; i < size; i++) {
            MathStack input = inputs[i] ;
            MathObject output = outputs[i] ;
            functionSimulation.addExtraTarget(input, output);
        }

        return functionSimulation ;
    }

    private MathStack toStack(JsonNode node, StackType stackType) {
        if (node.isArray()) {
            return toStack( asArray(node), stackType) ;
        }

        boolean immutable = stackType == StackType.GLOBAL || stackType == StackType.INPUT ;

        MathStack stack = new MathStack(stackType, immutable);
        MathObject obj = toMathObject(node);
        stack.add(obj);

        return stack ;
    }

    private MathStack toStack(ArrayNode node, StackType stackType) {
        boolean immutable = stackType == StackType.GLOBAL || stackType == StackType.INPUT ;

        MathStack stack = new MathStack(stackType, immutable);

        Iterator<JsonNode> elements = node.elements();
        while (elements.hasNext()) {
            JsonNode entry =  elements.next();
            MathObject obj = toMathObject(entry);
            stack.add(obj);
        }

        return stack ;
    }

    private MathValue[] toMathValues(ArrayNode node) {
        int size = node.size();
        MathValue[] mathValues = arithmeticUnit.newValuesArray(size);

        for (int i = 0; i < mathValues.length; i++) {
            JsonNode valNode = node.get(i);
            mathValues[i] = toMathValue( valNode );
        }
        
        return mathValues;
    }

    private MathValue toMathValue(JsonNode node) {
        Object n ;

        if ( node.isBoolean() ) {
            n = node.asBoolean() ? 1 : 0 ;
        }
        else if ( node.isTextual() ) {
            n = node.asText() ;
        }
        else if ( node.isLong() || node.isInt() ) {
            n = node.asLong() ;
        }
        else if ( node.isDouble() || node.isFloat() ) {
            n = node.asDouble() ;
        }
        else if ( node.isNumber() ) {
            n = node.asDouble();
        }
        else {
            throw new IllegalStateException("Can't read node as MathValue: "+ node) ;
        }

        return arithmeticUnit.newValue(n) ;
    }

    private MathObject toMathObject(JsonNode node) {
        if ( node.isArray() ) {
            MathValue[] mathValues = toMathValues((ArrayNode) node);

            if ( mathValues.length == 1 ) {
                return new MathObjectSingleton(arithmeticUnit, mathValues[0]) ;
            }
            else {
                return new MathObjectCollection(arithmeticUnit, mathValues) ;
            }
        }
        else {
            MathValue mathValue = mathValue = toMathValue(node);
            return new MathObjectSingleton<>(arithmeticUnit, mathValue) ;
        }
    }

    private int getInt(ObjectNode jsonNode, String name, int defaultValue) {
        JsonNode node = get(jsonNode, name);
        return node != null ? node.asInt() : defaultValue ;
    }

    private double getDouble(ObjectNode jsonNode, String name, double defaultValue) {
        JsonNode node = get(jsonNode, name);
        return node != null ? node.asDouble() : defaultValue ;
    }

    private JsonNode get(ObjectNode jsonNode, String name) {
        JsonNode val = jsonNode.get(name);
        if (val != null) return val ;

        String nameLower = name.toLowerCase();
        val = jsonNode.get(nameLower);
        if (val != null) return val ;

        val = jsonNode.get(name.toUpperCase());
        if (val != null) return val ;

        Iterator<String> fieldNames = jsonNode.fieldNames();

        while (fieldNames.hasNext()) {
            String field =  fieldNames.next();
            if (field.toLowerCase().equals(nameLower)) {
                return jsonNode.get(field) ;
            }
        }

        return null ;
    }

    private ObjectNode asObject(JsonNode jsonNode) {
        if ( !jsonNode.isObject() ) throw new IllegalStateException("Expected object node: "+ jsonNode) ;
        return (ObjectNode) jsonNode;
    }

    private ArrayNode asArray(JsonNode jsonNode) {
        if ( !jsonNode.isArray()) throw new IllegalStateException("Expected array node: "+ jsonNode) ;
        return (ArrayNode) jsonNode;
    }

    public static void main(String[] args) throws IOException {

        File jsonFile = new File(args[0]) ;

        System.out.println("----------------------------------------------");
        System.out.println("jsonFile: "+ jsonFile);
        System.out.println("----------------------------------------------");

        FunctionSimulationJSONCodec codec = new FunctionSimulationJSONCodec();

        FunctionSimulation functionSimulation = codec.read(jsonFile);

        MathFunction function = functionSimulation.findFunction();

        System.out.println("----------------------------------------------");
        System.out.println(function);

    }

}
