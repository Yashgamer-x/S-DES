package com.yashgamerx;

import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.Scanner;

@Log
public class Main {

    static void main(String[] args) {

        var in = new Scanner(System.in);

        System.out.print("Enter 10-bit key (e.g. 1010000010): ");
        var key = parseBinaryInput(in.nextLine(), 10);

        System.out.print("Enter 8-bit plaintext (e.g. 10111101): ");
        var plainText = parseBinaryInput(in.nextLine(), 8);

        System.out.print("Enter 1 for SDES\nEnter 2 for S1~Modified SDES\n");
        var choice = in.nextInt();

        log.info("Plain Text: " + Arrays.toString(plainText));
        log.info("Key: " + Arrays.toString(key));
        switch (choice) {
            case 1:{
                var cipherText = SDES.encrypt(plainText, key);
                log.info("Cipher Text: " + Arrays.toString(cipherText));

                var decryptedText = SDES.decrypt(cipherText, key);
                log.info("Decrypted Text: " + Arrays.toString(decryptedText));
            }break;
            case 2:{
                var cipherText = ModifiedSDES.encrypt(plainText, key);
                log.info("Cipher Text: " + Arrays.toString(cipherText));

                var decryptedText = ModifiedSDES.decrypt(cipherText, key);
                log.info("Decrypted Text: " + Arrays.toString(decryptedText));
            }break;
            default:{
                log.info("Invalid choice");
            }
        }
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