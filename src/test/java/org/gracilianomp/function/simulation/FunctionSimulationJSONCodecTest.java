package org.gracilianomp.function.simulation;

import org.gracilianomp.arithmetic.MathObject;
import org.gracilianomp.function.MathFunction;
import org.gracilianomp.function.MathStack;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class FunctionSimulationJSONCodecTest {

    @Test
    public void testModule() throws IOException {
        String jsonFilePath = "/org/gracilianomp/function/simulation/function-simulation-module.json" ;
        testFunctionSimulationCodec(jsonFilePath);
    }

    @Test
    public void testPythagoras() throws IOException {
        String jsonFilePath = "/org/gracilianomp/function/simulation/function-simulation-pythagoras.json" ;
        testFunctionSimulationCodec(jsonFilePath);
    }

    private void testFunctionSimulationCodec(String jsonFilePath) throws IOException {
        InputStream in = FunctionSimulationJSONCodecTest.class.getResourceAsStream(jsonFilePath);

        Assert.assertNotNull(in);

        FunctionSimulationJSONCodec codec = new FunctionSimulationJSONCodec();

        FunctionSimulation functionSimulation = codec.read(in);

        Assert.assertNotNull(functionSimulation);

        Assert.assertTrue(functionSimulation.hasExtraTargets());

        MathFunction function = functionSimulation.findFunction();

        Assert.assertNotNull(function);

        int extraTargetsSize = functionSimulation.getExtraTargetsSize();

        MathStack[] extraInputs = functionSimulation.getExtraInputs();
        MathObject[] extraTargets = functionSimulation.getExtraTargets();

        for (int i = 0; i < extraTargetsSize; i++) {
            MathStack extraInput = extraInputs[i];
            MathObject extraTarget = extraTargets[i];

            MathFunction function2 = function.copy(extraInput);

            MathObject result = function2.getResult();

            Assert.assertEquals( result.getValue().getValueInteger() , extraTarget.getValue().getValueInteger() , 0.00001 );

        }

        System.out.println(function);
    }

}
