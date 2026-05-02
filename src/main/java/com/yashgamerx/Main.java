package com.yashgamerx;

import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.Scanner;

@Log
public class Main {

    public static void main(String[] args) {

        var in = new Scanner(System.in);

        System.out.print("""
            Enter 1 for SDES
            Enter 2 for Modified SDES
            Enter 3 for Brute Force SDES
            """);
        var choice = in.nextInt();
        in.nextLine(); // consume newline

        switch (choice) {

            case 1: {
                System.out.print("Enter 10-bit key (e.g. 1010000010): ");
                var key = parseBinaryInput(in.nextLine(), 10);

                System.out.print("Enter 8-bit plaintext (e.g. 10111101): ");
                var plainText = parseBinaryInput(in.nextLine(), 8);

                log.info("Plain Text: " + Arrays.toString(plainText));
                log.info("Key: " + Arrays.toString(key));

                var cipherText = SDES.encrypt(plainText, key);
                log.info("Cipher Text: " + Arrays.toString(cipherText));

                var decryptedText = SDES.decrypt(cipherText, key);
                log.info("Decrypted Text: " + Arrays.toString(decryptedText));
            } break;

            case 2: {
                System.out.print("Enter 10-bit key (e.g. 1010000010): ");
                var key = parseBinaryInput(in.nextLine(), 10);

                System.out.print("Enter 8-bit plaintext (e.g. 10111101): ");
                var plainText = parseBinaryInput(in.nextLine(), 8);

                log.info("Plain Text: " + Arrays.toString(plainText));
                log.info("Key: " + Arrays.toString(key));

                var cipherText = ModifiedSDES.encrypt(plainText, key);
                log.info("Cipher Text: " + Arrays.toString(cipherText));

                var decryptedText = ModifiedSDES.decrypt(cipherText, key);
                log.info("Decrypted Text: " + Arrays.toString(decryptedText));
            } break;

            case 3: {
                System.out.print("Enter known plaintext (8-bit): ");
                var knownPlain = parseBinaryInput(in.nextLine(), 8);

                System.out.print("Enter known ciphertext (8-bit): ");
                var knownCipher = parseBinaryInput(in.nextLine(), 8);

                bruteForceSDES(knownPlain, knownCipher);
            } break;

            default: {
                log.info("Invalid choice");
            }
        }
    }

    private static void bruteForceSDES(int[] plainText, int[] targetCipherText) {
        for (int i = 0; i < 1024; i++) {

            int[] key = intTo10BitArray(i);

            int[] result = SDES.encrypt(plainText, key);

            if (Arrays.equals(result, targetCipherText)) {
                System.out.println("Key found: " + Arrays.toString(key));
            }
        }
    }

    private static int[] intTo10BitArray(int value) {
        int[] bits = new int[10];
        for (int i = 9; i >= 0; i--) {
            bits[i] = value & 1;
            value >>= 1;
        }
        return bits;
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