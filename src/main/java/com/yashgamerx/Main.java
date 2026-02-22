package com.yashgamerx;

public class Main {
    static void main() {
        final var key = new int[]{1, 0, 1, 0, 0, 0, 0, 0, 1, 0};
        final var plainText = new int[]{1, 0, 0, 1, 0, 1, 1, 1};
        SDES.encrypt(plainText, key);
    }
}
