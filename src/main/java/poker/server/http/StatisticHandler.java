package poker.server.http;

import poker.database.StatisticDAO;
import poker.model.PlayerStatistic;
import poker.util.JsonUtil;
import poker.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StatisticHandler implements HttpHandler {

    private final StatisticDAO statisticDAO = new StatisticDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if ("GET".equalsIgnoreCase(method)) {
            if ("/api/statistics/leaderboard/wins".equals(path)) {
                handleLeaderboardWins(exchange);
            } else if ("/api/statistics/leaderboard/biggest-win".equals(path)) {
                handleLeaderboardBiggestWin(exchange);
            } else if ("/api/statistics/me".equals(path)) {
                handleMyStats(exchange);
            } else if ("/api/statistics/history/me".equals(path)) {
                handleMyHistory(exchange);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
            }
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
        }
    }

    private void handleMyHistory(HttpExchange exchange) throws IOException {
        Long userId = getUserIdFromAuthHeader(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }
        
        // Return last 20 games for simplicity in pure java
        sendResponse(exchange, 200, JsonUtil.toJson(statisticDAO.getMatchHistory(userId, 20)));
    }

    private void handleLeaderboardWins(HttpExchange exchange) throws IOException {
        List<PlayerStatistic> list = statisticDAO.getLeaderboardByWins(20);
        sendResponse(exchange, 200, JsonUtil.toJson(list));
    }

    private void handleLeaderboardBiggestWin(HttpExchange exchange) throws IOException {
        List<PlayerStatistic> list = statisticDAO.getLeaderboardByBiggestWin(20);
        sendResponse(exchange, 200, JsonUtil.toJson(list));
    }

    private void handleMyStats(HttpExchange exchange) throws IOException {
        Long userId = getUserIdFromAuthHeader(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }

        PlayerStatistic stat = statisticDAO.findByUserId(userId).orElse(new PlayerStatistic());
        sendResponse(exchange, 200, JsonUtil.toJson(stat));
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

