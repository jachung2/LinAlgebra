/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LinSolver;

/**
 *
 * @author User
 */
import java.math.*;
import java.util.Objects;
import java.lang.*;
public class RationalBigInteger extends Number implements Comparable<RationalBigInteger>{
    public static final RationalBigInteger ZERO = new RationalBigInteger("0");
    public static final RationalBigInteger ONE = new RationalBigInteger("1");
    public static final RationalBigInteger MINUS_ONE = new RationalBigInteger("-1");

    private BigInteger numerator = new BigInteger("0");
    private BigInteger denominator = new BigInteger("1");

    public RationalBigInteger() {
        this(0, 1);
    }
    public RationalBigInteger(BigInteger n) {
        this(n.toString());
    }

    RationalBigInteger(String n) {
        if (!n.contains("/")) {
            numerator = new BigInteger(n);
            denominator = BigInteger.ONE;
        }
        else {
            String[] tokens = n.split("/");
            numerator = new BigInteger(tokens[0]);
            denominator = new BigInteger(tokens[1]);
        }
    }

    RationalBigInteger(int n) {
        numerator = new BigInteger(n + "");
        denominator = BigInteger.ONE;
    }

    RationalBigInteger(double n) {
        int numDecimals = numDecimals(n);
        StringBuilder s1 = new StringBuilder(n + "");
        s1.deleteCharAt(s1.length() - numDecimals - 1);
        numerator = new BigInteger(s1.toString());
        StringBuilder denominatorString = new StringBuilder("1");

        for(int i = 1; i <= numDecimals; i++) {
            denominatorString.append("0");
        }
        denominator = new BigInteger(denominatorString.toString());
        BigInteger gcd = gcd(numerator, denominator);
        numerator = ((denominator.compareTo(BigInteger.ZERO) > 0) ?
                BigInteger.ONE : BigInteger.ONE.negate()).multiply(numerator).divide(gcd);
        denominator = (denominator.abs()).divide(gcd);
    }

    RationalBigInteger(long numerator, long denominator) {
        this.numerator = new BigInteger(numerator + "");
        this.denominator = new BigInteger(denominator + "");
    }

    RationalBigInteger(BigInteger numerator, BigInteger denominator) {
        BigInteger gcd = gcd(numerator, denominator);
        this.numerator = ((denominator.compareTo(BigInteger.ZERO) > 0) ?
                BigInteger.ONE : BigInteger.ONE.negate()).multiply(numerator).divide(gcd);
        this.denominator = (denominator.abs()).divide(gcd);
    }

    public RationalBigInteger abs() {
        return (numerator.compareTo(BigInteger.ZERO) > 0) ? this : this.multiply(MINUS_ONE);
    }

    private static int numDecimals(double n) {
        String s = n + "";
        String lengthMantissa = s.substring(s.indexOf("."),s.length() - 1);
        return lengthMantissa.length();
    }

    private static long lcm(long a, long b){
        return a * (b / gcd(a, b));
    }

    private static BigInteger lcm(BigInteger a, BigInteger b) {
        return a.multiply(b.divide(gcd(a, b)));
    }

    private static BigInteger gcd(BigInteger a, BigInteger b) {
        BigInteger r;
        if(b.compareTo(BigInteger.ZERO) < 0) {
            a = a.negate();
        }
        b = b.abs();

        while(!b.equals(BigInteger.ZERO)) {
            r = a.mod(b);
            a = b;
            b = r;
        }
        return a;
    }

    private static long gcd(long a, long b) {
        long r;
        while(b != 0) {
            r = a % b;
            a = b;
            b = r;
        }
        return a;
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    public RationalBigInteger add(RationalBigInteger secondRational) {
        BigInteger n1 = numerator.multiply(secondRational.getDenominator());
        BigInteger n2 = denominator.multiply(secondRational.getNumerator());
        BigInteger n = n1.add(n2);
        BigInteger d = denominator.multiply(secondRational.getDenominator());
        return new RationalBigInteger(n, d);
    }

    public RationalBigInteger subtract(RationalBigInteger secondRational) {
        BigInteger n1 = numerator.multiply(secondRational.getDenominator());
        BigInteger n2 = denominator.multiply(secondRational.getNumerator());
        BigInteger n = n1.subtract(n2);
        BigInteger d = denominator.multiply(secondRational.getDenominator());
        return new RationalBigInteger(n, d);
    }

    public RationalBigInteger multiply(RationalBigInteger secondRational) {
        BigInteger n = numerator.multiply(secondRational.getNumerator());
        BigInteger d = denominator.multiply(secondRational.getDenominator());
        return new RationalBigInteger(n, d);
    }

    public RationalBigInteger divide(RationalBigInteger secondRational) {
        BigInteger n = numerator.multiply(secondRational.getDenominator());
        BigInteger d = denominator.multiply(secondRational.getNumerator());
        return new RationalBigInteger(n, d);
    }

    public RationalBigInteger negate() {
        if (numerator.equals(BigInteger.ZERO)) {
            return this;
        }
        BigInteger n = this.numerator.negate();
        BigInteger d = this.denominator;
        return new RationalBigInteger(n, d);
    }

    public int numDigits() {
        return this.toString().length();
    }

    @Override
    public String toString() {
        if (denominator.equals(BigInteger.ONE)) {
            return numerator + "";
        }
        else
            return numerator + "/" + denominator;
    }

    @Override
    public boolean equals(Object other) {
        return this.subtract((RationalBigInteger)(other)).getNumerator().equals(BigInteger.ZERO);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.numerator);
        hash = 29 * hash + Objects.hashCode(this.denominator);
        return hash;
    }

    @Override
    public int intValue() {
        return (int)doubleValue();
    }

    @Override
    public float floatValue() {
        return (float)doubleValue();
    }

    @Override
    public double doubleValue() {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    @Override
    public long longValue() {
        return (long)doubleValue();
    }

    public int compareTo(RationalBigInteger o) {
        if(this.subtract(o).getNumerator().compareTo(BigInteger.ZERO) > 0) {
            return 1;
        }
        else if (this.subtract(o).getNumerator().compareTo(BigInteger.ZERO) < 0) {
            return -1;
        }
        else
            return 0;
    }
}