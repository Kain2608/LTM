package com.poker.server.http;

import com.poker.database.UserDAO;
import com.poker.model.User;
import com.poker.util.JsonUtil;
import com.poker.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class AuthHandler implements HttpHandler {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if ("POST".equalsIgnoreCase(method)) {
            if ("/api/auth/signin".equals(path)) {
                handleSignIn(exchange);
            } else if ("/api/auth/signup".equals(path)) {
                handleSignUp(exchange);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
            }
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
        }
    }

    private void handleSignIn(HttpExchange exchange) throws IOException {
        String requestBody = getRequestBody(exchange);
        AuthRequest req = JsonUtil.fromJson(requestBody, AuthRequest.class);

        if (req == null || req.username == null || req.password == null) {
            sendResponse(exchange, 400, "{\"error\":\"Bad Request\"}");
            return;
        }

        Optional<User> userOpt = userDAO.findByUsername(req.username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (userDAO.verifyPassword(req.password, user.getPasswordHash())) {
                String token = JwtUtil.generateToken(user.getUserId(), user.getUsername());
                AuthResponse res = new AuthResponse(token, user.getUserId(), user.getUsername());
                sendResponse(exchange, 200, JsonUtil.toJson(res));
                return;
            }
        }
        sendResponse(exchange, 401, "{\"error\":\"Invalid credentials\"}");
    }

    private void handleSignUp(HttpExchange exchange) throws IOException {
        String requestBody = getRequestBody(exchange);
        AuthRequest req = JsonUtil.fromJson(requestBody, AuthRequest.class);

        if (req == null || req.username == null || req.password == null || req.email == null) {
            sendResponse(exchange, 400, "{\"error\":\"Bad Request\"}");
            return;
        }

        if (userDAO.findByUsername(req.username).isPresent()) {
            sendResponse(exchange, 400, "{\"error\":\"Username already taken\"}");
            return;
        }

        User newUser = userDAO.createUser(req.username, req.email, req.password);
        if (newUser != null) {
            sendResponse(exchange, 200, "{\"message\":\"User registered successfully!\"}");
        } else {
            sendResponse(exchange, 500, "{\"error\":\"Internal Server Error\"}");
        }
    }

    private String getRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // Inner DTOs
    static class AuthRequest {
        String username;
        String email;
        String password;
    }

    static class AuthResponse {
        String token;
        Long id;
        String username;

        public AuthResponse(String token, Long id, String username) {
            this.token = token;
            this.id = id;
            this.username = username;
        }
    }
}
