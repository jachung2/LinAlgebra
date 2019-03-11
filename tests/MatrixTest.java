package LinSolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatrixTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void TestNull()
    {

        for (NullTest testcase : NULL_TESTS) {
            Matrix m = new Matrix(testcase.data);
            Matrix calculatedNull = Matrix.NullM(new Matrix(m));
            Matrix expectedNull = new Matrix((testcase.datanull));
            calculatedNull.print();
            expectedNull.print();

            try {
                Matrix prod = m.multiply(calculatedNull);
                assertEquals(prod, Matrix.zeroMatrix(m.getM(), calculatedNull.getN()));
            } catch (Matrix.IllegalDimensionException e) {};

        }

    }
    @Test
    void matMul() {
    }
//    @Test
    void testCat() {
        Matrix A = new Matrix(new int[][] {{1, 2, 3, 1}, {1, 1, 2, 1}, {1, 2, 3, 1}});
        Matrix ea = Matrix.getRE(A);

        Matrix nullM = Matrix.NullM(A.transpose());
        System.out.println("Ea is ");
        ea.print();
        System.out.println("nullm is ");
        nullM.print();
    }

    private static class NullTest {
        int[][] data;
        int[][] datanull;
        NullTest(int[][] data, int[][] datanull) {
            this.data = data;
            this.datanull = datanull;
        }
    }

    private static final List<NullTest> NULL_TESTS = new ArrayList<>() {
        {
            add(new NullTest(
                    new int[][]{{1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}},
                    new int[][] {{-1, -1, -1}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}
                    ));
            add(new NullTest(
                    new int[][]{{1, 1, 1, 0}, {0, 0, 0, 1}},
                    new int[][] {{-1, -1}, {0, 1}, {1, 0}, {0, 0}}
                    ));
            add(new NullTest(
                    new int[][] {{1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}},
                    new int[][] {{-1, -1, -1}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}
                    ));
            add(new NullTest(
                    new int[][] {{1, 0, -5, 1, 4}, {-2, 1, 6, -2, -2}, {0, 2, -8, 1, 9}},
                    new int[][] {{5, -7}, {4, -6}, {1, 0}, {0, 3}, {0, 1}}
            ));
            add(new NullTest(
                    new int[][] {{1, 0, -3, 2}, {0, 1, -5, 4}, {3, -2, 1, -2}},
                    new int[][] {{3, -2}, {5, -4}, {1, 0}, {0, 1}}
            ));
        }
    };
}