package org.ApplicationContext;

public class Sin implements MathFunction {

    @Override
    public double calculate(double arg) {
        return Math.sin(arg);
    }
}
