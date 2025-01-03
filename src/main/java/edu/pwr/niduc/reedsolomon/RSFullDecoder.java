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
        int[] syndromes = calculateSyndromes(received);
        System.out.println("Syndromes: " + Arrays.toString(syndromes));

        if (isAllZero(syndromes)) {
            System.out.println("No correction needed");
            return received; // No errors
        }

        int[][] euclideanResult = euclideanAlgorithm(syndromes);
        int[] errorLocatorPoly = euclideanResult[1]; // Lambda(x)
        int[] omega = euclideanResult[0]; // Omega(x)

        System.out.println("Error Locator Polynomial: " + Arrays.toString(errorLocatorPoly));
        System.out.println("Omega: " + Arrays.toString(omega));

        int[] errorPositions = findErrorPositions(errorLocatorPoly);
        System.out.println("Error positions: " + Arrays.toString(errorPositions));

        if (errorPositions.length == 0) {
            System.out.println("No errors found (unexpected)");
            return received; // Error positions not found
        }

        int[] errorMagnitudes = findErrorMagnitudes(omega, errorLocatorPoly, errorPositions);
        System.out.println("Error magnitudes: " + Arrays.toString(errorMagnitudes));

        return correctErrors(received, errorPositions, errorMagnitudes);
    }


    private int[] calculateSyndromes(int[] received) {
        int[] syndromes = new int[2 * t];

        for (int i = 0; i < 2 * t; i++) {
            int alphaPower = i + 1;
            int evaluation = 0;

            for (int j = 0; j < received.length; j++) {
                int powResult = gf.pow(gf.alpha, alphaPower * j);
                int term = gf.multiplyAlpha(received[j], powResult);
                evaluation = gf.addAlpha(evaluation, term);
            }
            syndromes[i] = evaluation;
        }

        System.out.println("Syndromes" + Arrays.toString(syndromes));
        return syndromes;
    }

    private boolean isAllZero(int[] array) {
        for (int value : array) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    private int[][] euclideanAlgorithm(int[] syndromes) {
        int[] r0 = new int[2 * t+2];
        r0[2*t+1] = 1; // x^(2t)
        System.out.println(Arrays.toString(r0));
        int[] r1 = syndromes;
        int[] sigma0 = {1};
        int[] sigma1 = {0};

        while (degree(r1) > t) {
            int[] quotient = gf.getQuotient(r0,r1);
            int[] remainder = gf.dividePolynomials(r0,r1);

            int[] sigmaNext = gf.addPolynomials(sigma0, gf.multiplyPolynomials(quotient, sigma1));

            r0 = r1;
            r1 = remainder;
            sigma0 = sigma1;
            sigma1 = sigmaNext;
            System.out.println("r0: " + Arrays.toString(r0));
            System.out.println("r1: " + Arrays.toString(r1));
            System.out.println("quotient: " + Arrays.toString(quotient));
            System.out.println("remainder: " + Arrays.toString(remainder));
            System.out.println("sigmaNext: " + Arrays.toString(sigmaNext));

        }

        return new int[][] {r1, sigma1}; // Omega(x) and Lambda(x)
    }

    private int degree(int[] poly) {
        for (int i = poly.length - 1; i >= 0; i--) {
            if (poly[i] != 0) {
                return i;
            }
        }
        return -1;
    }

    private int[] findErrorPositions(int[] errorLocatorPoly) {
        List<Integer> errorPositions = new ArrayList<>();

        for (int i = 0; i < gf.q; i++) {
            int value = 0;
            for (int j = 0; j < errorLocatorPoly.length; j++) {
                value = gf.addAlpha(value, gf.multiplyAlpha(errorLocatorPoly[j], gf.pow(gf.alpha, i * j)));
            }
            if (value == 0) {
                errorPositions.add(gf.q - 1 - i);
            }
        }

        return errorPositions.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] findErrorMagnitudes(int[] omega, int[] errorLocatorPoly, int[] errorPositions) {
        int[] errorMagnitudes = new int[errorPositions.length];

        for (int i = 0; i < errorPositions.length; i++) {
            int xi = gf.pow(gf.alpha, errorPositions[i]);
            int numerator = gf.evaluatePolynomial(omega, xi);
            int denominator = gf.evaluatePolynomialDerivative(errorLocatorPoly, xi);

            if (denominator == 0) {
                throw new ArithmeticException("Division by zero in error magnitude calculation");
            }

            errorMagnitudes[i] = gf.divideAlpha(numerator, denominator);
        }

        return errorMagnitudes;
    }

    private int[] correctErrors(int[] received, int[] errorPositions, int[] errorMagnitudes) {
        int[] corrected = Arrays.copyOf(received, received.length);
        for (int i = 0; i < errorPositions.length; i++) {
            corrected[errorPositions[i]] = gf.addAlpha(corrected[errorPositions[i]], errorMagnitudes[i]);
        }
        return corrected;
    }
    public int[] reverse(int[] array) {
        int start = 0;
        int end = array.length - 1;

        while (start < end) {
            int temp = array[start];  // Tymczasowo zapisz wartość z początku
            array[start] = array[end];  // Zamień wartość z końca na początek
            array[end] = temp;  // Zapisz tymczasową wartość na końcu
            start++;  // Przesuń wskaźnik początku w prawo
            end--;  // Przesuń wskaźnik końca w lewo
        }
        return array;
    }
}