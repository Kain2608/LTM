package poker.client.net;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

public class GameWebSocketClient extends WebSocketClient {

    private Consumer<String> messageHandler;
    private Consumer<Boolean> connectionStatusHandler;

    public GameWebSocketClient(URI serverUri, String token) {
        super(serverUri, Map.of("Authorization", "Bearer " + token));
    }

    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    public void setConnectionStatusHandler(Consumer<Boolean> handler) {
        this.connectionStatusHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[WebSocket Client] Connected to Poker WebSocket Server!");
        if (connectionStatusHandler != null) {
            connectionStatusHandler.accept(true);
        }
    }

    @Override
    public void onMessage(String message) {
        System.out.println("[WebSocket Client] Received: " + message);
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[WebSocket Client] Connection closed: " + reason);
        if (connectionStatusHandler != null) {
            connectionStatusHandler.accept(false);
        }
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("[WebSocket Client] Error: " + ex.getMessage());
    }

    public static GameWebSocketClient connectToServer(String token, Consumer<String> onMessage, Consumer<Boolean> onStatus) {
        if (token == null || token.isBlank()) {
            System.err.println("Cannot connect WebSocket without an authenticated session.");
            return null;
        }
        try {
            String configuredUrl = System.getenv("POKER_WS_URL");
            String serverUrl = configuredUrl == null || configuredUrl.isBlank()
                    ? "ws://localhost:8081"
                    : configuredUrl;
            URI serverUri = new URI(serverUrl);
            GameWebSocketClient wsClient = new GameWebSocketClient(serverUri, token);
            wsClient.setMessageHandler(onMessage);
            wsClient.setConnectionStatusHandler(onStatus);
            wsClient.connect();
            return wsClient;
        } catch (Exception e) {
            System.err.println("Failed to initialize WebSocket client: " + e.getMessage());
            return null;
        }
    }
}
