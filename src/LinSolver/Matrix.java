package LinSolver;

/**
 * Matrix class.
 * @author Jon
 */

import java.util.Arrays;
import java.util.Scanner;
public class Matrix {
    private static final RationalBigInteger NEG_ONE = new RationalBigInteger("-1");
    private static final RationalBigInteger ZERO = new RationalBigInteger("0");
    private static final RationalBigInteger ONE = new RationalBigInteger("1");

    private static boolean verbose = false;

    private final RationalBigInteger[][] data;
    private final int M;
    private final int N;

    private int pivotColumnIndex;
    private int rank;

    private Matrix Q;
    private Matrix R;
    private Sqrt[] lengths;

    public Matrix() {
        this.M = 0;
        this.N = 0;
        data = new RationalBigInteger[M][N];
    }

    public Matrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new RationalBigInteger[M][N];
        for(int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                data[i][j] = ZERO;
            }
        }
    }

    public Matrix(int[][] data) {
        this.M = data.length;
        this.N = data[0].length;
        this.data = new RationalBigInteger[M][N];
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                this.data[i][j] = new RationalBigInteger(data[i][j]);
            }
        }
    }

    public Matrix(double[][] data) {
        this.M = data.length;
        this.N = data[0].length;
        this.data = new RationalBigInteger[M][N];
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                this.data[i][j] = new RationalBigInteger(data[i][j]);
            }
        }
    }

    public Matrix(RationalBigInteger[][] data) {
        this.M = data.length;
        this.N = data[0].length;
        this.data = new RationalBigInteger[M][N];
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    public Matrix(Matrix A) {
        this(A.data);
    }

    /**
     * @return m x m identity corresponding to this matrix.
     */
    private Matrix getIdentity() {
        Matrix identity = new Matrix(M, M);
        for(int i = 0; i < identity.M; i++) {
            for(int j = 0; j < identity.M; j++) {
                if(i == j) {
                    identity.data[i][j] = ONE;
                }
                else identity.data[i][j] =  ZERO;
            }
        }
        return identity;
    }

    /**
     * @return the inverse if it exists.
     * @throws SingularMatrixException
     */
    public Matrix getInverse() throws SingularMatrixException {
        RationalBigInteger detM = det(this);
        if (detM.equals(ZERO)) {
            throw new SingularMatrixException();
        }
        Matrix adj = Matrix.adj(this);
        return Matrix.scale(adj, new RationalBigInteger("1/" + detM));
    }

    /**
     * @param m the matrix to scale.
     * @param c constant scale.
     * @return the scaled matrix.
     */
    private static Matrix scale(Matrix m, RationalBigInteger c) {
        Matrix res = new Matrix(m);
        for(int i = 0; i < m.M; i++) {
            res.scaleRow(c, i);
        }
        return res;
    }

    /**
     * @param A the matrix to find the inverse of.
     * @return the [R | E], where R is a row reduced echelon form of A.
     */
    public static Matrix getRE(Matrix A) {
        Matrix I = A.getIdentity();
        Matrix RE = new Matrix();
        try {
            RE = Matrix.cat(A, I);
            RE.REF(A.N);

        } catch(IllegalDimensionException e) {
            System.out.println(e);
        }
        return RE;
    }

    /**
     * @param m the input matrix.
     * @return the null matrix associated with the matrix.
     */
    public static Matrix NullM(Matrix m) {
        Matrix RE = getRE(m.transpose());
        Matrix nullM = RE.subMatrix(RE.rank, RE.N - RE.M);
        return nullM.transpose();
    }

    private void REF() {
        REF(N);
    }

    private void REF(int end) {
        rank = 0;
        for (int i = 0; i < Math.min(end, M); i++) {
            if (verbose) print();
            sortRows(i, this);
            if (verbose) print();

            int pivotColumn = getPivotPosition(i, end);

            RationalBigInteger multiple;
            if (!data[i][pivotColumn].equals(ZERO)) {
                multiple = ONE.divide(data[i][pivotColumn]);
                rank++;
            } else {
                multiple = ONE;
            }

            scaleRow(multiple, i);
            zeroOutPivotColumn(i, pivotColumn);

            i = pivotColumn;
        }
        System.out.println("Rank is " + rank);
        sortRows(0, this);

    }

    private void RREF() {
        RREF(N);
    }

    private void RREF(int end) {
        REF(end);
        for (int i = M - 1; i >= 0; i--) {
            findNextPivotIndex(i, end);
            if (!data[i][pivotColumnIndex].equals(ZERO)) {
                zeroOutAbove(i, pivotColumnIndex);
                if (data[i][pivotColumnIndex].equals(NEG_ONE)) {
                    scaleRow(NEG_ONE, i);
                }
            }
        }
    }

    /**
     * Sort on rows.
     * @param startRow begin row to sort.
     * @param m the matrix to row sort.
     */
    public static void sortRows(int startRow, Matrix m) {
        for(int i = startRow; i < m.M; i++) {
            for(int j = i + 1; j < m.M; j++) {
                if (numLeadingZeros(j, m) < numLeadingZeros(i, m)) {
                    swap(j, i, m);
                }
            }
        }
    }

    public static Matrix RREFMatrix(Matrix m) {
        Matrix A = new Matrix(m);
        A.RREF();
        return A;
    }

    public static Matrix REFMatrix(Matrix m) {
        Matrix A = new Matrix(m);
        A.REF();
        return A;
    }

    /**
     * @param B rhs
     * @return this @ B
     * @throws IllegalDimensionException
     */
    public Matrix multiply(Matrix B) throws IllegalDimensionException {
        if(N != B.M) {
            throw new IllegalDimensionException();
        }
        Matrix C = new Matrix(M, B.N);
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < B.N; j++) {
                RationalBigInteger sumProduct = ZERO;
                for(int k = 0; k < N; k++) {
                    sumProduct = sumProduct.add(data[i][k].multiply(B.data[k][j]));
                }
                C.data[i][j] = sumProduct;
            }
        }
        return C;
    }

    /**
     * @param B rhs
     * @return this + B
     * @throws IllegalDimensionException
     */
    public Matrix add(Matrix B) throws IllegalDimensionException {
        if(M != B.M || N != B.N) {
            throw new IllegalDimensionException();
        }
        Matrix C = new Matrix(M, N);
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                C.data[i][j] = data[i][j].add(B.data[i][j]);
            }
        }
        return C;
    }

    /**
     * Obtains QR decomposition of a matrix using Gram-Schmidt process.
     */
    public void QR() throws IllegalDimensionException, SingularMatrixException {
        RationalBigInteger[][] QData = new RationalBigInteger[M][N];

        if(N > M) {
            throw new SingularMatrixException();
        }
        Sqrt[] lengths = new Sqrt[N];
        for(int i = 0; i < N; i++) {

            Vector vectorX = getVector(this, i);
            // Projection of x onto itself just gives x
            Vector projXontoW = Vector.getZeroVector(M);
            Vector vectorY = vectorX;

            if (i > 0) {
                for(int j = i - 1; j >= 0; j--) {
                    // Orthogonal basis vector v
                    Vector vectorV = getVector(QData, j);
                    // Calculate projection x onto W by adding its projection onto V
                    projXontoW = projXontoW.add(vectorX.proj(vectorV));
                }
                // Orthogonal Y
                vectorY = vectorX.subtract(projXontoW);
            }

            lengths[i] = vectorY.getLength();
            setVector(QData, i, vectorY);
        }
        Q = new Matrix(QData);
        R = Q.transpose().multiply(this);

        Q.lengths = lengths;
        R.lengths = lengths;
    }

    public String QRtoString() {
        // Call after QR.
        int[] maxValues = Q.largestColumnValues(true);
        String matString = "Q:\n";
        for(int i = 0; i < Q.M; i++) {
            for(int j = 0; j < Q.N; j++) {
                int maxColumnDigits = (j == 0) ? maxValues[j] : maxValues[j] + 2;
                String currentElement;

                if (Q.lengths[j].isONE() || Q.data[i][j].equals(RationalBigInteger.ZERO)) {
                    currentElement = String.format("%" + maxColumnDigits + "s", Q.data[i][j]);
                } else {
                    currentElement = String.format("%" + maxColumnDigits + "s", Q.data[i][j] + "/" + Q.lengths[j]);
                }

                matString = matString + currentElement;
            }
            matString = (i == Q.M - 1) ? matString : matString + "\n";
        }
        matString += "\n\nR:\n";
        for(int i = 0; i < R.M; i++) {
            for(int j = 0; j < R.N; j++) {
                int maxColumnDigits = (j == 0) ? maxValues[j] : maxValues[j] + 2;
                String currentElement;

                if (R.lengths[i].isONE() || R.data[i][j].equals(RationalBigInteger.ZERO)) {
                    currentElement = String.format("%" + maxColumnDigits + "s", R.data[i][j]);
                } else {
                    currentElement = String.format("%" + maxColumnDigits + "s", R.data[i][j] + "/" + R.lengths[i]);
                }

                matString = matString + currentElement;
            }
            matString = (i == R.M - 1) ? matString : matString + "\n";
        }
        return matString;
    }

    /**
     * @param M the input matrix.
     * @return the adjugate matrix of M.
     */
    public static Matrix adj(Matrix M) {
        if(M.M != M.N) {
            System.out.println("Matrix isn't invertible.");
        }
        RationalBigInteger[][] adjData = new RationalBigInteger[M.M][M.M];
        if(M.M == 2) {
            adjData[0][0] = M.data[1][1];
            adjData[0][1] = M.data[1][0].multiply(NEG_ONE);
            adjData[1][0] = M.data[0][1].multiply(NEG_ONE);
            adjData[1][1] = M.data[0][0];
        }
        else {
            for(int i = 0; i < M.M; i++) {
                for(int j = 0; j < M.M; j++) {
                    adjData[i][j] = ((i + j) % 2 == 0) ? det(subMatrix(M, i, j)) : det(subMatrix(M, i, j)).multiply(NEG_ONE);
                }
            }
        }
        return new Matrix(adjData).transpose();
    }

    /**
     * Calculate determinant using cofactor expansion across row.
     * @param A the input matrix.
     * @return the determinant.
     */
    public static RationalBigInteger det(Matrix A) {
        if (A.M != A.N) {
            return ZERO;
        }
        if (A.M == 2) {
            return (A.data[0][0].multiply(A.data[1][1])).subtract(A.data[0][1].multiply(A.data[1][0]));
        }
        RationalBigInteger cofactor;
        RationalBigInteger determinant = ZERO;
        for(int j = 0; j < A.N; j++) {
            if (j % 2 == 0) {
                cofactor = A.data[0][j].multiply(det(subMatrix(A, 0, j)));
            }
            else {
                cofactor = NEG_ONE.multiply(A.data[0][j].multiply(det(subMatrix(A, 0, j))));
            }
            determinant = determinant.add(cofactor);
        }
        return determinant;
    }

    /**
     * Helper function for calculating determinant.
     * @param A the input matrix.
     * @param row
     * @param column
     * @return
     */
    private static Matrix subMatrix(Matrix A, int row, int column) {

        int a = 0;
        RationalBigInteger[][] subMatrix = new RationalBigInteger[A.M - 1][A.N - 1];
        for (int i = 0; i < A.M; i++) {
            int b = 0;
            for(int j = 0; j < A.N; j++) {
                if(i != row && j != column){
                    subMatrix[a][b] = A.data[i][j];
                    b++;
                }
            }
            if (i != row) {
                a++;
            }
        }
        return new Matrix(subMatrix);
    }

    public void fill() {
        Scanner input = new Scanner(System.in);
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                System.out.println("Enter row " + i + " as a line: ");
                data[i][j] = new RationalBigInteger(input.next());
            }
        }
    }

    public void print() {
        int[] maxValues = largestColumnValues(false);
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                int maxColumnDigits = maxValues[j] + 2;
                String format = "%" + maxColumnDigits + "s";
                System.out.printf(format, data[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static Matrix cat(Matrix lhs, Matrix rhs) throws IllegalDimensionException {
        if (lhs.M == rhs.M) {
            Matrix res = new Matrix(lhs.M, lhs.N + rhs.N);
            for (int i = 0; i < res.M; i++) {
                for (int j = 0; j < res.N; j++) {
                    if (j < lhs.N) {
                        res.data[i][j] = lhs.data[i][j];
                    } else {
                        res.data[i][j] = rhs.data[i][j - lhs.N];
                    }
                }
            }
            return res;
        }

        throw new IllegalDimensionException();
    }

    public Matrix transpose() {
        RationalBigInteger[][] matrix = new RationalBigInteger[N][M];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                matrix[j][i] = data[i][j];
            }
        }
        return new Matrix(matrix);
    }

    /**
     * @return the largest value in each column as a 1-D array.
     */
    public int[] largestColumnValues(boolean qr) {
        int[] maxValues = new int[N];
        for(int j = 0; j < N; j++) {
            int max = data[0][j].numDigits();
            int sqrtSize = 0;
            if (qr) {
                sqrtSize = lengths[j].stringSize() + 1;
                max += sqrtSize;
            }
            for(int i = 0; i < M; i++) {
                int elementSize = data[i][j].numDigits() + sqrtSize;
                if (elementSize > max) {
                    max = elementSize;
                }
            }
            maxValues[j] = max;
        }
        return maxValues;
    }

    public void scaleRow(RationalBigInteger n, int row) {
        for(int i = 0; i < data[row].length; i++) {
            data[row][i] = n.multiply(data[row][i]);
        }
    }

    /**
     * Adds a multiple n of row 1 to row 2
     * @param n
     * @param row1
     * @param row2
     */
    public void addMultiple(RationalBigInteger n, int row1, int row2) {
        for(int i = 0; i < data[row1].length; i++) {
            data[row2][i] = data[row2][i].add(n.multiply(data[row1][i]));
        }
    }

    public void zeroOutPivotColumn(int row, int column) {
        RationalBigInteger multiple = data[row][column].multiply(NEG_ONE);
        for (int i = row + 1; i < M; i++) {
            if (!data[i][column].equals(ZERO)) {
                RationalBigInteger val = data[i][column];
                addMultiple(multiple.multiply(val), row, i);
            }
        }
    }

    private void zeroOutAbove(int row, int column) {
        RationalBigInteger multiple = data[row][column].multiply(NEG_ONE);
        for (int i = row - 1; i >= 0; i--) {
            if (!data[i][column].equals(ZERO)) {
                RationalBigInteger val = data[i][column];
                addMultiple(multiple.multiply(val), row, i);
            }
        }
    }

    private void findNextPivotIndex(int row, int end) {
        for(int piv = 0; piv < end; piv++) {
            if(!(data[row][piv].equals(ZERO))) {
                pivotColumnIndex = piv;
                return;
            }
        }
    }

    /**
     * For performing [A | I] row reductions
     * @param row the row index of A.
     * @param end the number of columns of A.
     * @return
     */
    private int getPivotPosition(int row, int end) {
        int pivotPosition = 0;
        while(pivotPosition < end  - 1 && data[row][pivotPosition].equals(ZERO)) {
            pivotPosition++;
        }
        return pivotPosition;
    }

    private static int numLeadingZeros(int row, Matrix m) {
        int numLeadingZeros = 0;
        for(int i = 0; i < m.N; i++) {
            if(m.data[row][i].equals(ZERO)) {
                numLeadingZeros++;
            }
            else break;
        }
        return numLeadingZeros;
    }

    /**
     * @param M rows
     * @param N cols
     * @return a random integer matrix of dimensions m x n.
     */
    public static Matrix random(int M, int N) {
        Matrix A = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                A.data[i][j] = new RationalBigInteger((int)(Math.random() * 2141) + 1);
            }
        }
        return A;
    }

    private static void setVector(Matrix m, int column, Vector x) {
        if (x.getData().length == m.M) {
            for(int i = 0; i < m.M; i++) {
                m.data[i][column] = x.getData()[i];
            }
        }
    }

    private static void setVector(RationalBigInteger[][] data, int column, Vector x) {
        if (x.getData().length == data.length) {
            for(int i = 0; i < data.length; i++) {
                data[i][column] = x.getData()[i];
            }
        }
    }

    private static Vector getVector(Matrix m, int column) {
        RationalBigInteger[] x = new RationalBigInteger[m.M];
        if (column < m.N) {
            for(int i = 0; i < m.M; i++) {
                x[i] = m.data[i][column];
            }
        }
        return new Vector(x);
    }

    private static Vector getVector(RationalBigInteger[][] data, int column) {
        RationalBigInteger[] x = new RationalBigInteger[data.length];
        if (column < data[0].length) {
            for(int i = 0; i < data.length; i++) {
                x[i] = data[i][column];
            }
        }
        return new Vector(x);
    }

    /**
     * Swaps 2 rows in a given matrix.
     * @param row1 row1
     * @param row2 row2
     * @param m the matrix
     */
    private static void swap(int row1, int row2, Matrix m) {
        RationalBigInteger[] temp = m.data[row1];
        m.data[row1] = m.data[row2];
        m.data[row2] = temp;
    }

    /**
     * @param i the begin row index.
     * @param j the begin column index.
     * @return The bottom right sub-matrix starting at (i, j).
     */
    private Matrix subMatrix(int i, int j) {
        Matrix res = new Matrix(M - i, N - j);
        for (int row = 0; row < res.M; row++) {
            System.arraycopy(data[i + row], j, res.data[row], 0, res.N);
        }
        return res;
    }

    /**
     * @param m rows
     * @param n cols
     * @return a zero matrix of dimension m x n
     */
    public static Matrix zeroMatrix(int m, int n) {
        int[][] data = new int[m][n];
        for (int[] row : data) {
            Arrays.fill(row, 0);
        }
        return new Matrix(data);
    }

    /**
     * For determining if two matrices are equal.
     * @param o other Matrix.
     * @return if two matrices are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Matrix) {
            if (((Matrix) o).getM() == M && ((Matrix) o).getN() == N) {
                for (int i = 0; i < M; i++) {
                    for (int j = 0; j < N; j++) {
                        if (!data[i][j].equals(((Matrix) o).data[i][j])) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @return a string representation of this matrix object.
     */
    @Override
    public String toString() {
        int[] maxValues = largestColumnValues(false);
        String matString = "";
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                int maxColumnDigits = (j == 0) ? maxValues[j] : maxValues[j] + 2;
                String currentElement = String.format("%" + maxColumnDigits + "s", data[i][j]);
                matString = matString + currentElement;
            }
            matString = (i == M - 1) ? matString : matString + "\n";
        }
        return matString;
    }

    public RationalBigInteger get(int i, int j) {
        return data[i][j];
    }


    public int getM() {
        return M;
    }

    public int getN() {
        return N;
    }

    public Matrix getQ() {
        return Q;
    }

    public Matrix getR() {
        return R;
    }

    /**
     * Turn on for verbose.
     * @param b true/false.
     */
    public static void verbose(boolean b) {
        verbose = b;
    }

    static class IllegalDimensionException extends  Exception {
        IllegalDimensionException() { super("Illegal Dimensions.");}
    }
    static class SingularMatrixException extends  Exception {
        SingularMatrixException() { super("Matrix is Singular.");}
    }
}
