package com.osama.redisclone.command;

import com.osama.redisclone.store.InMemoryStore;

import java.util.Arrays;
import java.util.List;

public class CommandProcessor {
    private final InMemoryStore store;

    public CommandProcessor(InMemoryStore store) {
        this.store = store;
    }

    public String process(String input) {
        ParsedCommand parsedCommand = parse(input);

        if (parsedCommand == null) {
            return "ERROR: Invalid command";
        }

        String commandName = parsedCommand.getCommandName();
        List<String> args = parsedCommand.getArguments();

        return switch (commandName) {
            case "SET" -> handleSet(args);
            case "GET" -> handleGet(args);
            case "DEL" -> handleDel(args);
            default -> "ERROR: Unsupported command";
        };
    }

    private ParsedCommand parse(String input) {
        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            return null;
        }

        String[] parts = trimmedInput.split("\\s+");
        String commandName = parts[0].toUpperCase();
        List<String> arguments = Arrays.asList(parts).subList(1, parts.length);

        return new ParsedCommand(commandName, arguments);
    }

    private String handleSet(List<String> args) {
        if (args.size() < 2) {
            return "ERROR: SET requires exactly 2 arguments: SET key value";
        }

        String key = args.get(0);
        String value = args.get(1);

        store.set(key, value);
        return "OK";
    }

    private String handleGet(List<String> args) {
        if (args.size() != 1) {
            return "ERROR: GET requires exactly 1 argument: GET key";
        }

        String key = args.get(0);
        String value = store.get(key);

        return value != null ? value : "(nil)";
    }

    private String handleDel(List<String> args) {
        if (args.size() != 1) {
            return "ERROR: DEL requires exactly 1 argument: DEL key";
        }

        String key = args.get(0);
        boolean deleted = store.delete(key);

        return deleted ? "1" : "0";
    }
}