package IlmiKitabi.BUSINESSLOGICLAYER.controllers;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Book;
import IlmiKitabi.DATAACCESSLAYER.BookDAO;

import java.util.List;

/**
 * SearchController - Handles book search operations
 * GRASP: Controller
 * Implements Use Case 3: Search & Filter Results
 * Follows SD 3
 */
public class SearchController {
    private BookDAO bookDAO;

    public SearchController() {
        this.bookDAO = new BookDAO();
    }

    /**
     * Search books with filters (SD 3: Search & Filter)
     */
    public List<Book> searchBooks(String keyword, String subject, String condition) {
        try {
            return bookDAO.searchBooks(keyword, subject, condition);
        } catch (Exception e) {
            System.err.println("Error searching books: " + e.getMessage());
            return List.of();
        }
    }
}