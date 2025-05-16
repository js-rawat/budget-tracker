package com.budgettracker.service;

import com.budgettracker.model.Budget;
import com.budgettracker.model.Category;
import com.budgettracker.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BudgetService {
    
    /**
     * Create a new budget
     * 
     * @param budget the budget to create
     * @return the created budget
     */
    Budget createBudget(Budget budget);
    
    /**
     * Get a budget by ID
     * 
     * @param id the budget ID
     * @return the budget if found
     */
    Optional<Budget> getBudgetById(UUID id);
    
    /**
     * Get budgets by user
     * 
     * @param user the user
     * @return list of budgets
     */
    List<Budget> getBudgetsByUser(User user);
    
    /**
     * Get budgets by user and month
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @return list of budgets
     */
    List<Budget> getBudgetsByUserAndMonth(User user, int year, int month);
    
    /**
     * Get budgets by user and category
     * 
     * @param user the user
     * @param category the category
     * @return list of budgets
     */
    List<Budget> getBudgetsByUserAndCategory(User user, Category category);
    
    /**
     * Get budget by user, category, and month
     * 
     * @param user the user
     * @param category the category
     * @param year the year
     * @param month the month (1-12)
     * @return the budget if found
     */
    Optional<Budget> getBudgetByUserCategoryAndMonth(User user, Category category, int year, int month);
    
    /**
     * Update a budget
     * 
     * @param id the budget ID
     * @param budget the updated budget data
     * @return the updated budget
     */
    Budget updateBudget(UUID id, Budget budget);
    
    /**
     * Delete a budget
     * 
     * @param id the budget ID
     */
    void deleteBudget(UUID id);
    
    /**
     * Get total budget amount for a user and month
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @param currency the currency code
     * @return the total budget amount
     */
    BigDecimal getTotalBudgetAmount(User user, int year, int month, String currency);
    
    /**
     * Get budget vs actual spending for a user and month
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @param currency the currency code
     * @return map of category to budget vs actual data
     */
    Map<Category, Map<String, BigDecimal>> getBudgetVsActual(User user, int year, int month, String currency);
    
    /**
     * Get budget utilization percentage for a user and month
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @param currency the currency code
     * @return map of category to utilization percentage
     */
    Map<Category, Double> getBudgetUtilization(User user, int year, int month, String currency);
    
    /**
     * Copy budgets from one month to another
     * 
     * @param user the user
     * @param sourceYear the source year
     * @param sourceMonth the source month (1-12)
     * @param targetYear the target year
     * @param targetMonth the target month (1-12)
     * @return list of copied budgets
     */
    List<Budget> copyBudgets(User user, int sourceYear, int sourceMonth, int targetYear, int targetMonth);
    
    /**
     * Adjust budget amount
     * 
     * @param id the budget ID
     * @param amount the new amount
     * @return the updated budget
     */
    Budget adjustBudgetAmount(UUID id, BigDecimal amount);
    
    /**
     * Get remaining budget amount
     * 
     * @param id the budget ID
     * @param currency the currency code
     * @return the remaining budget amount
     */
    BigDecimal getRemainingBudgetAmount(UUID id, String currency);
    
    /**
     * Get budget history for a category
     * 
     * @param user the user
     * @param category the category
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return map of month to budget amount
     */
    Map<LocalDate, BigDecimal> getBudgetHistory(User user, Category category, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get categories without budget for a month
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @return list of categories without budget
     */
    List<Category> getCategoriesWithoutBudget(User user, int year, int month);
    
    /**
     * Create default budgets for a user
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @return list of created budgets
     */
    List<Budget> createDefaultBudgets(User user, int year, int month);
    
    /**
     * Get budget efficiency
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return map of category to efficiency score
     */
    Map<Category, Double> getBudgetEfficiency(User user, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Suggest budget adjustments
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @param currency the currency code
     * @return map of category to suggested adjustment
     */
    Map<Category, BigDecimal> suggestBudgetAdjustments(User user, int year, int month, String currency);
    
    /**
     * Get budget trends
     * 
     * @param user the user
     * @param category the category
     * @param months the number of months
     * @param currency the currency code
     * @return map of month to budget and actual data
     */
    Map<LocalDate, Map<String, BigDecimal>> getBudgetTrends(User user, Category category, int months, String currency);
    
    /**
     * Check if budget exists
     * 
     * @param user the user
     * @param category the category
     * @param year the year
     * @param month the month (1-12)
     * @return true if budget exists
     */
    boolean budgetExists(User user, Category category, int year, int month);
}