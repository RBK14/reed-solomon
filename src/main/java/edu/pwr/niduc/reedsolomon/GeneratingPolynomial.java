package edu.pwr.niduc.reedsolomon;

import edu.pwr.niduc.util.InvalidCorrectionValueException;

public class GeneratingPolynomial {

    private final GaloisField galoisField;

    public GeneratingPolynomial(int m) {
        this.galoisField = new GaloisField(m);
    }

    public int [] generatePolynomial(int t) {
        if (2*t < 1) {
            throw new InvalidCorrectionValueException("Invalid correction value");
        }

        // Pierwszy pierwiastek wielomianu generującego g(x) = x + alfa
        int[] generatingPolynomial = new int[]{2,1};

        // Mnożenie przez kolejne pierwiastki wielomianu generującego aż do alfa^2t
        for (int i = 3; i <= 2*t + 1; i++){
            int[] root = new int[]{i,1};
            generatingPolynomial = galoisField.multiplyPolynomials(generatingPolynomial, root);
        }
        return generatingPolynomial;
    }
}
