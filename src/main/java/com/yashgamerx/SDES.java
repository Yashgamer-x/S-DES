package com.yashgamerx;

import lombok.extern.java.Log;

import java.util.Arrays;

@Log
public class SDES {
    public static final int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    public static final int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};
    public static final int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
    public static final int[] IP_INVERSE = {4, 1, 3, 5, 7, 2, 8, 6};
    public static final int[] EP    = {4,1,2,3,2,3,4,1};
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

    /**
     * Breaks the entire block into two halves <br>
     * E.g. 10010010 into 1001 & 0010
     * @return a two-dimensional array, [[1,0,0,1],[0,0,1,0]]
     * */
    private static int[][] breakIntoHalf(int[] block){
        //Break the entire 10 bit key into 2 halves
        final var HALF_LENGTH = block.length/2;
        var leftHalf = new int[HALF_LENGTH];
        var rightHalf = new int[HALF_LENGTH];
        for (int i = 0; i < HALF_LENGTH; i++) {
            leftHalf[i] = block[i];
            rightHalf[i] = block[i+HALF_LENGTH];
        }
        return new int[][]{leftHalf, rightHalf};
    }

    /**
     * Performs a P10 permutation using [3, 5, 2, 7, 4, 10, 1, 9, 8, 6] and then breaks the
     * permutation into left and right half
     * */
    private static int[][] p10Permutation(int[] key){
        //Permute the key based on P10's permutation
        final var p10_Permutation = new int[10];
        for (int i = 0; i < p10_Permutation.length; i++) {
            p10_Permutation[i] = key[P10[i]-1];
        }

        //Return the left and right half
        return breakIntoHalf(p10_Permutation);
    }

    /**
     * Performs a P8 permutation using [6, 3, 7, 4, 8, 5, 10, 9] and returns to permute
     * */
    private static int[] p8Permutation(int[] leftKey, int[] rightKey) {
        //Step 1: Merge left and right key
        var mergedKey = mergeKey(leftKey, rightKey);

        //Step 2: Permute the key based on P10's permutation
        var p8_Permutation = new int[8];
        for (int i = 0; i < p8_Permutation.length; i++) {
            p8_Permutation[i] = mergedKey[P8[i]-1];
        }

        //Return the permuted key
        return p8_Permutation;
    }

    /**
     * Performs a P4 permutation using [2, 4, 3, 1] and returns to permute
     * */
    private static int[] p4Permutation(int[] key) {
        final var p4 = new int[4];
        for(int i = 0; i < p4.length; i++){
            p4[i] = key[P4[i]-1];
        }
        return p4;
    }

    /**
     * Performs a single left shift to the given half/key
     * */
    private static void leftShift(int[] half){
        final var leftElement = half[0];
        for (int i = 1; i < half.length; i++) {
            half[i-1] = half[i];
        }
        half[half.length-1] = leftElement;
    }

    /**
     * Merges the left and right key and returns a new merged array.
     * */
    private static int[] mergeKey(int[] leftKey, int[] rightKey){
        final var merged = new int[leftKey.length+rightKey.length];
        final var length = merged.length/2;
        for(int i = 0; i < length; i++){
            merged[i] = leftKey[i];
            merged[i+length] = rightKey[i];
        }
        return merged;
    }

    /**
     * {@param key A 10 bit key provided by the user} <br>
     *  Uses the provided key to compute it and return K1 and K2 <br>
     *  @return E.g. [[1,0,0,1], [1,0,0,0]]
     * */
    private static int[][] keyGeneration(int[] key){
        //Step 1: Permute the Key
        final var permutatedKey = p10Permutation(key);

        //Step 2: Left shift the left and right half key
        final var leftKey = permutatedKey[0];
        final var rightKey = permutatedKey[1];
        leftShift(leftKey); leftShift(rightKey);

        //Step 3: Permute 10 bit merged key into P8 and get K1
        final var K1 = p8Permutation(leftKey, rightKey);

        //Step 4: Left shift x2 the left and right half key
        leftShift(leftKey); leftShift(rightKey);
        leftShift(leftKey); leftShift(rightKey);

        //Step 3: Permute 10 bit merged key into P8 and get K1
        final var K2 = p8Permutation(leftKey, rightKey);

        return new int[][]{K1, K2};
    }

    /**
     * Permutes the user provided Text using Initial Permutation [2, 6, 3, 1, 4, 8, 5, 7]
     * */
    private static int[] initialPermutation(int[] plainText){
        final var permutation = new int[plainText.length];
        for (int i = 0; i < permutation.length; i++) {
            permutation[i] = plainText[IP[i]-1];
        }
        return permutation;
    }

    /**
     * Permutes the user provided Text using Inverse of Initial Permutation [4, 1, 3, 5, 7, 2, 8, 6]
     * */
    private static int[] inversePermutation(int[] cipherText){
        final var permutation = new int[cipherText.length];
        for (int i = 0; i < permutation.length; i++) {
            permutation[i] = cipherText[IP_INVERSE[i]-1];
        }
        return permutation;
    }

    /**
     * {@param rightKey The Right Half of the IP key} <br>
     * Uses the right key to calculate for EP Key
     * */
    private static int[] EP_Function(int[] rightKey){
        final var epKey = new int[rightKey.length*2]; // Length of 8
        for (int i = 0; i < EP.length; i++) {
            epKey[i] = rightKey[EP[i]-1];
        }
        return epKey;
    }

    /**
     * Helper function that converts any array's Binary in Array to an actual int value
     * */
    private static int binaryArrayToInt(int[] array){
        int result = 0;
        for (int i : array) {
            result ^= i;
            result <<= 1;
        }
        result >>= 1;
        return result;
    }

    /**
     * Helper function that converts int to Binary in Array but only works for 2 bits
     * */
    private static int[] intToBinaryArray(int value){
        var result = new int[2];
        result[1] = value&1;
        value >>= 1;
        result[0] = value&1;
        return result;
    }

    /**
     * XOR helper function that XORs two bit arrays <br>
     * E.g. array1 = [1,0,0,1]    array2 = [0,1,0,1]<br>
     * Result: [1,1,0,0]
     * */
    private static int[] XOR(int[] array1, int[] array2){
        var result = new int[array1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = array1[i] ^ array2[i];
        }
        return result;
    }

    /**
     * Calculates 2 bits of S0 using provided key
     * */
    private static int[] S0_Calculate(int[] key){
        final var rowArray = new int[]{key[0], key[3]};
        final var columnArray = new int[]{key[1], key[2]};
        final var row = binaryArrayToInt(rowArray);
        final var column = binaryArrayToInt(columnArray);
        final var s0Value = S0[row][column];
        final var s0Result = intToBinaryArray(s0Value);
        System.out.println("S0: "+Arrays.toString(s0Result));
        return s0Result;
    }

    /**
     * Calculates 2 bits of S1 using provided key
     * */
    private static int[] S1_Calculate(int[] key){
        final var rowArray = new int[]{key[0], key[3]};
        final var columnArray = new int[]{key[1], key[2]};
        final var row = binaryArrayToInt(rowArray);
        final var column = binaryArrayToInt(columnArray);
        final var s1Value = S1[row][column];
        final var s1Result = intToBinaryArray(s1Value);
        System.out.println("S1: "+Arrays.toString(s1Result));
        return s1Result;
    }

    /**
     * Complex function requires Initial Permutation and a key; either it is Key 1 or Key 2
     * */
    private static int[] complexFunction(int[] initialPermutation, int[] key){
        final var LRKey = breakIntoHalf(initialPermutation);
        final var leftKey = LRKey[0];
        final var rightKey = LRKey[1];
        final var epKey = EP_Function(rightKey);
        System.out.println("EP Key: "+Arrays.toString(epKey));

        final var XORKey = XOR(epKey, key);
        System.out.println("XOR Key: "+Arrays.toString(XORKey));

        final var LRXORKey = breakIntoHalf(XORKey);
        final var leftXORKey = LRXORKey[0];
        final var rightXORKey = LRXORKey[1];

        final var calculatedS0 = S0_Calculate(leftXORKey);
        final var calculatedS1 = S1_Calculate(rightXORKey);
        final var mergedSBox = mergeKey(calculatedS0, calculatedS1);
        System.out.println("Merged SBox: "+Arrays.toString(mergedSBox));

        final var permutedP4 = p4Permutation(mergedSBox);
        System.out.println("Permuted P4: "+Arrays.toString(permutedP4));

        final var LXP4 = XOR(permutedP4, leftKey);
        System.out.println("LXP 4: "+Arrays.toString(LXP4));

        return mergeKey(LXP4, rightKey);
    }

    /**
     * Given a full block, it breaks it into halves and switches the left and right block and merges them back.
     * */
    public static int[] switchBlocks(int[] initialPermutation){
        final var brokenKeys = breakIntoHalf(initialPermutation);
        return mergeKey(brokenKeys[1], brokenKeys[0]);
    }

    /**
     * Encrypts the plain text into cipher text
     * */
    public static int[] encrypt(int[] plainText, int[] key){
        if(plainText.length != 8) throw new IllegalArgumentException("Incorrect Plain-Text length");
        if(key.length != 10) throw new IllegalArgumentException("Incorrect Key length");
        var permutedGeneratedKeys = keyGeneration(key);
        var K1 = permutedGeneratedKeys[0];
        var K2 = permutedGeneratedKeys[1];
        System.out.println("Key 1: "+Arrays.toString(K1));
        System.out.println("Key 2: "+Arrays.toString(K2));

        var initialledPermutation = initialPermutation(plainText);
        System.out.println("After IP: "+Arrays.toString(initialledPermutation));

        final var fk1 = complexFunction(initialledPermutation, K1);
        System.out.println("FK1: "+Arrays.toString(fk1));

        final var SW = switchBlocks(fk1);
        log.info("SW: "+Arrays.toString(SW));

        final var fk2 = complexFunction(SW, K2);
        System.out.println("FK2: "+Arrays.toString(fk2));

        final var inversePermutation = inversePermutation(fk2);
        System.out.println("Inverse Permutation: "+Arrays.toString(inversePermutation));

        return inversePermutation;
    }

    /**
     * Decrypts the plain text into cipher text
     * */
    public static int[] decrypt(int[] cipherText, int[] key){
        if(cipherText.length != 8) throw new IllegalArgumentException("Incorrect Cipher-Text length");
        if(key.length != 10) throw new IllegalArgumentException("Incorrect Key length");
        var permutedGeneratedKeys = keyGeneration(key);
        var K1 = permutedGeneratedKeys[0];
        var K2 = permutedGeneratedKeys[1];
        System.out.println("Key 1: "+Arrays.toString(K1));
        System.out.println("Key 2: "+Arrays.toString(K2));

        var initialledPermutation = initialPermutation(cipherText);
        System.out.println("After IP: "+Arrays.toString(initialledPermutation));

        final var fk2 = complexFunction(initialledPermutation, K2);
        System.out.println("FK1: "+Arrays.toString(fk2));

        final var SW = switchBlocks(fk2);
        log.info("SW: "+Arrays.toString(SW));

        final var fk1 = complexFunction(SW, K1);
        System.out.println("FK2: "+Arrays.toString(fk1));

        final var inversePermutation = inversePermutation(fk1);
        System.out.println("Inverse Permutation: "+Arrays.toString(inversePermutation));

        return inversePermutation;
    }
}
