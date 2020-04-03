package no.nav.pensjon.testdata.repository.support;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PrimaryKeySwapperTest extends PrimaryKeySwapper{

    @Test
    void updatePrimaryKey() {
        primaryKeyRegistry.put("key", "value");
        updatePrimaryKey("value", "value2");

        assertEquals("value2", primaryKeyRegistry.get("key"));
    }
}