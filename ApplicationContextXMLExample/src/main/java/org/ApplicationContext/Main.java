package org.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        MathFunction sin = new Sin();
//        MathFunction cos = new Cos();
//        MathFunction log = new Log();
//
//        Calculator calculator = new Calculator();
//        calculator.setFunctions(Map.of("sin", sin, "cos", cos, "log", log));
//
//        System.out.println(calculator.doCalculation("sin", 2*Math.PI*30/360));
//        System.out.println(calculator.doCalculation("cos", 2*Math.PI*30/360));
//        System.out.println(calculator.doCalculation("log", 2.71828));

        var ctx = new ClassPathXmlApplicationContext("beans.xml");

        Calculator calculator = (Calculator) ctx.getBean("calculatorBean");

        System.out.println(calculator.doCalculation("sin", 2*Math.PI*30/360));
        System.out.println(calculator.doCalculation("cos", 2*Math.PI*30/360));
        System.out.println(calculator.doCalculation("log", 2.71828));

    }
}