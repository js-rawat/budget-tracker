package com.budgettracker.repository;

import com.budgettracker.model.Category;
import com.budgettracker.model.Subcategory;
import com.budgettracker.model.Transaction;
import com.budgettracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    List<Transaction> findByUser(User user);
    
    List<Transaction> findByUserOrderByTransactionDateDesc(User user);
    
    List<Transaction> findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(
            User user, LocalDate startDate, LocalDate endDate);
    
    List<Transaction> findByUserAndCategoryOrderByTransactionDateDesc(User user, Category category);
    
    List<Transaction> findByUserAndSubcategoryOrderByTransactionDateDesc(User user, Subcategory subcategory);
    
    List<Transaction> findByUserAndCategoryAndTransactionDateBetweenOrderByTransactionDateDesc(
            User user, Category category, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND YEAR(t.transactionDate) = :year AND MONTH(t.transactionDate) = :month")
    List<Transaction> findByUserAndYearAndMonth(
            @Param("user") User user, @Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.currency = :currency AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double sumAmountByUserAndCurrencyAndDateRange(
            @Param("user") User user, @Param("currency") String currency, 
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.category = :category AND t.currency = :currency AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double sumAmountByUserAndCategoryAndCurrencyAndDateRange(
            @Param("user") User user, @Param("category") Category category, @Param("currency") String currency, 
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}