package IlmiKitabi.PresentationLayer.student;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.AccountController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.PresentationLayer.common.WelcomeView;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * StudentLoginView - Login screen for students
 * Implements Use Case 2: View/Update Info (Login part)
 * Follows Sequence Diagram SD 2
 */
public class StudentLoginView {
    private Stage stage;
    private AccountController accountController;

    public StudentLoginView(Stage stage) {
        this.stage = stage;
        this.accountController = new AccountController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        // Header with logo and signup link
        HBox header = createHeader();
        root.setTop(header);

        // Main content area - centered beautifully
        HBox content = new HBox(80);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40, 60, 40, 60));

        VBox loginForm = createLoginForm();
        VBox imageSection = createImageSection();

        HBox.setHgrow(loginForm, Priority.ALWAYS);
        HBox.setHgrow(imageSection, Priority.ALWAYS);

        content.getChildren().addAll(loginForm, imageSection);

        // Center wrapper ensures perfect vertical alignment
        VBox centerWrapper = new VBox(content);
        centerWrapper.setAlignment(Pos.CENTER);
        root.setCenter(centerWrapper);

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Student Login");
        stage.show();
    }

    /**
     * Creates header with logo and signup navigation
     */
    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 40, 20, 40));
        header.setStyle(
                "-fx-background-color: white;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        header.setAlignment(Pos.CENTER_LEFT);

        // Back button
        Button backBtn = new Button("← Back");
        backBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #2C3E2E;" +
                        "-fx-font-size: 14;" +
                        "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> {
            WelcomeView welcomeView = new WelcomeView(stage);
            welcomeView.show();
        });

        // Logo
        Label ilmi = new Label("Ilmi");
        ilmi.setFont(Font.font("Arial", FontWeight.NORMAL, 28));
        ilmi.setTextFill(Color.web("#C4A57B"));

        Label kitabi = new Label("Kitabi");
        kitabi.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        kitabi.setTextFill(Color.web("#2C3E2E"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label noAccount = new Label("Don't have an account?");
        noAccount.setFont(Font.font("Arial", 14));

        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setStyle(
                "-fx-background-color: #2C3E2E;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 30;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        signUpBtn.setOnAction(e -> {
            StudentSignUpView signUpView = new StudentSignUpView(stage);
            signUpView.show();
        });

        header.getChildren().addAll(backBtn, new Label("  "), ilmi, kitabi, spacer, noAccount, signUpBtn);
        HBox.setMargin(signUpBtn, new Insets(0, 0, 0, 10));

        return header;
    }

    /**
     * Creates the login form
     */
    private VBox createLoginForm() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(50));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(480);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        // Title
        Label title = new Label("Student Log In");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Welcome back! Please login to your account.");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(350);

        // Email
        Label emailLabel = new Label("NU Email");
        emailLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        emailLabel.setTextFill(Color.web("#2C3E2E"));

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your NU email (e.g., i23xxxx@nu.edu.pk)");
        styleTextField(emailField);

        // Password
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        passwordLabel.setTextFill(Color.web("#2C3E2E"));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        styleTextField(passwordField);

        // Remember Me / Forgot
        HBox options = new HBox();
        options.setAlignment(Pos.CENTER_LEFT);

        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.setFont(Font.font("Arial", 13));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.setFont(Font.font("Arial", 13));
        forgotPassword.setStyle("-fx-text-fill: #C4A57B; -fx-border-color: transparent;");

        options.getChildren().addAll(rememberMe, spacer, forgotPassword);
        options.setPrefWidth(350);

        // Login button
        Button loginBtn = new Button("Log In");
        loginBtn.setPrefWidth(350);
        loginBtn.setStyle(
                "-fx-background-color: #2C3E2E;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 14;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
                return;
            }

            Student student = accountController.loginStudent(email, password);

            if (student != null) {
                if (!student.isActive()) {
                    showAlert("Account Pending",
                            "Your account is awaiting admin approval. You'll be notified via email once approved.",
                            Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Success", "Welcome back, " + student.getName() + "!",
                            Alert.AlertType.INFORMATION);
                    StudentDashboardView dashboard = new StudentDashboardView(stage, student);
                    dashboard.show();
                }
            } else {
                showAlert("Login Failed",
                        "Invalid email or password. Please try again.",
                        Alert.AlertType.ERROR);
            }
        });

        // Divider
        HBox divider = new HBox(10);
        divider.setAlignment(Pos.CENTER);
        divider.setPrefWidth(350);

        Separator line1 = new Separator();
        line1.setPrefWidth(140);
        Label orText = new Label("OR");
        orText.setFont(Font.font("Arial", 12));
        orText.setTextFill(Color.web("#999999"));
        Separator line2 = new Separator();
        line2.setPrefWidth(140);

        divider.getChildren().addAll(line1, orText, line2);

        // Social login
        HBox socialLogin = createSocialLogin();

        card.getChildren().addAll(
                title, subtitle,
                emailLabel, emailField,
                passwordLabel, passwordField,
                options, loginBtn,
                divider, socialLogin
        );

        return card;
    }

    /**
     * Social login section
     */
    private HBox createSocialLogin() {
        HBox social = new HBox(15);
        social.setAlignment(Pos.CENTER);
        social.setPrefWidth(350);

        Label loginWith = new Label("Continue with");
        loginWith.setFont(Font.font("Arial", 13));
        loginWith.setTextFill(Color.web("#666666"));

        Button googleBtn = createSocialButton("G", "#DB4437");
        Button facebookBtn = createSocialButton("f", "#1877F2");
        Button linkedinBtn = createSocialButton("in", "#0A66C2");

        social.getChildren().addAll(loginWith, googleBtn, facebookBtn, linkedinBtn);
        return social;
    }

    private Button createSocialButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16;" +
                        "-fx-cursor: hand;"
        );
        btn.setPrefSize(45, 45);
        return btn;
    }

    /**
     * Illustration section
     */
    private VBox createImageSection() {
        VBox imageBox = new VBox(20);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPrefWidth(420);
        imageBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 50;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        Label bookStack = new Label("📚");
        bookStack.setFont(Font.font(160));

        Label caption = new Label("Access thousands of books\nfrom your fellow students");
        caption.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 18));
        caption.setTextFill(Color.web("#2C3E2E"));
        caption.setTextAlignment(TextAlignment.CENTER);
        caption.setWrapText(true);
        caption.setMaxWidth(300);

        imageBox.getChildren().addAll(bookStack, caption);
        return imageBox;
    }

    /**
     * Styles text input fields
     */
    private void styleTextField(Control field) {
        field.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 12;" +
                        "-fx-border-color: #D0D0D0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 14;"
        );
        if (field instanceof TextField) {
            ((TextField) field).setPrefWidth(350);
        } else if (field instanceof PasswordField) {
            ((PasswordField) field).setPrefWidth(350);
        }
    }

    /**
     * Shows alert dialog
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
