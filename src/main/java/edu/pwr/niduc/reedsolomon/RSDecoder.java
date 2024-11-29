package edu.pwr.niduc.reedsolomon;

import java.util.Arrays;
import java.util.stream.IntStream;

public class RSDecoder {
    private final int t;
    private final GaloisField galoisField;
    private final GeneratingPolynomial generatingPolynomial;

    public RSDecoder(int m, int t){
        this.t = t;
        this.galoisField = new GaloisField(m);
        this.generatingPolynomial = new GeneratingPolynomial(m,t);
    }

    public int[] simpleDecode(int[] encodedMessage) {
        if (encodedMessage == null || encodedMessage.length == 0) {
            throw new IllegalArgumentException("Encoded message cannot be null or empty.");
        }

        int[] correctedVector = Arrays.copyOf(encodedMessage, encodedMessage.length);
        int n = correctedVector.length;
        int k = n - 2 * t; // Wyznaczenie długości części informacyjnej
        int[] generator = generatingPolynomial.generatePolynomial();

        for (int i = 0; i <= k; i++) {
            // Obliczanie syndromu jako reszty z dzielenia
            int[] syndrome = galoisField.dividePolynomials(correctedVector, generator);

            // Sprawdzenie, czy syndrom jest zerowy (brak błędów)
            if (isZeroSyndrome(syndrome)) {
                System.out.println("No correction needed");
                return correctedVector;
            }

            // Liczenie wagi syndromu
            int syndromeWeight = calculateHammingWeight(syndrome);

            if (syndromeWeight <= t) {
                // Korekcja błędów
                correctedVector = galoisField.addPolynomials(correctedVector, syndrome);

                // Przywracanie oryginalnej kolejności
                for (int j = 0; j < i; j++) {
                    correctedVector = shiftLeft(correctedVector);
                }
                return correctedVector; // Zwracanie skorygowanego wektora
            } else {
                // Obracanie wektora cyklicznie w prawo
                correctedVector = shiftRight(correctedVector);

                // Sprawdzanie niekorygowalnych błędów
                if (i == k) {
                    System.out.println("Bledy niekorygowalne");
                    // Przywracanie oryginalnej kolejności
                    for (int j = 0; j < i; j++) {
                        correctedVector = shiftLeft(correctedVector);
                    }
                    return correctedVector;
                }
            }
        }
        return correctedVector;
    }

    private boolean isZeroSyndrome(int[] syndrome) {
        for (int el : syndrome) {
            if (el != 0) return false;
        }
        return true;
    }

    private int calculateHammingWeight(int[] vector) {
        int weight = 0;
        for (int el : vector) {
            if (el != 0) weight++;
        }
        return weight;
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

    public static int[] reverseArrayWithStream(int[] array) {
        return IntStream.range(0, array.length)
                .map(i -> array[array.length - 1 - i])
                .toArray();
    }

}
