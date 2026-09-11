package poker.server.http;

import poker.database.UserDAO;
import poker.model.User;
import poker.util.JsonUtil;
import poker.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ProfileHandler implements HttpHandler {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if ("GET".equalsIgnoreCase(method) && "/api/profile/me".equals(path)) {
            handleGetMyProfile(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
        }
    }

    private void handleGetMyProfile(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        Long userId = null;
        String errorMessage = "Unauthorized";
        
        if (authHeader == null) {
            errorMessage = "Missing Authorization header";
        } else if (!authHeader.startsWith("Bearer ")) {
            errorMessage = "Invalid Authorization format. Must start with 'Bearer '";
        } else {
            String token = authHeader.substring(7);
            userId = JwtUtil.validateTokenAndGetUserId(token);
            if (userId == null) {
                errorMessage = "Invalid or expired token";
            }
        }

        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"" + errorMessage + "\"}");
            return;
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPasswordHash(null); // Don't expose password hash
            sendResponse(exchange, 200, JsonUtil.toJson(user));
        } else {
            sendResponse(exchange, 404, "{\"error\":\"User not found\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
