package com.osama.redisclone.command;

import com.osama.redisclone.store.InMemoryStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandProcessorTest {

    @Test
    void setShouldStoreValueAndReturnOk() {
        InMemoryStore store = new InMemoryStore();
        CommandProcessor processor = new CommandProcessor(store);

        String response = processor.process("SET name osama");

        assertEquals("OK", response);
        assertEquals("osama", store.get("name"));
    }

    @Test
    void getShouldReturnStoredValue() {
        InMemoryStore store = new InMemoryStore();
        store.set("name", "osama");
        CommandProcessor processor = new CommandProcessor(store);

        String response = processor.process("GET name");

        assertEquals("osama", response);
    }

    @Test
    void getShouldReturnNilForMissingKey() {
        InMemoryStore store = new InMemoryStore();
        CommandProcessor processor = new CommandProcessor(store);

        String response = processor.process("GET missing");

        assertEquals("(nil)", response);
    }

    @Test
    void delShouldReturnOneWhenKeyExists() {
        InMemoryStore store = new InMemoryStore();
        store.set("name", "osama");
        CommandProcessor processor = new CommandProcessor(store);

        String response = processor.process("DEL name");

        assertEquals("1", response);
        assertNull(store.get("name"));
    }

    @Test
    void delShouldReturnZeroWhenKeyDoesNotExist() {
        InMemoryStore store = new InMemoryStore();
        CommandProcessor processor = new CommandProcessor(store);

        String response = processor.process("DEL missing");

        assertEquals("0", response);
    }

    @Test
    void setShouldSupportValuesWithSpaces() {
        InMemoryStore store = new InMemoryStore();
        CommandProcessor processor = new CommandProcessor(store);

        String response = processor.process("SET bio Computer Science student at Liverpool");

        assertEquals("OK", response);
        assertEquals("Computer Science student at Liverpool", store.get("bio"));
    }

    @Test
    void invalidCommandShouldReturnError() {
        InMemoryStore store = new InMemoryStore();
        CommandProcessor processor = new CommandProcessor(store);

        String response = processor.process("UNKNOWN something");

        assertEquals("ERROR: Unsupported command", response);
    }

    @Test
    void emptyInputShouldReturnError() {
        InMemoryStore store = new InMemoryStore();
        CommandProcessor processor = new CommandProcessor(store);

        String response = processor.process("   ");

        assertEquals("ERROR: Invalid command", response);
    }
}