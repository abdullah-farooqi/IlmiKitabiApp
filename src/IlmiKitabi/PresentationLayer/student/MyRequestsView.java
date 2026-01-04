package IlmiKitabi.PresentationLayer.student;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.DATAACCESSLAYER.DatabaseConnection;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * MyRequestsView - View incoming and outgoing requests
 * Handle accept/reject of requests and create actual transactions
 */
public class MyRequestsView {
    private Stage stage;
    private Student student;
    private VBox contentContainer;

    public MyRequestsView(Stage stage, Student student) {
        this.stage = stage;
        this.student = student;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        VBox header = createHeader();
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);

        contentContainer = new VBox(30);
        contentContainer.setPadding(new Insets(30, 40, 30, 40));
        loadRequests();

        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - My Requests");
        stage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(30, 40, 20, 40));
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E2E; -fx-font-size: 14; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            StudentDashboardView dashboard = new StudentDashboardView(stage, student);
            dashboard.show();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #2C3E2E; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadRequests());

        topBar.getChildren().addAll(backBtn, spacer, refreshBtn);

        Label title = new Label("📨 My Requests");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Manage incoming and outgoing book requests");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(topBar, title, subtitle);
        return header;
    }

    private void loadRequests() {
        contentContainer.getChildren().clear();

        // Incoming Requests (Others requesting my books)
        VBox incomingSection = createIncomingRequestsSection();

        // Outgoing Requests (My requests to others)
        VBox outgoingSection = createOutgoingRequestsSection();

        contentContainer.getChildren().addAll(incomingSection, outgoingSection);
    }

    private VBox createIncomingRequestsSection() {
        VBox section = new VBox(20);

        Label sectionTitle = new Label("📥 Incoming Requests (People want your books)");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        VBox requestsContainer = new VBox(15);
        List<RequestData> incomingRequests = getIncomingRequests();

        if (incomingRequests.isEmpty()) {
            requestsContainer.getChildren().add(createEmptyState("No incoming requests"));
        } else {
            for (RequestData request : incomingRequests) {
                requestsContainer.getChildren().add(createIncomingRequestCard(request));
            }
        }

        section.getChildren().addAll(sectionTitle, requestsContainer);
        return section;
    }

    private VBox createOutgoingRequestsSection() {
        VBox section = new VBox(20);

        Label sectionTitle = new Label("📤 My Requests (Books you requested)");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        VBox requestsContainer = new VBox(15);
        List<RequestData> outgoingRequests = getOutgoingRequests();

        if (outgoingRequests.isEmpty()) {
            requestsContainer.getChildren().add(createEmptyState("You haven't requested any books yet"));
        } else {
            for (RequestData request : outgoingRequests) {
                requestsContainer.getChildren().add(createOutgoingRequestCard(request));
            }
        }

        section.getChildren().addAll(sectionTitle, requestsContainer);
        return section;
    }

    private HBox createIncomingRequestCard(RequestData request) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        // Icon
        Label icon = new Label(request.requestType.equals("LEND") ? "📖" : "💰");
        icon.setFont(Font.font(50));

        // Details
        VBox detailsBox = new VBox(8);
        detailsBox.setPrefWidth(500);

        Label bookTitle = new Label(request.bookTitle);
        bookTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        bookTitle.setTextFill(Color.web("#2C3E2E"));

        Label requesterInfo = new Label("Requested by: " + request.requesterName);
        requesterInfo.setFont(Font.font("Arial", 14));
        requesterInfo.setTextFill(Color.web("#666666"));

        Label typeLabel = new Label(request.requestType.equals("LEND") ? "Wants to BORROW" : "Wants to BUY");
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        typeLabel.setTextFill(Color.WHITE);
        typeLabel.setStyle(
                "-fx-background-color: " + (request.requestType.equals("LEND") ? "#4CAF50" : "#FF9800") + "; " +
                        "-fx-padding: 5 10; " +
                        "-fx-background-radius: 5;"
        );

        Label dateLabel = new Label("Requested: " + new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(request.requestDate));
        dateLabel.setFont(Font.font("Arial", 11));
        dateLabel.setTextFill(Color.web("#999999"));

        detailsBox.getChildren().addAll(bookTitle, requesterInfo, typeLabel, dateLabel);

        // Status and Actions
        VBox actionsBox = new VBox(10);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setPrefWidth(300);

        if (request.status.equals("Pending")) {
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);

            Button acceptBtn = new Button("✓ Accept");
            acceptBtn.setStyle(
                    "-fx-background-color: #4CAF50; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 10 25; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 5; " +
                            "-fx-font-weight: bold;"
            );
            acceptBtn.setOnAction(e -> handleAcceptRequest(request));

            Button rejectBtn = new Button("✗ Reject");
            rejectBtn.setStyle(
                    "-fx-background-color: #F44336; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 10 25; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 5; " +
                            "-fx-font-weight: bold;"
            );
            rejectBtn.setOnAction(e -> handleRejectRequest(request));

            buttons.getChildren().addAll(acceptBtn, rejectBtn);
            actionsBox.getChildren().add(buttons);
        } else {
            Label statusLabel = new Label(request.status);
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            statusLabel.setTextFill(Color.WHITE);
            statusLabel.setStyle(
                    "-fx-background-color: " + getStatusColor(request.status) + "; " +
                            "-fx-padding: 8 20; " +
                            "-fx-background-radius: 5;"
            );
            actionsBox.getChildren().add(statusLabel);
        }

        card.getChildren().addAll(icon, detailsBox, actionsBox);
        return card;
    }

    private HBox createOutgoingRequestCard(RequestData request) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        // Icon
        Label icon = new Label(request.requestType.equals("LEND") ? "📖" : "💰");
        icon.setFont(Font.font(50));

        // Details
        VBox detailsBox = new VBox(8);
        detailsBox.setPrefWidth(600);

        Label bookTitle = new Label(request.bookTitle);
        bookTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        bookTitle.setTextFill(Color.web("#2C3E2E"));

        Label ownerInfo = new Label("From: " + request.ownerName);
        ownerInfo.setFont(Font.font("Arial", 14));
        ownerInfo.setTextFill(Color.web("#666666"));

        Label dateLabel = new Label("Requested: " + new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(request.requestDate));
        dateLabel.setFont(Font.font("Arial", 11));
        dateLabel.setTextFill(Color.web("#999999"));

        detailsBox.getChildren().addAll(bookTitle, ownerInfo, dateLabel);

        // Status
        VBox statusBox = new VBox(10);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPrefWidth(200);

        Label statusLabel = new Label(request.status);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setStyle(
                "-fx-background-color: " + getStatusColor(request.status) + "; " +
                        "-fx-padding: 8 20; " +
                        "-fx-background-radius: 5;"
        );
        statusBox.getChildren().add(statusLabel);

        card.getChildren().addAll(icon, detailsBox, statusBox);
        return card;
    }

    /**
     * Accept request and create actual transaction
     */
    private void handleAcceptRequest(RequestData request) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Accept Request");
        confirm.setHeaderText("Accept this request?");
        confirm.setContentText(
                "Book: " + request.bookTitle + "\n" +
                        "Requester: " + request.requesterName + "\n" +
                        "Type: " + (request.requestType.equals("LEND") ? "Borrow" : "Purchase") + "\n\n" +
                        "This will create a transaction and mark the book as unavailable."
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = createTransaction(request);

                if (success) {
                    updateRequestStatus(request.requestId, "Accepted");
                    showAlert("Request Accepted!",
                            "Transaction created successfully.\n" +
                                    "The book has been marked as unavailable.\n" +
                                    "Please contact " + request.requesterName + " to arrange handover.",
                            Alert.AlertType.INFORMATION);
                    loadRequests();
                } else {
                    showAlert("Error", "Failed to create transaction. Please try again.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    /**
     * Reject request
     */
    private void handleRejectRequest(RequestData request) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reject Request");
        confirm.setHeaderText("Reject this request?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                updateRequestStatus(request.requestId, "Rejected");
                showAlert("Request Rejected", "The request has been rejected.", Alert.AlertType.INFORMATION);
                loadRequests();
            }
        });
    }

    /**
     * Create actual transaction (borrow or purchase)
     */
    /**
     * Create actual transaction (borrow or purchase)
     */
    private boolean createTransaction(RequestData request) {
        if (request.requestType.equals("LEND")) {
            return createBorrowTransaction(request);
        } else {
            return createPurchaseTransaction(request);
        }
    }

    /**
     * ✅ FIXED: Create borrow transaction with proper connection handling
     */
    private boolean createBorrowTransaction(RequestData request) {
        String sql = "INSERT INTO borrow_transactions " +
                "(transaction_id, borrower_id, lender_id, book_id, borrow_date, due_date, " +
                "is_returned, is_overdue, status) " +
                "VALUES (?, ?, ?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), FALSE, FALSE, 'Active')";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);

            String transactionId = "BRW" + System.currentTimeMillis();

            stmt.setString(1, transactionId);
            stmt.setString(2, request.requesterId);
            stmt.setString(3, request.ownerId);
            stmt.setString(4, request.bookId);

            int result = stmt.executeUpdate();

            if (result > 0) {
                // Mark book as unavailable
                markBookUnavailable(request.bookId);
                // Update offer status
                updateOfferStatus(request.bookId, "LEND", "Borrowed");
                System.out.println("✅ Borrow transaction created: " + transactionId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error creating borrow transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // ✅ CRITICAL: Close resources in finally block
            try {
                if (stmt != null) stmt.close();
                // Don't close connection - it's managed by singleton
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ✅ FIXED: Create purchase transaction with proper connection handling
     */
    private boolean createPurchaseTransaction(RequestData request) {
        String sql = "INSERT INTO purchase_transactions " +
                "(transaction_id, buyer_id, seller_id, book_id, amount, transaction_date, " +
                "status, payment_method) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), 'Completed', 'Cash')";

        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement priceStmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();

            // First, get the price
            double price = 0.0;
            String priceQuery = "SELECT price FROM selling_offers WHERE book_id = ?";
            priceStmt = conn.prepareStatement(priceQuery);
            priceStmt.setString(1, request.bookId);
            ResultSet rs = priceStmt.executeQuery();

            if (rs.next()) {
                price = rs.getDouble("price");
            }

            // Close price statement before creating main statement
            priceStmt.close();

            // Now create the transaction
            stmt = conn.prepareStatement(sql);
            String transactionId = "PUR" + System.currentTimeMillis();

            stmt.setString(1, transactionId);
            stmt.setString(2, request.requesterId);
            stmt.setString(3, request.ownerId);
            stmt.setString(4, request.bookId);
            stmt.setDouble(5, price);

            int result = stmt.executeUpdate();

            if (result > 0) {
                // Mark book as unavailable
                markBookUnavailable(request.bookId);
                // Update offer status
                updateOfferStatus(request.bookId, "SELL", "Sold");
                System.out.println("✅ Purchase transaction created: " + transactionId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error creating purchase transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // ✅ CRITICAL: Close resources in finally block
            try {
                if (priceStmt != null) priceStmt.close();
                if (stmt != null) stmt.close();
                // Don't close connection - it's managed by singleton
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ✅ Helper methods with proper connection handling
     */
    private void markBookUnavailable(String bookId) {
        String sql = "UPDATE books SET is_available = FALSE WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bookId);
            stmt.executeUpdate();
            System.out.println("✅ Book marked unavailable: " + bookId);

        } catch (SQLException e) {
            System.err.println("❌ Error marking book unavailable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateOfferStatus(String bookId, String offerType, String status) {
        String table = offerType.equals("LEND") ? "lending_offers" : "selling_offers";
        String sql = "UPDATE " + table + " SET status = ? WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, bookId);
            stmt.executeUpdate();
            System.out.println("✅ Offer status updated: " + status);

        } catch (SQLException e) {
            System.err.println("❌ Error updating offer status: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private double getSellingPrice(String bookId) {
        String sql = "SELECT price FROM selling_offers WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void updateRequestStatus(String requestId, String status) {
        String sql = "UPDATE book_requests SET status = ? WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, requestId);
            stmt.executeUpdate();
            System.out.println("✅ Request status updated to: " + status);
        } catch (SQLException e) {
            System.err.println("❌ Error updating request status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<RequestData> getIncomingRequests() {
        List<RequestData> requests = new ArrayList<>();
        String sql = "SELECT br.*, b.title as book_title, s.name as requester_name " +
                "FROM book_requests br " +
                "JOIN books b ON br.book_id = b.book_id " +
                "JOIN students s ON br.requester_id = s.student_id " +
                "WHERE br.owner_id = ? " +
                "ORDER BY br.request_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RequestData data = new RequestData();
                data.requestId = rs.getString("request_id");
                data.requesterId = rs.getString("requester_id");
                data.ownerId = rs.getString("owner_id");
                data.bookId = rs.getString("book_id");
                data.bookTitle = rs.getString("book_title");
                data.requesterName = rs.getString("requester_name");
                data.requestType = rs.getString("request_type");
                data.status = rs.getString("status");
                data.requestDate = rs.getTimestamp("request_date");
                requests.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requests;
    }

    private List<RequestData> getOutgoingRequests() {
        List<RequestData> requests = new ArrayList<>();
        String sql = "SELECT br.*, b.title as book_title, s.name as owner_name " +
                "FROM book_requests br " +
                "JOIN books b ON br.book_id = b.book_id " +
                "JOIN students s ON br.owner_id = s.student_id " +
                "WHERE br.requester_id = ? " +
                "ORDER BY br.request_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RequestData data = new RequestData();
                data.requestId = rs.getString("request_id");
                data.requesterId = rs.getString("requester_id");
                data.ownerId = rs.getString("owner_id");
                data.bookId = rs.getString("book_id");
                data.bookTitle = rs.getString("book_title");
                data.ownerName = rs.getString("owner_name");
                data.requestType = rs.getString("request_type");
                data.status = rs.getString("status");
                data.requestDate = rs.getTimestamp("request_date");
                requests.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requests;
    }

    private String getStatusColor(String status) {
        switch (status) {
            case "Pending": return "#FF9800";
            case "Accepted": return "#4CAF50";
            case "Rejected": return "#F44336";
            default: return "#666666";
        }
    }

    private VBox createEmptyState(String message) {
        VBox empty = new VBox(15);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(60));
        empty.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label emptyIcon = new Label("📭");
        emptyIcon.setFont(Font.font(60));

        Label emptyText = new Label(message);
        emptyText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        emptyText.setTextFill(Color.web("#666666"));

        empty.getChildren().addAll(emptyIcon, emptyText);
        return empty;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class RequestData {
        String requestId;
        String requesterId;
        String ownerId;
        String bookId;
        String bookTitle;
        String requesterName;
        String ownerName;
        String requestType;
        String status;
        java.util.Date requestDate;
    }
}