package edu.pwr.niduc;

import edu.pwr.niduc.reedsolomon.*;
import edu.pwr.niduc.util.Transmitter;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        // Parametry kodu RS(63,43)
        final int m = 6;
        final int t = 10;

        RSEncoder rsEncoder = new RSEncoder(m,t);
        RSDecoder rsDecoder = new RSDecoder(m,t);
        RSFullDecoder rsFullDecoder = new RSFullDecoder(m,t);
        Transmitter transmitter = new Transmitter(m);

        int[] messagePolynomial = new int[]{5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42};
        System.out.println("Input message: " + Arrays.toString(messagePolynomial));
        int [] encodeMessage = rsEncoder.encodeMessage(messagePolynomial);
        System.out.println("Encoded message:   "+ Arrays.toString(encodeMessage));
        int[] corruptedMessage = transmitter.simulateRandomErrors(encodeMessage, 2);
        System.out.println("Corrupted message: "+ Arrays.toString(corruptedMessage));
        int[] decodedMessage = rsDecoder.simpleDecode(corruptedMessage);
        System.out.println("Decoded message: "+ Arrays.toString(decodedMessage));
        decodedMessage = rsFullDecoder.decode(corruptedMessage);
        System.out.println("Full decoded message: " + Arrays.toString(decodedMessage));
        for (int i = 0; i < decodedMessage.length; i++) {
            if(decodedMessage[i] != encodeMessage[i]){
                System.out.println("Bład "+i+" Decoded = "+decodedMessage[i]+" encoded = "+encodeMessage[i]);
            }
        }
    }
}
