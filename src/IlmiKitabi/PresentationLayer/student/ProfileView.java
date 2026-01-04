package IlmiKitabi.PresentationLayer.student;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.AccountController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;

/**
 * ProfileView - View and update student profile
 * Implements Use Case 2: View/Update Info
 * Follows SD 2: View/Update Info sequence diagram
 */
public class ProfileView {
    private Stage stage;
    private Student student;
    private AccountController accountController;

    private TextField nameField;
    private TextField phoneField;
    private ComboBox<String> deptComboBox;
    private Button editBtn;
    private Button saveBtn;
    private Button cancelBtn;

    public ProfileView(Stage stage, Student student) {
        this.stage = stage;
        this.student = student;
        this.accountController = new AccountController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        VBox header = createHeader();
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);

        VBox content = createContent();
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - My Profile");
        stage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(30, 40, 20, 40));
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E2E; -fx-font-size: 14; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            StudentDashboardView dashboard = new StudentDashboardView(stage, student);
            dashboard.show();
        });

        Label title = new Label("My Profile");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("View and manage your account information");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(backBtn, title, subtitle);
        return header;
    }

    private VBox createContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(40, 80, 40, 80));
        content.setMaxWidth(800);

        // Profile picture section
        HBox profilePicSection = new HBox(30);
        profilePicSection.setAlignment(Pos.CENTER_LEFT);
        profilePicSection.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 30; " +
                        "-fx-background-radius: 12;"
        );

        VBox picBox = new VBox(10);
        picBox.setAlignment(Pos.CENTER);

        Label avatarLabel = new Label("👤");
        avatarLabel.setFont(Font.font(80));
        avatarLabel.setStyle(
                "-fx-background-color: #F5F3E8; " +
                        "-fx-padding: 20; " +
                        "-fx-background-radius: 50;"
        );

        Button changePicBtn = new Button("Change Photo");
        changePicBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #C4A57B; " +
                        "-fx-cursor: hand; " +
                        "-fx-underline: true;"
        );
        changePicBtn.setOnAction(e -> showComingSoon("Change Photo"));

        picBox.getChildren().addAll(avatarLabel, changePicBtn);

        VBox infoBox = new VBox(8);
        Label nameLabel = new Label(student.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nameLabel.setTextFill(Color.web("#2C3E2E"));

        Label emailLabel = new Label(student.getNuEmail());
        emailLabel.setFont(Font.font("Arial", 16));
        emailLabel.setTextFill(Color.web("#666666"));

        Label memberSinceLabel = new Label("Member since " +
                new SimpleDateFormat("MMMM yyyy").format(student.getRegistrationDate()));
        memberSinceLabel.setFont(Font.font("Arial", 13));
        memberSinceLabel.setTextFill(Color.web("#999999"));

        infoBox.getChildren().addAll(nameLabel, emailLabel, memberSinceLabel);

        profilePicSection.getChildren().addAll(picBox, infoBox);

        // Account information section
        VBox accountSection = createAccountSection();

        // Account status
        VBox statusSection = createStatusSection();

        content.getChildren().addAll(profilePicSection, accountSection, statusSection);

        return content;
    }

    private VBox createAccountSection() {
        VBox section = new VBox(20);
        section.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 30; " +
                        "-fx-background-radius: 12;"
        );

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label sectionTitle = new Label("Account Information");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        editBtn = new Button("✏️ Edit");
        editBtn.setStyle(
                "-fx-background-color: #2C3E2E; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 8 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"
        );
        editBtn.setOnAction(e -> enableEditing());

        headerBox.getChildren().addAll(sectionTitle, spacer, editBtn);

        // Form fields
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20, 0, 0, 0));

        // Student ID (read-only)
        Label idLabel = new Label("Student ID:");
        idLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField idField = new TextField(student.getStudentID());
        idField.setEditable(false);
        styleTextField(idField);
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);

        // Full Name
        Label nameLabel = new Label("Full Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        nameField = new TextField(student.getName());
        nameField.setEditable(false);
        styleTextField(nameField);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);

        // NU Email (read-only)
        Label emailLabel = new Label("NU Email:");
        emailLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField emailField = new TextField(student.getNuEmail());
        emailField.setEditable(false);
        styleTextField(emailField);
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);

        // Phone Number
        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        phoneField = new TextField(student.getPhoneNumber());
        phoneField.setEditable(false);
        styleTextField(phoneField);
        grid.add(phoneLabel, 0, 3);
        grid.add(phoneField, 1, 3);

        // Department
        Label deptLabel = new Label("Department:");
        deptLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        deptComboBox = new ComboBox<>();
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
        deptComboBox.setValue(student.getDepartment());
        deptComboBox.setDisable(true);
        styleComboBox(deptComboBox);
        grid.add(deptLabel, 0, 4);
        grid.add(deptComboBox, 1, 4);

        // Save/Cancel buttons (initially hidden)
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        saveBtn = new Button("💾 Save Changes");
        saveBtn.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 25; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5; " +
                        "-fx-font-weight: bold;"
        );
        saveBtn.setVisible(false);
        saveBtn.setOnAction(e -> saveChanges());

        cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
                "-fx-background-color: #F44336; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 25; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"
        );
        cancelBtn.setVisible(false);
        cancelBtn.setOnAction(e -> cancelEditing());

        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        section.getChildren().addAll(headerBox, grid, buttonBox);

        return section;
    }

    private VBox createStatusSection() {
        VBox section = new VBox(15);
        section.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 30; " +
                        "-fx-background-radius: 12;"
        );

        Label sectionTitle = new Label("Account Status");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        HBox statusBox = new HBox(15);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label("Status:");
        statusLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        Label activeLabel = new Label(student.isActive() ? "✓ Active" : "⏳ Pending Approval");
        activeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        activeLabel.setTextFill(student.isActive() ? Color.web("#4CAF50") : Color.web("#FF9800"));
        activeLabel.setStyle(
                "-fx-background-color: " + (student.isActive() ? "#E8F5E9" : "#FFF3E0") + "; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 5;"
        );

        statusBox.getChildren().addAll(statusLabel, activeLabel);

        Label regDateLabel = new Label("Registration Date: " +
                new SimpleDateFormat("dd MMMM yyyy, hh:mm a").format(student.getRegistrationDate()));
        regDateLabel.setFont(Font.font("Arial", 13));
        regDateLabel.setTextFill(Color.web("#666666"));

        section.getChildren().addAll(sectionTitle, statusBox, regDateLabel);

        return section;
    }

    /**
     * Enable editing mode (SD 2: Update Info)
     */
    private void enableEditing() {
        nameField.setEditable(true);
        nameField.setStyle("-fx-background-color: #FFF9E6; -fx-padding: 10; -fx-border-color: #C4A57B; -fx-border-width: 2;");

        phoneField.setEditable(true);
        phoneField.setStyle("-fx-background-color: #FFF9E6; -fx-padding: 10; -fx-border-color: #C4A57B; -fx-border-width: 2;");

        deptComboBox.setDisable(false);
        deptComboBox.setStyle("-fx-background-color: #FFF9E6; -fx-border-color: #C4A57B; -fx-border-width: 2;");

        editBtn.setVisible(false);
        saveBtn.setVisible(true);
        cancelBtn.setVisible(true);
    }

    /**
     * Save profile changes (SD 2: Update Info)
     */
    private void saveChanges() {
        String newName = nameField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String newDept = deptComboBox.getValue();

        if (newName.isEmpty() || newPhone.isEmpty() || newDept == null) {
            showAlert("Validation Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        // Update via controller (SD 2)
        boolean updated = accountController.updateStudentProfile(
                student.getStudentID(),
                newName,
                newPhone,
                newDept
        );

        if (updated) {
            // Update local student object
            student.setName(newName);
            student.setPhoneNumber(newPhone);
            student.setDepartment(newDept);

            showAlert("Success", "Profile updated successfully!", Alert.AlertType.INFORMATION);
            disableEditing();
        } else {
            showAlert("Error", "Failed to update profile. Please try again.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Cancel editing
     */
    private void cancelEditing() {
        nameField.setText(student.getName());
        phoneField.setText(student.getPhoneNumber());
        deptComboBox.setValue(student.getDepartment());
        disableEditing();
    }

    /**
     * Disable editing mode
     */
    private void disableEditing() {
        nameField.setEditable(false);
        styleTextField(nameField);

        phoneField.setEditable(false);
        styleTextField(phoneField);

        deptComboBox.setDisable(true);
        styleComboBox(deptComboBox);

        editBtn.setVisible(true);
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);
    }

    private void styleTextField(TextField field) {
        field.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 10; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
        field.setPrefWidth(400);
    }

    private void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
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

    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(feature);
        alert.setContentText("This feature will be implemented soon.");
        alert.showAndWait();
    }
}