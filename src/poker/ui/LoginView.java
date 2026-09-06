package poker.ui;

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

public class LoginView extends BorderPane {

    public interface LoginActions {
        void onLoginSuccess(String username);
        void onSwitchToRegister();
    }

    public LoginView(LoginActions actionHandler) {
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

        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(440);
        formBox.setPrefWidth(440);

        Image formFrameImg = AssetLoader.getFormFrameImage();
        if (formFrameImg != null) {
            BackgroundImage formBI = new BackgroundImage(formFrameImg,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, false));
            formBox.setBackground(new Background(formBI));
            formBox.setPadding(new Insets(50, 45, 50, 45));
        } else {
            formBox.setPadding(new Insets(40));
            formBox.setStyle("-fx-background-color: rgba(20, 25, 35, 0.85); -fx-background-radius: 10; -fx-border-color: #4a5d7c; -fx-border-radius: 10; -fx-border-width: 2;");
        }

        Image logoImg = AssetLoader.getLogoImage();
        javafx.scene.image.ImageView logoView = null;
        if (logoImg != null) {
            logoView = new javafx.scene.image.ImageView(logoImg);
            logoView.setFitWidth(180);
            logoView.setPreserveRatio(true);
        }

        Label titleLabel = new Label("POKER LOGIN");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 16px; -fx-background-color: #2a354c; -fx-text-fill: white;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 16px; -fx-background-color: #2a354c; -fx-text-fill: white;");

        Button loginButton = new Button("LOGIN");
        Image btnImg = AssetLoader.getButtonFrame1Image();
        if (btnImg != null) {
            BackgroundImage btnBI = new BackgroundImage(btnImg,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, false));
            loginButton.setBackground(new Background(btnBI));
            loginButton.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffd700; -fx-cursor: hand;");
            loginButton.setPrefSize(320, 70);
        } else {
            loginButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 5;");
            loginButton.setMaxWidth(Double.MAX_VALUE);
        }

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                errorLabel.setText("Please enter username and password!");
                errorLabel.setVisible(true);
            } else {
                if (poker.model.UserManager.validateLogin(username, password)) {
                    actionHandler.onLoginSuccess(username);
                } else {
                    errorLabel.setText("Invalid credentials! Saved in users.txt");
                    errorLabel.setVisible(true);
                }
            }
        });

        Button registerButton = new Button("Don't have an account? Register");
        registerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-underline: true;");
        registerButton.setOnAction(e -> actionHandler.onSwitchToRegister());

        if (logoView != null) {
            formBox.getChildren().addAll(logoView, titleLabel, errorLabel, usernameField, passwordField, loginButton, registerButton);
        } else {
            formBox.getChildren().addAll(titleLabel, errorLabel, usernameField, passwordField, loginButton, registerButton);
        }
        
        this.setCenter(formBox);
    }
}
