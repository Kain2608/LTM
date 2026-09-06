package poker.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class HistoryPanelNode extends VBox {
    private final VBox logContent = new VBox(4);
    private final ScrollPane scrollPane = new ScrollPane();

    public HistoryPanelNode() {
        this.setPrefWidth(240);
        this.setStyle("-fx-background-color: rgba(10, 14, 22, 0.85); -fx-padding: 10; " +
                "-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 0 0 0 1;");

        Label title = new Label("GAME LOG");
        title.setStyle("-fx-text-fill: #d4af37; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 0 0 8 0;");
        title.setAlignment(Pos.CENTER);

        logContent.setStyle("-fx-background-color: transparent;");

        scrollPane.setContent(logContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        this.getChildren().addAll(title, scrollPane);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
    }

    public void addLog(String text, String category) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(210);

        String style = "-fx-font-size: 11px; -fx-padding: 2 4;";
        switch (category.toLowerCase()) {
            case "system":
            case "phase":
                style += " -fx-text-fill: #f39c12; -fx-font-weight: bold;";
                break;
            case "win":
                style += " -fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-background-color: rgba(46, 204, 113, 0.15); -fx-background-radius: 4;";
                break;
            case "raise":
                style += " -fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                break;
            case "call":
                style += " -fx-text-fill: #3498db;";
                break;
            case "fold":
                style += " -fx-text-fill: #7f8c8d;";
                break;
            case "blind":
                style += " -fx-text-fill: #9b59b6;";
                break;
            default:
                style += " -fx-text-fill: #ecf0f1;";
                break;
        }

        label.setStyle(style);
        logContent.getChildren().add(label);

        // Auto scroll to bottom
        scrollPane.setVvalue(1.0);
    }

    public void clearLog() {
        logContent.getChildren().clear();
    }
}
