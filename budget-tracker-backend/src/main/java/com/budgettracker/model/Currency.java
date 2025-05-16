package com.budgettracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a currency in the system.
 */
@Entity
@Table(name = "currencies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The currency code (e.g., USD, EUR, ZAR, INR)
     */
    @Column(name = "code", nullable = false, unique = true, length = 3)
    private String code;
    
    /**
     * The currency name (e.g., US Dollar, Euro, South African Rand, Indian Rupee)
     */
    @Column(name = "name", nullable = false)
    private String name;
    
    /**
     * The currency symbol (e.g., $, €, R, ₹)
     */
    @Column(name = "symbol", nullable = false)
    private String symbol;
    
    /**
     * The number of decimal places for the currency
     */
    @Column(name = "decimal_places", nullable = false)
    private int decimalPlaces;
    
    /**
     * The locale for the currency (e.g., en_US, de_DE, en_ZA, hi_IN)
     */
    @Column(name = "locale")
    private String locale;
    
    /**
     * The format pattern for the currency
     */
    @Column(name = "format_pattern")
    private String formatPattern;
    
    /**
     * Whether this is the default currency
     */
    @Column(name = "is_default")
    private boolean isDefault;
    
    /**
     * Whether this currency is active
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}