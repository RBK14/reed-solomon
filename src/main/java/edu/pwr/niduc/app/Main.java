package edu.pwr.niduc.app;

import edu.pwr.niduc.reedsolomon.GaloisField;
import edu.pwr.niduc.reedsolomon.GeneratingPolynomial;
import edu.pwr.niduc.reedsolomon.RSEncoder;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // Parametry kodu RS(63,43)
        int m = 6;
        int t = 10;

        RSEncoder rsEncoder = new RSEncoder(m,t);
        int[] messagePolynomial = new int[]{1,23,11};
        System.out.println(Arrays.toString(messagePolynomial));
        int [] c = rsEncoder.encodeMessage(messagePolynomial);
        GeneratingPolynomial generatingPolynomial = new GeneratingPolynomial(6,10);
        GaloisField gf = new GaloisField(6);
        System.out.println("wiadomosc zakodowana: "+ Arrays.toString(c));
        System.out.println("wielomian generacyjny: "+Arrays.toString(generatingPolynomial.generatePolynomial()));
        System.out.println("Dzielenie: "+Arrays.toString(gf.dividePolynomials(c,generatingPolynomial.generatePolynomial())));
        System.out.println("wiadomosc zakodowana: "+ Arrays.toString(c));

        System.out.println("Decoded: "+ Arrays.toString(rsEncoder.simpleDecode(new int[]{23, 38, 48, 1, 5, 60, 7, 47, 15, 8, 8, 61, 36, 33, 20, 40, 3, 63, 52, 25, 2, 22, 13})));
    }
}
