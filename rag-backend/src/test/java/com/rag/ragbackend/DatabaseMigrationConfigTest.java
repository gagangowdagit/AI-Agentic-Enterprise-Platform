package com.rag.ragbackend;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseMigrationConfigTest {

    @Test
    void applicationProperties_shouldUseFlywayForSchemaManagement() throws IOException {
        Path propertiesFile = Path.of("src/main/resources/application.properties");
        String content = Files.readString(propertiesFile);

        assertTrue(content.contains("spring.jpa.hibernate.ddl-auto=none"),
                "Hibernate should not validate the schema while Flyway manages it");
        assertTrue(content.contains("spring.flyway.enabled=true"),
                "Flyway must be enabled for migration-based schema creation");
    }
}
