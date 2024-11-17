package edu.pwr.niduc.reedsolomon;

import java.util.Arrays;
import static edu.pwr.niduc.reedsolomon.GaloisField64.multiplyPolynomials;

public class GeneratingPolynomial {

    public static int [] generatePolynomial(int t) {
        if (2*t < 1) {
            // TODO: InvalidCorrectionValueException
            return null;
        }

        int[] generatingPolynomial = new int[]{2,1};        // g(x) = x + alfa

        for (int i = 3; i <= 2*t; i++){
            int[] root = new int[]{i,1};
            generatingPolynomial = multiplyPolynomials(generatingPolynomial, root);
        }
        return generatingPolynomial;
    }
}
