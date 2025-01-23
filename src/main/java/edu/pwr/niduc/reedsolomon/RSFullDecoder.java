package edu.pwr.niduc.reedsolomon;

import java.util.*;

public class RSFullDecoder {
    private final GaloisField gf;
    private final int t;

    public RSFullDecoder(int m, int t) {
        this.gf = new GaloisField(m);
        this.t = t;
    }

    public int[] decode(int[] received) {
        validateInput(received);

        int[] syndromes = calculateSyndromes(received);
        log("Syndromes: " + Arrays.toString(syndromes));

        if (isAllZero(syndromes)) {
            log("No correction needed");
            return received;
        }

        int[][] euclideanResult = euclideanAlgorithm(syndromes);
        int[] errorLocatorPoly = euclideanResult[1];
        int[] omega = euclideanResult[0];


        int[] errorPositions = findErrorPositions(errorLocatorPoly);

        if (errorPositions.length == 0) {
            log("No errors found (unexpected)");
            return received;
        }

        int[] errorMagnitudes = findErrorMagnitudes(omega, errorLocatorPoly, errorPositions);

        return correctErrors(received, errorPositions, errorMagnitudes);
    }

    private void validateInput(int[] received) {
        if (received == null || received.length == 0) {
            throw new IllegalArgumentException("Received message cannot be null or empty");
        }
    }

    private int[] calculateSyndromes(int[] received) {
        int[] syndromes = new int[2 * t];
        for (int i = 0; i < 2 * t; i++) {
            int evaluation = 0;
            int alphaPower = i + 1;

            for (int j = 0; j < received.length; j++) {
                int powResult = gf.pow(gf.alpha, alphaPower * j);
                evaluation = gf.addAlpha(evaluation, gf.multiplyAlpha(received[j], powResult));
            }
            syndromes[i] = evaluation;
        }
        return syndromes;
    }

    private boolean isAllZero(int[] array) {
        for (int value : array) {
            if (value != 0) return false;
        }
        return true;
    }

    private int[][] euclideanAlgorithm(int[] syndromes) {
        // r0 = x^(2t)
        int[] r0 = new int[2 * t + 1];
        r0[2 * t] = 1;

        // r1 = syndromy (S(x))
        int[] r1 = Arrays.copyOf(syndromes, syndromes.length);

        // sigma0 = 1
        int[] sigma0 = {1};
        // sigma1 = 0
        int[] sigma1 = {0};

        while (degree(r1) >= t) {
            int[] quotient = gf.getQuotient(r0, r1);
            int[] remainder = gf.addPolynomials(r0,gf.multiplyPolynomials(quotient,r1));
            int[] sigmaNext = gf.addPolynomials(sigma0, gf.multiplyPolynomials(quotient, sigma1));

            r0 = r1;
            r1 = remainder;
            sigma0 = sigma1;
            sigma1 = sigmaNext;
        }

        // Zwracamy [ reszta, lokalizator ], czyli [omega, sigma]
        return new int[][] {r1, sigma1};
    }

    private int degree(int[] poly) {
        for (int i = poly.length - 1; i >= 0; i--) {
            if (poly[i] != 0) return i;
        }
        return -1;
    }

    private int[] findErrorPositions(int[] errorLocatorPoly) {
        int n =64;

        List<Integer> errorPositions = new ArrayList<>();
        // Sprawdzamy i = 0..n-1
        for (int i = 0; i < n; i++) {
            // Evaluate sigma( alpha^i )
            int Xi = gf.pow(gf.alpha, i);
            int value = gf.evaluatePolynomial(errorLocatorPoly, Xi);

            if (value == 0) {
                // Błąd w pozycji (n - 1 - i)
                errorPositions.add(n - 1 - i);
            }
        }

        // Walidacja
        if (errorPositions.size() > t) {
            throw new RuntimeException("Found more errors than can be corrected!");
        }

        return errorPositions.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] findErrorMagnitudes(int[] omega,
                                      int[] errorLocatorPoly,
                                      int[] errorPositions) {
        int[] errorMagnitudes = new int[errorPositions.length];
        for (int i = 0; i < errorPositions.length; i++) {
            int xi = gf.pow(gf.alpha, errorPositions[i]);

            int numerator = gf.evaluatePolynomial(omega, xi);
            int denominator = gf.evaluatePolynomialDerivative(errorLocatorPoly, xi);

            if (denominator == 0) {
                throw new ArithmeticException(
                        "Division by zero in error magnitude calculation"
                );
            }
            errorMagnitudes[i] = gf.divideAlpha(numerator, denominator);
        }
        return errorMagnitudes;
    }

    private int[] correctErrors(int[] received,
                                int[] errorPositions,
                                int[] errorMagnitudes) {
        int[] corrected = Arrays.copyOf(received, received.length);

        for (int i = 0; i < errorPositions.length; i++) {
            int index = errorPositions[i];
            if (index < 0 || index >= received.length) {
                throw new IllegalArgumentException(
                        "Invalid error index: " + index
                );
            }
            corrected[index] = gf.addAlpha(corrected[index], errorMagnitudes[i]);
            log("Corrected position: " + index + ", new value: " + corrected[index]);
        }

        return corrected;
    }

    private void log(String message) {
        System.out.println(message);
    }
}
