package IlmiKitabi.PresentationLayer.student;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.ComplaintController;
import IlmiKitabi.BUSINESSLOGICLAYER.controllers.TransactionController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Complaint;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.BUSINESSLOGICLAYER.models.TransactionHistory;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.sql.*;
import IlmiKitabi.DATAACCESSLAYER.DatabaseConnection;

public class FileComplaintView {
    private Stage stage;
    private Student student;
    private ComplaintController complaintController;
    private TransactionController transactionController;

    public FileComplaintView(Stage stage, Student student) {
        this.stage = stage;
        this.student = student;
        this.complaintController = new ComplaintController();
        this.transactionController = new TransactionController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        VBox header = createHeader();
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox content = createContent();
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1100, 750);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - File Complaint");
        stage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(30, 40, 20, 40));
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E2E; -fx-font-size: 14; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new StudentDashboardView(stage, student).show());

        Label title = new Label("⚠️ File a Complaint");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Report issues with transactions or student behavior");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(backBtn, title, subtitle);
        return header;
    }

    private VBox createContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30, 60, 30, 60));

        content.getChildren().addAll(
                createNewComplaintSection(),
                createMyComplaintsSection()
        );

        return content;
    }

    private VBox createNewComplaintSection() {
        VBox section = new VBox(20);
        section.setStyle("-fx-background-color: white; -fx-padding: 40; -fx-background-radius: 12;");

        Label sectionTitle = new Label("File New Complaint");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        Label transLabel = new Label("Related Transaction:");
        transLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        ComboBox<TransactionItem> transCombo = new ComboBox<>();
        transCombo.setPromptText("Select transaction (optional)");
        transCombo.setPrefWidth(600);

        List<TransactionHistory> transactions = transactionController.getTransactionHistory(student.getStudentID());
        for (TransactionHistory t : transactions) {
            transCombo.getItems().add(new TransactionItem(
                    t.getTransactionID(),
                    t.getBookTitle() + " - " + t.getTransactionType() + " with " + t.getOtherPartyName()
            ));
        }

        grid.add(transLabel, 0, 0);
        grid.add(transCombo, 1, 0);

        Label categoryLabel = new Label("Category: *");
        categoryLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(
                "Wrong Book Received", "Book Not Returned", "Damaged Book",
                "Payment Issue", "No Show at Meeting", "Rude Behavior", "Other"
        );
        categoryCombo.setPromptText("Select complaint category");
        categoryCombo.setPrefWidth(600);

        grid.add(categoryLabel, 0, 1);
        grid.add(categoryCombo, 1, 1);

        Label descLabel = new Label("Description: *");
        descLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe the issue...");
        descArea.setPrefRowCount(6);
        descArea.setPrefWidth(600);

        grid.add(descLabel, 0, 2);
        grid.add(descArea, 1, 2);

        Label noteLabel = new Label("Note: Admin reviews all complaints. False complaints may lead to penalties.");
        noteLabel.setFont(Font.font("Arial", 11));
        noteLabel.setTextFill(Color.web("#999999"));
        noteLabel.setWrapText(true);

        grid.add(noteLabel, 1, 3);

        Button submitBtn = new Button("📤 Submit Complaint");
        submitBtn.setStyle(
                "-fx-background-color: #F44336; -fx-text-fill: white; -fx-padding: 14 40; " +
                        "-fx-font-size: 16; -fx-background-radius: 8;"
        );

        submitBtn.setOnAction(e -> {
            if (categoryCombo.getValue() == null) {
                showAlert("Validation Error", "Please select a complaint category", Alert.AlertType.ERROR);
                return;
            }

            if (descArea.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Complaint description is required", Alert.AlertType.ERROR);
                return;
            }

            TransactionItem t = transCombo.getValue();
            String transID = null;
            String accusedID = null;

            if (t != null) {
                transID = t.transactionID;
                accusedID = getAccusedIDFromTransaction(transID);

                if (accusedID == null) {
                    showAlert("Error", "Cannot find the other student in this transaction.", Alert.AlertType.ERROR);
                    return;
                }
            } else {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Enter Student ID");
                dialog.setHeaderText("General Complaint");
                dialog.setContentText("Enter the Student ID you want to report:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    accusedID = result.get().trim();

                    if (!verifyStudentExists(accusedID)) {
                        showAlert("Error", "Student does not exist.", Alert.AlertType.ERROR);
                        return;
                    }
                } else {
                    return;
                }
            }

            boolean success = complaintController.fileComplaint(
                    student.getStudentID(),
                    accusedID,
                    transID,
                    descArea.getText().trim(),
                    categoryCombo.getValue()
            );

            if (success) {
                showAlert("Success", "Your complaint has been submitted.", Alert.AlertType.INFORMATION);
                new FileComplaintView(stage, student).show();
            } else {
                showAlert("Error", "Failed to submit complaint.", Alert.AlertType.ERROR);
            }
        });

        VBox box = new VBox(20, sectionTitle, grid, submitBtn);
        return box;
    }

    private VBox createMyComplaintsSection() {
        VBox section = new VBox(20);

        Label sectionTitle = new Label("My Complaints");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        VBox container = new VBox(15);
        List<Complaint> complaints = complaintController.getStudentComplaints(student.getStudentID());

        if (complaints.isEmpty()) {
            Label empty = new Label("No complaints filed yet.");
            empty.setFont(Font.font(14));
            container.getChildren().add(empty);
        } else {
            for (Complaint c : complaints) {
                container.getChildren().add(createComplaintCard(c));
            }
        }

        section.getChildren().addAll(sectionTitle, container);
        return section;
    }

    private HBox createComplaintCard(Complaint c) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        VBox details = new VBox(8);

        Label category = new Label(c.getCategory());
        category.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label desc = new Label(c.getDescription());
        desc.setWrapText(true);

        Label date = new Label("Filed: " + new SimpleDateFormat("dd MMM yyyy").format(c.getFiledDate()));

        details.getChildren().addAll(category, desc, date);

        VBox statusBox = new VBox(10);
        statusBox.setAlignment(Pos.CENTER_RIGHT);

        Label status = new Label(c.getStatus());
        status.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        status.setTextFill(Color.WHITE);

        switch (c.getStatus()) {
            case "Pending": status.setStyle("-fx-background-color: #FF9800; -fx-padding: 8 20;"); break;
            case "Resolved": status.setStyle("-fx-background-color: #4CAF50; -fx-padding: 8 20;"); break;
            default: status.setStyle("-fx-background-color: #F44336; -fx-padding: 8 20;");
        }

        statusBox.getChildren().addAll(status);

        card.getChildren().addAll(details, statusBox);
        return card;
    }

    // -------------------------------------------------------------------------
    // ✔ FUNCTION 1 — GET ACCUSED ID FROM TRANSACTION
    // -------------------------------------------------------------------------
    private String getAccusedIDFromTransaction(String transactionID) {
        if (transactionID == null) return null;

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

            // Borrow Transaction
            String sql = "SELECT CASE " +
                    " WHEN borrower_id = ? THEN lender_id " +
                    " WHEN lender_id = ? THEN borrower_id " +
                    " END AS accused_id " +
                    "FROM borrow_transactions WHERE transaction_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, student.getStudentID());
            stmt.setString(2, student.getStudentID());
            stmt.setString(3, transactionID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getString("accused_id") != null)
                return rs.getString("accused_id");

            // Purchase Transaction
            sql = "SELECT CASE " +
                    " WHEN buyer_id = ? THEN seller_id " +
                    " WHEN seller_id = ? THEN buyer_id " +
                    " END AS accused_id " +
                    "FROM purchase_transactions WHERE transaction_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, student.getStudentID());
            stmt.setString(2, student.getStudentID());
            stmt.setString(3, transactionID);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getString("accused_id") != null)
                return rs.getString("accused_id");

            // Exchange Transaction
            sql = "SELECT CASE " +
                    " WHEN student1_id = ? THEN student2_id " +
                    " WHEN student2_id = ? THEN student1_id " +
                    " END AS accused_id " +
                    "FROM exchange_transactions WHERE transaction_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, student.getStudentID());
            stmt.setString(2, student.getStudentID());
            stmt.setString(3, transactionID);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getString("accused_id") != null)
                return rs.getString("accused_id");

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // ✔ FUNCTION 2 — VERIFY STUDENT EXISTS
    // -------------------------------------------------------------------------
    private boolean verifyStudentExists(String studentID) {
        String sql = "SELECT COUNT(*) AS count FROM students WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private static class TransactionItem {
        String transactionID;
        String display;

        TransactionItem(String transactionID, String display) {
            this.transactionID = transactionID;
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}
