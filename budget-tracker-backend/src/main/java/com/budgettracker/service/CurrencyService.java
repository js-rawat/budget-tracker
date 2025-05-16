package com.budgettracker.service;

import com.budgettracker.model.Currency;
import com.budgettracker.model.User;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {
    
    /**
     * Get all currencies
     * 
     * @return list of all currencies
     */
    List<Currency> getAllCurrencies();
    
    /**
     * Get currency by code
     * 
     * @param code the currency code
     * @return the currency if found
     */
    Optional<Currency> getCurrencyByCode(String code);
    
    /**
     * Get currency by ID
     * 
     * @param id the currency ID
     * @return the currency if found
     */
    Optional<Currency> getCurrencyById(Long id);
    
    /**
     * Create currency
     * 
     * @param code the currency code
     * @param name the currency name
     * @param symbol the currency symbol
     * @param decimalPlaces the number of decimal places
     * @return the created currency
     */
    Currency createCurrency(String code, String name, String symbol, int decimalPlaces);
    
    /**
     * Update currency
     * 
     * @param id the currency ID
     * @param code the currency code
     * @param name the currency name
     * @param symbol the currency symbol
     * @param decimalPlaces the number of decimal places
     * @return the updated currency
     */
    Currency updateCurrency(Long id, String code, String name, String symbol, int decimalPlaces);
    
    /**
     * Delete currency
     * 
     * @param id the currency ID
     * @return true if currency was deleted successfully
     */
    boolean deleteCurrency(Long id);
    
    /**
     * Get user's preferred currency
     * 
     * @param user the user
     * @return the preferred currency
     */
    Currency getUserPreferredCurrency(User user);
    
    /**
     * Set user's preferred currency
     * 
     * @param user the user
     * @param currencyCode the currency code
     * @return true if preferred currency was set successfully
     */
    boolean setUserPreferredCurrency(User user, String currencyCode);
    
    /**
     * Get user's secondary currency
     * 
     * @param user the user
     * @return the secondary currency
     */
    Currency getUserSecondaryCurrency(User user);
    
    /**
     * Set user's secondary currency
     * 
     * @param user the user
     * @param currencyCode the currency code
     * @return true if secondary currency was set successfully
     */
    boolean setUserSecondaryCurrency(User user, String currencyCode);
    
    /**
     * Check if currency code exists
     * 
     * @param code the currency code
     * @return true if currency code exists
     */
    boolean currencyCodeExists(String code);
    
    /**
     * Format amount according to currency
     * 
     * @param amount the amount
     * @param currencyCode the currency code
     * @return the formatted amount
     */
    String formatAmount(double amount, String currencyCode);
    
    /**
     * Format amount according to currency with custom decimal places
     * 
     * @param amount the amount
     * @param currencyCode the currency code
     * @param decimalPlaces the number of decimal places
     * @return the formatted amount
     */
    String formatAmount(double amount, String currencyCode, int decimalPlaces);
    
    /**
     * Parse formatted amount
     * 
     * @param formattedAmount the formatted amount
     * @param currencyCode the currency code
     * @return the parsed amount
     */
    double parseFormattedAmount(String formattedAmount, String currencyCode);
    
    /**
     * Get supported currencies
     * 
     * @return list of supported currency codes
     */
    List<String> getSupportedCurrencies();
    
    /**
     * Check if currency is supported
     * 
     * @param currencyCode the currency code
     * @return true if currency is supported
     */
    boolean isCurrencySupported(String currencyCode);
    
    /**
     * Get currency symbol
     * 
     * @param currencyCode the currency code
     * @return the currency symbol
     */
    String getCurrencySymbol(String currencyCode);
    
    /**
     * Get currency name
     * 
     * @param currencyCode the currency code
     * @return the currency name
     */
    String getCurrencyName(String currencyCode);
    
    /**
     * Get currency decimal places
     * 
     * @param currencyCode the currency code
     * @return the number of decimal places
     */
    int getCurrencyDecimalPlaces(String currencyCode);
    
    /**
     * Get default currency
     * 
     * @return the default currency
     */
    Currency getDefaultCurrency();
    
    /**
     * Set default currency
     * 
     * @param currencyCode the currency code
     * @return true if default currency was set successfully
     */
    boolean setDefaultCurrency(String currencyCode);
    
    /**
     * Get currency by locale
     * 
     * @param locale the locale
     * @return the currency if found
     */
    Optional<Currency> getCurrencyByLocale(String locale);
    
    /**
     * Get currency locale
     * 
     * @param currencyCode the currency code
     * @return the locale
     */
    String getCurrencyLocale(String currencyCode);
    
    /**
     * Set currency locale
     * 
     * @param currencyCode the currency code
     * @param locale the locale
     * @return true if locale was set successfully
     */
    boolean setCurrencyLocale(String currencyCode, String locale);
    
    /**
     * Get currency format pattern
     * 
     * @param currencyCode the currency code
     * @return the format pattern
     */
    String getCurrencyFormatPattern(String currencyCode);
    
    /**
     * Set currency format pattern
     * 
     * @param currencyCode the currency code
     * @param pattern the format pattern
     * @return true if format pattern was set successfully
     */
    boolean setCurrencyFormatPattern(String currencyCode, String pattern);
}