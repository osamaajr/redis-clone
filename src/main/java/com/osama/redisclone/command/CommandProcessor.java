package com.osama.redisclone.command;

import com.osama.redisclone.store.InMemoryStore;

import java.util.ArrayList;
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

        String[] firstSplit = trimmedInput.split("\\s+", 2);
        String commandName = firstSplit[0].toUpperCase();

        if (firstSplit.length == 1) {
            return new ParsedCommand(commandName, List.of());
        }

        String remainder = firstSplit[1];

        if (commandName.equals("SET")) {
            return parseSetCommand(remainder, commandName);
        }

        List<String> arguments = List.of(remainder.split("\\s+"));
        return new ParsedCommand(commandName, arguments);
    }

    private ParsedCommand parseSetCommand(String remainder, String commandName) {
        String[] setParts = remainder.split("\\s+", 2);

        if (setParts.length < 2) {
            return new ParsedCommand(commandName, List.of(setParts));
        }

        String key = setParts[0];
        String value = setParts[1];

        List<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(value);

        return new ParsedCommand(commandName, arguments);
    }

    private String handleSet(List<String> args) {
        if (args.size() != 2) {
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