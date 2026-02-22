package com.yashgamerx;

import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.Scanner;

@Log
public class Main {

    static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.print("Enter 10-bit key (e.g. 1010000010): ");
        int[] key = parseBinaryInput(in.nextLine(), 10);

        System.out.print("Enter 8-bit plaintext (e.g. 10111101): ");
        int[] plainText = parseBinaryInput(in.nextLine(), 8);

        log.info("Plain Text: " + Arrays.toString(plainText));
        log.info("Key: " + Arrays.toString(key));

        int[] cipherText = SDES.encrypt(plainText, key);

        log.info("Cipher Text: " + Arrays.toString(cipherText));

        int[] decryptedText = SDES.decrypt(cipherText, key);

        log.info("Decrypted Text: " + Arrays.toString(decryptedText));
    }

    private static int[] parseBinaryInput(String input, int expectedLength) {

        if (input.length() != expectedLength || !input.matches("[01]+")) {
            throw new IllegalArgumentException(
                    "Input must be exactly " + expectedLength + " bits (0 or 1 only)."
            );
        }

        int[] bits = new int[expectedLength];

        for (int i = 0; i < expectedLength; i++) {
            bits[i] = input.charAt(i) - '0';
        }

        return bits;
    }
}