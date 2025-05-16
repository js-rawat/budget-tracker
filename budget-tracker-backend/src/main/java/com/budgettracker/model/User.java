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
 * Entity representing a user in the system.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The username
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    /**
     * The email address
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    /**
     * The password hash
     */
    @Column(name = "password", nullable = false)
    private String password;
    
    /**
     * The first name
     */
    @Column(name = "first_name")
    private String firstName;
    
    /**
     * The last name
     */
    @Column(name = "last_name")
    private String lastName;
    
    /**
     * Whether the email is verified
     */
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;
    
    /**
     * Whether the account is locked
     */
    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked = false;
    
    /**
     * The number of failed login attempts
     */
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;
    
    /**
     * The authentication provider (e.g., local, google, facebook)
     */
    @Column(name = "auth_provider")
    private String authProvider = "local";
    
    /**
     * The preferred currency
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "preferred_currency_id")
    private Currency preferredCurrency;
    
    /**
     * The secondary currency
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "secondary_currency_id")
    private Currency secondaryCurrency;
    
    /**
     * The user's budgets
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Budget> budgets = new HashSet<>();
    
    /**
     * The user's transactions
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();
    
    /**
     * The user's categories
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> categories = new HashSet<>();
    
    /**
     * The date and time when the user was created
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * The date and time when the user was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * The date and time when the user last logged in
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}