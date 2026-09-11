package poker.server;

import poker.database.DatabaseConnectionManager;
import poker.server.http.HttpApiServer;
import poker.server.websocket.PokerWebSocketServer;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

public final class ServerMain {
    private ServerMain() {
    }

    public static void main(String[] args) throws InterruptedException {
        int httpPort = getPort("POKER_HTTP_PORT", 8080);
        int webSocketPort = getPort("POKER_WS_PORT", 8081);

        verifyDatabaseConnection();

        HttpApiServer httpServer;
        PokerWebSocketServer webSocketServer;
        try {
            httpServer = new HttpApiServer(httpPort);
            webSocketServer = new PokerWebSocketServer(new InetSocketAddress(webSocketPort));
            httpServer.start();
            webSocketServer.start();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot start Poker backend", e);
        }

        System.out.println("Poker HTTP API listening on port " + httpPort);
        System.out.println("Poker WebSocket listening on port " + webSocketPort);

        CountDownLatch shutdownSignal = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            httpServer.stop();
            try {
                webSocketServer.stop(1_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            shutdownSignal.countDown();
        }, "poker-server-shutdown"));
        shutdownSignal.await();
    }

    private static void verifyDatabaseConnection() {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            System.out.println("Connected to MySQL database: " + connection.getCatalog());
        } catch (Exception e) {
            System.err.println("MySQL is unavailable: " + e.getMessage());
        }
    }

    private static int getPort(String environmentName, int defaultPort) {
        String configuredPort = System.getenv(environmentName);
        if (configuredPort == null || configuredPort.isBlank()) {
            return defaultPort;
        }
        try {
            return Integer.parseInt(configuredPort);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(environmentName + " must be a valid port", e);
        }
    }
}
