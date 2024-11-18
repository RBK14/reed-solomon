    package edu.pwr.niduc.reedsolomon;

    import edu.pwr.niduc.util.OutOfGaloisFieldException;

    import java.util.*;

    public class GaloisField {

        private final int m;
        private final int q;
        private final Map<Integer, int[]> galoisFieldElements;

        public GaloisField(int m) {
            this.m = m;
            this.q = (int) Math.pow(2,m);
            this.galoisFieldElements = generateGaloisFieldElements();
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

        public int[] dividePolynomials(int[] dividend, int[] divisor){
            if (divisor.length == 0 || (divisor.length == 1 && divisor[0] == 0)) {
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

        private int multiplyAlpha(int alpha1, int alpha2) {
            validateElements(alpha1, alpha2);

            if (alpha1 == 0 || alpha2 == 0) {
                return 0;
            }
            return 1 + ((alpha1 + alpha2 - 2) % (q - 1));
        }

        private int addAlpha(int alpha1, int alpha2) {
            validateElements(alpha1, alpha2);

            int[] result = new int[m];

            int[] vector1 = galoisFieldElements.get(alpha1);
            int[] vector2 = galoisFieldElements.get(alpha2);

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

        private int divideAlpha(int alpha1, int alpha2) {
            validateElements(alpha1, alpha2);

            if (alpha2 == 0) {
                throw new IllegalArgumentException("Division by zero is not defined in Galois Field.");
            }

            if (alpha1 == 0) {
                return 0; // 0 / alpha2 = 0
            }

            return 1 + ((alpha1 - alpha2 + (q - 1)) % (q - 1));
        }

        private Map<Integer, int[]> generateGaloisFieldElements() {
            Map<Integer, int[]> elements = new HashMap<>();

            // Generowanie sekwencji startowej
            List<Integer> startingSequence = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                if (i == 0) {
                    startingSequence.add(1);
                } else {
                    startingSequence.add(0);
                }
            }

            List<Integer> sequence = new ArrayList<>(startingSequence);

            // Generowanie sekwencji
            for (int i = 0; i < q; i++) {
                sequence.add(sequence.get(i) ^ sequence.get(i+1));
            }

            // Dodanie wektora zerowego na początek listy elementów pola Galois
            elements.put(0,new int[m]);

            // Odczytanie postaci wektorowej elementów z sekwencji pseudolosowej
            for (int alpha = 0; alpha < q - 1; alpha++) {
                List<Integer> subSequence = sequence.subList(alpha, alpha + m);
                int[] subSequenceArray = subSequence.stream().mapToInt(Integer::intValue).toArray();
                elements.put(alpha + 1, subSequenceArray);
            }
            return elements;
        }

        private int getDegree(int[] polynomial) {
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
    }
