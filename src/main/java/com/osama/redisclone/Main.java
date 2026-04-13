package com.osama.redisclone;

import com.osama.redisclone.command.CommandProcessor;
import com.osama.redisclone.persistence.PersistenceManager;
import com.osama.redisclone.server.RedisServer;
import com.osama.redisclone.store.InMemoryStore;

public class Main {
    public static void main(String[] args) {
        InMemoryStore store = new InMemoryStore();
        PersistenceManager persistenceManager = new PersistenceManager("redis-clone.aof");
        CommandProcessor processor = new CommandProcessor(store, persistenceManager);

        System.out.println("Redis clone starting...");
        System.out.println("Persistence log: " + persistenceManager.getLogFilePath());
        System.out.println("Replaying persisted commands...");

        for (String command : persistenceManager.readAllCommands()) {
            processor.processWithoutPersistence(command);
        }

        System.out.println("Replay complete.");

        RedisServer server = new RedisServer(6379, processor);
        server.start();
    }
}