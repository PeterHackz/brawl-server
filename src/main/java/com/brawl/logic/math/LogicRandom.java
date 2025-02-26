package com.brawl.logic.math;

import java.security.SecureRandom;

import com.brawl.logic.debug.Debugger;

public class LogicRandom {

    private static SecureRandom secureRandom;
    private static final String ascii_chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String code_chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";

    public static void init() {
        secureRandom = new SecureRandom();
        Debugger.info("LogicRandom: SecureRandom was initialized successfully");
    }

    public static byte[] randomBytes(int size) {
        byte[] result = new byte[size];
        secureRandom.nextBytes(result);
        return result;
    }

    public static int randomInt() {
        return secureRandom.nextInt();
    }

    public static int rangedInt(int min, int max) {
        int randInt = randomInt();
        int range = max - min + 1;
        return (randInt % range) + min;
    }

    public static int nextInt(int bound) {
        return secureRandom.nextInt(bound);
    }

    public static String randString(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(ascii_chars.charAt(nextInt(ascii_chars.length())));
        }
        return sb.toString();
    }
    public static String randCode(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(code_chars.charAt(nextInt(code_chars.length())));
        }
        return sb.toString();
    }

}