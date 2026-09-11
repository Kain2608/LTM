package poker.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import poker.client.net.ApiClient;
import poker.model.Friend;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FriendsView extends BorderPane {

    private final VBox friendsListContainer = new VBox(10);
    private final VBox requestsListContainer = new VBox(10);
    private final Gson gson = new Gson();
    private final Label statusLabel = new Label();

    public FriendsView(Runnable onClose) {
        this.setStyle("-fx-background-color: #0b0f19;");
        this.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 15, 0));

        Label titleLabel = new Label("FRIENDS SYSTEM");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#ffd700"));

        Button closeBtn = new Button("Back to Lobby");
        closeBtn.setStyle("-fx-background-color: #374151; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        closeBtn.setOnAction(e -> {
            if (onClose != null) onClose.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleLabel, spacer, closeBtn);
        this.setTop(header);

        // Status notification label
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusLabel.setVisible(false);

        // Center Tab Pane
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-width: 140px; -fx-tab-max-width: 180px;");

        // Tab 1: Friends List
        Tab friendsTab = new Tab("My Friends");
        friendsTab.setClosable(false);
        ScrollPane friendsScroll = createScrollPane(friendsListContainer);
        friendsTab.setContent(friendsScroll);

        // Tab 2: Requests
        Tab requestsTab = new Tab("Friend Requests");
        requestsTab.setClosable(false);
        ScrollPane requestsScroll = createScrollPane(requestsListContainer);
        requestsTab.setContent(requestsScroll);

        // Tab 3: Add Friend
        Tab addTab = new Tab("+ Add Friend");
        addTab.setClosable(false);
        addTab.setContent(createAddFriendPane());

        tabPane.getTabs().addAll(friendsTab, requestsTab, addTab);
        
        VBox centerBox = new VBox(10, statusLabel, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        this.setCenter(centerBox);

        // Load initial data
        loadFriendsData();
    }

    private ScrollPane createScrollPane(VBox container) {
        container.setPadding(new Insets(15));
        container.setAlignment(Pos.TOP_CENTER);
        
        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #111827; -fx-background-color: #111827; -fx-border-color: #1f2937;");
        return scroll;
    }

    private VBox createAddFriendPane() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #111827; -fx-border-color: #1f2937; -fx-border-radius: 8;");

        Label label = new Label("ENTER FRIEND USER ID");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.WHITE);

        TextField friendIdField = new TextField();
        friendIdField.setPromptText("User ID (e.g. 2)");
        friendIdField.setMaxWidth(300);
        friendIdField.setStyle("-fx-font-size: 16px; -fx-background-color: #1f2937; -fx-text-fill: white; -fx-border-color: #374151; -fx-border-radius: 5;");

        Button sendBtn = new Button("SEND FRIEND REQUEST");
        sendBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        sendBtn.setPrefWidth(220);
        sendBtn.setPrefHeight(40);

        sendBtn.setOnAction(e -> {
            String text = friendIdField.getText().trim();
            if (text.isEmpty()) {
                showStatus("Please enter a valid User ID", true);
                return;
            }
            try {
                Long friendId = Long.parseLong(text);
                sendBtn.setDisable(true);
                ApiClient.sendFriendRequest(friendId).thenAccept(success -> {
                    Platform.runLater(() -> {
                        sendBtn.setDisable(false);
                        if (success) {
                            showStatus("Friend request sent successfully!", false);
                            friendIdField.clear();
                            loadFriendsData();
                        } else {
                            showStatus("Failed to send request. Check Friend ID.", true);
                        }
                    });
                });
            } catch (NumberFormatException ex) {
                showStatus("Friend ID must be a number", true);
            }
        });

        box.getChildren().addAll(label, friendIdField, sendBtn);
        return box;
    }

    public void loadFriendsData() {
        // Load Friends List
        ApiClient.fetchFriends().thenAccept(json -> {
            Type listType = new TypeToken<ArrayList<Friend>>(){}.getType();
            List<Friend> friends = gson.fromJson(json, listType);
            Platform.runLater(() -> renderFriendsList(friends));
        });

        // Load Friend Requests
        ApiClient.fetchFriendRequests().thenAccept(json -> {
            Type listType = new TypeToken<ArrayList<Friend>>(){}.getType();
            List<Friend> requests = gson.fromJson(json, listType);
            Platform.runLater(() -> renderRequestsList(requests));
        });
    }

    private void renderFriendsList(List<Friend> friends) {
        friendsListContainer.getChildren().clear();
        if (friends == null || friends.isEmpty()) {
            Label emptyLabel = new Label("No friends added yet.");
            emptyLabel.setTextFill(Color.GRAY);
            friendsListContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Friend f : friends) {
            HBox card = new HBox(15);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(12, 20, 12, 20));
            card.setStyle("-fx-background-color: #1f2937; -fx-background-radius: 8; -fx-border-color: #374151; -fx-border-radius: 8;");

            Label avatarLabel = new Label("👤");
            avatarLabel.setFont(Font.font(20));

            Label nameLabel = new Label(f.getFriendUsername() != null ? f.getFriendUsername() : "Friend #" + f.getFriendId());
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            nameLabel.setTextFill(Color.WHITE);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button removeBtn = new Button("Unfriend");
            removeBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4;");
            removeBtn.setOnAction(e -> {
                ApiClient.removeFriend(f.getFriendshipId()).thenAccept(success -> {
                    Platform.runLater(() -> {
                        if (success) {
                            showStatus("Removed friend", false);
                            loadFriendsData();
                        }
                    });
                });
            });

            card.getChildren().addAll(avatarLabel, nameLabel, spacer, removeBtn);
            friendsListContainer.getChildren().add(card);
        }
    }

    private void renderRequestsList(List<Friend> requests) {
        requestsListContainer.getChildren().clear();
        if (requests == null || requests.isEmpty()) {
            Label emptyLabel = new Label("No pending friend requests.");
            emptyLabel.setTextFill(Color.GRAY);
            requestsListContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Friend r : requests) {
            HBox card = new HBox(15);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(12, 20, 12, 20));
            card.setStyle("-fx-background-color: #1f2937; -fx-background-radius: 8; -fx-border-color: #374151; -fx-border-radius: 8;");

            Label nameLabel = new Label("User #" + r.getFriendId() + " wants to be friends");
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            nameLabel.setTextFill(Color.WHITE);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button acceptBtn = new Button("Accept");
            acceptBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4;");
            acceptBtn.setOnAction(e -> {
                ApiClient.acceptFriendRequest(r.getFriendshipId()).thenAccept(success -> {
                    Platform.runLater(() -> {
                        if (success) {
                            showStatus("Accepted friend request!", false);
                            loadFriendsData();
                        }
                    });
                });
            });

            Button rejectBtn = new Button("Reject");
            rejectBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4;");
            rejectBtn.setOnAction(e -> {
                ApiClient.removeFriend(r.getFriendshipId()).thenAccept(success -> {
                    Platform.runLater(() -> {
                        if (success) {
                            showStatus("Rejected friend request", false);
                            loadFriendsData();
                        }
                    });
                });
            });

            card.getChildren().addAll(nameLabel, spacer, acceptBtn, rejectBtn);
            requestsListContainer.getChildren().add(card);
        }
    }

    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(isError ? Color.RED : Color.LIGHTGREEN);
        statusLabel.setVisible(true);
    }
}

