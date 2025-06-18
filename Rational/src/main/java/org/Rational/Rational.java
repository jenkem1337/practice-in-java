package org.Rational;


import javax.management.relation.Relation;

public record Rational(long numerator, long denominator) {
    private static long gcd(long p, long q) {
        if (p < 0) p = -p;
        if (q < 0) q = -q;
        if (0 == q) return p;
        else return gcd(q, p % q);

    }
    public Rational times(Rational rational) {

        long newNumerator = numerator() * rational.numerator();
        long newDenominator = denominator() * rational.denominator();
        long gcdResponse = gcd(newNumerator, newDenominator);
        return new Rational(newNumerator/gcdResponse, newDenominator/gcdResponse);
    }

    public Rational dividedBy(Rational rational) {
        long newNumerator = numerator() * rational.denominator();
        long newDenominator = denominator() * rational.numerator();
        long gcdResponse = gcd(newNumerator, newDenominator);
        return new Rational(newNumerator/gcdResponse, newDenominator/gcdResponse);
    }

    public Rational plus(Rational rational) {
        if(denominator() == rational.denominator()){
            long newNumerator = numerator() + rational.numerator();
            long gcdResponse = gcd(newNumerator, denominator());
            return new Rational(newNumerator/gcdResponse, denominator()/gcdResponse);
        }
        long actualRationalGcd  = gcd(numerator(), denominator());
        long otherRationalGcd   = gcd(rational.numerator(), rational.denominator());

        Rational newActualRational = new Rational(numerator()/actualRationalGcd, denominator()/actualRationalGcd);
        Rational newOtherRational  = new Rational(rational.numerator()/otherRationalGcd, rational.denominator()/otherRationalGcd);

        if(newActualRational.denominator() == newOtherRational.denominator()){
            long newNumerator = newOtherRational.numerator() + newOtherRational.numerator();
            long gcdResponse = gcd(newNumerator, newActualRational.denominator());
            return new Rational(newNumerator/gcdResponse, newActualRational.denominator()/gcdResponse);
        }

        long a = newActualRational.numerator() * newOtherRational.denominator();
        long x = newActualRational.denominator() * newOtherRational.denominator();
        long b = newOtherRational.numerator() * newActualRational.denominator();
        long y = newOtherRational.denominator() * newActualRational.denominator();

        long numeratorRes = a + b;
        return new Rational(numeratorRes, x);
    }
    public Rational minus(Rational other) {
        return this.plus(other.negate());
    }
    private Rational negate() {
        return new Rational(-numerator(), denominator());
    }
}
