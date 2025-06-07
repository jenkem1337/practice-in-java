package org.ApplicationContext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ComponentScan(basePackages = "org.ApplicationContext")
public class CalculatorConfiguration {
    @Bean
    @Qualifier("sinBean")
    protected MathFunction sin(){
        return new Sin();
    }
    @Bean
    @Qualifier("cosBean")
    protected MathFunction cos(){
        return new Cos();
    }
    @Bean
    @Qualifier("logBean")
    protected MathFunction log(){
        return new Log();
    }

    @Bean("calculatorBean")
    public Calculator createCalculator(
            @Qualifier("sinBean")
            MathFunction sin,
            @Qualifier("cosBean")
            MathFunction cos,
            @Qualifier("logBean")
            MathFunction log
    ) {
        var calculator = new Calculator();
        calculator.setFunctions(Map.of("sin", sin, "cos", cos, "log", log));
        return calculator;
    }

}
