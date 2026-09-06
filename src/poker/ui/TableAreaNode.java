package poker.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Circle;
import poker.model.Card;
import poker.model.PokerGameEngine;

import java.util.List;

public class TableAreaNode extends StackPane {
    private final HBox communityCardsBox = new HBox(8);
    private final Label potLabel = new Label("Total Pot : $0");
    private final VBox centerBox = new VBox(12);
    private final StackPane dealerButton = new StackPane();
    private final ImageView tableImageView = new ImageView();

    public TableAreaNode() {
        buildTableUI();
    }

    private void buildTableUI() {
        this.setPrefSize(920, 480);
        this.setMinSize(840, 440);

        // 1. High-Res OpenDecks Golden Rim Poker Table PNG
        Image tableImg = AssetLoader.getTableImage();
        if (tableImg != null) {
            tableImageView.setImage(tableImg);
            tableImageView.setFitWidth(860);
            tableImageView.setFitHeight(440);
            tableImageView.setPreserveRatio(true);
            tableImageView.setEffect(new DropShadow(30, Color.rgb(0, 0, 0, 0.95)));
        }

        // 2. Dealer Button (Golden D coin)
        Circle dCoin = new Circle(14);
        dCoin.setFill(LinearGradient.valueOf("linear-gradient(to bottom, #f39c12, #d4af37)"));
        dCoin.setStroke(Color.web("#ffffff"));
        dCoin.setStrokeWidth(1.5);
        dCoin.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.7)));

        Label dLabel = new Label("D");
        dLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: 900; -fx-font-size: 13px;");

        dealerButton.getChildren().addAll(dCoin, dLabel);
        dealerButton.setTranslateX(-240);
        dealerButton.setTranslateY(80);

        // 3. Total Pot Label
        potLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold; -fx-font-size: 16px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.9), 6, 0, 0, 1);");

        // 4. Community Cards Box
        communityCardsBox.setAlignment(Pos.CENTER);
        communityCardsBox.setMinHeight(105);

        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(potLabel, communityCardsBox);

        this.getChildren().addAll(tableImageView, dealerButton, centerBox);
    }

    public void updateTable(PokerGameEngine engine, int heroSeatIndex) {
        potLabel.setText("Total Pot : $" + String.format("%,d", engine.getPot()));

        // Position Dealer Button relative to local hero view
        int dealerIdx = 0;
        for (int i = 0; i < engine.getPlayers().size(); i++) {
            if (engine.getPlayers().get(i).getRole() == poker.model.Player.PlayerRole.DEALER) {
                dealerIdx = i;
                break;
            }
        }

        // Compute Visual Dealer Index relative to local Hero seat
        int nSeats = engine.getPlayers().size();
        int visualDealerIdx = (dealerIdx - heroSeatIndex + nSeats) % nSeats;

        // Adjust Dealer Button coordinates on table felt based on visual seat
        switch (visualDealerIdx) {
            case 0: dealerButton.setTranslateX(-90); dealerButton.setTranslateY(110); break; // Visual Bottom (Hero)
            case 1: dealerButton.setTranslateX(-270); dealerButton.setTranslateY(60); break; // Visual Bottom-Left
            case 2: dealerButton.setTranslateX(-270); dealerButton.setTranslateY(-60); break; // Visual Top-Left
            case 3: dealerButton.setTranslateX(0); dealerButton.setTranslateY(-110); break; // Visual Top-Center
            case 4: dealerButton.setTranslateX(270); dealerButton.setTranslateY(-60); break; // Visual Top-Right
            case 5: dealerButton.setTranslateX(270); dealerButton.setTranslateY(60); break; // Visual Bottom-Right
        }

        // Update Community Cards
        List<Card> commCards = engine.getCommunityCards();
        communityCardsBox.getChildren().clear();

        for (int i = 0; i < 5; i++) {
            ImageView cardView = new ImageView();
            cardView.setFitWidth(65);
            cardView.setFitHeight(94);
            cardView.setPreserveRatio(true);

            if (i < commCards.size()) {
                Card card = commCards.get(i);
                cardView.setImage(AssetLoader.getCardImage(card));
                cardView.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.8)));
            } else {
                cardView.setImage(AssetLoader.getCardBackImage("blue"));
                cardView.setOpacity(0.18);
            }
            communityCardsBox.getChildren().add(cardView);
        }
    }
}
