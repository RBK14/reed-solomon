package edu.pwr.niduc.reedsolomon;

import edu.pwr.niduc.util.InvalidCorrectionValueException;

import java.util.Arrays;

public class RSEncoder {

    private final int t;
    private final int q;
    private final GaloisField galoisField;
    private final GeneratingPolynomial generatingPolynomial;

    public RSEncoder(int m, int t) {
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
        log("Padding message with zeros...");
        System.out.println("Padded message: " + Arrays.toString(messagePolynomial));

        // Generowanie wielomianu generującego
        int[] generatingPolynomial = this.generatingPolynomial.generatePolynomial();

        // Obliczanie n-k
        int n = messagePolynomial.length + 2 * t; // n = k + r (r = 2t)
        int k = messagePolynomial.length;
        int power = n - k;

        log("Encoding message...");

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

    private void log(String message) {
        System.out.println("[RSEncoder] " + message);
    }
}
