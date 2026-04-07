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
        String[] parts = remainder.split("\\s+");

        if (parts.length < 2) {
            return new ParsedCommand(commandName, List.of(parts));
        }

        if (parts.length >= 4 && parts[parts.length - 2].equalsIgnoreCase("EX")) {
            String key = parts[0];
            String ttl = parts[parts.length - 1];
            String value = String.join(" ", java.util.Arrays.asList(parts).subList(1, parts.length - 2));

            List<String> arguments = new ArrayList<>();
            arguments.add(key);
            arguments.add(value);
            arguments.add("EX");
            arguments.add(ttl);

            return new ParsedCommand(commandName, arguments);
        }

        String key = parts[0];
        String value = String.join(" ", java.util.Arrays.asList(parts).subList(1, parts.length));

        List<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(value);

        return new ParsedCommand(commandName, arguments);
    }

    private String handleSet(List<String> args) {
        if (args.size() == 2) {
            String key = args.get(0);
            String value = args.get(1);
            store.set(key, value);
            return "OK";
        }

        if (args.size() == 4 && args.get(2).equalsIgnoreCase("EX")) {
            String key = args.get(0);
            String value = args.get(1);

            try {
                long ttlSeconds = Long.parseLong(args.get(3));

                if (ttlSeconds <= 0) {
                    return "ERROR: TTL must be greater than 0";
                }

                store.set(key, value, ttlSeconds);
                return "OK";
            } catch (NumberFormatException e) {
                return "ERROR: TTL must be a valid number";
            }
        }

        return "ERROR: SET syntax is SET key value [EX seconds]";
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