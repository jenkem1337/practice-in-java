package org.ApplicationContext;

import java.util.Map;
public class Calculator {
    private Map<String, MathFunction> functions;

    public void setFunctions(Map<String, MathFunction> functions) {
        this.functions = functions;
    }

    public double doCalculation(String functionKey, double arg) {
        MathFunction function = functions.get(functionKey);
        return function.calculate(arg);
    }
}
