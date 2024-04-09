package com.hidecarbon.hidecarbon.util;

import java.security.SecureRandom;

public class RandomStringGenerator {

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int STRING_LENGTH = 20;
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(randomIndex));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String randomString = generateRandomString();
//        System.out.println(randomString);
    }
}