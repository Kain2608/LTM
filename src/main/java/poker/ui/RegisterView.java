package poker.ui;

import poker.client.net.ApiClient;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RegisterView extends BorderPane {

    public interface RegisterActions {
        void onRegisterSuccess(String username);
        void onSwitchToLogin();
    }

    public RegisterView(RegisterActions actionHandler) {
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

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(420);
        formBox.setPrefWidth(420);
        formBox.setPadding(new Insets(25, 42, 28, 42));
        formBox.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(9,20,25,0.94), rgba(4,10,15,0.97)); " +
                "-fx-background-radius: 24; -fx-border-color: rgba(229,185,68,0.62); -fx-border-radius: 24; " +
                "-fx-border-width: 1.4; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.78), 28, 0.12, 0, 10);");

        Image logoImg = AssetLoader.getLogoImage();
        javafx.scene.image.ImageView logoView = null;
        if (logoImg != null) {
            logoView = new javafx.scene.image.ImageView(logoImg);
            logoView.setFitWidth(96);
            logoView.setPreserveRatio(true);
        }

        Label titleLabel = new Label("CREATE ACCOUNT");
        titleLabel.setTextFill(Color.web("#f5d77c"));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(46);
        usernameField.setMaxWidth(Double.MAX_VALUE);
        usernameField.setStyle(authFieldStyle());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(46);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle(authFieldStyle());

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setPrefHeight(46);
        confirmPasswordField.setMaxWidth(Double.MAX_VALUE);
        confirmPasswordField.setStyle(authFieldStyle());

        Button registerButton = new Button("REGISTER");
        registerButton.setPrefHeight(48);
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle("-fx-font-size: 15px; -fx-font-weight: 900; " +
                "-fx-background-color: linear-gradient(to bottom, #f3d477, #b98628); -fx-text-fill: #101820; " +
                "-fx-background-radius: 10; -fx-border-color: #ffe8a3; -fx-border-radius: 10; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(229,185,68,0.32), 10, 0.25, 0, 3);");

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill all fields!");
                errorLabel.setTextFill(Color.RED);
                errorLabel.setVisible(true);
                return;
            } else if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match!");
                errorLabel.setTextFill(Color.RED);
                errorLabel.setVisible(true);
                return;
            }

            registerButton.setDisable(true);
            errorLabel.setText("Registering with Server...");
            errorLabel.setTextFill(Color.YELLOW);
            errorLabel.setVisible(true);

            String email = username.toLowerCase() + "@poker.com";
            ApiClient.register(username, email, password).thenAccept(result -> {
                Platform.runLater(() -> {
                    registerButton.setDisable(false);
                    if (result != null && result.success) {
                        actionHandler.onRegisterSuccess(username);
                    } else {
                        errorLabel.setTextFill(Color.RED);
                        errorLabel.setText(result != null ? result.message : "Registration failed");
                        errorLabel.setVisible(true);
                    }
                });
            }).exceptionally(ex -> {
                Platform.runLater(() -> {
                    registerButton.setDisable(false);
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("Register error: " + ex.getMessage());
                    errorLabel.setVisible(true);
                });
                return null;
            });
        });

        Button loginButton = new Button("Already have an account? Login");
        loginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #b8c8ce; -fx-underline: true; -fx-cursor: hand;");
        loginButton.setOnAction(e -> actionHandler.onSwitchToLogin());

        if (logoView != null) {
            formBox.getChildren().addAll(logoView, titleLabel, errorLabel, usernameField, passwordField, confirmPasswordField, registerButton, loginButton);
        } else {
            formBox.getChildren().addAll(titleLabel, errorLabel, usernameField, passwordField, confirmPasswordField, registerButton, loginButton);
        }

        this.setCenter(formBox);
    }

    private String authFieldStyle() {
        return "-fx-font-size: 14px; -fx-background-color: rgba(255,255,255,0.075); -fx-text-fill: white; " +
                "-fx-prompt-text-fill: #82939a; -fx-padding: 0 14; -fx-background-radius: 10; " +
                "-fx-border-color: rgba(255,255,255,0.12); -fx-border-radius: 10;";
    }
}
