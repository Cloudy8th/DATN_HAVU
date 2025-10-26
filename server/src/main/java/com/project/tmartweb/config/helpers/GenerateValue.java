package com.project.tmartweb.config.helpers;

import java.util.Random;
import java.util.UUID;

public class GenerateValue {

    public static String generateUsername() {
        return randomUsername();
    }

    private static String randomUsername() {
        Random random = new Random();
        UUID uuid = UUID.randomUUID();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final int LENGTH = 8;
        StringBuilder username = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            username.append(characters.charAt(random.nextInt(characters.length())));
            username.append(uuid.toString().charAt(random.nextInt(uuid.toString().length())));
        }
        return username.toString();
    }
}
