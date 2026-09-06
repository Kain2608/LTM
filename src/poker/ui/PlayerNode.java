package poker.ui;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import poker.model.Card;
import poker.model.Player;

import java.util.List;

public class PlayerNode extends VBox {
    private final Player player;
    private int visualSeatPosition; // 0: Bottom (Hero View), 1: Bottom-Left, 2: Top-Left, 3: Top-Center, 4: Top-Right, 5: Bottom-Right

    private final ImageView avatarView = new ImageView();
    private final Label nameLabel = new Label();
    private final Label chipsLabel = new Label();
    private final Label levelBadge = new Label("35");
    private final Label flagBadge = new Label("🌐");
    private final HBox cardsBox = new HBox(-12); // Overlapping cards
    private final Label emojiBubble = new Label();
    private final StackPane winBanner = new StackPane();
    private final Label winTextLabel = new Label("WIN");
    private final Label winAmountLabel = new Label();

    private final StackPane avatarContainer = new StackPane();
    private final VBox infoBox = new VBox(1);
    private final HBox betBox = new HBox(4);
    private final ImageView betChipView = new ImageView();
    private final Label betAmountLabel = new Label();
    private final StackPane avatarWithBadges;

    public PlayerNode(Player player, int visualSeatPosition, String levelStr, String flagStr) {
        this.player = player;
        this.visualSeatPosition = visualSeatPosition;
        this.setSpacing(4);
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(160);

        if (levelStr != null) levelBadge.setText(levelStr);
        if (flagStr != null) flagBadge.setText(flagStr);

        // Avatar setup
        avatarView.setFitWidth(60);
        avatarView.setFitHeight(60);
        avatarView.setPreserveRatio(true);
        if (player.getAvatarFileName() != null) {
            avatarView.setImage(AssetLoader.getAvatarImage(player.getAvatarFileName()));
        }

        Circle clip = new Circle(30, 30, 28);
        avatarView.setClip(clip);

        Circle borderCircle = new Circle(30, 30, 29);
        borderCircle.setFill(Color.TRANSPARENT);
        borderCircle.setStroke(Color.web("#d4af37"));
        borderCircle.setStrokeWidth(2.5);

        // Level badge (top-left)
        levelBadge.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #ecf0f1; -fx-font-weight: bold; " +
                "-fx-font-size: 9px; -fx-padding: 1 4; -fx-background-radius: 4; -fx-border-color: #7f8c8d; -fx-border-radius: 4;");

        // Flag badge (bottom-right)
        flagBadge.setStyle("-fx-font-size: 11px;");

        avatarContainer.getChildren().addAll(borderCircle, avatarView);
        
        avatarWithBadges = new StackPane(avatarContainer, levelBadge, flagBadge);
        StackPane.setAlignment(levelBadge, Pos.TOP_LEFT);
        StackPane.setAlignment(flagBadge, Pos.BOTTOM_RIGHT);

        // Name & Chips box
        nameLabel.setText(player.getName());
        nameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 12px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 3, 0, 0, 1);");

        chipsLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 12px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 3, 0, 0, 1);");

        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().addAll(nameLabel, chipsLabel);

        // Cards box setup
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.setMinHeight(72);

        // WIN Banner setup
        winTextLabel.setStyle("-fx-text-fill: linear-gradient(to bottom, #ffffff, #ffeaa7); " +
                "-fx-font-weight: 900; -fx-font-size: 20px; -fx-letter-spacing: 2px; " +
                "-fx-effect: dropshadow(three-pass-box, #d35400, 8, 0, 0, 1);");
        
        winAmountLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 13px;");

        VBox winContent = new VBox(0, winTextLabel, winAmountLabel);
        winContent.setAlignment(Pos.CENTER);

        winBanner.setStyle("-fx-background-color: linear-gradient(to right, rgba(230, 126, 34, 0.9), rgba(241, 196, 15, 0.9)); " +
                "-fx-padding: 4 18; -fx-background-radius: 15; -fx-border-color: #ffffff; -fx-border-radius: 15; -fx-border-width: 1.5;");
        winBanner.setEffect(new DropShadow(15, Color.web("#f39c12")));
        winBanner.getChildren().add(winContent);
        winBanner.setVisible(false);

        // Emoji bubble
        emojiBubble.setStyle("-fx-font-size: 26px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 1);");
        emojiBubble.setVisible(false);

        // Bet display box
        betChipView.setFitWidth(18);
        betChipView.setFitHeight(18);
        betChipView.setPreserveRatio(true);
        betAmountLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold; -fx-font-size: 11px;");
        betBox.setAlignment(Pos.CENTER);
        betBox.setStyle("-fx-background-color: rgba(0,0,0,0.75); -fx-padding: 2 6; -fx-background-radius: 10;");
        betBox.getChildren().addAll(betChipView, betAmountLabel);
        betBox.setVisible(false);

        rebuildLayout();
        updateView(false, false, 0);
    }

    public void setVisualSeatPosition(int visualSeatPosition) {
        this.visualSeatPosition = visualSeatPosition;
        rebuildLayout();
    }

    private void rebuildLayout() {
        this.getChildren().clear();
        StackPane cardsWinStack = new StackPane(cardsBox, winBanner, emojiBubble);

        if (visualSeatPosition == 0) {
            // Visual Seat 0 (Hero Position at Bottom of screen): Cards above avatar
            StackPane.setAlignment(emojiBubble, Pos.TOP_LEFT);
            this.getChildren().addAll(betBox, cardsWinStack, avatarWithBadges, infoBox);
        } else {
            // Visual Seats around top/sides: Cards attached to avatar
            StackPane.setAlignment(emojiBubble, Pos.TOP_RIGHT);
            this.getChildren().addAll(avatarWithBadges, infoBox, cardsWinStack, betBox);
        }
    }

    public void updateView(boolean isTurn, boolean showAllCards, int heroSeatIndex) {
        nameLabel.setText(player.getName());
        chipsLabel.setText("$" + String.format("%,d", player.getChips()));

        // Turn highlight
        if (isTurn) {
            this.setEffect(new DropShadow(22, Color.web("#f1c40f")));
            avatarContainer.setStyle("-fx-border-color: #f39c12; -fx-border-width: 3; -fx-border-radius: 50;");
        } else {
            this.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.6)));
            avatarContainer.setStyle("");
        }

        // Folded / All in
        if (player.getStatus() == Player.PlayerStatus.FOLDED) {
            this.setOpacity(0.45);
        } else {
            this.setOpacity(1.0);
        }

        // Bet display
        if (player.getCurrentBet() > 0) {
            betChipView.setImage(AssetLoader.getChipImageForValue(player.getCurrentBet()));
            betAmountLabel.setText("$" + String.format("%,d", player.getCurrentBet()));
            betBox.setVisible(true);
        } else {
            betBox.setVisible(false);
        }

        // Cards display: Reveal cards if this player is the Local Hero at heroSeatIndex or if Showdown
        boolean isHero = (player.getSeatIndex() == heroSeatIndex);
        cardsBox.getChildren().clear();
        List<Card> holeCards = player.getHoleCards();
        if (holeCards != null && !holeCards.isEmpty()) {
            for (Card card : holeCards) {
                ImageView cardView = new ImageView();
                cardView.setFitWidth(52);
                cardView.setFitHeight(76);
                cardView.setPreserveRatio(true);

                if (isHero || showAllCards || player.getStatus() == Player.PlayerStatus.ALL_IN) {
                    cardView.setImage(AssetLoader.getCardImage(card));
                } else {
                    cardView.setImage(AssetLoader.getCardBackImage("red"));
                }

                DropShadow shadow = new DropShadow(6, Color.rgb(0, 0, 0, 0.7));
                cardView.setEffect(shadow);
                cardsBox.getChildren().add(cardView);
            }
        }
    }

    public void showWin(int amount) {
        winAmountLabel.setText("$" + String.format("%,d", amount));
        winBanner.setVisible(true);

        ScaleTransition st = new ScaleTransition(Duration.millis(400), winBanner);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1.1);
        st.setToY(1.1);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    public void hideWin() {
        winBanner.setVisible(false);
    }

    public void triggerEmoji(String emoji) {
        emojiBubble.setText(emoji);
        emojiBubble.setVisible(true);

        ScaleTransition st = new ScaleTransition(Duration.millis(350), emojiBubble);
        st.setFromX(0.2);
        st.setFromY(0.2);
        st.setToX(1.3);
        st.setToY(1.3);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.setOnFinished(e -> {
            javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(Duration.seconds(2));
            pt.setOnFinished(ev -> emojiBubble.setVisible(false));
            pt.play();
        });
        st.play();
    }

    public Player getPlayer() {
        return player;
    }
}
