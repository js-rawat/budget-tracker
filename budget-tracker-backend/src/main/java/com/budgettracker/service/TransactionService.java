package com.budgettracker.service;

import com.budgettracker.model.Category;
import com.budgettracker.model.Subcategory;
import com.budgettracker.model.Transaction;
import com.budgettracker.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface TransactionService {
    
    /**
     * Create a new transaction
     * 
     * @param transaction the transaction to create
     * @return the created transaction
     */
    Transaction createTransaction(Transaction transaction);
    
    /**
     * Get a transaction by ID
     * 
     * @param id the transaction ID
     * @return the transaction if found
     */
    Optional<Transaction> getTransactionById(UUID id);
    
    /**
     * Get transactions by user
     * 
     * @param user the user
     * @return list of transactions
     */
    List<Transaction> getTransactionsByUser(User user);
    
    /**
     * Get transactions by user and date range
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @return list of transactions
     */
    List<Transaction> getTransactionsByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get transactions by user and category
     * 
     * @param user the user
     * @param category the category
     * @return list of transactions
     */
    List<Transaction> getTransactionsByUserAndCategory(User user, Category category);
    
    /**
     * Get transactions by user and subcategory
     * 
     * @param user the user
     * @param subcategory the subcategory
     * @return list of transactions
     */
    List<Transaction> getTransactionsByUserAndSubcategory(User user, Subcategory subcategory);
    
    /**
     * Get transactions by user, category, and date range
     * 
     * @param user the user
     * @param category the category
     * @param startDate the start date
     * @param endDate the end date
     * @return list of transactions
     */
    List<Transaction> getTransactionsByUserCategoryAndDateRange(
            User user, Category category, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get transactions by user, subcategory, and date range
     * 
     * @param user the user
     * @param subcategory the subcategory
     * @param startDate the start date
     * @param endDate the end date
     * @return list of transactions
     */
    List<Transaction> getTransactionsByUserSubcategoryAndDateRange(
            User user, Subcategory subcategory, LocalDate startDate, LocalDate endDate);
    
    /**
     * Update a transaction
     * 
     * @param id the transaction ID
     * @param transaction the updated transaction data
     * @return the updated transaction
     */
    Transaction updateTransaction(UUID id, Transaction transaction);
    
    /**
     * Delete a transaction
     * 
     * @param id the transaction ID
     */
    void deleteTransaction(UUID id);
    
    /**
     * Get total amount by user and date range
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the total amount
     */
    BigDecimal getTotalAmountByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get total amount by user, category, and date range
     * 
     * @param user the user
     * @param category the category
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the total amount
     */
    BigDecimal getTotalAmountByUserCategoryAndDateRange(
            User user, Category category, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get total amount by user, subcategory, and date range
     * 
     * @param user the user
     * @param subcategory the subcategory
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the total amount
     */
    BigDecimal getTotalAmountByUserSubcategoryAndDateRange(
            User user, Subcategory subcategory, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get monthly totals by user
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return map of month to total amount
     */
    Map<LocalDate, BigDecimal> getMonthlyTotalsByUser(
            User user, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get monthly totals by user and category
     * 
     * @param user the user
     * @param category the category
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return map of month to total amount
     */
    Map<LocalDate, BigDecimal> getMonthlyTotalsByUserAndCategory(
            User user, Category category, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get monthly totals by user and subcategory
     * 
     * @param user the user
     * @param subcategory the subcategory
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return map of month to total amount
     */
    Map<LocalDate, BigDecimal> getMonthlyTotalsByUserAndSubcategory(
            User user, Subcategory subcategory, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get transactions by description containing
     * 
     * @param user the user
     * @param description the description pattern
     * @return list of matching transactions
     */
    List<Transaction> getTransactionsByUserAndDescriptionContaining(User user, String description);
    
    /**
     * Import transactions from CSV
     * 
     * @param user the user
     * @param csvContent the CSV content
     * @param mappings the column mappings
     * @return the number of transactions imported
     */
    int importTransactionsFromCsv(User user, String csvContent, Map<String, String> mappings);
    
    /**
     * Export transactions to CSV
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @return the CSV content
     */
    String exportTransactionsToCsv(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get transactions by amount range
     * 
     * @param user the user
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @param currency the currency code
     * @return list of transactions
     */
    List<Transaction> getTransactionsByUserAndAmountRange(
            User user, BigDecimal minAmount, BigDecimal maxAmount, String currency);
    
    /**
     * Get transactions by type
     * 
     * @param user the user
     * @param type the transaction type
     * @return list of transactions
     */
    List<Transaction> getTransactionsByUserAndType(User user, String type);
    
    /**
     * Get transactions by payment method
     * 
     * @param user the user
     * @param paymentMethod the payment method
     * @return list of transactions
     */
    List<Transaction> getTransactionsByUserAndPaymentMethod(User user, String paymentMethod);
    
    /**
     * Get recurring transactions
     * 
     * @param user the user
     * @return list of recurring transactions
     */
    List<Transaction> getRecurringTransactions(User user);
    
    /**
     * Create recurring transaction
     * 
     * @param transaction the transaction template
     * @param frequency the frequency
     * @param endDate the end date
     * @return the created recurring transaction ID
     */
    UUID createRecurringTransaction(Transaction transaction, String frequency, LocalDate endDate);
    
    /**
     * Update recurring transaction
     * 
     * @param recurringTransactionId the recurring transaction ID
     * @param transaction the updated transaction template
     * @param frequency the updated frequency
     * @param endDate the updated end date
     * @return the updated recurring transaction ID
     */
    UUID updateRecurringTransaction(
            UUID recurringTransactionId, Transaction transaction, String frequency, LocalDate endDate);
    
    /**
     * Delete recurring transaction
     * 
     * @param recurringTransactionId the recurring transaction ID
     */
    void deleteRecurringTransaction(UUID recurringTransactionId);
}