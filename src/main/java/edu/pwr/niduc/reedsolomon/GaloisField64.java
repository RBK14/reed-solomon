package edu.pwr.niduc.reedsolomon;

import edu.pwr.niduc.util.OutOfGaloisFieldException;

import java.util.*;

public class GaloisField64 {

    private static final int GF_POWER = 6;                       // m = 6
    private static final int GF_SIZE = 64;                       // q = 64
    private static final int[] PRIMITIVE_POLY = {1,1,0,0,0,0,1}; // p(x) = x^6 + x + 1

    private final Map<Integer, int[]> galoisFieldElements;

    public GaloisField64() {
        this.galoisFieldElements = generateGaloisFieldElements();
    }

    private Map<Integer, int[]> generateGaloisFieldElements() {
        Map<Integer, int[]> elements = new HashMap<>();
        List<Integer> sequence = new ArrayList<>(Arrays.asList(1,0,0,0,0,0));

        // Generowanie sekwencji
        for (int i = 0; i < GF_SIZE; i++) {
            sequence.add(sequence.get(i) ^ sequence.get(i+1));
        }

        // Odczytanie postaci wektorowej elementów z sekwencji pseudolosowej
        for (int alpha = 0; alpha < GF_SIZE - 1; alpha++) {
            List<Integer> subSequence = sequence.subList(alpha, alpha + GF_POWER);
            int[] subSequenceArray = subSequence.stream().mapToInt(Integer::intValue).toArray();
            elements.put(alpha, subSequenceArray);
        }

        // Dodanie wektora zerowego na ostatniej pozycji możliwej pozycji (GF_SIZE)
        elements.put(GF_SIZE, new int[GF_POWER]);

        return elements;
    }

    public int[] multiply(int alpha1, int alpha2) {
        // Sprawdzanie, wielomiany są elementami pola Galois
        if (alpha1 > GF_SIZE - 2 && alpha1 != GF_SIZE) {
            throw new OutOfGaloisFieldException("Alpha^" + alpha1 + " is not Galois Field (2" + GF_POWER + ") element");
        }
        if (alpha2 > GF_SIZE - 2 && alpha2 != GF_SIZE) {
            throw new OutOfGaloisFieldException("Alpha^" + alpha2 + " is not Galois Field (2" + GF_POWER + ") element");
        }

        // Mnożenie przez 0
        if (alpha1 == GF_SIZE || alpha2 == GF_SIZE) return new int[GF_POWER];

        // Mnożenie postaci multiplikatywnych wielomianów
        int result = (alpha1 + alpha2) % (GF_SIZE - 1);

        return galoisFieldElements.get(result);
    }

    public int[] add(int[] poly1, int[] poly2) {
        // Sprawdzanie, czy wielomiany są elementami pola Galois
        if (poly1.length > GF_POWER || poly2.length > GF_POWER) {
            throw new OutOfGaloisFieldException("One of the polynomials is not Galois Field (2" + GF_POWER + ") element");
        }

        int[] result = new int[GF_POWER];

        // Dodawanie cyfr na odpowiadających pozycjach
        for (int i = 0; i < Math.max(poly1.length, poly2.length); i++) {
            if (i > poly1.length - 1) {
                result[i] = poly2[i];
            } else if (i > poly2.length - 1) {
                result[i] = poly1[i];
            } else {
                result[i] = poly1[i] + poly2[i];
            }
        }
        return result;
    }
}
