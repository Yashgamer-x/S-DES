package com.yashgamerx;

import lombok.extern.java.Log;

import java.util.Arrays;

@Log
public class SDES {
    public static final int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    public static final int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};
    public static final int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
    public static final int[] IP_INVERSE = {4, 1, 3, 5, 7, 2, 8, 6};
    public static final int[] EP = {4, 1, 2, 3, 2, 3, 4, 1};
    public static final int[] P4 = {2, 4, 3, 1};

    public static final int[][] S0 = {
            {1,0,3,2},
            {3,2,1,0},
            {0,2,1,3},
            {3,1,3,2}
    };

    public static final int[][] S1 = {
            {0,1,2,3},
            {2,0,1,3},
            {3,0,1,0},
            {2,1,0,3}
    };

    private static int[][] breakIntoHalf(int[] block){
        //Break the entire 10 bit key into 2 halves
        final var HALF_LENGTH = block.length/2;
        var leftHalf = new int[HALF_LENGTH];
        var rightHalf = new int[HALF_LENGTH];

        for (int i = 0; i < HALF_LENGTH; i++) {
            leftHalf[i] = block[i];
            rightHalf[i] = block[i + HALF_LENGTH];
        }

        return new int[][]{leftHalf, rightHalf};
    }

    /**
     * Performs a P10 permutation using [3, 5, 2, 7, 4, 10, 1, 9, 8, 6] and then breaks the
     * permutation into left and right half
     * */
    private static int[][] p10Permutation(int[] key){
        final var p10 = new int[10];

        for (int i = 0; i < p10.length; i++) {
            p10[i] = key[P10[i] - 1];
        }

        return breakIntoHalf(p10);
    }

    /**
     * Performs a P8 permutation using [6, 3, 7, 4, 8, 5, 10, 9] and returns to permute
     * */
    private static int[] p8Permutation(int[] leftKey, int[] rightKey) {
        var merged = mergeKey(leftKey, rightKey);
        var p8 = new int[8];

        for (int i = 0; i < p8.length; i++) {
            p8[i] = merged[P8[i] - 1];
        }

        return p8;
    }

    /**
     * Performs a P4 permutation using [2, 4, 3, 1] and returns to permute
     * */
    private static int[] p4Permutation(int[] key) {
        var p4 = new int[4];

        for (int i = 0; i < p4.length; i++) {
            p4[i] = key[P4[i] - 1];
        }

        return p4;
    }

    /**
     * Performs a single left shift to the given half/key
     * */
    private static void leftShift(int[] half){
        int first = half[0];

        for (int i = 1; i < half.length; i++) {
            half[i - 1] = half[i];
        }

        half[half.length - 1] = first;
    }

    /**
     * Merges the left and right key and returns a new merged array.
     * */
    private static int[] mergeKey(int[] leftKey, int[] rightKey){
        var merged = new int[leftKey.length + rightKey.length];

        for (int i = 0; i < leftKey.length; i++) {
            merged[i] = leftKey[i];
            merged[i + leftKey.length] = rightKey[i];
        }

        return merged;
    }

    /**
     * {@param key A 10 bit key provided by the user} <br>
     *  Uses the provided key to compute it and return K1 and K2 <br>
     *  @return E.g. [[1,0,0,1], [1,0,0,0]]
     * */
    private static int[][] keyGeneration(int[] key){
        var halves = p10Permutation(key);

        var left = halves[0];
        var right = halves[1];

        leftShift(left); leftShift(right);
        var K1 = p8Permutation(left, right);

        leftShift(left); leftShift(right);
        leftShift(left); leftShift(right);
        var K2 = p8Permutation(left, right);

        return new int[][]{K1, K2};
    }

    private static int[] initialPermutation(int[] input){
        var result = new int[input.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = input[IP[i] - 1];
        }

        return result;
    }

    private static int[] inversePermutation(int[] input){
        var result = new int[input.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = input[IP_INVERSE[i] - 1];
        }

        return result;
    }

    private static int[] EP_Function(int[] right){
        var ep = new int[8];

        for (int i = 0; i < ep.length; i++) {
            ep[i] = right[EP[i] - 1];
        }

        return ep;
    }

    /**
     * Helper function that converts any array's Binary in Array to an actual int value
     * */
    private static int binaryArrayToInt(int[] array){
        int result = 0;
        for (int bit : array) {
            result = (result << 1) | bit;
        }
        return result;
    }

    /**
     * Helper function that converts int to Binary in Array but only works for 2 bits
     * */
    private static int[] intToBinaryArray(int value){
        return new int[]{
                (value >> 1) & 1,
                value & 1
        };
    }

    private static int[] XOR(int[] a, int[] b){
        var result = new int[a.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = a[i] ^ b[i];
        }

        return result;
    }

    private static int[] S0_Calculate(int[] input){
        int row = binaryArrayToInt(new int[]{input[0], input[3]});
        int col = binaryArrayToInt(new int[]{input[1], input[2]});
        return intToBinaryArray(S0[row][col]);
    }

    private static int[] S1_Calculate(int[] input){
        int row = binaryArrayToInt(new int[]{input[0], input[3]});
        int col = binaryArrayToInt(new int[]{input[1], input[2]});
        return intToBinaryArray(S1[row][col]);
    }

    private static int[] complexFunction(int[] input, int[] key){
        var halves = breakIntoHalf(input);

        var left = halves[0];
        var right = halves[1];

        var ep = EP_Function(right);
        var xored = XOR(ep, key);

        var split = breakIntoHalf(xored);

        var s0 = S0_Calculate(split[0]);
        var s1 = S1_Calculate(split[1]);

        var merged = mergeKey(s0, s1);
        var p4 = p4Permutation(merged);

        var newLeft = XOR(p4, left);

        return mergeKey(newLeft, right);
    }

    public static int[] switchBlocks(int[] input){
        var halves = breakIntoHalf(input);
        return mergeKey(halves[1], halves[0]);
    }

    /**
     * Encrypts the plain text into cipher text
     * */
    public static int[] encrypt(int[] plainText, int[] key){
        var keys = keyGeneration(key);

        var ip = initialPermutation(plainText);
        var fk1 = complexFunction(ip, keys[0]);
        var sw = switchBlocks(fk1);
        var fk2 = complexFunction(sw, keys[1]);

        return inversePermutation(fk2);
    }

    /**
     * Decrypts the plain text into cipher text
     * */
    public static int[] decrypt(int[] cipherText, int[] key){
        var keys = keyGeneration(key);

        var ip = initialPermutation(cipherText);
        var fk2 = complexFunction(ip, keys[1]);
        var sw = switchBlocks(fk2);
        var fk1 = complexFunction(sw, keys[0]);

        return inversePermutation(fk1);
    }
}