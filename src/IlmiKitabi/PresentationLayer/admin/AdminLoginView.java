package IlmiKitabi.PresentationLayer.admin;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.AdminController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
import IlmiKitabi.PresentationLayer.common.WelcomeView;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * AdminLoginView - Admin Login Screen
 */
public class AdminLoginView {
    private Stage stage;
    private AdminController adminController;

    public AdminLoginView(Stage stage) {
        this.stage = stage;
        this.adminController = new AdminController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        HBox header = createHeader();
        root.setTop(header);

        HBox content = new HBox(80);
        content.setPadding(new Insets(100, 100, 100, 100));
        content.setAlignment(Pos.CENTER);

        VBox loginForm = createLoginForm();
        VBox imageSection = createImageSection();

        content.getChildren().addAll(loginForm, imageSection);
        root.setCenter(content);

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Admin Login");
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 40, 20, 40));
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E2E; -fx-font-size: 14; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            WelcomeView welcomeView = new WelcomeView(stage);
            welcomeView.show();
        });

        Label ilmi = new Label("Ilmi");
        ilmi.setFont(Font.font("Arial", FontWeight.NORMAL, 28));
        ilmi.setTextFill(Color.web("#C4A57B"));

        Label kitabi = new Label("Kitabi");
        kitabi.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        kitabi.setTextFill(Color.web("#2C3E2E"));

        Label admin = new Label(" - Admin Portal");
        admin.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        admin.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(backBtn, new Label("  "), ilmi, kitabi, admin);

        return header;
    }

    private VBox createLoginForm() {
        VBox form = new VBox(20);
        form.setMaxWidth(400);
        form.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Admin Log In");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Manage registrations and student accounts");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField emailField = new TextField();
        emailField.setPromptText("Enter admin email");
        styleTextField(emailField);

        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter admin password");
        styleTextField(passwordField);

        Button loginBtn = new Button("Log In");
        loginBtn.setStyle(
                "-fx-background-color: #2C3E2E; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 14; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );
        loginBtn.setPrefWidth(350);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
                return;
            }

            Admin admin = adminController.loginAdmin(email, password);

            if (admin != null) {
                showAlert("Success", "Welcome, " + admin.getName() + "!", Alert.AlertType.INFORMATION);
                AdminDashboardView dashboard = new AdminDashboardView(stage, admin);
                dashboard.show();
            } else {
                showAlert("Login Failed", "Invalid email or password", Alert.AlertType.ERROR);
            }
        });

        Label hint = new Label("Default: admin@nu.edu.pk / admin123");
        hint.setFont(Font.font("Arial", 11));
        hint.setTextFill(Color.web("#999999"));

        form.getChildren().addAll(title, subtitle, emailLabel, emailField, passwordLabel, passwordField, loginBtn, hint);
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        return form;
    }

    private VBox createImageSection() {
        VBox imageBox = new VBox(20);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setStyle("-fx-background-color: white; -fx-padding: 50; -fx-background-radius: 15;");

        Label icon = new Label("👨‍💼");
        icon.setFont(Font.font(150));

        Label caption = new Label("Admin Control Panel");
        caption.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        caption.setTextFill(Color.web("#2C3E2E"));

        imageBox.getChildren().addAll(icon, caption);
        return imageBox;
    }

    private void styleTextField(Control field) {
        field.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-border-color: #D0D0D0; -fx-border-radius: 8; -fx-background-radius: 8;");
        ((TextField) field).setPrefWidth(350);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}