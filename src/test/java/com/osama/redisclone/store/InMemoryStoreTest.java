package com.osama.redisclone.store;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStoreTest {

    @Test
    void setAndGetShouldReturnStoredValue() {
        InMemoryStore store = new InMemoryStore();

        store.set("name", "osama");

        assertEquals("osama", store.get("name"));
    }

    @Test
    void getShouldReturnNullForMissingKey() {
        InMemoryStore store = new InMemoryStore();

        assertNull(store.get("missing"));
    }

    @Test
    void deleteShouldReturnTrueWhenKeyExists() {
        InMemoryStore store = new InMemoryStore();
        store.set("name", "osama");

        boolean deleted = store.delete("name");

        assertTrue(deleted);
        assertNull(store.get("name"));
    }

    @Test
    void deleteShouldReturnFalseWhenKeyDoesNotExist() {
        InMemoryStore store = new InMemoryStore();

        boolean deleted = store.delete("missing");

        assertFalse(deleted);
    }

    @Test
    void getShouldReturnNullAfterTtlExpires() throws InterruptedException {
        InMemoryStore store = new InMemoryStore();

        store.set("temp", "value", 1);
        Thread.sleep(1100);

        assertNull(store.get("temp"));
    }

    @Test
    void getShouldReturnValueBeforeTtlExpires() {
        InMemoryStore store = new InMemoryStore();

        store.set("temp", "value", 5);

        assertEquals("value", store.get("temp"));
    }
}