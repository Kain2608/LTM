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
import javafx.scene.shape.Ellipse;
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
        this.setStyle("-fx-background-color: radial-gradient(center 50% 48%, radius 58%, rgba(20,78,61,0.20), transparent 72%);");

        // Vector layers keep the table polished even when optional image assets are missing.
        Ellipse tableShadow = createEllipse(438, 214, Color.rgb(0, 0, 0, 0.72));
        tableShadow.setTranslateY(12);
        tableShadow.setEffect(new DropShadow(34, Color.rgb(0, 0, 0, 0.88)));

        Ellipse outerRail = createEllipse(430, 207, Color.web("#8b642c"));
        outerRail.setStroke(Color.web("#d8b35b"));
        outerRail.setStrokeWidth(3);

        Ellipse innerRail = createEllipse(412, 190, Color.web("#17120e"));
        innerRail.setStroke(Color.web("#49351d"));
        innerRail.setStrokeWidth(3);

        Ellipse felt = createEllipse(398, 177, Color.web("#0b503b"));
        felt.setStroke(Color.web("#188060"));
        felt.setStrokeWidth(2);

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
        potLabel.setStyle("-fx-background-color: rgba(4, 18, 15, 0.88); -fx-text-fill: #ffe083; -fx-font-weight: 900; " +
                "-fx-font-size: 16px; -fx-padding: 7 18; -fx-background-radius: 18; " +
                "-fx-border-color: rgba(255,224,131,0.55); -fx-border-radius: 18; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.85), 9, 0, 0, 2);");

        // 4. Community Cards Box
        communityCardsBox.setAlignment(Pos.CENTER);
        communityCardsBox.setMinHeight(105);

        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(potLabel, communityCardsBox);

        this.getChildren().addAll(tableShadow, outerRail, innerRail, felt, tableImageView, dealerButton, centerBox);
    }

    private Ellipse createEllipse(double radiusX, double radiusY, Color fill) {
        Ellipse ellipse = new Ellipse(radiusX, radiusY);
        ellipse.setFill(fill);
        ellipse.setMouseTransparent(true);
        return ellipse;
    }

    public void updateTable(PokerGameEngine engine, int heroSeatIndex) {
        potLabel.setText("◆  TOTAL POT   $" + String.format("%,d", engine.getPot()));

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
