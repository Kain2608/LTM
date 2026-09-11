package poker.server.websocket;

import poker.util.JwtUtil;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class PokerWebSocketServer extends WebSocketServer {

    public PokerWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String authorization = handshake.getFieldValue("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            conn.close(1008, "Missing authentication token");
            return;
        }

        Long userId = JwtUtil.validateTokenAndGetUserId(authorization.substring(7));
        if (userId == null) {
            conn.close(1008, "Invalid or expired authentication token");
            return;
        }

        System.out.println("Authenticated WebSocket connection for user " + userId + ": " + conn.getRemoteSocketAddress());
        conn.send("Welcome to Poker Game Server!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress() + " - Reason: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message from " + conn.getRemoteSocketAddress() + ": " + message);
        // Simple echo for now
        conn.send("Server received: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("An error occurred on connection " + (conn != null ? conn.getRemoteSocketAddress() : "null"));
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started successfully");
    }
}
