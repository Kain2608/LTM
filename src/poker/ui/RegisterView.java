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

        Label titleLabel = new Label("CREATE ACCOUNT");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 16px; -fx-background-color: #2a354c; -fx-text-fill: white;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 16px; -fx-background-color: #2a354c; -fx-text-fill: white;");
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setStyle("-fx-font-size: 16px; -fx-background-color: #2a354c; -fx-text-fill: white;");

        Button registerButton = new Button("REGISTER");
        Image btnImg = AssetLoader.getButtonFrame2Image();
        if (btnImg != null) {
            BackgroundImage btnBI = new BackgroundImage(btnImg,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, false));
            registerButton.setBackground(new Background(btnBI));
            registerButton.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffd700; -fx-cursor: hand;");
            registerButton.setPrefSize(320, 70);
        } else {
            registerButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 5;");
            registerButton.setMaxWidth(Double.MAX_VALUE);
        }

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                errorLabel.setText("Please fill all fields!");
                errorLabel.setVisible(true);
            } else if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match!");
                errorLabel.setVisible(true);
            } else {
                if (poker.model.UserManager.registerUser(username, password)) {
                    actionHandler.onRegisterSuccess(username);
                } else {
                    errorLabel.setText("Username already exists in users.txt!");
                    errorLabel.setVisible(true);
                }
            }
        });

        Button loginButton = new Button("Already have an account? Login");
        loginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-underline: true;");
        loginButton.setOnAction(e -> actionHandler.onSwitchToLogin());

        if (logoView != null) {
            formBox.getChildren().addAll(logoView, titleLabel, errorLabel, usernameField, passwordField, confirmPasswordField, registerButton, loginButton);
        } else {
            formBox.getChildren().addAll(titleLabel, errorLabel, usernameField, passwordField, confirmPasswordField, registerButton, loginButton);
        }
        
        this.setCenter(formBox);
    }
}
