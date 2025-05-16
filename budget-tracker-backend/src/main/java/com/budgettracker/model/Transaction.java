package com.budgettracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a transaction in the system.
 */
@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The user who owns this transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * The category of this transaction
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    /**
     * The amount of this transaction
     */
    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    /**
     * The currency of this transaction
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;
    
    /**
     * The date of this transaction
     */
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
    
    /**
     * The description of this transaction
     */
    @Column(name = "description")
    private String description;
    
    /**
     * The notes for this transaction
     */
    @Column(name = "notes")
    private String notes;
    
    /**
     * The type of this transaction (e.g., expense, income)
     */
    @Column(name = "type", nullable = false)
    private String type;
    
    /**
     * The payment method (e.g., cash, credit card, bank transfer)
     */
    @Column(name = "payment_method")
    private String paymentMethod;
    
    /**
     * The status of this transaction (e.g., pending, completed, failed)
     */
    @Column(name = "status", nullable = false)
    private String status = "completed";
    
    /**
     * Whether this transaction is recurring
     */
    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring = false;
    
    /**
     * The recurrence pattern (e.g., monthly, yearly)
     */
    @Column(name = "recurrence_pattern")
    private String recurrencePattern;
    
    /**
     * The location where this transaction occurred
     */
    @Column(name = "location")
    private String location;
    
    /**
     * The tags associated with this transaction
     */
    @Column(name = "tags")
    private String tags;
    
    /**
     * The date and time when this transaction was created
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * The date and time when this transaction was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}