package edu.pwr.niduc.reedsolomon;

import edu.pwr.niduc.util.OutOfGaloisFieldException;

import java.util.*;

public class GaloisField {

    private final int m;
    public final int q;
    public final int alpha;
    private final Map<Integer, int[]> galoisFieldElements;

    public GaloisField(int m) {
        this.m = m;
        this.q = (int) Math.pow(2,m);
        this.galoisFieldElements = generateGaloisFieldElements();
        this.alpha = convertToMultiplicative(new int[]{0,0,0,0,1,0});
    }

    public int[] multiplyPolynomials(int[] poly1, int[] poly2) {
        if (Arrays.equals(poly1, new int[poly1.length]) || Arrays.equals(poly2, new int[poly2.length])) {
            return new int[1];
        }

        int[] result = new int[poly1.length + poly2.length - 1];

        for (int i = 0; i < poly1.length; i++) {
            for (int j = 0; j < poly2.length; j++) {
                result[i + j] = addAlpha(result[i+j], multiplyAlpha(poly1[i], poly2[j]));
            }
        }
        return result;
    }
    public int pow(int alpha, int exponent) {
        validateElement(alpha);

        // Handle edge cases
        if (exponent == 0) {
            return 1; // Any element to the power of 0 is 1
        }
        if (alpha == 0) {
            return 0; // 0 to any positive power is 0
        }

        // Reduce exponent modulo (q - 1), leveraging the cyclic property of Galois fields
        exponent = exponent % (q - 1);

        int result = 1; // Start with the multiplicative identity
        for (int i = 0; i < exponent; i++) {
            result = multiplyAlpha(result, alpha);
        }

        return result;
    }



    public int[] addPolynomials(int[] poly1, int[] poly2) {
        int[] result = new int[Math.max(poly1.length, poly2.length)];

        for (int i = 0; i < result.length; i++) {
            if (poly1.length - 1 < i) {
                result[i] = poly2[i];
            } else if (poly2.length - 1 < i) {
                result[i] = poly1[i];
            } else {
                result[i] = addAlpha(poly1[i], poly2[i]);
            }
        }
        return result;
    }

    public int[] dividePolynomials(int[] dividend, int[] divisor) {
        if (divisor == null || divisor.length == 0 || (divisor.length == 1 && divisor[0] == 0)) {
            throw new IllegalArgumentException("Divisor cannot be zero.");
        }

        int[] remainder = Arrays.copyOf(dividend, dividend.length);
        int divisorDegree = getDegree(divisor);
        int divisorLeadingCoeff = divisor[divisorDegree];

        for (int i = getDegree(dividend); i >= divisorDegree; i--) {
            if (remainder[i] == 0) continue;

            int coefficient = divideAlpha(remainder[i], divisorLeadingCoeff);

            for (int j = 0; j <= divisorDegree; j++) {
                remainder[i - j] = addAlpha(remainder[i - j], multiplyAlpha(coefficient, divisor[divisorDegree - j]));
            }
        }
        return trimZeros(remainder);
    }

    public int convertToMultiplicative(int[] vector) {
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

    public int[] convertToVector(int alpha) {
        return galoisFieldElements.get(alpha);
    }

    protected int multiplyAlpha(int alpha1, int alpha2) {
        // Debugowanie: Wyświetlanie wejściowych wartości alpha

        validateElements(alpha1, alpha2);

        // Mnożenie przez zero zwraca zero
        if (alpha1 == 0 || alpha2 == 0) {
            return 0;
        }

        int result = 1 + ((alpha1 + alpha2 - 2) % (q - 1));

        // Debugowanie: Wyświetlanie wyniku

        return result;
    }


    public int addAlpha(int alpha1, int alpha2) {
        validateElements(alpha1, alpha2);

        // zawsze maja dlugosc 6
        int[] vector1 = galoisFieldElements.get(alpha1);
        int[] vector2 = galoisFieldElements.get(alpha2);

        int[] result = new int[m];

        // XOR
        for (int i = 0; i < m; i++) {
            result[i] = vector1[i] ^ vector2[i];
        }

        // Konwersja na multiplikatywny
        Integer resultAlpha = findAlphaForVector(result);
        if (resultAlpha == null) {
            throw new RuntimeException("Result vector does not map to a valid Galois field element.");
        }

        return resultAlpha;
    }

    private Integer findAlphaForVector(int[] vector) {
        for (Map.Entry<Integer, int[]> entry : galoisFieldElements.entrySet()) {
            if (Arrays.equals(entry.getValue(), vector)) {
                return entry.getKey();
            }
        }
        return null;
    }


    int divideAlpha(int alpha1, int alpha2) {
        validateElements(alpha1, alpha2);

        if (alpha2 == 0) {
            throw new ArithmeticException("Division by zero is not defined in Galois Field.");
        }

        if (alpha1 == 0) {
            return 0; // 0 / alpha2 = 0
        }

        return 1 + ((alpha1 - alpha2 + (q - 1)) % (q - 1));
    }

    private Map<Integer, int[]> generateGaloisFieldElements() {
        Map<Integer, int[]> elements = new HashMap<>();

        // Definicja nierozkładalnego wielomianu dla m = 6: x^6 + x + 1
        int irreduciblePolynomial = 0b1000011; // x^6 + x + 1 w postaci binarnej

        // Wektor zerowy
        elements.put(0, new int[m]);

        // Startujemy od "1", czyli 0b000001 w postaci binarnej
        int currentElement = 1; // Wartość odpowiadająca α^0

        for (int i = 1; i < q; i++) {
            // Zamieniamy bieżący element na wektor binarny
            elements.put(i, convertToBinaryVector(currentElement, m));

            // Obliczamy następny element
            currentElement <<= 1; // Mnożenie przez α (przesunięcie w lewo)
            if ((currentElement & (1 << m)) != 0) { // Jeśli przekraczamy stopień m
                currentElement ^= irreduciblePolynomial; // Redukcja modularna
            }
        }

        return elements;
    }

    // Funkcja pomocnicza: zamiana liczby na wektor binarny o długości m
    private int[] convertToBinaryVector(int value, int length) {
        int[] vector = new int[length];
        for (int i = 0; i < length; i++) {
            vector[length - i - 1] = (value >> i) & 1; // Ekstrakcja i-tego bitu
        }
        return vector;
    }


    int getDegree(int[] polynomial) {
        for (int i = polynomial.length - 1; i >= 0; i--) {
            if (polynomial[i] != 0) {
                return i;
            }
        }
        return 0;
    }

    private int[] trimZeros(int[] polynomial) {
        int degree = getDegree(polynomial);
        return Arrays.copyOf(polynomial, degree + 1);
    }

    private void validateElements(int alpha1, int alpha2) {
        if ((alpha1 < 0 || alpha1 > q-1) || (alpha2 < 0 || alpha2 > q-1)) {
            throw new OutOfGaloisFieldException("One of the elements in not Galois Field element");
        }
    }
    private void validateElement(int alpha) {
        if (alpha < 0 || alpha >= q) {
            throw new OutOfGaloisFieldException("Alpha value is out of range.");
        }
    }


    // Evaluates a polynomial at a given point in the Galois field
    public int evaluatePolynomial(int[] polynomial, int x) {
        validateElement(x);

        int result = 0;
        for (int i = 0; i < polynomial.length; i++) {
            int term = multiplyAlpha(polynomial[i], pow(x, i));
            result = addAlpha(result, term);
        }

        return result;
    }

    // Evaluates the derivative of a polynomial at a given point in the Galois field
    public int evaluatePolynomialDerivative(int[] polynomial, int x) {
        validateElement(x);

        int result = 0;
        for (int i = 1; i < polynomial.length; i++) {
            // Multiply by the degree of the term (i)
            if (i % 2 == 1) { // Only include terms with odd degrees in characteristic 2 fields
                int term = multiplyAlpha(polynomial[i], pow(x, i - 1));
                result = addAlpha(result, term);
            }
        }

        return result;
    }
    public int[] getQuotient(int[] dividend, int[] divisor) {
        if (divisor == null || divisor.length == 0 || (divisor.length == 1 && divisor[0] == 0)) {
            throw new IllegalArgumentException("Divisor cannot be zero.");
        }

        if (getDegree(dividend) < getDegree(divisor)) {
            return new int[]{0}; // Iloraz jest zerowy, gdy stopień dzielnika przekracza stopień dzielnej
        }

        int[] remainder = Arrays.copyOf(dividend, dividend.length);
        int divisorDegree = getDegree(divisor);
        int divisorLeadingCoeff = divisor[divisorDegree];

        int[] quotient = new int[getDegree(dividend) - divisorDegree + 1]; // Stopień ilorazu

        for (int i = getDegree(dividend); i >= divisorDegree; i--) {
            if (remainder[i] == 0) continue;

            int coefficient = divideAlpha(remainder[i], divisorLeadingCoeff);
            quotient[i - divisorDegree] = coefficient;

            for (int j = 0; j <= divisorDegree; j++) {
                remainder[i - j] = addAlpha(remainder[i - j], multiplyAlpha(coefficient, divisor[divisorDegree - j]));
            }
        }

        return quotient;
    }

    public static void main(String[] args) {
        GaloisField gf = new GaloisField(6);
        int[] divided = gf.getQuotient(new int[] {1,1,1,0,1,1}, new int[]{1,1,0,1,0,1});
        int[] remainder = gf.dividePolynomials(new int[] {1,1,1,0,1,1}, new int[]{1,1,0,1,0,1});
        System.out.println("Quotient: "+Arrays.toString(divided)+", Remainder: "+ Arrays.toString(remainder));
    }

}
