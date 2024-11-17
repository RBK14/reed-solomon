package edu.pwr.niduc.reedsolomon;

import edu.pwr.niduc.util.InvalidCorrectionValueException;

import java.util.Arrays;
import java.util.stream.IntStream;

public class RSEncoder {

    private final int t;
    private final GaloisField64 galoisField64;
    private final GeneratingPolynomial generatingPolynomial;

    public RSEncoder(int t) {
        this.t = t;
        this.galoisField64 = new GaloisField64();
        this.generatingPolynomial = new GeneratingPolynomial();
    }

    public int[] encode_message(int[] message) {
        if (t < 1) {
            throw new InvalidCorrectionValueException("Correction value t must be greater or equal to 1");
        }

        // Generowanie wielomianu generującego
        int[] genPoly = generatingPolynomial.generatePolynomial(t);

        // Obliczanie n-k
        int n = message.length + 2 * t; // n = k + r (r = 2t)
        int k = message.length;
        int nMinusK = n - k;

        // Przesunięcie wiadomości przez mnożenie przez x^(n-k)
        int[] shiftedMessage = new int[message.length + nMinusK];
        System.arraycopy(message, 0, shiftedMessage, 0, message.length);

        // Obliczenie reszty (r(x))
        int[] remainder = galoisField64.dividePolynomials(shiftedMessage, genPoly);

        // Połączenie wiadomości z resztą
        return mergeArraysUsingStreams(message, remainder);
    }

    public static int[] mergeArraysUsingStreams(int[] arr1, int[] arr2) {
        return IntStream.concat(Arrays.stream(arr1), Arrays.stream(arr2)).toArray();
    }
}
