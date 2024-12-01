package edu.pwr.niduc.util;

import edu.pwr.niduc.reedsolomon.GaloisField;

import java.util.Arrays;

public class MessageConverter {

    private final int m;
    private final int t;
    private final GaloisField galoisField;

    public MessageConverter(int m, int t) {
        this.m = m;
        this.t = t;
        this.galoisField = new GaloisField(m);
    }

    public int[] convertBinaryToPolynomial(String message) {
        int[] messageBinary = convertMessageToBinary(message);
        int[] polynomial = new int[messageBinary.length / m];

        // Zamiana ciągu m znaków binarnych na postać multiplikatywna alfa
        for (int i = 0; i < polynomial.length; i++) {
            int k = m * i;
            int[] subArray = Arrays.copyOfRange(messageBinary, k, k + m);
            polynomial[i] = galoisField.convertToMultiplicative(subArray);
        }
        return polynomial;
    }

    private int[] convertMessageToBinary(String message) {
        // Przekształcenie wiadomości na binarną reprezentację całkowitą
        StringBuilder binaryString = new StringBuilder();
        for (char c : message.toCharArray()) {
            String binaryChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binaryString.append(binaryChar); // Dodanie każdego znaku w postaci binarnej
        }

        // Długość binarnej reprezentacji
        int bitLength = binaryString.length();
        validateMessageLength(bitLength);

        // Zaokrąglenie do najbliższej wielokrotności m
        bitLength = ((bitLength + m - 1) / m) * m;

        // Wypełnienie tablicy bitowej, jeśli bitLength > binaryString.length(), reszta tablicy zostaje wypełniona zerami
        int[] binaryArray = new int[bitLength];
        for (int i = 0; i < binaryString.length(); i++) {
            binaryArray[i] = binaryString.charAt(i) - '0'; // Konwersja char ('0' lub '1') na int
        }
        return binaryArray;
    }

    public String convertBinaryToMessage(int[] messageBinary) {
        return null;
    }

    public int[] convertPolynomialToBinary(int[] messagePolynomial) {
        return null;
    }

    private void validateMessageLength(int bitLength) {
        int maxLength = (((int) Math.pow(2,m) - 1) - 2*t) * m;
        if (bitLength > maxLength) {
            throw new MessageTooLongException("Message length must be less than or equal to " + maxLength);
        }
    }
}
