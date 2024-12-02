package edu.pwr.niduc.reedsolomon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Transmitter {

    private  final int m;
    private final int q;
    private final GaloisField galoisField;
    private final Random rand;

    public Transmitter(int m) {
        this.m = m;
        this.q = (int) Math.pow(2,m);
        this.galoisField = new GaloisField(m);
        this.rand = new Random();
    }

    public int[] simulateRandomErrors(int[] encodedMessage, int errorsNum) {
        if (errorsNum > encodedMessage.length) {
            throw new IllegalArgumentException("Number of errors is greater than number of encoded bytes");
        }

        List<Integer> positionsToSwap = new ArrayList<>();
        while (errorsNum > 0) {
            int position = rand.nextInt(encodedMessage.length);
            if (!positionsToSwap.contains(position)) {
                positionsToSwap.add(position);
                errorsNum--;
            }
        }

        log("Simulating random errors...");

        int[] outputMessage = Arrays.copyOf(encodedMessage, encodedMessage.length);
        for (Integer position : positionsToSwap) {
            int error;
            do {
                error = rand.nextInt(q-1);
            } while (error == outputMessage[position]);

            outputMessage[position] = error;
        }

        return outputMessage;
    }

    public int[] simulateBurstErrors(int[] encodedMessage, int burstLength) {
        if (burstLength > encodedMessage.length * m) {
            throw new IllegalArgumentException("Number of errors is greater than number of encoded bytes");
        }

        int[] binaryMessage = new int[encodedMessage.length * m];

        for (int i = 0; i < encodedMessage.length; i++) {
            int alpha = encodedMessage[i];
            int[] vector = galoisField.convertToVector(alpha);
            System.arraycopy(vector, 0, binaryMessage, i * m, vector.length);
        }

        int lastPossiblePosition = binaryMessage.length - burstLength - 1;
        int burstStartingPosition = rand.nextInt(lastPossiblePosition);

        log("Simulating burst errors...");

        for (int i = 0; i < burstLength; i++) {
            if (binaryMessage[burstStartingPosition] == 1) {
                binaryMessage[burstStartingPosition] = 0;
            } else {
                binaryMessage[burstStartingPosition] = 1;
            }
            burstStartingPosition++;
        }

        int[] outputMessage = new int[encodedMessage.length];

        for (int i = 0; i < outputMessage.length; i++) {
            int[] vector = new int[m];
            System.arraycopy(binaryMessage, i * m, vector, 0, m);
            int alpha = galoisField.convertToMultiplicative(vector);
            outputMessage[i] = alpha;
        }

        return outputMessage;
    }

    private void log(String message) {
        System.out.println("[Transmitter] " + message);
    }
}