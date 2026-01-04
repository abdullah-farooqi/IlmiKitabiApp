package IlmiKitabi.BUSINESSLOGICLAYER.controllers;

import IlmiKitabi.BUSINESSLOGICLAYER.models.*;
import IlmiKitabi.DATAACCESSLAYER.TransactionDAO;
import java.util.List;

/**
 * TransactionController - Handles transaction operations
 * GRASP: Controller
 * Implements Use Cases 5, 6, 8: Borrow, Return, Buy
 */
public class TransactionController {
    private TransactionDAO transactionDAO;

    public TransactionController() {
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Get student's transaction history
     */
    public List<TransactionHistory> getTransactionHistory(String studentID) {
        try {
            return transactionDAO.getStudentTransactions(studentID);
        } catch (Exception e) {
            System.err.println("Error retrieving transaction history: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Get active borrow transactions
     */
    public List<BorrowTransaction> getActiveBorrowTransactions(String studentID) {
        try {
            return transactionDAO.getActiveBorrowTransactions(studentID);
        } catch (Exception e) {
            System.err.println("Error retrieving active transactions: " + e.getMessage());
            return List.of();
        }
    }
}