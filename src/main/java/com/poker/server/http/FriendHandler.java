package com.poker.server.http;

import com.poker.database.FriendDAO;
import com.poker.model.Friend;
import com.poker.util.JsonUtil;
import com.poker.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FriendHandler implements HttpHandler {

    private final FriendDAO friendDAO = new FriendDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        Long userId = getUserIdFromAuthHeader(exchange);
        
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }

        if ("GET".equalsIgnoreCase(method)) {
            if ("/api/friends".equals(path)) {
                List<Friend> friends = friendDAO.getFriendsList(userId);
                sendResponse(exchange, 200, JsonUtil.toJson(friends));
            } else if ("/api/friends/requests".equals(path)) {
                List<Friend> requests = friendDAO.getPendingRequests(userId);
                sendResponse(exchange, 200, JsonUtil.toJson(requests));
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
            }
        } else if ("POST".equalsIgnoreCase(method)) {
            // Very simplified URL parsing for /api/friends/request/{friendId}
            if (path.startsWith("/api/friends/request/")) {
                try {
                    Long friendId = Long.parseLong(path.substring("/api/friends/request/".length()));
                    boolean success = friendDAO.sendFriendRequest(userId, friendId);
                    sendResponse(exchange, success ? 200 : 400, "{\"success\":" + success + "}");
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"error\":\"Invalid friend ID\"}");
                }
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
            }
        } else if ("PUT".equalsIgnoreCase(method)) {
            if (path.startsWith("/api/friends/accept/")) {
                try {
                    Long friendshipId = Long.parseLong(path.substring("/api/friends/accept/".length()));
                    boolean success = friendDAO.acceptFriendRequest(friendshipId, userId);
                    sendResponse(exchange, success ? 200 : 400, "{\"success\":" + success + "}");
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"error\":\"Invalid friendship ID\"}");
                }
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
            }
        } else if ("DELETE".equalsIgnoreCase(method)) {
            if (path.startsWith("/api/friends/remove/")) {
                try {
                    Long friendshipId = Long.parseLong(path.substring("/api/friends/remove/".length()));
                    boolean success = friendDAO.removeOrRejectFriend(friendshipId, userId);
                    sendResponse(exchange, success ? 200 : 400, "{\"success\":" + success + "}");
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"error\":\"Invalid friendship ID\"}");
                }
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
            }
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
        }
    }

    private Long getUserIdFromAuthHeader(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return JwtUtil.validateTokenAndGetUserId(token);
        }
        return null;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
