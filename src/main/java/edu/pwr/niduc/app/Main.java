package edu.pwr.niduc.app;

import edu.pwr.niduc.reedsolomon.RSEncoder;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // Parametry kodu RS(63,43)
        int m = 6;
        int t = 10;

        RSEncoder rsEncoder = new RSEncoder(m,t);
        int[] messageBinary = rsEncoder.convertMessageToBinary("Hello World!");
        int[] messagePolynomial = rsEncoder.convertBinaryToPolynomial(messageBinary);
        System.out.println(Arrays.toString(messagePolynomial));
        System.out.println(Arrays.toString(Arrays.stream(rsEncoder.encodeMessage(messagePolynomial)).toArray()));
    }
}
