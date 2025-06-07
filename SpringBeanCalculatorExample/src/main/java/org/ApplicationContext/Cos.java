package org.ApplicationContext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

public class Cos implements MathFunction{

    @Override
    public double calculate(double arg) {
        return Math.cos(arg);
    }
}
