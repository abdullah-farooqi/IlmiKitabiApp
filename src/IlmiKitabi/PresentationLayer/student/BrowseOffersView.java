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
import java.sql.*;
import java.util.List;

public class BrowseOffersView {
    private Stage stage;
    private Student student;
    private SearchController searchController;
    private VBox resultsContainer;

    public BrowseOffersView(Stage stage, Student student) {
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
        stage.setTitle("Ilmi Kitabi - Browse Offers");
        stage.show();

        performSearch(null, "All", "All", "All");
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

        Label title = new Label("📖 Browse Available Books");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Find and request books from fellow students");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(backBtn, title, subtitle);
        return header;
    }

    private VBox createContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30, 40, 30, 40));

        VBox searchSection = createSearchSection();
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

        TextField searchField = new TextField();
        searchField.setPromptText("Search by title or author...");
        searchField.setPrefWidth(350);
        searchField.setStyle("-fx-padding: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14;");

        Label typeLabel = new Label("Type:");
        typeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("All", "Lending", "Selling");
        typeCombo.setValue("All");
        typeCombo.setPrefWidth(130);

        Label subjectLabel = new Label("Subject:");
        subjectLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<String> subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll("All", "Computer Science", "Software Engineering", "Mathematics",
                "Physics", "Chemistry", "Biology", "English", "Business", "Economics", "Management", "Other");
        subjectCombo.setValue("All");
        subjectCombo.setPrefWidth(180);

        Label conditionLabel = new Label("Condition:");
        conditionLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<String> conditionCombo = new ComboBox<>();
        conditionCombo.getItems().addAll("All", "New", "Like New", "Good", "Fair", "Acceptable");
        conditionCombo.setValue("All");
        conditionCombo.setPrefWidth(130);

        Button searchBtn = new Button("🔍 Search");
        searchBtn.setStyle("-fx-background-color: #2C3E2E; -fx-text-fill: white; -fx-padding: 12 30; -fx-cursor: hand; " +
                "-fx-font-size: 14; -fx-font-weight: bold; -fx-background-radius: 8;");
        searchBtn.setOnAction(e -> performSearch(searchField.getText().trim(), typeCombo.getValue(),
                subjectCombo.getValue(), conditionCombo.getValue()));

        searchBox.getChildren().addAll(searchField, typeLabel, typeCombo, subjectLabel, subjectCombo,
                conditionLabel, conditionCombo, searchBtn);

        section.getChildren().addAll(sectionTitle, searchBox);
        return section;
    }

    private void performSearch(String keyword, String offerType, String subject, String condition) {
        resultsContainer.getChildren().clear();

        List<Book> books = searchController.searchBooks(
                keyword == null || keyword.isEmpty() ? null : keyword, subject, condition);

        if (!offerType.equals("All")) {
            String type = offerType.equals("Lending") ? "LEND" : "SELL";
            books = books.stream().filter(b -> type.equals(b.getOfferType())).collect(java.util.stream.Collectors.toList());
        }

        books = books.stream().filter(b -> !isMyOffer(b)).collect(java.util.stream.Collectors.toList());

        Label resultsTitle = new Label("Available Books (" + books.size() + " found)");
        resultsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        resultsTitle.setTextFill(Color.web("#2C3E2E"));
        resultsContainer.getChildren().add(resultsTitle);

        if (books.isEmpty()) {
            resultsContainer.getChildren().add(createEmptyState());
        } else {
            GridPane grid = new GridPane();
            grid.setHgap(20);
            grid.setVgap(20);

            int col = 0, row = 0;
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
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        card.setPrefSize(400, 320);

        Label bookIcon = new Label("📖");
        bookIcon.setFont(Font.font(40));

        Label title = new Label(book.getTitle());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#2C3E2E"));
        title.setWrapText(true);
        title.setMaxWidth(360);

        Label author = new Label("by " + book.getAuthor());
        author.setFont(Font.font("Arial", 13));
        author.setTextFill(Color.web("#666666"));

        HBox detailsBox = new HBox(15);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        Label subject = new Label("📚 " + book.getSubject());
        subject.setFont(Font.font("Arial", 12));
        subject.setTextFill(Color.web("#999999"));
        Label condition = new Label("✨ " + book.getCondition());
        condition.setFont(Font.font("Arial", 12));
        condition.setTextFill(Color.web("#999999"));
        detailsBox.getChildren().addAll(subject, condition);

        HBox offerBox = new HBox(10);
        offerBox.setAlignment(Pos.CENTER_LEFT);
        Label offerType = new Label(book.getOfferType().equals("LEND") ? "FOR LENDING" : "FOR SALE");
        offerType.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        offerType.setTextFill(Color.WHITE);
        offerType.setStyle("-fx-background-color: " + (book.getOfferType().equals("LEND") ? "#4CAF50" : "#FF9800") +
                "; -fx-padding: 5 10; -fx-background-radius: 5;");

        if (book.getPrice() != null && book.getPrice() > 0) {
            Label price = new Label("PKR " + String.format("%.0f", book.getPrice()));
            price.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            price.setTextFill(Color.web("#2C3E2E"));
            offerBox.getChildren().addAll(offerType, price);
        } else {
            offerBox.getChildren().add(offerType);
        }

        Label owner = new Label("👤 " + (book.getOwnerName() != null ? book.getOwnerName() : "Unknown"));
        owner.setFont(Font.font("Arial", 13));
        owner.setTextFill(Color.web("#666666"));

        Button requestBtn = new Button(book.getOfferType().equals("LEND") ? "📩 Request to Borrow" : "📩 Request to Buy");
        requestBtn.setStyle("-fx-background-color: #2C3E2E; -fx-text-fill: white; -fx-padding: 10 20; " +
                "-fx-cursor: hand; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 13;");
        requestBtn.setMaxWidth(Double.MAX_VALUE);
        requestBtn.setOnAction(e -> handleRequest(book));

        card.getChildren().addAll(bookIcon, title, author, detailsBox, offerBox, owner, requestBtn);
        return card;
    }

    private void handleRequest(Book book) {
        System.out.println("🔄 Starting book request process...");
        System.out.println("   Book ID: " + book.getBookID());
        System.out.println("   Offer Type: " + book.getOfferType());

        // Get owner ID from the database
        String ownerID = getOwnerID(book.getBookID(), book.getOfferType());

        if (ownerID == null) {
            showAlert("Error", "Could not find book owner. Please try again later.", Alert.AlertType.ERROR);
            return;
        }

        System.out.println("✅ Found owner: " + ownerID);

        // Show confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Request Book");
        confirm.setHeaderText(book.getOfferType().equals("LEND") ? "Request to Borrow" : "Request to Buy");
        confirm.setContentText("Book: " + book.getTitle() + "\nOwner: " + book.getOwnerName() + "\n" +
                (book.getPrice() != null && book.getPrice() > 0 ? "Price: PKR " + String.format("%.0f", book.getPrice()) + "\n" : "") +
                "\nSend request to the book owner?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // ✅ FIXED: Create request with proper owner ID
                boolean success = createBookRequest(book, ownerID);
                if (success) {
                    showAlert("Request Sent!",
                            "Your request has been sent to " + book.getOwnerName() + ".\n\n" +
                                    "They will review your request and contact you.\nPlease check 'My Requests' for updates.",
                            Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to send request. Please try again.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private boolean createBookRequest(Book book, String ownerID) {
        String sql = "INSERT INTO book_requests (request_id, requester_id, owner_id, book_id, " +
                "request_type, request_date, status) VALUES (?, ?, ?, ?, ?, NOW(), 'Pending')";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String requestID = "REQ" + System.currentTimeMillis();

            System.out.println("📝 Creating book request:");
            System.out.println("   - Request ID: " + requestID);
            System.out.println("   - Requester: " + student.getStudentID());
            System.out.println("   - Owner: " + ownerID);
            System.out.println("   - Book: " + book.getBookID());
            System.out.println("   - Type: " + book.getOfferType());

            stmt.setString(1, requestID);
            stmt.setString(2, student.getStudentID());
            stmt.setString(3, ownerID);  // ✅ FIXED: Proper owner ID
            stmt.setString(4, book.getBookID());
            stmt.setString(5, book.getOfferType());

            int result = stmt.executeUpdate();
            if (result > 0) {
                System.out.println("✅ Request created successfully: " + requestID);
                return true;
            } else {
                System.err.println("❌ Failed to create request - no rows affected");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error creating request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String getOwnerID(String bookID, String offerType) {
        String table = offerType.equals("LEND") ? "lending_offers" : "selling_offers";
        String sql = "SELECT student_id FROM " + table + " WHERE book_id = ? AND status = 'Available' LIMIT 1";

        System.out.println("🔍 Looking for owner in table: " + table);
        System.out.println("   Book ID: " + bookID);

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String ownerID = rs.getString("student_id");
                System.out.println("✅ Found owner ID: " + ownerID + " for book: " + bookID);
                return ownerID;
            } else {
                System.err.println("❌ No owner found for book: " + bookID + " in table: " + table);
                // Let's check if the book exists in either table
                checkBookInBothTables(bookID);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding owner: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void checkBookInBothTables(String bookID) {
        System.out.println("🔍 Checking book existence in both tables:");

        String[] tables = {"lending_offers", "selling_offers"};
        for (String table : tables) {
            String sql = "SELECT student_id, status FROM " + table + " WHERE book_id = ?";
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bookID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String status = rs.getString("status");
                    String studentID = rs.getString("student_id");
                    System.out.println("   📋 Found in " + table + " - Status: " + status + ", Student ID: " + studentID);
                } else {
                    System.out.println("   ❌ Not found in " + table);
                }
            } catch (SQLException e) {
                System.err.println("   Error checking " + table + ": " + e.getMessage());
            }
        }
    }

    private boolean isMyOffer(Book book) {
        String sql = "SELECT COUNT(*) as count FROM (" +
                "SELECT book_id FROM lending_offers WHERE book_id = ? AND student_id = ? " +
                "UNION ALL SELECT book_id FROM selling_offers WHERE book_id = ? AND student_id = ?) as my_books";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getBookID());
            stmt.setString(2, student.getStudentID());
            stmt.setString(3, book.getBookID());
            stmt.setString(4, student.getStudentID());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private VBox createEmptyState() {
        VBox empty = new VBox(20);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(60));
        empty.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label emptyIcon = new Label("📭");
        emptyIcon.setFont(Font.font(80));

        Label emptyText = new Label("No books available");
        emptyText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        emptyText.setTextFill(Color.web("#666666"));

        Label emptyHint = new Label("Try adjusting your search filters or check back later");
        emptyHint.setFont(Font.font("Arial", 14));
        emptyHint.setTextFill(Color.web("#999999"));

        empty.getChildren().addAll(emptyIcon, emptyText, emptyHint);
        return empty;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}