package com.budgettracker.service;

import com.budgettracker.model.Category;
import com.budgettracker.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService {
    
    /**
     * Create a new category
     * 
     * @param category the category to create
     * @return the created category
     */
    Category createCategory(Category category);
    
    /**
     * Get a category by ID
     * 
     * @param id the category ID
     * @return the category if found
     */
    Optional<Category> getCategoryById(UUID id);
    
    /**
     * Get a category by name and user
     * 
     * @param name the category name
     * @param user the user
     * @return the category if found
     */
    Optional<Category> getCategoryByNameAndUser(String name, User user);
    
    /**
     * Get categories by user
     * 
     * @param user the user
     * @return list of categories
     */
    List<Category> getCategoriesByUser(User user);
    
    /**
     * Update a category
     * 
     * @param id the category ID
     * @param category the updated category data
     * @return the updated category
     */
    Category updateCategory(UUID id, Category category);
    
    /**
     * Delete a category
     * 
     * @param id the category ID
     */
    void deleteCategory(UUID id);
    
    /**
     * Check if a category with the given name exists for the user
     * 
     * @param name the category name
     * @param user the user
     * @return true if the category exists
     */
    boolean existsByNameAndUser(String name, User user);
    
    /**
     * Get total spending by category for a user within a date range
     * 
     * @param user the user
     * @param category the category
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the total spending
     */
    BigDecimal getTotalSpendingByCategory(
            User user, Category category, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get monthly spending trends by category
     * 
     * @param user the user
     * @param category the category
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return map of month to total amount
     */
    Map<LocalDate, BigDecimal> getMonthlySpendingTrendsByCategory(
            User user, Category category, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get categories by name containing
     * 
     * @param user the user
     * @param name the name pattern
     * @return list of matching categories
     */
    List<Category> getCategoriesByUserAndNameContaining(User user, String name);
    
    /**
     * Get top spending categories for a user within a date range
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @param limit the maximum number of results
     * @param currency the currency code
     * @return map of category to total amount, ordered by amount descending
     */
    Map<Category, BigDecimal> getTopSpendingCategories(
            User user, LocalDate startDate, LocalDate endDate, int limit, String currency);
    
    /**
     * Get default categories
     * 
     * @return list of default categories
     */
    List<Category> getDefaultCategories();
    
    /**
     * Create default categories for a user
     * 
     * @param user the user
     * @return list of created categories
     */
    List<Category> createDefaultCategoriesForUser(User user);
    
    /**
     * Get category distribution for a user within a date range
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return map of category to percentage
     */
    Map<Category, Double> getCategoryDistribution(
            User user, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Move transactions from one category to another
     * 
     * @param sourceCategory the source category
     * @param targetCategory the target category
     * @return the number of transactions moved
     */
    int moveTransactions(Category sourceCategory, Category targetCategory);
    
    /**
     * Merge categories
     * 
     * @param sourceCategory the source category
     * @param targetCategory the target category
     * @return the merged category
     */
    Category mergeCategories(Category sourceCategory, Category targetCategory);
    
    /**
     * Get categories without transactions
     * 
     * @param user the user
     * @return list of categories without transactions
     */
    List<Category> getCategoriesWithoutTransactions(User user);
    
    /**
     * Get categories with transactions
     * 
     * @param user the user
     * @return list of categories with transactions
     */
    List<Category> getCategoriesWithTransactions(User user);
    
    /**
     * Get categories with budgets for a specific month
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @return list of categories with budgets
     */
    List<Category> getCategoriesWithBudgets(User user, int year, int month);
    
    /**
     * Get categories without budgets for a specific month
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @return list of categories without budgets
     */
    List<Category> getCategoriesWithoutBudgets(User user, int year, int month);
}