package org.ApplicationContext;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(CalculatorConfiguration.class);
        ctx.refresh();

        Calculator calculator = ctx.getBean( "calculatorBean", Calculator.class);
        System.out.println(calculator.doCalculation("sin", 2*Math.PI*30/360));
        System.out.println(calculator.doCalculation("cos", 2*Math.PI*30/360));
        System.out.println(calculator.doCalculation("log", 2.71828));

    }
}