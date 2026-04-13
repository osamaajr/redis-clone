package com.osama.redisclone.server;

import com.osama.redisclone.command.CommandProcessor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {
    private final int port;
    private final CommandProcessor processor;

    public RedisServer(int port, CommandProcessor processor) {
        this.port = port;
        this.processor = processor;
    }

    public void start() {
        System.out.println("Starting Redis server on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                handleClient(clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server", e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            writer.println("Connected to Redis clone. Type EXIT to disconnect.");

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                if (inputLine.trim().equalsIgnoreCase("EXIT")) {
                    writer.println("Goodbye.");
                    break;
                }

                if (inputLine.trim().isEmpty()) {
                    continue;
                }

                String response = processor.process(inputLine);
                writer.println(response);
            }
        } catch (IOException e) {
            System.out.println("Client connection error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                System.out.println("Failed to close client socket.");
            }
        }
    }
}
