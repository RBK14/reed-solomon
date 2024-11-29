package edu.pwr.niduc.app;

import edu.pwr.niduc.reedsolomon.GaloisField;
import edu.pwr.niduc.reedsolomon.GeneratingPolynomial;
import edu.pwr.niduc.reedsolomon.RSDecoder;
import edu.pwr.niduc.reedsolomon.RSEncoder;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // Parametry kodu RS(63,43)
        int m = 6;
        int t = 10;

        RSEncoder rsEncoder = new RSEncoder(m,t);
        RSDecoder rsDecoder = new RSDecoder(m,t);

        int[] mP = new int[]{ 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43
        };
        System.out.println(Arrays.toString(mP));
        int [] c = rsEncoder.encodeMessage(mP);
        GeneratingPolynomial generatingPolynomial = new GeneratingPolynomial(6,10);
        GaloisField gf = new GaloisField(6);
        System.out.println("wiadomosc zakodowana: "+ Arrays.toString(c));
        System.out.println("wielomian generacyjny: "+Arrays.toString(generatingPolynomial.generatePolynomial()));
        System.out.println("Dzielenie: "+Arrays.toString(gf.dividePolynomials(c,generatingPolynomial.generatePolynomial())));
        System.out.println("wiadomosc zakodowana: "+ Arrays.toString(c));
        int[] corrected = rsDecoder.simpleDecode(new int[]{39, 30, 36, 14, 32, 51, 5, 8, 60, 44, 44, 37, 37, 55, 51, 53, 16, 33, 16, 58, 0, 0, 0, 0, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43});
        System.out.println("Decoded: "+ Arrays.toString(corrected));
    }
}
