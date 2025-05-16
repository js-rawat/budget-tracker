package com.budgettracker.service;

import com.budgettracker.model.CurrencyRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CurrencyRateService {
    
    /**
     * Get currency rate by base currency and target currency for a specific date
     * 
     * @param baseCurrency the base currency code
     * @param targetCurrency the target currency code
     * @param date the date
     * @return the currency rate if found
     */
    Optional<CurrencyRate> getCurrencyRate(String baseCurrency, String targetCurrency, LocalDate date);
    
    /**
     * Get latest currency rate by base currency and target currency
     * 
     * @param baseCurrency the base currency code
     * @param targetCurrency the target currency code
     * @return the currency rate if found
     */
    Optional<CurrencyRate> getLatestCurrencyRate(String baseCurrency, String targetCurrency);
    
    /**
     * Get currency rates by base currency for a specific date
     * 
     * @param baseCurrency the base currency code
     * @param date the date
     * @return map of target currency to rate
     */
    Map<String, BigDecimal> getCurrencyRatesByBaseCurrency(String baseCurrency, LocalDate date);
    
    /**
     * Get latest currency rates by base currency
     * 
     * @param baseCurrency the base currency code
     * @return map of target currency to rate
     */
    Map<String, BigDecimal> getLatestCurrencyRatesByBaseCurrency(String baseCurrency);
    
    /**
     * Get currency rate history
     * 
     * @param baseCurrency the base currency code
     * @param targetCurrency the target currency code
     * @param startDate the start date
     * @param endDate the end date
     * @return list of currency rates
     */
    List<CurrencyRate> getCurrencyRateHistory(String baseCurrency, String targetCurrency, LocalDate startDate, LocalDate endDate);
    
    /**
     * Create currency rate
     * 
     * @param currencyRate the currency rate
     * @return the created currency rate
     */
    CurrencyRate createCurrencyRate(CurrencyRate currencyRate);
    
    /**
     * Update currency rate
     * 
     * @param currencyRate the currency rate
     * @return the updated currency rate
     */
    CurrencyRate updateCurrencyRate(CurrencyRate currencyRate);
    
    /**
     * Delete currency rate
     * 
     * @param id the currency rate ID
     */
    void deleteCurrencyRate(Long id);
    
    /**
     * Convert amount from one currency to another
     * 
     * @param amount the amount to convert
     * @param fromCurrency the source currency code
     * @param toCurrency the target currency code
     * @param date the date for the exchange rate
     * @return the converted amount
     */
    BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency, LocalDate date);
    
    /**
     * Convert amount from one currency to another using latest rates
     * 
     * @param amount the amount to convert
     * @param fromCurrency the source currency code
     * @param toCurrency the target currency code
     * @return the converted amount
     */
    BigDecimal convertCurrencyWithLatestRates(BigDecimal amount, String fromCurrency, String toCurrency);
    
    /**
     * Fetch latest currency rates from external API
     * 
     * @param baseCurrency the base currency code
     * @return true if rates were fetched successfully
     */
    boolean fetchLatestRates(String baseCurrency);
    
    /**
     * Fetch historical currency rates from external API
     * 
     * @param baseCurrency the base currency code
     * @param date the date
     * @return true if rates were fetched successfully
     */
    boolean fetchHistoricalRates(String baseCurrency, LocalDate date);
    
    /**
     * Schedule currency rates update
     * 
     * @param baseCurrency the base currency code
     * @param cronExpression the cron expression
     * @return true if schedule was set successfully
     */
    boolean scheduleCurrencyRatesUpdate(String baseCurrency, String cronExpression);
    
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
     * Get currency rate provider
     * 
     * @return the currency rate provider name
     */
    String getCurrencyRateProvider();
    
    /**
     * Set currency rate provider
     * 
     * @param providerName the currency rate provider name
     * @return true if provider was set successfully
     */
    boolean setCurrencyRateProvider(String providerName);
    
    /**
     * Get available currency rate providers
     * 
     * @return list of available currency rate provider names
     */
    List<String> getAvailableCurrencyRateProviders();
    
    /**
     * Get currency rate provider API key
     * 
     * @param providerName the currency rate provider name
     * @return the API key
     */
    String getCurrencyRateProviderApiKey(String providerName);
    
    /**
     * Set currency rate provider API key
     * 
     * @param providerName the currency rate provider name
     * @param apiKey the API key
     * @return true if API key was set successfully
     */
    boolean setCurrencyRateProviderApiKey(String providerName, String apiKey);
    
    /**
     * Get last update time
     * 
     * @return the last update time
     */
    LocalDate getLastUpdateTime();
    
    /**
     * Get currency rate update frequency
     * 
     * @return the update frequency in hours
     */
    int getCurrencyRateUpdateFrequency();
    
    /**
     * Set currency rate update frequency
     * 
     * @param hours the update frequency in hours
     * @return true if frequency was set successfully
     */
    boolean setCurrencyRateUpdateFrequency(int hours);
    
    /**
     * Get currency rate update status
     * 
     * @return the update status
     */
    String getCurrencyRateUpdateStatus();
    
    /**
     * Get currency rate update history
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of update history entries
     */
    List<Map<String, Object>> getCurrencyRateUpdateHistory(LocalDate startDate, LocalDate endDate);
}