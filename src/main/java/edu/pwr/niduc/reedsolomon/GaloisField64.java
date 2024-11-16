package edu.pwr.niduc.reedsolomon;

import edu.pwr.niduc.util.OutOfGaloisFieldException;

public class GaloisField64 {

    private static final int m = 6;
    private static final int q = (int) Math.pow(2,m);
    private static final int[] ZT = new int[]{
            6, 12, 32, 24, 62, 1, 26, 48, 45, 61, 25, 2, 35, 52, 23,
            33, 47, 27, 56, 59, 42, 50, 15, 4, 11, 7, 18, 41, 60, 46,
            34, 3, 16, 31, 13, 54, 44, 49, 43, 55, 28, 21, 39, 37, 9,
            30, 17, 8, 38, 22, 53, 14, 51, 36, 40, 19, 58, 57, 20, 29,
            10, 5
    };

    public static int multiply(int x, int y) {
        validatePolynomials(x, y);

        if (x == 0 || y == 0) {
            return 0;
        }
        return 1 + ((x + y - 2) % (q - 1));
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static int add(int x, int y) {
        validatePolynomials(x,y);

        if (x == 0 || y == 0) {
            return x+y;
        } else if (x == y) {
            return 0;
        } else {
            if (x < y) {
                int h = x;
                x = y;
                y = h;
            }
            return ((y + ZT[x - y - 1] - 1) % (q - 1)) + 1;
        }
    }

    private static void validatePolynomials(int x, int y) {
        if ((x < 0 || x > q-1) || (y < 0 || y > q-1)) {
            throw new OutOfGaloisFieldException("One of the polynomials in not Galois Field element");
        }
    }
}
