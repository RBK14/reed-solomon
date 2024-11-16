package edu.pwr.niduc.reedsolomon;

import edu.pwr.niduc.util.OutOfGaloisFieldException;

import java.util.*;

public class GaloisField64 {

    private static final int m = 6;                                         // m = 6
    private static final int q = (int) Math.pow(2,m);                       // q = 64

    private final Map<Integer, int[]> galoisFieldElements;

    public GaloisField64() {
        this.galoisFieldElements = generateGaloisFieldElements();
    }

    private Map<Integer, int[]> generateGaloisFieldElements() {
        Map<Integer, int[]> elements = new HashMap<>();
        List<Integer> sequence = new ArrayList<>(Arrays.asList(1,0,0));

        // Generowanie sekwencji
        for (int i = 0; i < q; i++) {
            sequence.add(sequence.get(i) ^ sequence.get(i+1));
        }

        // Odczytanie postaci wektorowej elementów z sekwencji pseudolosowej
        for (int alpha = 0; alpha < q - 1; alpha++) {
            List<Integer> subSequence = sequence.subList(alpha, alpha + m);
            int[] subSequenceArray = subSequence.stream().mapToInt(Integer::intValue).toArray();
            elements.put(alpha, subSequenceArray);
        }

        // Dodanie wektora zerowego na ostatniej pozycji możliwej pozycji (GF_SIZE)
        elements.put(q, new int[m]);

        return elements;
    }

    public int multiply(int alpha1, int alpha2) {
        // Sprawdzanie, wielomiany są elementami pola Galois
        if (alpha1 > q - 2 && alpha1 != q) {
            throw new OutOfGaloisFieldException("Alpha^" + alpha1 + " is not Galois Field (2" + m + ") element");
        }
        if (alpha2 > q - 2 && alpha2 != q) {
            throw new OutOfGaloisFieldException("Alpha^" + alpha2 + " is not Galois Field (2" + m + ") element");
        }

        // Mnożenie przez 0
        if (alpha1 == q || alpha2 == q) return q;

        // Mnożenie postaci multiplikatywnych wielomianów
        return (alpha1 + alpha2) % (q - 1);
    }

    public int add(int alpha1, int alpha2) {
        // TODO: Sprawdzanie, czy wielomiany są elementami pola Galois

        int[] vector1 = galoisFieldElements.get(alpha1);
        int[] vector2 = galoisFieldElements.get(alpha2);

        int[] result = new int[m];

        // Dodawanie cyfr na odpowiadających pozycjach
        for (int i = 0; i < Math.max(vector1.length, vector2.length); i++) {
            if (i > vector1.length - 1) {
                result[i] = vector2[i];
            } else if (i > vector2.length - 1) {
                result[i] = vector1[i];
            } else {
                result[i] = vector1[i] ^ vector2[i];
            }
        }
        return convertToMultiplicative(result);
    }

    private int convertToMultiplicative(int[] vector) {
        int alpha = -1;

        for (Map.Entry<Integer, int[]> entry : galoisFieldElements.entrySet()) {
            if (Arrays.equals(entry.getValue(), vector)) {
                alpha = entry.getKey();
                break;
            }
        }

        if (alpha == -1) {
            throw new RuntimeException("Cannot find alpha");
        }
        return alpha;
    }
}
