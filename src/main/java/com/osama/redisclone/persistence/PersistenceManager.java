package com.osama.redisclone.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public class PersistenceManager {
    private final Path logFilePath;

    public PersistenceManager(String fileName) {
        this.logFilePath = Path.of(fileName);
        initializeLogFile();
    }

    private void initializeLogFile() {
        try {
            if (Files.notExists(logFilePath)) {
                Files.createFile(logFilePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize persistence log file: " + logFilePath, e);
        }
    }

    public synchronized void appendCommand(String command) {
        try {
            Files.writeString(
                logFilePath,
                command + System.lineSeparator(),
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to append command to persistence log", e);
        }
    }

    public List<String> readAllCommands() {
        try {
            return Files.readAllLines(logFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read persistence log", e);
        }
    }

    public String getLogFilePath() {
        return logFilePath.toString();
    }
}