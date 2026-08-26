package com.learning.store.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Test
    @DisplayName("A transaction id carries today's date and an 8-character suffix")
    void transactionIdMatchesExpectedFormat() {
        String today = LocalDate.now().format(DATE);

        assertThat(IdGenerator.transactionId())
                .matches("TXN-" + today + "-[0-9A-Z]{8}");
    }

    @Test
    @DisplayName("A certificate code carries today's date and an 8-character suffix")
    void certificateCodeMatchesExpectedFormat() {
        String today = LocalDate.now().format(DATE);

        assertThat(IdGenerator.certificateCode())
                .matches("CERT-" + today + "-[0-9A-Z]{8}");
    }

    @Test
    @DisplayName("A verification id is a 10-character code with no date")
    void verificationIdMatchesExpectedFormat() {
        assertThat(IdGenerator.verificationId()).matches("VER-[0-9A-Z]{10}");
    }

    @Test
    @DisplayName("Generated ids do not collide, so they stay usable as unique keys")
    void generatedIdsAreUnique() {
        Set<String> transactions = new HashSet<>();
        Set<String> verifications = new HashSet<>();

        for (int i = 0; i < 500; i++) {
            transactions.add(IdGenerator.transactionId());
            verifications.add(IdGenerator.verificationId());
        }

        assertThat(transactions).hasSize(500);
        assertThat(verifications).hasSize(500);
    }
}
