package com.budgettracker.repository;

import com.budgettracker.model.Budget;
import com.budgettracker.model.Category;
import com.budgettracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    
    List<Budget> findByUser(User user);
    
    List<Budget> findByUserAndCurrency(User user, String currency);
    
    List<Budget> findByUserAndCategory(User user, Category category);
    
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND :date BETWEEN b.startDate AND b.endDate")
    List<Budget> findByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
    
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.currency = :currency AND :date BETWEEN b.startDate AND b.endDate")
    List<Budget> findByUserAndCurrencyAndDate(
            @Param("user") User user, @Param("currency") String currency, @Param("date") LocalDate date);
    
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.category = :category AND :date BETWEEN b.startDate AND b.endDate")
    List<Budget> findByUserAndCategoryAndDate(
            @Param("user") User user, @Param("category") Category category, @Param("date") LocalDate date);
    
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.category = :category AND b.currency = :currency AND :date BETWEEN b.startDate AND b.endDate")
    Optional<Budget> findByUserAndCategoryAndCurrencyAndDate(
            @Param("user") User user, @Param("category") Category category, 
            @Param("currency") String currency, @Param("date") LocalDate date);
}