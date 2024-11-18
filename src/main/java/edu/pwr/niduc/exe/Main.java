package edu.pwr.niduc.exe;

import edu.pwr.niduc.reedsolomon.RSEncoder;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // Parametry kodu RS(63,43)
        int m = 6;
        int t = 10;

        RSEncoder rsEncoder = new RSEncoder(m,t);
        int[] message = rsEncoder.convertMessageToPolynomial("Hello World!");
        System.out.println(Arrays.toString(message));
        System.out.println(Arrays.toString(Arrays.stream(rsEncoder.encodeMessage(message)).toArray()));
    }
}
