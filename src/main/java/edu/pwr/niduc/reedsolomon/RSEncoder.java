package edu.pwr.niduc.reedsolomon;

import edu.pwr.niduc.util.InvalidCorrectionValueException;
import edu.pwr.niduc.util.MessageTooLongException;

import java.util.Arrays;

public class RSEncoder {

    private final int m;
    private final int t;
    private final int q;
    private final GaloisField galoisField;
    private final GeneratingPolynomial generatingPolynomial;

    public RSEncoder(int m, int t) {
        this.m = m;
        this.t = t;
        this.q = (int) Math.pow(2, m);
        this.galoisField = new GaloisField(m);
        this.generatingPolynomial = new GeneratingPolynomial(m,t);
    }

    public int[] encodeMessage(int[] messagePolynomial) {
        if (t < 1) {
            throw new InvalidCorrectionValueException("Correction value t must be greater or equal to 1");
        }
        messagePolynomial = padMessageWithZeros(messagePolynomial);
        System.out.println("Padded message: " + Arrays.toString(messagePolynomial));

        // Generowanie wielomianu generującego
        int[] generatingPolynomial = this.generatingPolynomial.generatePolynomial();

        // Obliczanie n-k
        int n = messagePolynomial.length + 2 * t; // n = k + r (r = 2t)
        int k = messagePolynomial.length;
        int power = n - k;

        // Przesunięcie wiadomości przez mnożenie przez x^(n-k)
        int[] shiftedMessage = new int[messagePolynomial.length + power];
        System.arraycopy(messagePolynomial, 0, shiftedMessage, power, messagePolynomial.length);

        // Obliczenie reszty (r(x))
        int[] remainder = galoisField.dividePolynomials(shiftedMessage, generatingPolynomial);

        // Połączenie wiadomości z resztą
        return galoisField.addPolynomials(remainder, shiftedMessage);
    }

    private int[] padMessageWithZeros(int[] messagePolynomial) {
        // Sprawdzanie, czy długość wiadomości już odpowiada docelowej długości
        int desiredLength = (q - 1) - 2 * t;
        if (messagePolynomial.length >= desiredLength) {
            return messagePolynomial; // Jeśli wiadomość jest równa lub dłuższa, zwróć oryginalną
        }

        // Wyliczenie liczby zer do dodania
        int zerosToAdd = desiredLength - messagePolynomial.length;

        // Tworzenie nowej tablicy z odpowiednią liczbą zer
        int[] paddedMessage = new int[desiredLength];
        System.arraycopy(messagePolynomial, 0, paddedMessage, zerosToAdd, messagePolynomial.length);

        return paddedMessage;
    }

    public int[] convertMessageToBinary(int message) {
        // Obliczanie minimalnej liczby bitów potrzebnych do reprezentacji wiadomości
        int bitLength = Integer.toBinaryString(message).length();
        validateMessageLength(bitLength);

        // Zaokrąglenie do najbliższej wielokrotności m
        bitLength = ((bitLength + m-1) / m) * m;

        // Wypełnienie tablicy bitami
        int[] binaryArray = new int[bitLength];
        for (int i = 0; i < bitLength; i++) {
            binaryArray[i] = (message >> i) & 1;
        }
        return binaryArray;
    }

    public int[] convertMessageToBinary(String message) {
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

    public int[] convertBinaryToPolynomial(int[] messageBinary) {
        int[] polynomial = new int[messageBinary.length / m];

        // Zamiana ciągu m znaków binarnych na postać multiplikatywna alfa
        for (int i = 0; i < polynomial.length; i++) {
            int k = m * i;
            int[] subArray = Arrays.copyOfRange(messageBinary, k, k + m);
            polynomial[i] = galoisField.convertToMultiplicative(subArray);
        }
        return polynomial;
    }


    private void validateMessageLength(int bitLength) {
        int maxLength = (((int) Math.pow(2,m) - 1) - 2*t) * m;
        if (bitLength > maxLength) {
            throw new MessageTooLongException("Message length must be less than or equal to " + maxLength);
        }
    }

}
