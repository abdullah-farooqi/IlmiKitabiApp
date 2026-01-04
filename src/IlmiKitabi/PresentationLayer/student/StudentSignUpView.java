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
 * StudentSignUpView - Student Registration Screen
 * Implements Use Case 1: Create Account
 * Follows Sequence Diagram SD 1
 */
public class StudentSignUpView {
    private Stage stage;
    private AccountController accountController;

    public StudentSignUpView(Stage stage) {
        this.stage = stage;
        this.accountController = new AccountController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        HBox header = createHeader();
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);

        HBox content = new HBox(60);
        content.setPadding(new Insets(60, 100, 60, 100));
        content.setAlignment(Pos.CENTER);

        VBox signUpForm = createSignUpForm();
        VBox imageSection = createImageSection();

        content.getChildren().addAll(signUpForm, imageSection);
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 750);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Student Sign Up");
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label haveAccount = new Label("Already have an account?");
        haveAccount.setFont(Font.font("Arial", 14));

        Button loginBtn = new Button("Log In");
        loginBtn.setStyle("-fx-background-color: #2C3E2E; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 5; -fx-cursor: hand;");
        loginBtn.setOnAction(e -> {
            StudentLoginView loginView = new StudentLoginView(stage);
            loginView.show();
        });

        header.getChildren().addAll(backBtn, new Label("  "), ilmi, kitabi, spacer, haveAccount, loginBtn);
        HBox.setMargin(loginBtn, new Insets(0, 0, 0, 10));

        return header;
    }

    private VBox createSignUpForm() {
        VBox form = new VBox(15);
        form.setMaxWidth(450);
        form.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Create Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Join the FAST NUCES book exchange community");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));
        subtitle.setWrapText(true);

        // Full Name
        Label nameLabel = new Label("Full Name *");
        nameLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        styleTextField(nameField);

        // NU Email
        Label emailLabel = new Label("NU Email *");
        emailLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField emailField = new TextField();
        emailField.setPromptText("e.g., i23xxxx@nu.edu.pk");
        styleTextField(emailField);
        Label emailHint = new Label("Use your official FAST NUCES email");
        emailHint.setFont(Font.font("Arial", 11));
        emailHint.setTextFill(Color.web("#999999"));

        // Phone Number
        Label phoneLabel = new Label("Phone Number *");
        phoneLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField phoneField = new TextField();
        phoneField.setPromptText("03XX-XXXXXXX");
        styleTextField(phoneField);

        // Department
        Label deptLabel = new Label("Department *");
        deptLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<String> deptComboBox = new ComboBox<>();
        deptComboBox.getItems().addAll(
                "Computer Science",
                "Software Engineering",
                "Information Technology",
                "Artificial Intelligence",
                "Data Science",
                "Cyber Security",
                "Business Administration",
                "Electrical Engineering",
                "Civil Engineering"
        );
        deptComboBox.setPromptText("Select your department");
        styleComboBox(deptComboBox);

        // Password
        Label passwordLabel = new Label("Password *");
        passwordLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Minimum 8 characters");
        styleTextField(passwordField);

        // Confirm Password
        Label confirmLabel = new Label("Confirm Password *");
        confirmLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Re-enter your password");
        styleTextField(confirmField);

        // Terms checkbox
        CheckBox termsCheck = new CheckBox("I agree to the Terms & Conditions");
        termsCheck.setFont(Font.font("Arial", 13));

        // Sign Up button
        Button signUpBtn = new Button("Create Account");
        signUpBtn.setStyle(
                "-fx-background-color: #2C3E2E; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 14; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );
        signUpBtn.setPrefWidth(400);

        // SD 1: Create Account flow
        signUpBtn.setOnAction(e -> {
            // Validation
            if (!validateInputs(nameField, emailField, phoneField, deptComboBox,
                    passwordField, confirmField, termsCheck)) {
                return;
            }

            // Create student object
            Student student = new Student(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    passwordField.getText(),
                    phoneField.getText().trim(),
                    deptComboBox.getValue()
            );

            // Register via controller (SD 1)
            if (accountController.registerStudent(student)) {
                showAlert("Success",
                        "Registration Successful!\n\n" +
                                "Your account has been submitted for admin approval.\n" +
                                "You'll receive an email notification once approved.\n\n" +
                                "Please check your email regularly.",
                        Alert.AlertType.INFORMATION);

                StudentLoginView loginView = new StudentLoginView(stage);
                loginView.show();
            } else {
                showAlert("Registration Failed",
                        "Could not create account.\n" +
                                "This email may already be registered.\n" +
                                "Please try a different email or contact support.",
                        Alert.AlertType.ERROR);
            }
        });

        form.getChildren().addAll(
                title, subtitle,
                nameLabel, nameField,
                emailLabel, emailField, emailHint,
                phoneLabel, phoneField,
                deptLabel, deptComboBox,
                passwordLabel, passwordField,
                confirmLabel, confirmField,
                termsCheck,
                signUpBtn
        );

        VBox.setMargin(title, new Insets(0, 0, 5, 0));
        VBox.setMargin(emailHint, new Insets(-10, 0, 0, 0));
        VBox.setMargin(signUpBtn, new Insets(10, 0, 0, 0));

        return form;
    }

    private boolean validateInputs(TextField nameField, TextField emailField, TextField phoneField,
                                   ComboBox<String> deptComboBox, PasswordField passwordField,
                                   PasswordField confirmField, CheckBox termsCheck) {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your full name", Alert.AlertType.ERROR);
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your NU email", Alert.AlertType.ERROR);
            return false;
        }

        if (!emailField.getText().matches("^[a-zA-Z0-9]+@nu\\.edu\\.pk$")) {
            showAlert("Invalid Email", "Please use valid NU email format: xxxx@nu.edu.pk", Alert.AlertType.ERROR);
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your phone number", Alert.AlertType.ERROR);
            return false;
        }

        if (deptComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select your department", Alert.AlertType.ERROR);
            return false;
        }

        if (passwordField.getText().length() < 8) {
            showAlert("Weak Password", "Password must be at least 8 characters long", Alert.AlertType.ERROR);
            return false;
        }

        if (!passwordField.getText().equals(confirmField.getText())) {
            showAlert("Password Mismatch", "Passwords do not match", Alert.AlertType.ERROR);
            return false;
        }

        if (!termsCheck.isSelected()) {
            showAlert("Terms Required", "Please agree to Terms & Conditions", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private VBox createImageSection() {
        VBox imageBox = new VBox(20);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 50; " +
                        "-fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        Label bookStack = new Label("📚");
        bookStack.setFont(Font.font(180));

        Label caption = new Label("Join thousands of students\nsharing knowledge");
        caption.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 18));
        caption.setTextFill(Color.web("#2C3E2E"));
        caption.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        imageBox.getChildren().addAll(bookStack, caption);
        return imageBox;
    }

    private void styleTextField(Control field) {
        field.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 12; " +
                        "-fx-border-color: #D0D0D0; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-font-size: 14;"
        );
        if (field instanceof TextField || field instanceof PasswordField) {
            ((TextField) field).setPrefWidth(400);
        }
    }

    private void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #D0D0D0; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8;"
        );
        comboBox.setPrefWidth(400);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}