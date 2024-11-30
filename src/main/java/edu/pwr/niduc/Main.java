package edu.pwr.niduc;

import edu.pwr.niduc.reedsolomon.*;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // Parametry kodu RS(63,43)
        final int m = 6;
        final int t = 10;

        RSEncoder rsEncoder = new RSEncoder(m,t);
        RSDecoder rsDecoder = new RSDecoder(m,t);
        Transmitter transmitter = new Transmitter(m);

        int[] messagePolynomial = new int[]{ 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42};
        System.out.println("Input message: " + Arrays.toString(messagePolynomial));
        int [] encodeMessage = rsEncoder.encodeMessage(messagePolynomial);
        System.out.println("Encoded message: "+ Arrays.toString(encodeMessage));
        // int[] corruptedMessage = transmitter.simulateBurstErrors(encodeMessage, 10);
        int[] corruptedMessage = new int[]{34, 37, 4, 59, 53, 8, 22, 12, 37, 30, 52, 6, 51, 32, 56, 60, 14, 5, 38, 15, 33, 0, 0, 0, 0, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42};
        // int[] corruptedMessage = new int[]{34, 37, 4, 59, 53, 8, 22, 12, 37, 30, 52, 6, 51, 32, 56, 60, 14, 5, 38, 32, 0, 0, 0, 0, 0, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42};
        System.out.println("Corrupted message: "+ Arrays.toString(corruptedMessage));
        int[] correctedMessage = rsDecoder.simpleDecode(corruptedMessage);
        System.out.println("Decoded message: "+ Arrays.toString(correctedMessage));
    }
}
