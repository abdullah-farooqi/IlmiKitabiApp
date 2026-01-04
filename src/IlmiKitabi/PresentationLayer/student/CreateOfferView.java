package IlmiKitabi.PresentationLayer.student;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.OfferController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Book;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * CreateOfferView - Create lending/selling offers
 * Implements Use Cases 4, 7: Lend, Sell
 * Follows SD 4, SD 7
 */
public class CreateOfferView {
    private Stage stage;
    private Student student;
    private String offerType;
    private OfferController offerController;

    public CreateOfferView(Stage stage, Student student, String offerType) {
        this.stage = stage;
        this.student = student;
        this.offerType = offerType;
        this.offerController = new OfferController();
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

        Scene scene = new Scene(root, 1000, 750);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Create " + offerType + " Offer");
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

        String emoji = offerType.equals("LEND") ? "📚" : "💰";
        Label title = new Label(emoji + " " + offerType.charAt(0) + offerType.substring(1).toLowerCase() + " a Book");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Fill in the details to create your offer");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(backBtn, title, subtitle);
        return header;
    }

    private VBox createContent() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(40, 100, 40, 100));
        content.setMaxWidth(800);

        VBox formSection = createFormSection();

        content.getChildren().add(formSection);

        return content;
    }

    private VBox createFormSection() {
        VBox section = new VBox(20);
        section.setStyle("-fx-background-color: white; -fx-padding: 40; -fx-background-radius: 12;");

        Label sectionTitle = new Label("Book Information");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20, 0, 0, 0));

        // Title
        Label titleLabel = new Label("Book Title *");
        titleLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField titleField = new TextField();
        titleField.setPromptText("Enter book title");
        styleTextField(titleField);
        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);

        // Author
        Label authorLabel = new Label("Author *");
        authorLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField authorField = new TextField();
        authorField.setPromptText("Enter author name");
        styleTextField(authorField);
        grid.add(authorLabel, 0, 1);
        grid.add(authorField, 1, 1);

        // Subject
        Label subjectLabel = new Label("Subject *");
        subjectLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<String> subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll(
                "Computer Science", "Software Engineering", "Mathematics",
                "Physics", "Chemistry", "Biology", "English", "Business",
                "Economics", "Management", "Other"
        );
        subjectCombo.setPromptText("Select subject");
        styleComboBox(subjectCombo);
        grid.add(subjectLabel, 0, 2);
        grid.add(subjectCombo, 1, 2);

        // Condition
        Label conditionLabel = new Label("Condition *");
        conditionLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<String> conditionCombo = new ComboBox<>();
        conditionCombo.getItems().addAll("New", "Like New", "Good", "Fair", "Acceptable");
        conditionCombo.setPromptText("Select condition");
        styleComboBox(conditionCombo);
        grid.add(conditionLabel, 0, 3);
        grid.add(conditionCombo, 1, 3);

        // Description
        Label descLabel = new Label("Description");
        descLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextArea descArea = new TextArea();
        descArea.setPromptText("Add any additional details about the book...");
        descArea.setPrefRowCount(4);
        styleTextArea(descArea);
        grid.add(descLabel, 0, 4);
        grid.add(descArea, 1, 4);

        // Offer-specific fields
        int nextRow = 5;
        TextField priceField = null;
        CheckBox negotiableCheck = null;
        TextField durationField = null;

        if (offerType.equals("SELL")) {
            Label priceLabel = new Label("Price (PKR) *");
            priceLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
            priceField = new TextField();
            priceField.setPromptText("Enter price");
            styleTextField(priceField);
            grid.add(priceLabel, 0, nextRow);
            grid.add(priceField, 1, nextRow);

            negotiableCheck = new CheckBox("Price is negotiable");
            negotiableCheck.setFont(Font.font("Arial", 13));
            grid.add(negotiableCheck, 1, nextRow + 1);

        } else if (offerType.equals("LEND")) {
            Label durationLabel = new Label("Duration (Days) *");
            durationLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
            durationField = new TextField();
            durationField.setPromptText("e.g., 30");
            styleTextField(durationField);
            grid.add(durationLabel, 0, nextRow);
            grid.add(durationField, 1, nextRow);

            Label hintLabel = new Label("How many days you want to lend this book");
            hintLabel.setFont(Font.font("Arial", 11));
            hintLabel.setTextFill(Color.web("#999999"));
            grid.add(hintLabel, 1, nextRow + 1);
        }

        // Submit button
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button submitBtn = new Button("📤 Publish Offer");
        submitBtn.setStyle(
                "-fx-background-color: #2C3E2E; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 14 40; " +
                        "-fx-font-size: 16; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 8;"
        );

        TextField finalPriceField = priceField;
        CheckBox finalNegotiableCheck = negotiableCheck;
        TextField finalDurationField = durationField;

        submitBtn.setOnAction(e -> {
            if (validateAndSubmit(titleField, authorField, subjectCombo, conditionCombo,
                    descArea, finalPriceField, finalNegotiableCheck, finalDurationField)) {
                showAlert("Success",
                        "Your " + offerType.toLowerCase() + " offer has been published!\n" +
                                "Other students can now see your offer.",
                        Alert.AlertType.INFORMATION);
                StudentDashboardView dashboard = new StudentDashboardView(stage, student);
                dashboard.show();
            }
        });

        buttonBox.getChildren().add(submitBtn);

        section.getChildren().addAll(sectionTitle, grid, buttonBox);

        return section;
    }

    private boolean validateAndSubmit(TextField titleField, TextField authorField,
                                      ComboBox<String> subjectCombo, ComboBox<String> conditionCombo,
                                      TextArea descArea, TextField priceField,
                                      CheckBox negotiableCheck, TextField durationField) {
        // Validate required fields
        if (titleField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter book title", Alert.AlertType.ERROR);
            return false;
        }

        if (authorField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter author name", Alert.AlertType.ERROR);
            return false;
        }

        if (subjectCombo.getValue() == null) {
            showAlert("Validation Error", "Please select subject", Alert.AlertType.ERROR);
            return false;
        }

        if (conditionCombo.getValue() == null) {
            showAlert("Validation Error", "Please select book condition", Alert.AlertType.ERROR);
            return false;
        }

        // Create book object
        Book book = new Book(
                titleField.getText().trim(),
                authorField.getText().trim(),
                subjectCombo.getValue(),
                descArea.getText().trim(),
                conditionCombo.getValue()
        );

        boolean success = false;

        if (offerType.equals("SELL")) {
            if (priceField.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Please enter price", Alert.AlertType.ERROR);
                return false;
            }

            try {
                double price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) {
                    showAlert("Validation Error", "Price must be greater than 0", Alert.AlertType.ERROR);
                    return false;
                }

                boolean isNegotiable = negotiableCheck.isSelected();
                success = offerController.createSellingOffer(student.getStudentID(), book, price, isNegotiable);

            } catch (NumberFormatException ex) {
                showAlert("Validation Error", "Please enter a valid price", Alert.AlertType.ERROR);
                return false;
            }

        } else if (offerType.equals("LEND")) {
            if (durationField.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Please enter lending duration", Alert.AlertType.ERROR);
                return false;
            }

            try {
                int duration = Integer.parseInt(durationField.getText().trim());
                if (duration <= 0 || duration > 365) {
                    showAlert("Validation Error", "Duration must be between 1 and 365 days", Alert.AlertType.ERROR);
                    return false;
                }

                success = offerController.createLendingOffer(student.getStudentID(), book, duration);

            } catch (NumberFormatException ex) {
                showAlert("Validation Error", "Please enter a valid duration", Alert.AlertType.ERROR);
                return false;
            }
        }

        if (!success) {
            showAlert("Error", "Failed to create offer. Please try again.", Alert.AlertType.ERROR);
        }

        return success;
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
        field.setPrefWidth(450);
    }

    private void styleComboBox(ComboBox<String> combo) {
        combo.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 1;");
        combo.setPrefWidth(450);
    }

    private void styleTextArea(TextArea area) {
        area.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
        area.setPrefWidth(450);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}