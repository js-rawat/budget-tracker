package com.budgettracker.service;

import com.budgettracker.model.Category;
import com.budgettracker.model.Subcategory;
import com.budgettracker.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface SubcategoryService {
    
    /**
     * Create a new subcategory
     * 
     * @param subcategory the subcategory to create
     * @return the created subcategory
     */
    Subcategory createSubcategory(Subcategory subcategory);
    
    /**
     * Get a subcategory by ID
     * 
     * @param id the subcategory ID
     * @return the subcategory if found
     */
    Optional<Subcategory> getSubcategoryById(UUID id);
    
    /**
     * Get a subcategory by name and category
     * 
     * @param name the subcategory name
     * @param category the parent category
     * @return the subcategory if found
     */
    Optional<Subcategory> getSubcategoryByNameAndCategory(String name, Category category);
    
    /**
     * Get subcategories by category
     * 
     * @param category the parent category
     * @return list of subcategories
     */
    List<Subcategory> getSubcategoriesByCategory(Category category);
    
    /**
     * Get subcategories by user
     * 
     * @param user the user
     * @return list of subcategories
     */
    List<Subcategory> getSubcategoriesByUser(User user);
    
    /**
     * Update a subcategory
     * 
     * @param id the subcategory ID
     * @param subcategory the updated subcategory data
     * @return the updated subcategory
     */
    Subcategory updateSubcategory(UUID id, Subcategory subcategory);
    
    /**
     * Delete a subcategory
     * 
     * @param id the subcategory ID
     */
    void deleteSubcategory(UUID id);
    
    /**
     * Check if a subcategory with the given name exists for the category
     * 
     * @param name the subcategory name
     * @param category the parent category
     * @return true if the subcategory exists
     */
    boolean existsByNameAndCategory(String name, Category category);
    
    /**
     * Get total spending by subcategory for a user within a date range
     * 
     * @param user the user
     * @param subcategory the subcategory
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the total spending
     */
    BigDecimal getTotalSpendingBySubcategory(
            User user, Subcategory subcategory, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get monthly spending trends by subcategory
     * 
     * @param user the user
     * @param subcategory the subcategory
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return map of month to total amount
     */
    Map<LocalDate, BigDecimal> getMonthlySpendingTrendsBySubcategory(
            User user, Subcategory subcategory, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Get subcategories by name containing
     * 
     * @param category the parent category
     * @param name the name pattern
     * @return list of matching subcategories
     */
    List<Subcategory> getSubcategoriesByCategoryAndNameContaining(Category category, String name);
    
    /**
     * Get top spending subcategories for a category within a date range
     * 
     * @param user the user
     * @param category the parent category
     * @param startDate the start date
     * @param endDate the end date
     * @param limit the maximum number of results
     * @param currency the currency code
     * @return map of subcategory to total amount, ordered by amount descending
     */
    Map<Subcategory, BigDecimal> getTopSpendingSubcategories(
            User user, Category category, LocalDate startDate, LocalDate endDate, int limit, String currency);
    
    /**
     * Get default subcategories for a category
     * 
     * @param category the parent category
     * @return list of default subcategories
     */
    List<Subcategory> getDefaultSubcategories(Category category);
    
    /**
     * Create default subcategories for a category
     * 
     * @param category the parent category
     * @return list of created subcategories
     */
    List<Subcategory> createDefaultSubcategoriesForCategory(Category category);
    
    /**
     * Get subcategory distribution for a category within a date range
     * 
     * @param user the user
     * @param category the parent category
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return map of subcategory to percentage
     */
    Map<Subcategory, Double> getSubcategoryDistribution(
            User user, Category category, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Move transactions from one subcategory to another
     * 
     * @param sourceSubcategory the source subcategory
     * @param targetSubcategory the target subcategory
     * @return the number of transactions moved
     */
    int moveTransactions(Subcategory sourceSubcategory, Subcategory targetSubcategory);
    
    /**
     * Merge subcategories
     * 
     * @param sourceSubcategory the source subcategory
     * @param targetSubcategory the target subcategory
     * @return the merged subcategory
     */
    Subcategory mergeSubcategories(Subcategory sourceSubcategory, Subcategory targetSubcategory);
    
    /**
     * Get subcategories without transactions
     * 
     * @param category the parent category
     * @return list of subcategories without transactions
     */
    List<Subcategory> getSubcategoriesWithoutTransactions(Category category);
    
    /**
     * Get subcategories with transactions
     * 
     * @param category the parent category
     * @return list of subcategories with transactions
     */
    List<Subcategory> getSubcategoriesWithTransactions(Category category);
    
    /**
     * Get subcategory by transaction
     * 
     * @param transactionId the transaction ID
     * @return the subcategory if found
     */
    Optional<Subcategory> getSubcategoryByTransaction(UUID transactionId);
    
    /**
     * Change subcategory's parent category
     * 
     * @param subcategoryId the subcategory ID
     * @param newCategoryId the new parent category ID
     * @return the updated subcategory
     */
    Subcategory changeParentCategory(UUID subcategoryId, UUID newCategoryId);
}