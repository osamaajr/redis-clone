package com.osama.redisclone.server;

import com.osama.redisclone.command.CommandProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisServer {
    private final int port;
    private final CommandProcessor processor;
    private final ExecutorService clientPool;

    public RedisServer(int port, CommandProcessor processor) {
        this.port = port;
        this.processor = processor;
        this.clientPool = Executors.newCachedThreadPool();
    }

    public void start() {
        System.out.println("Starting Redis server on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                clientPool.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server", e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
            Socket socket = clientSocket;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
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
            System.out.println("Client disconnected.");
        }
    }
}
