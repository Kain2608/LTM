package com.poker;

import com.poker.server.http.HttpApiServer;
import com.poker.server.websocket.PokerWebSocketServer;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Poker Server (Pure Java)...");

        // 1. Start HTTP API Server
        int httpPort = 8080;
        try {
            HttpApiServer httpServer = new HttpApiServer(httpPort);
            httpServer.start();
            System.out.println("HTTP API Server is listening on port " + httpPort);
        } catch (Exception e) {
            System.err.println("Failed to start HTTP API Server: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Start WebSocket Game Server
        int wsPort = 8081;
        try {
            PokerWebSocketServer wsServer = new PokerWebSocketServer(new InetSocketAddress(wsPort));
            wsServer.start();
            System.out.println("WebSocket Game Server is listening on port " + wsPort);
        } catch (Exception e) {
            System.err.println("Failed to start WebSocket Game Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
