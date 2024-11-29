package edu.pwr.niduc.reedsolomon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Transmitter {

    private final int q;
    private final Random rand;

    public Transmitter(int m) {
        this.q = (int) Math.pow(2,m);
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
        if (burstLength > encodedMessage.length) {
            throw new IllegalArgumentException("Number of errors is greater than number of encoded bytes");
        }

        int lastPossiblePosition = encodedMessage.length - burstLength - 1;
        int burstStartingPosition = rand.nextInt(lastPossiblePosition);

        int[] outputMessage = Arrays.copyOf(encodedMessage, encodedMessage.length);
        for (int i = 0; i < burstLength; i++) {
            outputMessage[burstStartingPosition] = rand.nextInt(q-1);
            burstStartingPosition++;
        }

        return outputMessage;
    }
}