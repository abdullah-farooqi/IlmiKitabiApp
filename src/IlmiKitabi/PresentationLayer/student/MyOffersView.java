package IlmiKitabi.PresentationLayer.student;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.DATAACCESSLAYER.*;
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
 * MyOffersView - View student's posted offers
 * Shows lending and selling offers created by the student
 */
public class MyOffersView {
    private Stage stage;
    private Student student;
    private VBox contentContainer;

    public MyOffersView(Stage stage, Student student) {
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
        loadOffers();

        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - My Offers");
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
        refreshBtn.setOnAction(e -> loadOffers());

        topBar.getChildren().addAll(backBtn, spacer, refreshBtn);

        Label title = new Label("📚 My Offers");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("View and manage your lending and selling offers");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(topBar, title, subtitle);
        return header;
    }

    private void loadOffers() {
        contentContainer.getChildren().clear();

        // Stats
        HBox statsBox = new HBox(20);

        int lendingCount = getLendingOffersCount();
        int sellingCount = getSellingOffersCount();

        statsBox.getChildren().addAll(
                createStatCard("Lending Offers", String.valueOf(lendingCount), "📚", "#4CAF50"),
                createStatCard("Selling Offers", String.valueOf(sellingCount), "💰", "#FF9800"),
                createStatCard("Total Offers", String.valueOf(lendingCount + sellingCount), "📊", "#2196F3")
        );

        // Lending Offers Section
        VBox lendingSection = createLendingSection();

        // Selling Offers Section
        VBox sellingSection = createSellingSection();

        contentContainer.getChildren().addAll(statsBox, lendingSection, sellingSection);
    }

    private VBox createStatCard(String title, String value, String icon, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        card.setPrefSize(380, 130);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(40));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        valueLabel.setTextFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 14));
        titleLabel.setTextFill(Color.web("#666666"));

        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }

    private VBox createLendingSection() {
        VBox section = new VBox(20);

        Label sectionTitle = new Label("Lending Offers");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        VBox offersContainer = new VBox(15);

        List<OfferData> lendingOffers = getLendingOffers();

        if (lendingOffers.isEmpty()) {
            offersContainer.getChildren().add(createEmptyState("No lending offers yet", "Create your first lending offer to share books with others"));
        } else {
            for (OfferData offer : lendingOffers) {
                offersContainer.getChildren().add(createOfferCard(offer, "LEND"));
            }
        }

        section.getChildren().addAll(sectionTitle, offersContainer);
        return section;
    }

    private VBox createSellingSection() {
        VBox section = new VBox(20);

        Label sectionTitle = new Label("Selling Offers");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        VBox offersContainer = new VBox(15);

        List<OfferData> sellingOffers = getSellingOffers();

        if (sellingOffers.isEmpty()) {
            offersContainer.getChildren().add(createEmptyState("No selling offers yet", "Create a selling offer to sell your books"));
        } else {
            for (OfferData offer : sellingOffers) {
                offersContainer.getChildren().add(createOfferCard(offer, "SELL"));
            }
        }

        section.getChildren().addAll(sectionTitle, offersContainer);
        return section;
    }

    private HBox createOfferCard(OfferData offer, String type) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        // Book icon
        VBox iconBox = new VBox();
        iconBox.setAlignment(Pos.CENTER);
        Label bookIcon = new Label("📖");
        bookIcon.setFont(Font.font(50));
        iconBox.getChildren().add(bookIcon);

        // Book details
        VBox detailsBox = new VBox(8);
        detailsBox.setPrefWidth(500);

        Label title = new Label(offer.bookTitle);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2C3E2E"));

        Label author = new Label("by " + offer.bookAuthor);
        author.setFont(Font.font("Arial", 14));
        author.setTextFill(Color.web("#666666"));

        HBox metaBox = new HBox(20);
        Label subject = new Label("📚 " + offer.subject);
        subject.setFont(Font.font("Arial", 12));
        subject.setTextFill(Color.web("#999999"));

        Label condition = new Label("✨ " + offer.condition);
        condition.setFont(Font.font("Arial", 12));
        condition.setTextFill(Color.web("#999999"));

        metaBox.getChildren().addAll(subject, condition);

        detailsBox.getChildren().addAll(title, author, metaBox);

        // Offer info
        VBox offerBox = new VBox(8);
        offerBox.setAlignment(Pos.CENTER);
        offerBox.setPrefWidth(250);

        if (type.equals("LEND")) {
            Label duration = new Label(offer.duration + " Days");
            duration.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            duration.setTextFill(Color.web("#4CAF50"));

            Label durationLabel = new Label("Lending Duration");
            durationLabel.setFont(Font.font("Arial", 12));
            durationLabel.setTextFill(Color.web("#999999"));

            offerBox.getChildren().addAll(duration, durationLabel);
        } else {
            Label price = new Label("PKR " + String.format("%.0f", offer.price));
            price.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            price.setTextFill(Color.web("#FF9800"));

            Label priceLabel = new Label(offer.isNegotiable ? "Negotiable" : "Fixed Price");
            priceLabel.setFont(Font.font("Arial", 12));
            priceLabel.setTextFill(Color.web("#999999"));

            offerBox.getChildren().addAll(price, priceLabel);
        }

        // Status and actions
        VBox statusBox = new VBox(10);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPrefWidth(200);

        Label status = new Label(offer.status);
        status.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        status.setTextFill(Color.WHITE);
        status.setStyle(
                "-fx-background-color: " + (offer.status.equals("Available") ? "#4CAF50" : "#FF9800") + "; " +
                        "-fx-padding: 6 15; " +
                        "-fx-background-radius: 5;"
        );

        Label postDate = new Label("Posted: " + new SimpleDateFormat("dd MMM yyyy").format(offer.postDate));
        postDate.setFont(Font.font("Arial", 11));
        postDate.setTextFill(Color.web("#999999"));

        Button deleteBtn = new Button("🗑️ Delete");
        deleteBtn.setStyle(
                "-fx-background-color: #F44336; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 8 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"
        );
        deleteBtn.setOnAction(e -> handleDeleteOffer(offer, type));

        statusBox.getChildren().addAll(status, postDate, deleteBtn);

        card.getChildren().addAll(iconBox, detailsBox, offerBox, statusBox);

        return card;
    }

    private VBox createEmptyState(String title, String message) {
        VBox empty = new VBox(15);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(60));
        empty.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label emptyIcon = new Label("📭");
        emptyIcon.setFont(Font.font(60));

        Label emptyTitle = new Label(title);
        emptyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        emptyTitle.setTextFill(Color.web("#666666"));

        Label emptyMsg = new Label(message);
        emptyMsg.setFont(Font.font("Arial", 13));
        emptyMsg.setTextFill(Color.web("#999999"));

        empty.getChildren().addAll(emptyIcon, emptyTitle, emptyMsg);
        return empty;
    }
    // Handle delete offer for lending or selling offers
    private void handleDeleteOffer(OfferData offer, String type) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Offer");
        confirm.setHeaderText("Are you sure you want to delete this offer?");
        confirm.setContentText(
                "Book: " + offer.bookTitle + "\nType: " + (type.equals("LEND") ? "Lending" : "Selling") +
                        "\n\nThis action cannot be undone."
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String sql;
                if (type.equals("LEND")) {
                    sql = "DELETE FROM lending_offers WHERE offer_id = ?";
                } else {
                    sql = "DELETE FROM selling_offers WHERE offer_id = ?";
                }

                try (Connection conn = DatabaseConnection.getInstance().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, offer.offerID);
                    int rows = stmt.executeUpdate();

                    if (rows > 0) {
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Success");
                        success.setHeaderText(null);
                        success.setContentText("Offer deleted successfully!");
                        success.showAndWait();

                        // Refresh the list
                        loadOffers();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText("Deletion failed");
                        error.setContentText("Could not delete the offer. Please try again.");
                        error.showAndWait();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Database Error");
                    error.setHeaderText("Could not delete offer");
                    error.setContentText("Error: " + e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    // Database methods
    private int getLendingOffersCount() {
        String sql = "SELECT COUNT(*) as count FROM lending_offers WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getSellingOffersCount() {
        String sql = "SELECT COUNT(*) as count FROM selling_offers WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private List<OfferData> getLendingOffers() {
        List<OfferData> offers = new ArrayList<>();
        String sql = "SELECT lo.*, b.title, b.author, b.subject, b.book_condition " +
                "FROM lending_offers lo JOIN books b ON lo.book_id = b.book_id " +
                "WHERE lo.student_id = ? ORDER BY lo.post_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OfferData offer = new OfferData();
                offer.offerID = rs.getString("offer_id");
                offer.bookTitle = rs.getString("title");
                offer.bookAuthor = rs.getString("author");
                offer.subject = rs.getString("subject");
                offer.condition = rs.getString("book_condition");
                offer.duration = rs.getInt("duration");
                offer.status = rs.getString("status");
                offer.postDate = rs.getTimestamp("post_date");
                offers.add(offer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offers;
    }

    private List<OfferData> getSellingOffers() {
        List<OfferData> offers = new ArrayList<>();
        String sql = "SELECT so.*, b.title, b.author, b.subject, b.book_condition " +
                "FROM selling_offers so JOIN books b ON so.book_id = b.book_id " +
                "WHERE so.student_id = ? ORDER BY so.post_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OfferData offer = new OfferData();
                offer.offerID = rs.getString("offer_id");
                offer.bookTitle = rs.getString("title");
                offer.bookAuthor = rs.getString("author");
                offer.subject = rs.getString("subject");
                offer.condition = rs.getString("book_condition");
                offer.price = rs.getDouble("price");
                offer.isNegotiable = rs.getBoolean("is_negotiable");
                offer.status = rs.getString("status");
                offer.postDate = rs.getTimestamp("post_date");
                offers.add(offer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offers;
    }

    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(feature);
        alert.setContentText("This feature will be implemented in the next iteration.");
        alert.showAndWait();
    }

    // Helper class for offer data
    private static class OfferData {
        String offerID;
        String bookTitle;
        String bookAuthor;
        String subject;
        String condition;
        int duration;
        double price;
        boolean isNegotiable;
        String status;
        java.util.Date postDate;
    }
}