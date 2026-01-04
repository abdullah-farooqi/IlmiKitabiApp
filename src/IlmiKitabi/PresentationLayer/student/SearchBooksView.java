package IlmiKitabi.PresentationLayer.student;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.SearchController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Book;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.DATAACCESSLAYER.DatabaseConnection;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SearchBooksView - Search and filter books
 * Implements Use Case 3: Search & Filter Results
 * Follows SD 3
 */
public class SearchBooksView {
    private Stage stage;
    private Student student;
    private SearchController searchController;
    private VBox resultsContainer;

    public SearchBooksView(Stage stage, Student student) {
        this.stage = stage;
        this.student = student;
        this.searchController = new SearchController();
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

        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Search Books");
        stage.show();

        // Load all books initially
        performSearch(null, "All", "All");
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

        Label title = new Label("🔍 Search Books");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Find books shared by fellow students");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(backBtn, title, subtitle);
        return header;
    }

    private VBox createContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30, 40, 30, 40));

        // Search and filter section
        VBox searchSection = createSearchSection();

        // Results section
        resultsContainer = new VBox(20);

        content.getChildren().addAll(searchSection, resultsContainer);

        return content;
    }

    private VBox createSearchSection() {
        VBox section = new VBox(20);
        section.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 12;");

        Label sectionTitle = new Label("Search & Filters");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        HBox searchBox = new HBox(15);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search by title or author...");
        searchField.setPrefWidth(400);
        searchField.setStyle(
                "-fx-padding: 12; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-font-size: 14;"
        );

        // Subject filter
        Label subjectLabel = new Label("Subject:");
        subjectLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<String> subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll(
                "All", "Computer Science", "Software Engineering", "Mathematics",
                "Physics", "Chemistry", "Biology", "English", "Business",
                "Economics", "Management", "Other"
        );
        subjectCombo.setValue("All");
        subjectCombo.setPrefWidth(180);

        // Condition filter
        Label conditionLabel = new Label("Condition:");
        conditionLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<String> conditionCombo = new ComboBox<>();
        conditionCombo.getItems().addAll("All", "New", "Like New", "Good", "Fair", "Acceptable");
        conditionCombo.setValue("All");
        conditionCombo.setPrefWidth(150);

        // Search button
        Button searchBtn = new Button("🔍 Search");
        searchBtn.setStyle(
                "-fx-background-color: #2C3E2E; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 12 30; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 14; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );
        searchBtn.setOnAction(e -> performSearch(
                searchField.getText().trim(),
                subjectCombo.getValue(),
                conditionCombo.getValue()
        ));

        // Clear button
        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #666666; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-border-width: 2; " +
                        "-fx-padding: 12 30; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 14; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-radius: 8;"
        );
        clearBtn.setOnAction(e -> {
            searchField.clear();
            subjectCombo.setValue("All");
            conditionCombo.setValue("All");
            performSearch(null, "All", "All");
        });

        searchBox.getChildren().addAll(
                searchField, subjectLabel, subjectCombo,
                conditionLabel, conditionCombo, searchBtn, clearBtn
        );

        section.getChildren().addAll(sectionTitle, searchBox);

        return section;
    }

    /**
     * Perform search (SD 3: Search & Filter)
     */
    private void performSearch(String keyword, String subject, String condition) {
        resultsContainer.getChildren().clear();

        // Search via controller
        List<Book> books = searchController.searchBooks(
                keyword == null || keyword.isEmpty() ? null : keyword,
                subject,
                condition
        );

        // Display results
        Label resultsTitle = new Label("Search Results (" + books.size() + " books found)");
        resultsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        resultsTitle.setTextFill(Color.web("#2C3E2E"));

        resultsContainer.getChildren().add(resultsTitle);

        if (books.isEmpty()) {
            VBox emptyBox = new VBox(20);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));
            emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

            Label emptyIcon = new Label("📚");
            emptyIcon.setFont(Font.font(80));

            Label emptyText = new Label("No books found");
            emptyText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            emptyText.setTextFill(Color.web("#666666"));

            Label emptyHint = new Label("Try adjusting your search filters");
            emptyHint.setFont(Font.font("Arial", 14));
            emptyHint.setTextFill(Color.web("#999999"));

            emptyBox.getChildren().addAll(emptyIcon, emptyText, emptyHint);
            resultsContainer.getChildren().add(emptyBox);
        } else {
            GridPane grid = new GridPane();
            grid.setHgap(20);
            grid.setVgap(20);

            int col = 0;
            int row = 0;

            for (Book book : books) {
                VBox bookCard = createBookCard(book);
                grid.add(bookCard, col, row);

                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }

            resultsContainer.getChildren().add(grid);
        }
    }

    private VBox createBookCard(Book book) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        card.setPrefSize(380, 300);

        // Book icon
        Label bookIcon = new Label("📖");
        bookIcon.setFont(Font.font(40));

        // Title
        Label title = new Label(book.getTitle());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#2C3E2E"));
        title.setWrapText(true);
        title.setMaxWidth(340);

        // Author
        Label author = new Label("by " + book.getAuthor());
        author.setFont(Font.font("Arial", 13));
        author.setTextFill(Color.web("#666666"));

        // Details
        HBox detailsBox = new HBox(15);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        Label subject = new Label("📚 " + book.getSubject());
        subject.setFont(Font.font("Arial", 12));
        subject.setTextFill(Color.web("#999999"));

        Label condition = new Label("✨ " + book.getCondition());
        condition.setFont(Font.font("Arial", 12));
        condition.setTextFill(Color.web("#999999"));

        detailsBox.getChildren().addAll(subject, condition);

        // ✅ FIXED: Offer type with proper null handling
        HBox offerBox = new HBox(10);
        offerBox.setAlignment(Pos.CENTER_LEFT);

        String offerTypeValue = (book.getOfferType() != null && !book.getOfferType().isEmpty())
                ? book.getOfferType()
                : "UNKNOWN";

        // Determine label text and color
        String offerLabelText = "UNKNOWN";
        String offerColor = "#666666";

        if (offerTypeValue.equals("LEND")) {
            offerLabelText = "FOR LENDING";
            offerColor = "#4CAF50";
        } else if (offerTypeValue.equals("SELL")) {
            offerLabelText = "FOR SALE";
            offerColor = "#FF9800";
        }

        Label offerType = new Label(offerLabelText);
        offerType.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        offerType.setTextFill(Color.WHITE);
        offerType.setStyle(
                "-fx-background-color: " + offerColor + "; " +
                        "-fx-padding: 5 10; " +
                        "-fx-background-radius: 5;"
        );

        if (book.getPrice() != null && book.getPrice() > 0) {
            Label price = new Label("PKR " + String.format("%.0f", book.getPrice()));
            price.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            price.setTextFill(Color.web("#2C3E2E"));
            offerBox.getChildren().addAll(offerType, price);
        } else {
            offerBox.getChildren().add(offerType);
        }

        // ✅ FIXED: Owner with proper null handling
        String ownerName = (book.getOwnerName() != null && !book.getOwnerName().isEmpty())
                ? book.getOwnerName()
                : "Unknown Owner";

        Label owner = new Label("👤 " + ownerName);
        owner.setFont(Font.font("Arial", 12));
        owner.setTextFill(Color.web("#999999"));

        // ✅ FIXED: Request button that actually sends request
        Button requestBtn = new Button("📩 Request");
        requestBtn.setStyle(
                "-fx-background-color: #C4A57B; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 8 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5; " +
                        "-fx-font-weight: bold;"
        );
        requestBtn.setMaxWidth(Double.MAX_VALUE);

        // ✅ Check if this is my own book
        boolean isMyBook = checkIfMyBook(book.getBookID());

        if (isMyBook) {
            requestBtn.setText("📚 My Book");
            requestBtn.setStyle(
                    "-fx-background-color: #666666; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 8 20; " +
                            "-fx-background-radius: 5; " +
                            "-fx-font-weight: bold;"
            );
            requestBtn.setDisable(true);
        } else {
            requestBtn.setOnAction(e -> handleBookRequest(book));
        }

        card.getChildren().addAll(bookIcon, title, author, detailsBox, offerBox, owner, requestBtn);

        return card;
    }

    /**
     * ✅ NEW: Check if book belongs to current student
     */
    private boolean checkIfMyBook(String bookID) {
        String sql = "SELECT COUNT(*) as count FROM books WHERE book_id = ? AND owner_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bookID);
            stmt.setString(2, student.getStudentID());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * ✅ NEW: Handle book request properly
     */
    private void handleBookRequest(Book book) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Request Book");
        confirm.setHeaderText(book.getOfferType().equals("LEND") ? "Request to Borrow" : "Request to Buy");
        confirm.setContentText("Book: " + book.getTitle() + "\n" +
                "Owner: " + book.getOwnerName() + "\n" +
                (book.getPrice() != null && book.getPrice() > 0 ? "Price: PKR " + String.format("%.0f", book.getPrice()) + "\n" : "") +
                "\nSend request to owner?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Get owner ID from database
                String ownerID = getBookOwnerID(book.getBookID());

                if (ownerID == null) {
                    showAlert("Error", "Could not find book owner.", Alert.AlertType.ERROR);
                    return;
                }

                boolean success = createRequest(book, ownerID);

                if (success) {
                    showAlert("Success", "Request sent successfully!\nThe owner will be notified.",
                            Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to send request. Please try again.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    /**
     * ✅ NEW: Get book owner ID
     */
    private String getBookOwnerID(String bookID) {
        String sql = "SELECT owner_id FROM books WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bookID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("owner_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ✅ NEW: Create request in database
     */
    private boolean createRequest(Book book, String ownerID) {
        String sql = "INSERT INTO book_requests (request_id, requester_id, owner_id, book_id, " +
                "request_type, request_date, status) VALUES (?, ?, ?, ?, ?, NOW(), 'Pending')";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String requestID = "REQ" + System.currentTimeMillis();

            stmt.setString(1, requestID);
            stmt.setString(2, student.getStudentID());
            stmt.setString(3, ownerID);
            stmt.setString(4, book.getBookID());
            stmt.setString(5, book.getOfferType());

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error creating request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        alert.setContentText("This feature will be implemented in the next iteration.");
        alert.showAndWait();
    }
}