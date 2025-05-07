package org.ApplicationContext;

public class Log implements MathFunction{
    @Override
    public double calculate(double arg) {
        return Math.log(arg);
    }
}
