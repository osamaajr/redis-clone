package com.osama.redisclone;

import com.osama.redisclone.command.CommandProcessor;
import com.osama.redisclone.store.InMemoryStore;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        InMemoryStore store = new InMemoryStore();
        CommandProcessor processor = new CommandProcessor(store);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Redis clone starting...");
        System.out.println("Available commands: SET key value | GET key | DEL key | EXIT");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                continue;
            }

            if (input.equalsIgnoreCase("EXIT")) {
                System.out.println("Shutting down.");
                break;
            }

            String response = processor.process(input);
            System.out.println(response);
        }

        scanner.close();
    }
}