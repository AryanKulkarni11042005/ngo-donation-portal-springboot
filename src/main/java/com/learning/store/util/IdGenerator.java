package com.learning.store.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Mirrors the ID formats used by the original Node backend so codes stay
 * consistent across rows created by either service.
 */
public final class IdGenerator {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    private IdGenerator() {
    }

    private static String randomSuffix(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String transactionId() {
        return "TXN-" + LocalDate.now().format(DATE) + "-" + randomSuffix(8);
    }

    public static String certificateCode() {
        return "CERT-" + LocalDate.now().format(DATE) + "-" + randomSuffix(8);
    }

    public static String verificationId() {
        return "VER-" + randomSuffix(10);
    }
}
