package edu.pwr.niduc.exe;

import edu.pwr.niduc.reedsolomon.RSEncoder;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] message = {
                3, 15, 27, 10, 19, 5, 22, 7, 31, 12,
                45, 2, 14, 28, 8, 35, 6, 18, 23, 9,
                50, 4, 16, 25, 13, 33, 11, 20, 37, 1,
                29, 21, 17, 26, 30, 38, 24, 32, 40, 41,
                39, 36, 34
        };


        System.out.println(Arrays.toString(Arrays.stream(RSEncoder.encode_message(message, 10)).toArray()));
    }
}
