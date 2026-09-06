package poker.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class HeaderNode extends HBox {
    public interface HeaderActions {
        void onResetGame();
        void onSwitchViewpoint(int seatIndex);
        void onLeaveRoom();
    }

    private final Label titleLabel = new Label("♠ OPENDECKS TEXAS HOLD'EM POKER ♠");
    private final Label infoLabel = new Label("Blinds: $10/$20 | Hand #0");
    private final Button resetBtn = new Button("RESET CHIPS");
    private final Button leaveBtn = new Button("🚪 LEAVE ROOM");
    private final ComboBox<String> viewSelector = new ComboBox<>();

    public HeaderNode(HeaderActions actions) {
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-background-color: rgba(10, 14, 22, 0.75); -fx-padding: 8 20; " +
                "-fx-border-color: rgba(212, 175, 55, 0.4); -fx-border-width: 0 0 1 0;");

        titleLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-weight: bold; -fx-font-size: 16px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 1);");

        infoLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 12px; -fx-font-weight: bold;");

        // Viewpoint Selector Dropdown
        viewSelector.getItems().addAll(
                "🔍 View: Seat 0 (AHNKCO)",
                "🔍 View: Seat 1 (Boss lang)",
                "🔍 View: Seat 2 (Mark92)",
                "🔍 View: Seat 3 (Fishi shsi)",
                "🔍 View: Seat 4 (3060ti)",
                "🔍 View: Seat 5 (rappdo11)"
        );
        viewSelector.getSelectionModel().select(0);
        viewSelector.setStyle("-fx-background-color: #2c3e50; -fx-mark-color: white; -fx-font-weight: bold; " +
                "-fx-font-size: 11px; -fx-background-radius: 5; -fx-cursor: hand;");

        viewSelector.setOnAction(e -> {
            int selectedSeat = viewSelector.getSelectionModel().getSelectedIndex();
            if (actions != null && selectedSeat >= 0) {
                actions.onSwitchViewpoint(selectedSeat);
            }
        });

        styleHeaderButton(resetBtn, "#c0392b");
        styleHeaderButton(leaveBtn, "#e67e22");

        resetBtn.setOnAction(e -> {
            if (actions != null) actions.onResetGame();
        });

        leaveBtn.setOnAction(e -> {
            if (actions != null) actions.onLeaveRoom();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(leaveBtn, titleLabel, infoLabel, spacer, viewSelector, resetBtn);
    }

    public void updateInfo(int handCount, int smallBlind, int bigBlind) {
        infoLabel.setText("Blinds: $" + smallBlind + "/$" + bigBlind + "  |  Hand #" + handCount);
    }

    private void styleHeaderButton(Button btn, String bgHex) {
        btn.setStyle("-fx-background-color: " + bgHex + "; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 5; -fx-cursor: hand;");
    }
}
