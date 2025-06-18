package org.Rational;

public class Main {
    public static void main(String[] args) {
        var rational = new Rational(4,13);
        System.out.println(rational.minus(new Rational(3,9)));
    }
}