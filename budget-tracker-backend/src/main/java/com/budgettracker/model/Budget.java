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
 * Entity representing a budget in the system.
 */
@Entity
@Table(name = "budgets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The user who owns this budget
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * The category for this budget
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    /**
     * The amount allocated for this budget
     */
    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    /**
     * The currency for this budget
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;
    
    /**
     * The start date for this budget
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    /**
     * The end date for this budget
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    /**
     * The name of this budget
     */
    @Column(name = "name", nullable = false)
    private String name;
    
    /**
     * The description of this budget
     */
    @Column(name = "description")
    private String description;
    
    /**
     * Whether this budget is recurring
     */
    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring = false;
    
    /**
     * The recurrence pattern (e.g., monthly, yearly)
     */
    @Column(name = "recurrence_pattern")
    private String recurrencePattern;
    
    /**
     * Whether this budget is active
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    /**
     * The date and time when this budget was created
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * The date and time when this budget was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}