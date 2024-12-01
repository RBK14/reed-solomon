package edu.pwr.niduc.reedsolomon;

import java.util.Arrays;

public class RSDecoder {
    private final int t;
    private final GaloisField galoisField;
    private final GeneratingPolynomial generatingPolynomial;

    public RSDecoder(int m, int t) {
        this.t = t;
        this.galoisField = new GaloisField(m);
        this.generatingPolynomial = new GeneratingPolynomial(m, t);
    }

    public int[] simpleDecode(int[] encodedMessage) {
        if (encodedMessage == null || encodedMessage.length == 0) {
            throw new IllegalArgumentException("Encoded message cannot be null or empty.");
        }

        int[] correctedVector = Arrays.copyOf(encodedMessage, encodedMessage.length);
        int n = correctedVector.length;
        int k = n - 2 * t; // Długość części informacyjnej
        int[] generator = generatingPolynomial.generatePolynomial();

        for (int rotation = 0; rotation < n; rotation++) {
            // Oblicz syndrom
            int[] syndrome = galoisField.dividePolynomials(correctedVector, generator);

            // Jeśli syndrom zerowy, nie ma błędów
            if (isZeroSyndrome(syndrome)) {
                log("No correction needed");
                return correctedVector;
            }

            // Liczba błędów
            int syndromeWeight = calculateHammingWeight(syndrome);

            if (syndromeWeight <= t) {
                log("Correcting errors...");
                correctedVector = correctErrors(correctedVector, syndrome, rotation);
                return correctedVector;
            }

            // Obracaj cyklicznie w prawo
            correctedVector = shiftRight(correctedVector);
        }

        log("Uncorrectable errors detected");
        return correctedVector;
    }

    public boolean testCyclicProperty(int[] codeword) {
        int n = codeword.length;
        int[] generator = generatingPolynomial.generatePolynomial();

        for (int i = 0; i < n; i++) {
            int[] syndrome = galoisField.dividePolynomials(codeword, generator);
            if (!isZeroSyndrome(syndrome)) {
                log("Cyclic property failed for rotation " + i);
                return false;
            }
            codeword = shiftRight(codeword);
        }

        log("Cyclic property verified for all rotations");
        return true;
    }

    public int[] correctErrors(int[] vector, int[] syndrome, int rotations) {
        // Popraw błędy za pomocą syndromu
        vector = galoisField.addPolynomials(vector, syndrome);

        // Przywróć oryginalną kolejność
        for (int i = 0; i < rotations; i++) {
            vector = shiftLeft(vector);
        }

        return vector;
    }

    private boolean isZeroSyndrome(int[] syndrome) {
        return Arrays.stream(syndrome).allMatch(el -> el == 0);
    }

    private int calculateHammingWeight(int[] vector) {
        return (int) Arrays.stream(vector).filter(el -> el != 0).count();
    }

    private int[] shiftRight(int[] vector) {
        int[] shifted = new int[vector.length];
        shifted[0] = vector[vector.length - 1];
        System.arraycopy(vector, 0, shifted, 1, vector.length - 1);
        return shifted;
    }

    private int[] shiftLeft(int[] vector) {
        int[] shifted = new int[vector.length];
        System.arraycopy(vector, 1, shifted, 0, vector.length - 1);
        shifted[vector.length - 1] = vector[0];
        return shifted;
    }

    private void log(String message) {
        System.out.println("[RSDecoder] " + message);
    }
}
