package com.learning.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Boots the whole application against an in-memory database, so a wiring mistake
 * fails the build without the pipeline needing a live Postgres.
 */
@SpringBootTest
@ActiveProfiles("test")
class StoreApplicationTests {

    @Test
    @DisplayName("The application context starts with every bean wired")
    void contextLoads() {
    }

}
