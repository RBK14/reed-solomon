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
        int trialNum = 400;

        // Maksymalna liczba błędów
        int maxBurstLength = 60;

        // Mapa wyników
        Map<Integer, Double> scores = new HashMap<>();

        for (int i = 0; i <= maxBurstLength; i++) {
            // Liczba poprawionych wiadomości
            int corrected = 0;
            for (int j = 0; j < trialNum; j++) {
                int[] encodedMessage = encoder.encodeMessage(inputMessage);
                int[] corruptedMessage = transmitter.simulateBurstErrors(encodedMessage, i);
                int[] outputMessage = decoder.simpleDecode(corruptedMessage);
                if (Arrays.equals(encodedMessage, outputMessage)) {
                    corrected++;
                }
            }
            double score = corrected / (double) trialNum;
            scores.put(i, score);
        }
        saveScoreToFile("burst", scores);
    }

    private static void saveScoreToFile(String type, Map<Integer, Double> scores) {
        String filePath = "./data/" + type + "_results.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("errors;score");
            for (Map.Entry<Integer, Double> entry : scores.entrySet()) {
                writer.newLine();
                writer.write(entry.getKey() + ";" + entry.getValue());
            }
            System.out.println("Scores saved successfully");
        } catch (IOException e) {
            System.err.println("Cannot save scores to file: " + e.getMessage());
        }
    }
}
