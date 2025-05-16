package com.budgettracker.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a category in the system.
 */
@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The user who owns this category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * The parent category (if this is a subcategory)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    /**
     * The subcategories of this category
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> subcategories = new HashSet<>();
    
    /**
     * The name of this category
     */
    @Column(name = "name", nullable = false)
    private String name;
    
    /**
     * The description of this category
     */
    @Column(name = "description")
    private String description;
    
    /**
     * The color code for this category
     */
    @Column(name = "color")
    private String color;
    
    /**
     * The icon for this category
     */
    @Column(name = "icon")
    private String icon;
    
    /**
     * The type of this category (e.g., expense, income)
     */
    @Column(name = "type", nullable = false)
    private String type;
    
    /**
     * Whether this category is a default category
     */
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;
    
    /**
     * Whether this category is active
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    /**
     * The date and time when this category was created
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * The date and time when this category was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * The budgets associated with this category
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Budget> budgets = new HashSet<>();
    
    /**
     * The transactions associated with this category
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();
}