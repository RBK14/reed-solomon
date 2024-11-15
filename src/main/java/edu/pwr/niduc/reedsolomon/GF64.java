package edu.pwr.niduc.reedsolomon;

import edu.pwr.niduc.util.OutOfGaloisFieldException;

public class GF64 {

    private static final int GF_POWER = 6;

    public static int[] add(int[] poly1, int[] poly2) {
        // Sprawdzanie, czy wielomiany należą do pola galois
        if (poly1.length > GF_POWER || poly2.length > GF_POWER) {
            throw new OutOfGaloisFieldException("One of the polynomial is outside the Galois Field (2" + GF_POWER + ")");
        }

        // Wyrównanie długości wielomianów
        if (poly1.length < GF_POWER) poly1 = alignPoly(poly1);
        if (poly2.length < GF_POWER) poly2 = alignPoly(poly2);

        // Dodawanie wielomianów
        int[] result = new int[GF_POWER];
        for (int i = 0; i < result.length; i++) {
            result[i] = poly1[i] ^ poly2[i];
        }
        return result;
    }

    private static int[] alignPoly (int[] poly) {
        int[] tempPoly = new int[GF_POWER];
        int startIndex = GF_POWER - poly.length;
        System.arraycopy(poly, 0, tempPoly, startIndex, poly.length);
        return tempPoly;
    }
}
