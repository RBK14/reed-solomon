package edu.pwr.niduc;

import edu.pwr.niduc.reedsolomon.RSDecoder;
import edu.pwr.niduc.reedsolomon.RSEncoder;
import edu.pwr.niduc.util.Transmitter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class ScoreCalculator {

    public static void main(String[] args) {

        // Parametry kodu RS(63,43)
        final int m = 6;
        final int t = 10;
        final int q = (int) Math.pow(2, m);

        Random rand = new Random();

        RSEncoder encoder = new RSEncoder(m,t);
        RSDecoder decoder = new RSDecoder(m,t);
        Transmitter transmitter = new Transmitter(m);

        // Generowanie losowej wiadomości
        int[] inputMessage = new int[(q - 1) - (2 * t)];
        for (int i = 0; i < inputMessage.length; i++) {
            inputMessage[i] = rand.nextInt(q);
        }

        int[] errors = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ,11 ,12};
        int[] trails = new int[]{5000, 5000, 5000, 10000, 10000, 10000, 10000, 10000, 30000, 30000, 50000, 5000, 5000};

        // Liczba próbek symulacji
        int trialNum = 50000;

        // Maksymalna liczba błędów
        int errorsMaxNum = 10;

        // Mapa wyników
        String filePath = "./data/symbol-random-test-results.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("errors;corrected;factor");

            for (int i = 0; i < errors.length; i++) {
                // Liczba poprawionych wiadomości
                int corrected = 0;
                for (int j = 0; j < trails[i]; j++) {
                    int[] encodedMessage = encoder.encodeMessage(inputMessage);
                    int[] corruptedMessage = transmitter.simulateRandomErrors(encodedMessage, errors[i]);
                    int[] outputMessage = decoder.simpleDecode(corruptedMessage);
                    if (Arrays.equals(encodedMessage, outputMessage)) {
                        corrected++;
                    }

                }
                double score = corrected / (double) trails[i];
                writer.newLine();
                writer.write(errors[i] + ";" + corrected + ";" + score);
            }
            System.out.println("Scores saved successfully");
        } catch (IOException e) {
                System.err.println("Cannot save scores to file: " + e.getMessage());
        }
    }
}
