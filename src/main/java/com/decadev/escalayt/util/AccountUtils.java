package com.decadev.escalayt.util;

import java.security.SecureRandom;

public class AccountUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789#*@$&";
    private static final int PASSWORD_LENGTH = 8;
    private static final int USERNAME_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    public static String generateRandomUsername() {
        StringBuilder username = new StringBuilder(USERNAME_LENGTH);
        for (int i = 0; i < USERNAME_LENGTH; i++) {
            username.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return username.toString();
    }
}
