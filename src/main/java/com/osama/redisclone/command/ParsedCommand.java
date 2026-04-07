package com.osama.redisclone.command;

import java.util.List;

public class ParsedCommand {
    private final String commandName;
    private final List<String> arguments;

    public ParsedCommand(String commandName, List<String> arguments) {
        this.commandName = commandName;
        this.arguments = arguments;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getArguments() {
        return arguments;
    }
}