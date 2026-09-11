package poker;

import poker.client.net.ApiClient;
import poker.client.net.GameWebSocketClient;
import poker.model.Player;
import poker.model.PokerGameEngine;
import poker.ui.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application implements PokerGameEngine.GameEventListener, ControlPanelView.ActionHandler, HeaderNode.HeaderActions {

    private PokerGameEngine gameEngine;
    private HeaderNode headerNode;
    private PokerTableContainer tableContainer;
    private HistoryPanelNode historyPanel;
    private ControlPanelView controlPanel;
    private Stage primaryStage;
    private LobbyView lobbyView;
    private String currentUsername = "Player1";
    private GameWebSocketClient wsClient;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("OpenDecks Texas Hold'em Poker - Fullstack Edition");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(750);

        // Show Login screen on startup
        showLoginScreen();
    }

    private void connectWebSocket() {
        if (wsClient != null) {
            wsClient.close();
        }
        new Thread(() -> {
            wsClient = GameWebSocketClient.connectToServer(
                    ApiClient.getCurrentToken(),
                    msg -> Platform.runLater(() -> {
                        if (historyPanel != null) {
                            historyPanel.addLog("📡 WS Server: " + msg, "system");
                        }
                    }),
                    status -> System.out.println("[Main] WebSocket connection status: " + (status ? "Connected" : "Disconnected"))
            );
        }, "WSClientThread").start();
    }

    private void showLoginScreen() {
        LoginView loginView = new LoginView(new LoginView.LoginActions() {
            @Override
            public void onLoginSuccess(String username) {
                currentUsername = username;
                connectWebSocket();
                showLobbyScreen();
            }

            @Override
            public void onSwitchToRegister() {
                showRegisterScreen();
            }
        });
        Scene scene = new Scene(loginView, 1280, 820);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showRegisterScreen() {
        RegisterView registerView = new RegisterView(new RegisterView.RegisterActions() {
            @Override
            public void onRegisterSuccess(String username) {
                showLoginScreen();
            }

            @Override
            public void onSwitchToLogin() {
                showLoginScreen();
            }
        });
        Scene scene = new Scene(registerView, 1280, 820);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showLobbyScreen() {
        lobbyView = new LobbyView(currentUsername, new LobbyView.LobbyActions() {
            @Override
            public void onJoinRoom(String roomId, boolean isPrivate, boolean isSpectator) {
                showGameScreen();
            }

            @Override
            public void onCreateRoomRequested() {
                CreateRoomDialog.show(primaryStage, (name, maxP, sb, bb, buyIn, isPriv, pass) -> {
                    String newId = "#" + (100 + (int)(Math.random() * 900));
                    String typeStr = isPriv ? "Private" : "Public";
                    LobbyView.RoomModel newRoom = new LobbyView.RoomModel(
                            newId, name, currentUsername, "1/" + maxP, "$" + sb + "/$" + bb, "$" + String.format("%,d", buyIn), typeStr, "Waiting"
                    );
                    lobbyView.addNewRoom(newRoom);
                    showGameScreen();
                });
            }

            @Override
            public void onOpenLeaderboard() {
                showLeaderboardScreen();
            }

            @Override
            public void onOpenProfile() {
                showProfileScreen();
            }

            @Override
            public void onOpenAdmin() {
                showAdminScreen();
            }

            @Override
            public void onOpenFriends() {
                showFriendsScreen();
            }

            @Override
            public void onLogout() {
                logout();
            }
        });
        Scene scene = new Scene(lobbyView, 1280, 820);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showLeaderboardScreen() {
        LeaderboardView view = new LeaderboardView(() -> showLobbyScreen());
        Scene scene = new Scene(view, 1280, 820);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showProfileScreen() {
        ProfileHistoryView view = new ProfileHistoryView(currentUsername, new ProfileHistoryView.ProfileActions() {
            @Override
            public void onBackToLobby() {
                showLobbyScreen();
            }

            @Override
            public void onLogout() {
                logout();
            }
        });
        Scene scene = new Scene(view, 1280, 820);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showFriendsScreen() {
        FriendsView view = new FriendsView(() -> showLobbyScreen());
        Scene scene = new Scene(view, 1280, 820);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAdminScreen() {
        AdminDashboardView view = new AdminDashboardView(() -> showLobbyScreen());
        Scene scene = new Scene(view, 1280, 820);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void logout() {
        if (wsClient != null) {
            wsClient.close();
            wsClient = null;
        }
        ApiClient.clearSession();
        currentUsername = "";
        showLoginScreen();
    }

    private void showGameScreen() {
        gameEngine = new PokerGameEngine();
        gameEngine.addListener(this);

        // Header
        headerNode = new HeaderNode(this);

        // Table
        tableContainer = new PokerTableContainer(gameEngine.getPlayers());

        // History
        historyPanel = new HistoryPanelNode();

        // Control Panel
        controlPanel = new ControlPanelView();
        controlPanel.setActionHandler(this);

        // Main Layout
        BorderPane centerLayout = new BorderPane();
        centerLayout.setCenter(tableContainer);
        centerLayout.setBottom(controlPanel);

        BorderPane mainRoot = new BorderPane();
        mainRoot.setTop(headerNode);
        mainRoot.setCenter(centerLayout);
        mainRoot.setRight(historyPanel);

        // Apply Red Velvet Casino Wallpaper to main window
        Image bgImg = AssetLoader.getBackgroundImage();
        if (bgImg != null) {
            BackgroundImage myBI = new BackgroundImage(bgImg,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, true));
            mainRoot.setBackground(new Background(myBI));
        } else {
            mainRoot.setStyle("-fx-background-color: #07090e;");
        }

        Scene scene = new Scene(mainRoot, 1280, 820);
        applyTheme(scene);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial UI Update
        updateAllViews();
        historyPanel.addLog("🃏 Welcome to Texas Hold'em Poker!", "system");
        historyPanel.addLog("Connected to Server as: " + currentUsername, "system");
    }

    private void applyTheme(Scene scene) {
        var theme = Main.class.getResource("/poker-game.css");
        if (theme != null) {
            scene.getStylesheets().add(theme.toExternalForm());
        }
    }

    private void updateAllViews() {
        Platform.runLater(() -> {
            headerNode.updateInfo(gameEngine.getHandCount(), gameEngine.getSmallBlind(), gameEngine.getBigBlind());
            tableContainer.updateView(gameEngine);
            controlPanel.updateControls(gameEngine);
        });
    }

    // --- ControlPanelView.ActionHandler ---
    @Override
    public void onAction(String actionType, int amount) {
        if (wsClient != null && wsClient.isOpen()) {
            wsClient.send("ACTION:" + actionType + ":" + amount + " by " + currentUsername);
        }
        gameEngine.processHumanAction(actionType, amount);
    }

    @Override
    public void onDealNextHand() {
        if (wsClient != null && wsClient.isOpen()) {
            wsClient.send("DEAL_HAND by " + currentUsername);
        }
        gameEngine.startNewHand();
    }

    @Override
    public void onEmojiTriggered(String emoji) {
        if (wsClient != null && wsClient.isOpen()) {
            wsClient.send("EMOJI:" + emoji + " by " + currentUsername);
        }
        tableContainer.triggerEmojiOnHero(emoji);
    }

    // --- HeaderNode.HeaderActions ---
    @Override
    public void onResetGame() {
        for (Player p : gameEngine.getPlayers()) {
            p.setChips(100000);
            p.setStatus(Player.PlayerStatus.ACTIVE);
        }
        historyPanel.addLog("🔄 All player stacks reset to $100,000", "system");
        updateAllViews();
    }

    @Override
    public void onSwitchViewpoint(int seatIndex) {
        tableContainer.setHeroSeatIndex(seatIndex);
        historyPanel.addLog("🔄 Viewpoint rotated to Seat #" + seatIndex + " (" + gameEngine.getPlayers().get(seatIndex).getName() + ")", "system");
        updateAllViews();
    }

    @Override
    public void onLeaveRoom() {
        showLobbyScreen();
    }

    // --- PokerGameEngine.GameEventListener ---
    @Override
    public void onGameStateChanged() {
        updateAllViews();
    }

    @Override
    public void onMessageLogged(String message, String category) {
        Platform.runLater(() -> {
            historyPanel.addLog(message, category);
        });
    }

    @Override
    public void onPlayerAction(Player player, String actionDescription) {
        updateAllViews();
    }

    @Override
    public void onShowdown(List<Player> winners, int amountWon) {
        updateAllViews();
        Platform.runLater(() -> {
            tableContainer.triggerShowdownWin(winners, amountWon);
        });
    }

    @Override
    public void stop() throws Exception {
        if (wsClient != null) {
            wsClient.close();
        }
        super.stop();
    }
}
