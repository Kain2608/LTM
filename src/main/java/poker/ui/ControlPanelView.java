package poker.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import poker.model.Player;
import poker.model.PokerGameEngine;

public class ControlPanelView extends HBox {
    public interface ActionHandler {
        void onAction(String actionType, int amount);
        void onDealNextHand();
        void onEmojiTriggered(String emoji);
    }

    private final Button foldBtn = new Button("FOLD");
    private final Button checkCallBtn = new Button("CHECK");
    private final Button raiseBtn = new Button("RAISE $20");
    private final Button dealBtn = new Button("DEAL HAND");

    private final Button presetMinBtn = new Button("MIN");
    private final Button presetHalfPotBtn = new Button("1/2 POT");
    private final Button presetPotBtn = new Button("POT");
    private final Button presetAllInBtn = new Button("ALL IN");

    private final Slider raiseSlider = new Slider();
    private final Label raiseAmountLabel = new Label("$20");

    private ActionHandler actionHandler;
    private int currentRaiseValue = 20;

    public ControlPanelView() {
        this.setMinHeight(118);
        this.setSpacing(16);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(14, 24, 32, 0.98), rgba(5, 10, 16, 0.99)); " +
                "-fx-padding: 13 20; -fx-border-color: rgba(229,185,68,0.32); -fx-border-width: 1 0 0 0; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.60), 12, 0, 0, -3);");

        buildControlUI();
    }

    public void setActionHandler(ActionHandler handler) {
        this.actionHandler = handler;
    }

    private void buildControlUI() {
        // --- Left Side: Options & Emoji Picker Bar ---
        CheckBox moveTableCb = new CheckBox("Move Table");
        CheckBox straddleCb = new CheckBox("Straddle ℹ");
        CheckBox sitOutCb = new CheckBox("Global Cash Game Sit-out");

        styleCheckBox(moveTableCb);
        styleCheckBox(straddleCb);
        styleCheckBox(sitOutCb);

        VBox checkBoxesGroup = new VBox(3, moveTableCb, straddleCb, sitOutCb);

        // Emoji Buttons (GGPoker Reaction bar)
        HBox emojiBar = new HBox(8);
        emojiBar.setAlignment(Pos.CENTER_LEFT);
        String[] emojis = {"💬", "😏", "🔥", "😱", "🥳"};

        for (String em : emojis) {
            Button emBtn = new Button(em);
            emBtn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-font-size: 16px; " +
                    "-fx-padding: 4 8; -fx-background-radius: 8; -fx-cursor: hand;");
            emBtn.setOnAction(e -> {
                if (actionHandler != null) actionHandler.onEmojiTriggered(em);
            });
            emBtn.setOnMouseEntered(e -> emBtn.setStyle("-fx-background-color: rgba(241, 196, 15, 0.3); -fx-font-size: 18px; " +
                    "-fx-padding: 4 8; -fx-background-radius: 8; -fx-cursor: hand;"));
            emBtn.setOnMouseExited(e -> emBtn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-font-size: 16px; " +
                    "-fx-padding: 4 8; -fx-background-radius: 8; -fx-cursor: hand;"));
            emojiBar.getChildren().add(emBtn);
        }

        VBox leftPanel = new VBox(6, checkBoxesGroup, emojiBar);
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setStyle("-fx-background-color: rgba(255,255,255,0.035); -fx-padding: 8 11; " +
                "-fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.07); -fx-border-radius: 10;");

        // --- Center / Right Side: Action Buttons & Slider ---
        styleButton(foldBtn, "#c0392b", "#e74c3c");
        styleButton(checkCallBtn, "#2980b9", "#3498db");
        styleButton(raiseBtn, "#d35400", "#e67e22");
        styleButton(dealBtn, "#27ae60", "#2ecc71");
        dealBtn.setStyle(dealBtn.getStyle() + " -fx-font-size: 15px; -fx-padding: 8 26;");

        stylePresetButton(presetMinBtn);
        stylePresetButton(presetHalfPotBtn);
        stylePresetButton(presetPotBtn);
        stylePresetButton(presetAllInBtn);

        raiseSlider.setPrefWidth(180);
        raiseSlider.setMin(20);
        raiseSlider.setMax(1000);
        raiseSlider.setValue(20);
        raiseSlider.setShowTickMarks(false);

        raiseAmountLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold; -fx-font-size: 13px;");

        raiseSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentRaiseValue = newVal.intValue();
            raiseAmountLabel.setText("$" + String.format("%,d", currentRaiseValue));
            raiseBtn.setText("RAISE $" + String.format("%,d", currentRaiseValue));
        });

        foldBtn.setOnAction(e -> { if (actionHandler != null) actionHandler.onAction("FOLD", 0); });
        checkCallBtn.setOnAction(e -> {
            if (actionHandler != null) {
                String text = checkCallBtn.getText();
                if (text.startsWith("CALL")) actionHandler.onAction("CALL", 0);
                else actionHandler.onAction("CHECK", 0);
            }
        });
        raiseBtn.setOnAction(e -> { if (actionHandler != null) actionHandler.onAction("RAISE", currentRaiseValue); });
        dealBtn.setOnAction(e -> { if (actionHandler != null) actionHandler.onDealNextHand(); });

        HBox presetsBox = new HBox(6, presetMinBtn, presetHalfPotBtn, presetPotBtn, presetAllInBtn);
        presetsBox.setAlignment(Pos.CENTER);

        HBox raiseControlBox = new HBox(8, presetsBox, raiseSlider, raiseAmountLabel);
        raiseControlBox.setAlignment(Pos.CENTER);
        raiseControlBox.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-padding: 8 10; " +
                "-fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.07); -fx-border-radius: 10;");

        HBox actionButtonsBox = new HBox(12, foldBtn, checkCallBtn, raiseControlBox, raiseBtn, dealBtn);
        actionButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(leftPanel, spacer, actionButtonsBox);
    }

    public void updateControls(PokerGameEngine engine) {
        PokerGameEngine.GamePhase phase = engine.getCurrentPhase();
        Player human = engine.getPlayers().get(0);
        boolean isHumanTurn = (engine.getCurrentTurnIndex() == 0) &&
                (phase != PokerGameEngine.GamePhase.IDLE && phase != PokerGameEngine.GamePhase.SHOWDOWN);

        if (phase == PokerGameEngine.GamePhase.IDLE || phase == PokerGameEngine.GamePhase.SHOWDOWN) {
            dealBtn.setVisible(true);
            dealBtn.setManaged(true);
            dealBtn.setText(phase == PokerGameEngine.GamePhase.IDLE ? "DEAL HAND" : "NEXT HAND");

            foldBtn.setDisable(true);
            checkCallBtn.setDisable(true);
            raiseBtn.setDisable(true);
            raiseSlider.setDisable(true);
            presetMinBtn.setDisable(true);
            presetHalfPotBtn.setDisable(true);
            presetPotBtn.setDisable(true);
            presetAllInBtn.setDisable(true);
        } else {
            dealBtn.setVisible(false);
            dealBtn.setManaged(false);

            boolean canAct = isHumanTurn && human.getStatus() == Player.PlayerStatus.ACTIVE;

            foldBtn.setDisable(!canAct);
            checkCallBtn.setDisable(!canAct);
            raiseBtn.setDisable(!canAct || human.getChips() <= (engine.getHighestBet() - human.getCurrentBet()));
            raiseSlider.setDisable(!canAct);
            presetMinBtn.setDisable(!canAct);
            presetHalfPotBtn.setDisable(!canAct);
            presetPotBtn.setDisable(!canAct);
            presetAllInBtn.setDisable(!canAct);

            int callAmount = engine.getHighestBet() - human.getCurrentBet();
            if (callAmount > 0) {
                checkCallBtn.setText("CALL $" + String.format("%,d", Math.min(callAmount, human.getChips())));
            } else {
                checkCallBtn.setText("CHECK");
            }

            int minR = Math.max(engine.getMinRaise(), engine.getBigBlind());
            int maxR = human.getChips() - callAmount;
            if (maxR < minR) maxR = minR;

            raiseSlider.setMin(minR);
            raiseSlider.setMax(maxR);
            if (raiseSlider.getValue() < minR || raiseSlider.getValue() > maxR) {
                raiseSlider.setValue(minR);
            }

            currentRaiseValue = (int) raiseSlider.getValue();
            raiseBtn.setText("RAISE $" + String.format("%,d", currentRaiseValue));

            final int fMinR = minR;
            final int fMaxR = maxR;
            final int fPot = engine.getPot();

            presetMinBtn.setOnAction(e -> raiseSlider.setValue(fMinR));
            presetHalfPotBtn.setOnAction(e -> raiseSlider.setValue(Math.min(fMaxR, Math.max(fMinR, fPot / 2))));
            presetPotBtn.setOnAction(e -> raiseSlider.setValue(Math.min(fMaxR, Math.max(fMinR, fPot))));
            presetAllInBtn.setOnAction(e -> raiseSlider.setValue(fMaxR));
        }
    }

    private void styleCheckBox(CheckBox cb) {
        cb.setStyle("-fx-text-fill: #9eb0ba; -fx-font-size: 11px; -fx-cursor: hand;");
    }

    private void styleButton(Button btn, String baseHex, String hoverHex) {
        btn.setPrefWidth(112);
        btn.setPrefHeight(42);
        btn.setStyle("-fx-background-color: linear-gradient(to bottom, " + hoverHex + ", " + baseHex + "); " +
                "-fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 12px; -fx-background-radius: 10; " +
                "-fx-border-color: rgba(255,255,255,0.18); -fx-border-radius: 10; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.45), 7, 0, 0, 2);");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: linear-gradient(to bottom, " + baseHex + ", " + hoverHex + "); " +
                "-fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 12px; -fx-background-radius: 10; " +
                "-fx-border-color: rgba(255,255,255,0.28); -fx-border-radius: 10; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.72), 10, 0, 0, 3);"));

        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: linear-gradient(to bottom, " + hoverHex + ", " + baseHex + "); " +
                "-fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 12px; -fx-background-radius: 10; " +
                "-fx-border-color: rgba(255,255,255,0.18); -fx-border-radius: 10; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.45), 7, 0, 0, 2);"));
    }

    private void stylePresetButton(Button btn) {
        btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: #cbd9df; " +
                "-fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 4 7; -fx-background-radius: 7; " +
                "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 7; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; " +
                "-fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 4 7; -fx-background-radius: 7; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: #cbd9df; " +
                "-fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 4 7; -fx-background-radius: 7; " +
                "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 7; -fx-cursor: hand;"));
    }
}
