package org.ApplicationContext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


public class Log implements MathFunction{
    @Override
    public double calculate(double arg) {
        return Math.log(arg);
    }
}
