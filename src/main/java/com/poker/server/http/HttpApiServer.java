package com.poker.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpApiServer {
    private final HttpServer server;

    public HttpApiServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Setup simple threading
        server.setExecutor(Executors.newFixedThreadPool(10));

        // Define routes
        server.createContext("/", new RootHandler());
        server.createContext("/api/health", new HealthCheckHandler());
        server.createContext("/api/auth", new AuthHandler());
        server.createContext("/api/statistics", new StatisticHandler());
        server.createContext("/api/profile", new ProfileHandler());
        server.createContext("/api/friends", new FriendHandler());
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    // Basic handler for root
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Poker API Server is running.";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // Basic handler for health check
    static class HealthCheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\":\"UP\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
