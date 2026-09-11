package poker.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminDashboardView extends BorderPane {

    public interface AdminActions {
        void onBackToLobby();
    }

    public AdminDashboardView(AdminActions actions) {
        // Background
        Image bgImg = AssetLoader.getLobbyBackgroundImage();
        if (bgImg != null) {
            BackgroundImage myBI = new BackgroundImage(bgImg,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, true));
            this.setBackground(new Background(myBI));
        } else {
            this.setStyle("-fx-background-color: #07090e;");
        }

        // Top Header
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: rgba(15, 20, 30, 0.9); -fx-border-color: #ef4444; -fx-border-width: 0 0 2 0;");

        Button btnBack = new Button("⬅️ Back to Lobby");
        btnBack.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnBack.setOnAction(e -> actions.onBackToLobby());

        Label title = new Label("🛠️ SYSTEM ADMIN DASHBOARD");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#ef4444"));

        topBar.getChildren().addAll(btnBack, title);
        this.setTop(topBar);

        // Center Content
        VBox container = new VBox(20);
        container.setPadding(new Insets(25, 40, 25, 40));

        // System Overview Cards
        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER);
        statsCards.getChildren().addAll(
                createCard("Total Users", "1,248", "#3b82f6"),
                createCard("Online Users", "84", "#10b981"),
                createCard("Active Rooms", "12", "#f59e0b"),
                createCard("System Chips", "$85.4M", "#8b5cf6")
        );

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: rgba(20, 28, 40, 0.9);");

        // Tab 1: User Management
        Tab userTab = new Tab("👥 User Management");
        userTab.setClosable(false);
        VBox userBox = createUserManagementTab();
        userTab.setContent(userBox);

        // Tab 2: Room & Game Monitoring
        Tab roomTab = new Tab("🃏 Active Games & Rooms");
        roomTab.setClosable(false);
        VBox roomBox = createRoomMonitoringTab();
        roomTab.setContent(roomBox);

        tabPane.getTabs().addAll(userTab, roomTab);

        container.getChildren().addAll(statsCards, tabPane);
        this.setCenter(container);
    }

    @SuppressWarnings("deprecation")
    private VBox createUserManagementTab() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        TableView<UserModel> userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<UserModel, String> colId = new TableColumn<>("User ID");
        colId.setCellValueFactory(data -> data.getValue().idProperty());

        TableColumn<UserModel, String> colName = new TableColumn<>("Username");
        colName.setCellValueFactory(data -> data.getValue().nameProperty());

        TableColumn<UserModel, String> colChips = new TableColumn<>("Total Chips");
        colChips.setCellValueFactory(data -> data.getValue().chipsProperty());

        TableColumn<UserModel, String> colStatus = new TableColumn<>("Account Status");
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        TableColumn<UserModel, Void> colAction = new TableColumn<>("Admin Action");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnToggle = new Button("Toggle Lock");
            {
                btnToggle.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btnToggle.setOnAction(e -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    if ("Active".equals(user.statusProperty().get())) {
                        user.statusProperty().set("Banned");
                    } else {
                        user.statusProperty().set("Active");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btnToggle);
            }
        });

        userTable.getColumns().add(colId);
        userTable.getColumns().add(colName);
        userTable.getColumns().add(colChips);
        userTable.getColumns().add(colStatus);
        userTable.getColumns().add(colAction);

        userTable.getItems().add(new UserModel("#U-1001", "PokerKing_99", "$4,250,000", "Active"));
        userTable.getItems().add(new UserModel("#U-1002", "Cheater_X", "$0", "Banned"));
        userTable.getItems().add(new UserModel("#U-1003", "LuckyQueen", "$2,950,000", "Active"));
        userTable.getItems().add(new UserModel("#U-1004", "SpamBot_01", "$10,000", "Banned"));

        box.getChildren().add(userTable);
        return box;
    }

    private VBox createRoomMonitoringTab() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        TableView<String> roomTable = new TableView<>();
        Label label = new Label("Live Rooms Monitored Real-time");
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        box.getChildren().addAll(label);
        return box;
    }

    private VBox createCard(String title, String val, String colorHex) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15, 25, 15, 25));
        card.setMinWidth(160);
        card.setStyle("-fx-background-color: rgba(20, 28, 45, 0.95); -fx-border-color: " + colorHex + "; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label valLbl = new Label(val);
        valLbl.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        valLbl.setTextFill(Color.web(colorHex));

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLbl.setTextFill(Color.LIGHTGRAY);

        card.getChildren().addAll(valLbl, titleLbl);
        return card;
    }

    public static class UserModel {
        private final javafx.beans.property.StringProperty id;
        private final javafx.beans.property.StringProperty name;
        private final javafx.beans.property.StringProperty chips;
        private final javafx.beans.property.StringProperty status;

        public UserModel(String id, String name, String chips, String status) {
            this.id = new javafx.beans.property.SimpleStringProperty(id);
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.chips = new javafx.beans.property.SimpleStringProperty(chips);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }

        public javafx.beans.property.StringProperty idProperty() { return id; }
        public javafx.beans.property.StringProperty nameProperty() { return name; }
        public javafx.beans.property.StringProperty chipsProperty() { return chips; }
        public javafx.beans.property.StringProperty statusProperty() { return status; }
    }
}
