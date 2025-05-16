package com.budgettracker.service;

import com.budgettracker.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    
    /**
     * Create a new user
     * 
     * @param user the user to create
     * @return the created user
     */
    User createUser(User user);
    
    /**
     * Get a user by ID
     * 
     * @param id the user ID
     * @return the user if found
     */
    Optional<User> getUserById(UUID id);
    
    /**
     * Get a user by email
     * 
     * @param email the user email
     * @return the user if found
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * Get a user by username
     * 
     * @param username the username
     * @return the user if found
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * Get all users
     * 
     * @return list of all users
     */
    List<User> getAllUsers();
    
    /**
     * Update a user
     * 
     * @param id the user ID
     * @param user the updated user data
     * @return the updated user
     */
    User updateUser(UUID id, User user);
    
    /**
     * Delete a user
     * 
     * @param id the user ID
     */
    void deleteUser(UUID id);
    
    /**
     * Check if a user with the given email exists
     * 
     * @param email the email
     * @return true if the user exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if a user with the given username exists
     * 
     * @param username the username
     * @return true if the user exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Change user password
     * 
     * @param id the user ID
     * @param currentPassword the current password
     * @param newPassword the new password
     * @return true if password was changed successfully
     */
    boolean changePassword(UUID id, String currentPassword, String newPassword);
    
    /**
     * Reset user password
     * 
     * @param email the user email
     * @return true if password reset was initiated successfully
     */
    boolean resetPassword(String email);
    
    /**
     * Complete password reset
     * 
     * @param token the reset token
     * @param newPassword the new password
     * @return true if password was reset successfully
     */
    boolean completePasswordReset(String token, String newPassword);
    
    /**
     * Activate user account
     * 
     * @param activationKey the activation key
     * @return true if account was activated successfully
     */
    boolean activateAccount(String activationKey);
    
    /**
     * Update user profile
     * 
     * @param id the user ID
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email
     * @param preferredCurrency the preferred currency
     * @return the updated user
     */
    User updateProfile(UUID id, String firstName, String lastName, String email, String preferredCurrency);
    
    /**
     * Update user settings
     * 
     * @param id the user ID
     * @param settings the settings map
     * @return the updated user
     */
    User updateSettings(UUID id, java.util.Map<String, Object> settings);
    
    /**
     * Get user settings
     * 
     * @param id the user ID
     * @return the user settings map
     */
    java.util.Map<String, Object> getUserSettings(UUID id);
    
    /**
     * Get user by activation key
     * 
     * @param activationKey the activation key
     * @return the user if found
     */
    Optional<User> getUserByActivationKey(String activationKey);
    
    /**
     * Get user by reset key
     * 
     * @param resetKey the reset key
     * @return the user if found
     */
    Optional<User> getUserByResetKey(String resetKey);
    
    /**
     * Search users by name or email
     * 
     * @param query the search query
     * @return list of matching users
     */
    List<User> searchUsers(String query);
    
    /**
     * Lock user account
     * 
     * @param id the user ID
     * @return true if account was locked successfully
     */
    boolean lockAccount(UUID id);
    
    /**
     * Unlock user account
     * 
     * @param id the user ID
     * @return true if account was unlocked successfully
     */
    boolean unlockAccount(UUID id);
    
    /**
     * Update user roles
     * 
     * @param id the user ID
     * @param roles the roles set
     * @return the updated user
     */
    User updateRoles(UUID id, java.util.Set<String> roles);
    
    /**
     * Get user roles
     * 
     * @param id the user ID
     * @return the user roles set
     */
    java.util.Set<String> getUserRoles(UUID id);
    
    /**
     * Check if user has role
     * 
     * @param id the user ID
     * @param role the role
     * @return true if user has the role
     */
    boolean hasRole(UUID id, String role);
}