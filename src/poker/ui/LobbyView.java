package poker.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LobbyView extends BorderPane {

    public interface LobbyActions {
        void onJoinRoom(String roomId, boolean isPrivate, boolean isSpectator);
        void onCreateRoomRequested();
        void onOpenLeaderboard();
        void onOpenProfile();
        void onOpenAdmin();
        void onLogout();
    }

    private final LobbyActions actions;
    private final String currentUsername;

    public LobbyView(String username, LobbyActions actions) {
        this.currentUsername = username;
        this.actions = actions;

        // Set Background
        Image bgImg = AssetLoader.getAuthBackgroundImage();
        if (bgImg != null) {
            BackgroundImage myBI = new BackgroundImage(bgImg,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, true));
            this.setBackground(new Background(myBI));
        } else {
            this.setStyle("-fx-background-color: #07090e;");
        }

        // Top Header
        HBox topBar = createTopBar();
        this.setTop(topBar);

        // Center Content: Room List & Controls
        VBox centerBox = createCenterContent();
        this.setCenter(centerBox);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(25);
        topBar.setPadding(new Insets(20, 40, 10, 40));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: transparent;");

        // --- Top-Left: Player Profile Avatar with Gold Frame ---
        // Pick one of the 6 player avatars deterministically based on username
        int avatarIndex = (Math.abs(currentUsername.hashCode()) % 6) + 1;
        Image rawAvatarImg = AssetLoader.getAvatarImage("avatar_player_" + avatarIndex + ".png");
        Image frameImg = AssetLoader.getAvatarFrameImage();

        StackPane avatarStack = new StackPane();
        avatarStack.setPrefSize(75, 75);

        if (rawAvatarImg != null) {
            javafx.scene.image.ImageView avatarView = new javafx.scene.image.ImageView(rawAvatarImg);
            avatarView.setFitWidth(50);
            avatarView.setFitHeight(50);
            avatarView.setPreserveRatio(true);

            // Circular clip for avatar
            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(25, 25, 25);
            avatarView.setClip(clip);

            avatarStack.getChildren().add(avatarView);
        }

        if (frameImg != null) {
            javafx.scene.image.ImageView frameView = new javafx.scene.image.ImageView(frameImg);
            frameView.setFitWidth(75);
            frameView.setFitHeight(75);
            frameView.setPreserveRatio(true);
            avatarStack.getChildren().add(frameView);
        }

        VBox userDetailBox = new VBox(4);
        userDetailBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(currentUsername);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.WHITE);

        Label statusLabel = new Label("🟢 Online");
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        statusLabel.setTextFill(Color.LIGHTGREEN);

        userDetailBox.getChildren().addAll(nameLabel, statusLabel);

        HBox profileSection = new HBox(12, avatarStack, userDetailBox);
        profileSection.setAlignment(Pos.CENTER_LEFT);
        profileSection.setCursor(javafx.scene.Cursor.HAND);

        // Click on Avatar or Profile info opens Profile & Stats dialog
        profileSection.setOnMouseClicked(e -> actions.onOpenProfile());

        // Hover scale effect
        profileSection.setOnMouseEntered(e -> {
            profileSection.setScaleX(1.05);
            profileSection.setScaleY(1.05);
        });
        profileSection.setOnMouseExited(e -> {
            profileSection.setScaleX(1.0);
            profileSection.setScaleY(1.0);
        });

        // --- Middle: Balance Chips Display ---
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        HBox balanceBox = new HBox(12);
        balanceBox.setAlignment(Pos.CENTER);
        balanceBox.setPadding(new Insets(10, 28, 10, 20));
        balanceBox.setStyle("-fx-background-color: linear-gradient(to right, #1c1917, #2d2418); " +
                "-fx-background-radius: 25; " +
                "-fx-border-color: linear-gradient(to right, #eab308, #ca8a04); " +
                "-fx-border-radius: 25; -fx-border-width: 2; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(234, 179, 8, 0.3), 10, 0, 0, 0);");

        Image chipStackImg = AssetLoader.loadImage("chips/chip_stack.png");
        if (chipStackImg == null) chipStackImg = AssetLoader.loadImage("chips/chip_gold.png");

        if (chipStackImg != null) {
            javafx.scene.image.ImageView chipIconView = new javafx.scene.image.ImageView(chipStackImg);
            chipIconView.setFitWidth(40);
            chipIconView.setFitHeight(40);
            chipIconView.setPreserveRatio(true);
            balanceBox.getChildren().add(chipIconView);
        } else {
            Label chipIcon = new Label("💰");
            chipIcon.setFont(Font.font("Arial", 22));
            balanceBox.getChildren().add(chipIcon);
        }

        VBox balanceTextContainer = new VBox(2);
        balanceTextContainer.setAlignment(Pos.CENTER_LEFT);

        Label balanceTitle = new Label("CHIPS BALANCE");
        balanceTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        balanceTitle.setTextFill(Color.web("#ca8a04"));

        Label balanceLabel = new Label("$100,000");
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        balanceLabel.setTextFill(Color.GOLD);

        balanceTextContainer.getChildren().addAll(balanceTitle, balanceLabel);
        balanceBox.getChildren().add(balanceTextContainer);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // --- Top-Right: Admin Button (Logout button removed from outer interface) ---
        Button btnAdmin = new Button("🛠️ Admin");
        btnAdmin.setStyle("-fx-background-color: #2a354c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnAdmin.setOnAction(e -> actions.onOpenAdmin());
        if (!"admin".equalsIgnoreCase(currentUsername)) {
            btnAdmin.setVisible(false);
            btnAdmin.setManaged(false);
        }

        HBox rightSection = new HBox(12, btnAdmin);
        rightSection.setAlignment(Pos.CENTER_RIGHT);

        topBar.getChildren().addAll(profileSection, spacer1, balanceBox, spacer2, rightSection);
        return topBar;
    }

    private VBox createCenterContent() {
        VBox container = new VBox(35);
        container.setPadding(new Insets(40, 50, 40, 50));
        container.setAlignment(Pos.CENTER);

        // Center Logo
        Image logoImg = AssetLoader.getLogoImage();
        if (logoImg != null) {
            javafx.scene.image.ImageView centerLogo = new javafx.scene.image.ImageView(logoImg);
            centerLogo.setFitWidth(260);
            centerLogo.setPreserveRatio(true);
            container.getChildren().add(centerLogo);
        }

        // 3 Main Icon Buttons in 1 Horizontal Row
        HBox buttonRow = new HBox(45);
        buttonRow.setAlignment(Pos.CENTER);

        // Button 1: Danh sách phòng (🃏)
        VBox btnRoomList = createIconActionButton(AssetLoader.getLobbyRoomListButtonImage(), "Danh sách phòng");
        btnRoomList.setOnMouseClicked(e -> showRoomListDialog());

        // Button 2: Tạo phòng (➕)
        VBox btnCreateRoom = createIconActionButton(AssetLoader.getLobbyCreateRoomButtonImage(), "Tạo phòng");
        btnCreateRoom.setOnMouseClicked(e -> actions.onCreateRoomRequested());

        // Button 3: Xếp hạng (🏆)
        VBox btnLeaderboard = createIconActionButton(AssetLoader.getLobbyLeaderboardButtonImage(), "Xếp hạng");
        btnLeaderboard.setOnMouseClicked(e -> actions.onOpenLeaderboard());

        buttonRow.getChildren().addAll(btnRoomList, btnCreateRoom, btnLeaderboard);

        // Populate Initial Active Rooms for data model
        allRooms = javafx.collections.FXCollections.observableArrayList();
        allRooms.add(new RoomModel("#101", "High Rollers VIP", "PokerKing", "4/6", "$500/$1000", "$100,000", "Public", "Playing"));
        allRooms.add(new RoomModel("#102", "Beginners Table", "LuckyCat", "2/6", "$10/$20", "$2,000", "Public", "Waiting"));
        allRooms.add(new RoomModel("#103", "Private Club #7", "ShadowPlayer", "5/6", "$100/$200", "$20,000", "Private", "Waiting"));
        allRooms.add(new RoomModel("#104", "Pro Tournament", "AceSpade", "6/6", "$250/$500", "$50,000", "Public", "Full"));
        filteredRooms = new javafx.collections.transformation.FilteredList<>(allRooms, p -> true);

        container.getChildren().add(buttonRow);
        return container;
    }

    private VBox createIconActionButton(Image iconImg, String labelText) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setCursor(javafx.scene.Cursor.HAND);

        if (iconImg != null) {
            javafx.scene.image.ImageView iconView = new javafx.scene.image.ImageView(iconImg);
            iconView.setFitWidth(110);
            iconView.setFitHeight(110);
            iconView.setPreserveRatio(true);
            box.getChildren().add(iconView);
        }

        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.GOLD);
        box.getChildren().add(label);

        // Subtle hover animation
        box.setOnMouseEntered(e -> {
            box.setScaleX(1.08);
            box.setScaleY(1.08);
            label.setTextFill(Color.WHITE);
        });
        box.setOnMouseExited(e -> {
            box.setScaleX(1.0);
            box.setScaleY(1.0);
            label.setTextFill(Color.GOLD);
        });

        return box;
    }

    public void showRoomListDialog() {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("🎴 DANH SÁCH PHÒNG POKER");
        
        javafx.stage.Window owner = this.getScene() != null ? this.getScene().getWindow() : null;
        if (owner != null) {
            dialog.initOwner(owner);
            dialog.setX(owner.getX());
            dialog.setY(owner.getY());
        }

        double width = owner != null ? owner.getWidth() : 1280;
        double height = owner != null ? owner.getHeight() : 820;

        VBox container = new VBox(20);
        container.setPadding(new Insets(20, 35, 25, 35));

        Image roomBg = AssetLoader.getRoomListBackgroundImage();
        if (roomBg != null) {
            BackgroundImage myBI = new BackgroundImage(roomBg,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, true));
            container.setBackground(new Background(myBI));
        } else {
            container.setStyle("-fx-background-color: #0b1329;");
        }

        // Top Header Title Bar
        HBox filterBar = new HBox(15);
        filterBar.setPadding(new Insets(12, 20, 12, 20));
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setStyle("-fx-background-color: rgba(11, 19, 41, 0.96); " +
                "-fx-border-color: linear-gradient(to right, #ca8a04, #3b82f6); " +
                "-fx-border-width: 0 0 2 0; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.85), 14, 0, 0, 4);");

        Button btnBack = new Button("◄ Quay lại Sảnh");
        btnBack.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btnBack.setStyle("-fx-background-color: linear-gradient(to right, #1e293b, #334155); " +
                "-fx-text-fill: white; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16 8 16;");
        btnBack.setOnMouseEntered(e -> btnBack.setStyle("-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6); " +
                "-fx-text-fill: white; -fx-border-color: #60a5fa; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16 8 16;"));
        btnBack.setOnMouseExited(e -> btnBack.setStyle("-fx-background-color: linear-gradient(to right, #1e293b, #334155); " +
                "-fx-text-fill: white; -fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16 8 16;"));
        btnBack.setOnAction(e -> dialog.close());

        Label dialogTitle = new Label("🎴 DANH SÁCH BÀN CHƠI POKER");
        dialogTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        dialogTitle.setTextFill(Color.web("#facc15"));
        dialogTitle.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.9), 6, 0.8, 0, 2);");

        filterBar.getChildren().addAll(btnBack, dialogTitle);

        // Main Content Area (Left Controls + Right Table View)
        HBox mainLayout = new HBox(25);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);

        // --- LEFT SIDEBAR (Controls & Quick Play) ---
        VBox leftSidebar = new VBox(20);
        leftSidebar.setPrefWidth(250);
        leftSidebar.setAlignment(Pos.TOP_CENTER);
        leftSidebar.setPadding(new Insets(10, 0, 10, 0));

        // 1. CHƠI NGAY (Big Round Metallic Green Button)
        VBox quickPlayBox = new VBox(8);
        quickPlayBox.setAlignment(Pos.CENTER);

        Button btnQuickPlay = new Button("CHƠI\nNGAY");
        btnQuickPlay.setPrefSize(120, 120);
        btnQuickPlay.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        btnQuickPlay.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        btnQuickPlay.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 60%, #34d399, #059669); " +
                "-fx-text-fill: white; -fx-background-radius: 100; " +
                "-fx-border-color: linear-gradient(to bottom right, #facc15, #ca8a04); -fx-border-radius: 100; -fx-border-width: 4; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(34, 197, 94, 0.6), 18, 0, 0, 4); -fx-cursor: hand;");
        btnQuickPlay.setOnMouseEntered(e -> btnQuickPlay.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 60%, #4ade80, #10b981); " +
                "-fx-text-fill: white; -fx-background-radius: 100; " +
                "-fx-border-color: #fde047; -fx-border-radius: 100; -fx-border-width: 4; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(74, 222, 128, 0.95), 22, 0, 0, 4); -fx-cursor: hand;"));
        btnQuickPlay.setOnMouseExited(e -> btnQuickPlay.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 60%, #34d399, #059669); " +
                "-fx-text-fill: white; -fx-background-radius: 100; " +
                "-fx-border-color: linear-gradient(to bottom right, #facc15, #ca8a04); -fx-border-radius: 100; -fx-border-width: 4; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(34, 197, 94, 0.6), 18, 0, 0, 4); -fx-cursor: hand;"));
        btnQuickPlay.setOnAction(e -> {
            if (!filteredRooms.isEmpty()) {
                dialog.close();
                actions.onJoinRoom(filteredRooms.get(0).getId(), false, false);
            } else {
                actions.onCreateRoomRequested();
            }
        });

        quickPlayBox.getChildren().add(btnQuickPlay);

        // 2. MỨC TIỀN Filter Box
        VBox moneyFilterBox = new VBox(8);
        moneyFilterBox.setPadding(new Insets(12));
        moneyFilterBox.setStyle("-fx-background-color: rgba(15, 23, 42, 0.95); -fx-border-color: #facc15; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 1.5;");

        Label lblMoneyTitle = new Label("MỨC TIỀN");
        lblMoneyTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblMoneyTitle.setTextFill(Color.web("#facc15"));

        ComboBox<String> comboMoneyFilter = new ComboBox<>();
        comboMoneyFilter.getItems().addAll("Tất cả", "$2k+", "$10k+", "$50k+");
        comboMoneyFilter.setValue("Tất cả");
        comboMoneyFilter.setMaxWidth(Double.MAX_VALUE);
        comboMoneyFilter.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #facc15; -fx-font-weight: bold; -fx-font-size: 14px;");

        moneyFilterBox.getChildren().addAll(lblMoneyTitle, comboMoneyFilter);

        // Search Field
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Tìm tên/ID phòng...");
        searchField.setStyle("-fx-font-size: 13px; -fx-background-color: #0f172a; -fx-text-fill: white; -fx-padding: 8; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6;");

        Region sidebarSpacer = new Region();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);

        leftSidebar.getChildren().addAll(quickPlayBox, moneyFilterBox, searchField);

        // --- RIGHT TABLE AREA ---
        VBox rightTableArea = new VBox(0);
        HBox.setHgrow(rightTableArea, Priority.ALWAYS);

        // Top Tabs: TẤT CẢ, BÀN TRỐNG, CHƯA CHƠI
        HBox tabsBar = new HBox(8);
        tabsBar.setPadding(new Insets(0, 0, 5, 0));

        Button tabAll = new Button("TẤT CẢ");
        Button tabEmpty = new Button("BÀN TRỐNG");
        Button tabWaiting = new Button("CHƯA CHƠI");

        String activeTabStyle = "-fx-background-color: #22c55e; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6 6 0 0; -fx-padding: 8 20 8 20; -fx-cursor: hand;";
        String inactiveTabStyle = "-fx-background-color: #1e293b; -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6 6 0 0; -fx-padding: 8 20 8 20; -fx-cursor: hand;";

        tabAll.setStyle(activeTabStyle);
        tabEmpty.setStyle(inactiveTabStyle);
        tabWaiting.setStyle(inactiveTabStyle);

        tabsBar.getChildren().addAll(tabAll, tabEmpty, tabWaiting);

        // Table Header
        HBox tableHeader = new HBox(10);
        tableHeader.setPadding(new Insets(12, 16, 12, 16));
        tableHeader.setStyle("-fx-background-color: #0b1329; -fx-border-color: #facc15; -fx-border-width: 0 0 2 0;");

        Label colBlinds = new Label("Mức Tiền");
        colBlinds.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        colBlinds.setTextFill(Color.web("#facc15"));
        colBlinds.setPrefWidth(120);

        Label colRoomName = new Label("Tên Phòng");
        colRoomName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        colRoomName.setTextFill(Color.web("#facc15"));
        colRoomName.setPrefWidth(200);

        Label colHost = new Label("Chủ phòng");
        colHost.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        colHost.setTextFill(Color.web("#facc15"));
        colHost.setPrefWidth(140);

        Label colSeats = new Label("Ghế trống");
        colSeats.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        colSeats.setTextFill(Color.web("#facc15"));
        colSeats.setPrefWidth(150);

        Label colBuyIn = new Label("Giới Hạn (Buy-in)");
        colBuyIn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        colBuyIn.setTextFill(Color.web("#facc15"));
        colBuyIn.setPrefWidth(140);

        Label colAction = new Label("Thao tác");
        colAction.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        colAction.setTextFill(Color.web("#facc15"));

        tableHeader.getChildren().addAll(colBlinds, colRoomName, colHost, colSeats, colBuyIn, colAction);

        // Table Rows List Box
        VBox rowsBox = new VBox(0);
        rowsBox.setStyle("-fx-background-color: transparent;");

        Runnable updateTableRows = () -> {
            rowsBox.getChildren().clear();
            int idx = 0;
            for (RoomModel room : filteredRooms) {
                rowsBox.getChildren().add(createTableRow(room, dialog, idx % 2 == 0));
                idx++;
            }
        };

        updateTableRows.run();
        filteredRooms.addListener((javafx.collections.ListChangeListener<RoomModel>) c -> updateTableRows.run());

        // Tab selection logic
        tabAll.setOnAction(e -> {
            tabAll.setStyle(activeTabStyle);
            tabEmpty.setStyle(inactiveTabStyle);
            tabWaiting.setStyle(inactiveTabStyle);
            applyTableFilters(searchField, comboMoneyFilter, "ALL");
        });
        tabEmpty.setOnAction(e -> {
            tabAll.setStyle(inactiveTabStyle);
            tabEmpty.setStyle(activeTabStyle);
            tabWaiting.setStyle(inactiveTabStyle);
            applyTableFilters(searchField, comboMoneyFilter, "EMPTY");
        });
        tabWaiting.setOnAction(e -> {
            tabAll.setStyle(inactiveTabStyle);
            tabEmpty.setStyle(inactiveTabStyle);
            tabWaiting.setStyle(activeTabStyle);
            applyTableFilters(searchField, comboMoneyFilter, "WAITING");
        });

        searchField.textProperty().addListener((obs, oldV, newV) -> applyTableFilters(searchField, comboMoneyFilter, "ALL"));
        comboMoneyFilter.valueProperty().addListener((obs, oldV, newV) -> applyTableFilters(searchField, comboMoneyFilter, "ALL"));

        ScrollPane scrollTable = new ScrollPane(rowsBox);
        scrollTable.setFitToWidth(true);
        VBox.setVgrow(scrollTable, Priority.ALWAYS);
        scrollTable.setStyle("-fx-background: transparent; -fx-background-color: rgba(11, 19, 41, 0.7);");

        rightTableArea.getChildren().addAll(tabsBar, tableHeader, scrollTable);

        mainLayout.getChildren().addAll(leftSidebar, rightTableArea);

        container.getChildren().addAll(filterBar, mainLayout);
        Scene scene = new Scene(container, width, height);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private HBox createTableRow(RoomModel room, Stage dialog, boolean isEven) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setAlignment(Pos.CENTER_LEFT);

        String bgStyle = isEven ? "rgba(15, 23, 42, 0.88)" : "rgba(26, 36, 56, 0.88)";
        row.setStyle("-fx-background-color: " + bgStyle + "; -fx-border-color: #1e293b; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #6b1724; -fx-border-color: #facc15; -fx-border-width: 1; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + bgStyle + "; -fx-border-color: #1e293b; -fx-border-width: 0 0 1 0;"));

        // 1. Mức Tiền
        Label blindsLbl = new Label(room.blindsProperty().get());
        blindsLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        blindsLbl.setTextFill(Color.web("#facc15"));
        blindsLbl.setPrefWidth(120);

        // 2. Tên Phòng
        Label nameLbl = new Label(room.getName() + " (" + room.getId() + ")");
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setPrefWidth(200);

        // 3. Chủ phòng
        Label hostLbl = new Label(room.getHost());
        hostLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        hostLbl.setTextFill(Color.web("#cbd5e1"));
        hostLbl.setPrefWidth(140);

        // 4. Ghế trống
        HBox seatsVisual = createSeatsVisual(room.playersProperty().get());
        seatsVisual.setPrefWidth(150);

        // 5. Giới hạn (Buy-in)
        Label buyInLbl = new Label(room.buyInProperty().get());
        buyInLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        buyInLbl.setTextFill(Color.web("#4ade80"));
        buyInLbl.setPrefWidth(140);

        // 6. Action Button
        Button btnJoinRow = new Button("VÀO BÀN");
        btnJoinRow.setStyle("-fx-background-color: linear-gradient(to right, #059669, #10b981); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6; -fx-cursor: hand;");
        btnJoinRow.setOnAction(e -> {
            dialog.close();
            actions.onJoinRoom(room.getId(), false, false);
        });

        row.setOnMouseClicked(e -> {
            dialog.close();
            actions.onJoinRoom(room.getId(), false, false);
        });

        row.getChildren().addAll(blindsLbl, nameLbl, hostLbl, seatsVisual, buyInLbl, btnJoinRow);
        return row;
    }

    private HBox createSeatsVisual(String playersStr) {
        HBox box = new HBox(4);
        box.setAlignment(Pos.CENTER_LEFT);

        int current = 3;
        int max = 6;
        if (playersStr != null && playersStr.contains("/")) {
            try {
                String[] parts = playersStr.split("/");
                current = Integer.parseInt(parts[0].trim());
                max = Integer.parseInt(parts[1].trim());
            } catch (Exception ignored) {}
        }

        for (int i = 0; i < max; i++) {
            Region block = new Region();
            block.setPrefSize(10, 14);
            if (i < current) {
                block.setStyle("-fx-background-color: #22c55e; -fx-background-radius: 2;");
            } else {
                block.setStyle("-fx-background-color: #334155; -fx-background-radius: 2;");
            }
            box.getChildren().add(block);
        }

        Label txt = new Label(" " + current + "/" + max);
        txt.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        txt.setTextFill(Color.web("#94a3b8"));
        box.getChildren().add(txt);
        return box;
    }

    private void applyTableFilters(TextField search, ComboBox<String> moneyFilter, String tabMode) {
        String searchText = search.getText().toLowerCase().trim();
        String moneyVal = moneyFilter.getValue();

        filteredRooms.setPredicate(room -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    room.getName().toLowerCase().contains(searchText) ||
                    room.getId().toLowerCase().contains(searchText) ||
                    room.getHost().toLowerCase().contains(searchText);

            boolean matchesTab = true;
            if ("EMPTY".equalsIgnoreCase(tabMode)) {
                String playersStr = room.playersProperty().get();
                if (playersStr != null && playersStr.contains("/")) {
                    try {
                        String[] parts = playersStr.split("/");
                        int current = Integer.parseInt(parts[0].trim());
                        int max = Integer.parseInt(parts[1].trim());
                        matchesTab = current < max;
                    } catch (Exception ignored) {}
                }
            } else if ("WAITING".equalsIgnoreCase(tabMode)) {
                matchesTab = "Waiting".equalsIgnoreCase(room.getStatus());
            }

            boolean matchesMoney = true;
            if (moneyVal != null && !moneyVal.equalsIgnoreCase("Tất cả")) {
                matchesMoney = room.getBuyIn() != null && room.getBuyIn().contains(moneyVal.replace("+", ""));
            }

            return matchesSearch && matchesTab && matchesMoney;
        });
    }

    private javafx.collections.ObservableList<RoomModel> allRooms;
    private javafx.collections.transformation.FilteredList<RoomModel> filteredRooms;

    public void addNewRoom(RoomModel room) {
        if (allRooms != null) {
            allRooms.add(0, room);
        }
    }

    // Helper model for TableView
    public static class RoomModel {
        private final javafx.beans.property.StringProperty id;
        private final javafx.beans.property.StringProperty name;
        private final javafx.beans.property.StringProperty host;
        private final javafx.beans.property.StringProperty players;
        private final javafx.beans.property.StringProperty blinds;
        private final javafx.beans.property.StringProperty buyIn;
        private final javafx.beans.property.StringProperty type;
        private final javafx.beans.property.StringProperty status;

        public RoomModel(String id, String name, String host, String players, String blinds, String buyIn, String type, String status) {
            this.id = new javafx.beans.property.SimpleStringProperty(id);
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.host = new javafx.beans.property.SimpleStringProperty(host);
            this.players = new javafx.beans.property.SimpleStringProperty(players);
            this.blinds = new javafx.beans.property.SimpleStringProperty(blinds);
            this.buyIn = new javafx.beans.property.SimpleStringProperty(buyIn);
            this.type = new javafx.beans.property.SimpleStringProperty(type);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }

        public javafx.beans.property.StringProperty idProperty() { return id; }
        public javafx.beans.property.StringProperty nameProperty() { return name; }
        public javafx.beans.property.StringProperty hostProperty() { return host; }
        public javafx.beans.property.StringProperty playersProperty() { return players; }
        public javafx.beans.property.StringProperty blindsProperty() { return blinds; }
        public javafx.beans.property.StringProperty buyInProperty() { return buyIn; }
        public javafx.beans.property.StringProperty typeProperty() { return type; }
        public javafx.beans.property.StringProperty statusProperty() { return status; }

        public String getId() { return id.get(); }
        public String getName() { return name.get(); }
        public String getHost() { return host.get(); }
        public String getBuyIn() { return buyIn.get(); }
        public String getType() { return type.get(); }
        public String getStatus() { return status.get(); }
    }
}
