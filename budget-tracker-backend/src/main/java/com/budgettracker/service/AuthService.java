package com.budgettracker.service;

import com.budgettracker.model.User;

import java.util.Map;
import java.util.Optional;

public interface AuthService {
    
    /**
     * Register a new user
     * 
     * @param username the username
     * @param email the email
     * @param password the password
     * @param firstName the first name
     * @param lastName the last name
     * @return the registered user
     */
    User register(String username, String email, String password, String firstName, String lastName);
    
    /**
     * Login with username and password
     * 
     * @param username the username
     * @param password the password
     * @return the authentication token
     */
    String login(String username, String password);
    
    /**
     * Login with email and password
     * 
     * @param email the email
     * @param password the password
     * @return the authentication token
     */
    String loginWithEmail(String email, String password);
    
    /**
     * Logout
     * 
     * @param token the authentication token
     * @return true if logout was successful
     */
    boolean logout(String token);
    
    /**
     * Validate token
     * 
     * @param token the authentication token
     * @return true if token is valid
     */
    boolean validateToken(String token);
    
    /**
     * Get user from token
     * 
     * @param token the authentication token
     * @return the user if token is valid
     */
    Optional<User> getUserFromToken(String token);
    
    /**
     * Refresh token
     * 
     * @param token the authentication token
     * @return the new authentication token
     */
    String refreshToken(String token);
    
    /**
     * Change password
     * 
     * @param userId the user ID
     * @param oldPassword the old password
     * @param newPassword the new password
     * @return true if password was changed successfully
     */
    boolean changePassword(String userId, String oldPassword, String newPassword);
    
    /**
     * Reset password
     * 
     * @param email the email
     * @return true if password reset email was sent successfully
     */
    boolean resetPassword(String email);
    
    /**
     * Confirm password reset
     * 
     * @param token the password reset token
     * @param newPassword the new password
     * @return true if password was reset successfully
     */
    boolean confirmPasswordReset(String token, String newPassword);
    
    /**
     * Verify email
     * 
     * @param token the email verification token
     * @return true if email was verified successfully
     */
    boolean verifyEmail(String token);
    
    /**
     * Resend verification email
     * 
     * @param email the email
     * @return true if verification email was sent successfully
     */
    boolean resendVerificationEmail(String email);
    
    /**
     * Check if username exists
     * 
     * @param username the username
     * @return true if username exists
     */
    boolean usernameExists(String username);
    
    /**
     * Check if email exists
     * 
     * @param email the email
     * @return true if email exists
     */
    boolean emailExists(String email);
    
    /**
     * Get token expiration time
     * 
     * @param token the authentication token
     * @return the expiration time in milliseconds
     */
    long getTokenExpirationTime(String token);
    
    /**
     * Get token claims
     * 
     * @param token the authentication token
     * @return the token claims
     */
    Map<String, Object> getTokenClaims(String token);
    
    /**
     * Generate token
     * 
     * @param user the user
     * @return the authentication token
     */
    String generateToken(User user);
    
    /**
     * Generate refresh token
     * 
     * @param user the user
     * @return the refresh token
     */
    String generateRefreshToken(User user);
    
    /**
     * Validate password
     * 
     * @param password the password
     * @return true if password is valid
     */
    boolean validatePassword(String password);
    
    /**
     * Encode password
     * 
     * @param password the password
     * @return the encoded password
     */
    String encodePassword(String password);
    
    /**
     * Check if password matches encoded password
     * 
     * @param password the password
     * @param encodedPassword the encoded password
     * @return true if password matches
     */
    boolean matchesPassword(String password, String encodedPassword);
    
    /**
     * Lock user account
     * 
     * @param userId the user ID
     * @return true if account was locked successfully
     */
    boolean lockUserAccount(String userId);
    
    /**
     * Unlock user account
     * 
     * @param userId the user ID
     * @return true if account was unlocked successfully
     */
    boolean unlockUserAccount(String userId);
    
    /**
     * Check if user account is locked
     * 
     * @param userId the user ID
     * @return true if account is locked
     */
    boolean isUserAccountLocked(String userId);
    
    /**
     * Get failed login attempts
     * 
     * @param userId the user ID
     * @return the number of failed login attempts
     */
    int getFailedLoginAttempts(String userId);
    
    /**
     * Reset failed login attempts
     * 
     * @param userId the user ID
     */
    void resetFailedLoginAttempts(String userId);
    
    /**
     * Get authentication provider
     * 
     * @param userId the user ID
     * @return the authentication provider
     */
    String getAuthenticationProvider(String userId);
    
    /**
     * Set authentication provider
     * 
     * @param userId the user ID
     * @param provider the authentication provider
     */
    void setAuthenticationProvider(String userId, String provider);
    
    /**
     * Get available authentication providers
     * 
     * @return list of available authentication providers
     */
    String[] getAvailableAuthenticationProviders();
}