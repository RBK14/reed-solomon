package edu.pwr.niduc;

import edu.pwr.niduc.reedsolomon.RSDecoder;
import edu.pwr.niduc.reedsolomon.RSEncoder;
import edu.pwr.niduc.reedsolomon.Transmitter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ScoreCalculator {

    public static void main(String[] args) {

        // Parametry kodu RS(63,43)
        final int m = 6;
        final int t = 10;

        Random rand = new Random();

        RSEncoder encoder = new RSEncoder(m,t);
        RSDecoder decoder = new RSDecoder(m,t);
        Transmitter transmitter = new Transmitter(m);

        // Generowanie losowej wiadomości
        int[] inputMessage = new int[43];
        for (int i = 0; i < inputMessage.length; i++) {
            inputMessage[i] = rand.nextInt(64);
        }

        // Liczba próbek symulacji
        int trialNum = 800;

        // Maksymalna liczba błędów
        int errorsMaxNum = 15;

        // Mapa wyników
        String filePath = "./data/symbol-random-results.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("errors;corrected;factor");

            for (int i = 0; i <= errorsMaxNum; i++) {
                // Liczba poprawionych wiadomości
                int corrected = 0;
                for (int j = 0; j < trialNum; j++) {
                    int[] encodedMessage = encoder.encodeMessage(inputMessage);
                    int[] corruptedMessage = transmitter.simulateRandomErrors(encodedMessage, i);
                    int[] outputMessage = decoder.simpleDecode(corruptedMessage);
                    if (Arrays.equals(encodedMessage, outputMessage)) {
                        corrected++;
                    }

                }
                double score = corrected / (double) trialNum;
                writer.newLine();
                writer.write(i + ";" + corrected + ";" + score);
            }
            System.out.println("Scores saved successfully");
        } catch (IOException e) {
                System.err.println("Cannot save scores to file: " + e.getMessage());
        }
    }
}
