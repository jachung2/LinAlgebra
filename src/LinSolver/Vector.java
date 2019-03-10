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
public class Vector {
    private int M;
    private RationalBigInteger[] data;
    public Vector(int[] data) {
        M = data.length;
        this.data = new RationalBigInteger[M];
        for(int i = 0; i < M; i++) {
            this.data[i] = new RationalBigInteger(data[i]);
        }
    }
    public Vector(RationalBigInteger[] data) {
        M = data.length;
        this.data = new RationalBigInteger[M];
        for(int i = 0; i < M; i++) {
            this.data[i] = data[i];
        }
    }
    public Sqrt getLength() {
        RationalBigInteger sum = RationalBigInteger.ZERO;
        for(int i = 0; i < data.length; i++) {
            sum = sum.add(data[i].multiply(data[i]));
        }
        return new Sqrt(sum);
    }
    public static Vector getZeroVector(int m) {
        RationalBigInteger[] data = new RationalBigInteger[m];
        for(int i = 0; i < m; i++) {
            data[i] = RationalBigInteger.ZERO;
        }
        return new Vector(data);
    }
    public Vector(Vector v) {
        this(v.data);
    }
    public RationalBigInteger[] getData() {
        return data;
    }
    public Vector add(Vector v) {
        RationalBigInteger[] vectorAddData = new RationalBigInteger[M];
        for(int i = 0; i < M; i++) {
            vectorAddData[i] = this.data[i].add(v.data[i]);
        }
        return new Vector(vectorAddData);
    }
    public Vector subtract(Vector v) {
        RationalBigInteger[] vectorSubtractData = new RationalBigInteger[M];
        for(int i = 0; i < M; i++) {
            vectorSubtractData[i] = this.data[i].subtract(v.data[i]);
        }
        return new Vector(vectorSubtractData);
    }
    public Vector multiply(RationalBigInteger scalar) {
        RationalBigInteger[] vectorProductData = new RationalBigInteger[M];
        for(int i = 0; i < M; i++) {
            vectorProductData[i] = data[i].multiply(scalar);
        }
        return new Vector(vectorProductData);
    }
    public RationalBigInteger dotProduct(Vector v) {
        RationalBigInteger dotProduct = RationalBigInteger.ZERO;
        if (M != v.M) {
            System.out.println("Illegal Dimensions");
            return RationalBigInteger.MINUS_ONE;
        }
        for(int i = 0; i < M; i++) {
            dotProduct = dotProduct.add(data[i].multiply(v.data[i]));
        }
        return dotProduct;
    }
    public Vector proj(Vector v) {
        RationalBigInteger scale = this.dotProduct(v);
        scale = scale.divide(v.dotProduct(v));
        return new Vector(v.multiply(scale));
    }
    @Override
    public String toString() {
        return java.util.Arrays.toString(data);
    }
    public static void main(String[] args) {
        int[] dataV1 = {-1, 3, 1, 1};
        Vector v1 = new Vector(dataV1);
        int[] dataV2 = {3, 1, 1, -1};
        Vector v2 = new Vector(dataV2);
        int[] dataX2 = {6, -8, -2, -4};
        Vector x2 = new Vector(dataX2);
        int[] dataX3 = {6, 3, 6, -3};
        Vector x3 = new Vector(dataX3);
        Vector x3Projv1 = x3.proj(v1);
        Vector x3Projv2 = x3.proj(v2);
        System.out.println(v1.getLength());

    }
}

