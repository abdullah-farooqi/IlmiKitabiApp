package IlmiKitabi.PresentationLayer.common;

import IlmiKitabi.PresentationLayer.student.StudentLoginView;
import IlmiKitabi.PresentationLayer.admin.AdminLoginView;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * WelcomeView - Initial screen where user selects role (Student or Admin)
 * GRASP Pattern: Pure Fabrication - Created to handle initial navigation
 */
public class WelcomeView {
    private Stage stage;

    public WelcomeView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        // --- Center Content ---
        VBox centerBox = new VBox(50);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(60));

        // --- Logo ---
        HBox logoBox = new HBox(5);
        logoBox.setAlignment(Pos.CENTER);

        Label ilmi = new Label("Ilmi");
        ilmi.setFont(Font.font("Arial", FontWeight.NORMAL, 56));
        ilmi.setTextFill(Color.web("#C4A57B"));

        Label kitabi = new Label("Kitabi");
        kitabi.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        kitabi.setTextFill(Color.web("#2C3E2E"));

        logoBox.getChildren().addAll(ilmi, kitabi);

        // --- Subtitle ---
        Label subtitle = new Label("Your Campus Book Exchange Platform");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitle.setTextFill(Color.web("#666666"));

        // --- Book emoji ---
        Label bookEmoji = new Label("📚");
        bookEmoji.setFont(Font.font(120));

        // --- Welcome message ---
        Label welcomeMsg = new Label("Welcome! Please select your role:");
        welcomeMsg.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 26));
        welcomeMsg.setTextFill(Color.web("#2C3E2E"));

        // --- Role selection buttons ---
        HBox buttonBox = new HBox(40);
        buttonBox.setAlignment(Pos.CENTER);

        VBox studentBox = createRoleButton(
                "👨‍🎓",
                "Student",
                "Lend, Borrow, Buy, Sell & Exchange Books",
                e -> navigateToStudentLogin()
        );

        VBox adminBox = createRoleButton(
                "👨‍💼",
                "Admin",
                "Manage Users, Approve Accounts & Handle Complaints",
                e -> navigateToAdminLogin()
        );

        buttonBox.getChildren().addAll(studentBox, adminBox);

        // --- Footer ---
        Label footerText = new Label("Made with ❤️ for FAST NUCES Community");
        footerText.setFont(Font.font("Arial", 14));
        footerText.setTextFill(Color.web("#999999"));
        footerText.setAlignment(Pos.CENTER);

        // --- Centering Wrapper ---
        VBox wrapper = new VBox(40, logoBox, subtitle, bookEmoji, welcomeMsg, buttonBox, footerText);
        wrapper.setAlignment(Pos.CENTER);
        root.setCenter(wrapper);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Welcome");
        stage.show();
    }

    /**
     * Creates a clickable role selection card
     */
    private VBox createRoleButton(String emoji, String role, String description,
                                  javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40, 50, 40, 50));
        box.setPrefSize(360, 320);
        box.setCursor(javafx.scene.Cursor.HAND);
        box.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 6);" +
                        "-fx-transition: all 0.3s ease;"
        );

        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font(90));

        Label roleLabel = new Label(role);
        roleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        roleLabel.setTextFill(Color.web("#2C3E2E"));

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setTextFill(Color.web("#666666"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(280);
        descLabel.setTextAlignment(TextAlignment.CENTER);

        Button selectBtn = new Button("Select");
        selectBtn.setStyle(
                "-fx-background-color: #2C3E2E;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 40;" +
                        "-fx-background-radius: 25;" +
                        "-fx-cursor: hand;"
        );
        selectBtn.setOnAction(handler);

        // --- Hover Effects ---
        box.setOnMouseEntered(e -> {
            box.setStyle(
                    "-fx-background-color: #2C3E2E;" +
                            "-fx-border-color: #2C3E2E;" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-radius: 15;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 8);"
            );
            roleLabel.setTextFill(Color.WHITE);
            descLabel.setTextFill(Color.web("#EAEAEA"));
        });

        box.setOnMouseExited(e -> {
            box.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: #E0E0E0;" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-radius: 15;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 6);"
            );
            roleLabel.setTextFill(Color.web("#2C3E2E"));
            descLabel.setTextFill(Color.web("#666666"));
        });

        // Make entire box clickable
        box.setOnMouseClicked(e -> handler.handle(null));

        box.getChildren().addAll(emojiLabel, roleLabel, descLabel, selectBtn);

        return box;
    }

    /**
     * Navigate to Student Login screen
     */
    private void navigateToStudentLogin() {
        StudentLoginView studentLogin = new StudentLoginView(stage);
        studentLogin.show();
    }

    /**
     * Navigate to Admin Login screen
     */
    private void navigateToAdminLogin() {
        AdminLoginView adminLogin = new AdminLoginView(stage);
        adminLogin.show();
    }
}
