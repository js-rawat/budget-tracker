package com.budgettracker.service;

import com.budgettracker.model.Budget;
import com.budgettracker.model.Transaction;
import com.budgettracker.model.User;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmailService {
    
    /**
     * Send welcome email to new user
     * 
     * @param user the user
     * @return true if email was sent successfully
     */
    boolean sendWelcomeEmail(User user);
    
    /**
     * Send password reset email
     * 
     * @param user the user
     * @param resetToken the password reset token
     * @return true if email was sent successfully
     */
    boolean sendPasswordResetEmail(User user, String resetToken);
    
    /**
     * Send account verification email
     * 
     * @param user the user
     * @param verificationToken the verification token
     * @return true if email was sent successfully
     */
    boolean sendAccountVerificationEmail(User user, String verificationToken);
    
    /**
     * Send monthly report email
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @param reportData the report data
     * @return true if email was sent successfully
     */
    boolean sendMonthlyReportEmail(User user, int year, int month, Map<String, Object> reportData);
    
    /**
     * Send budget alert email
     * 
     * @param user the user
     * @param budget the budget
     * @param percentageUsed the percentage of budget used
     * @return true if email was sent successfully
     */
    boolean sendBudgetAlertEmail(User user, Budget budget, double percentageUsed);
    
    /**
     * Send transaction notification email
     * 
     * @param user the user
     * @param transaction the transaction
     * @return true if email was sent successfully
     */
    boolean sendTransactionNotificationEmail(User user, Transaction transaction);
    
    /**
     * Send export data email
     * 
     * @param user the user
     * @param exportFile the export file
     * @param startDate the start date
     * @param endDate the end date
     * @return true if email was sent successfully
     */
    boolean sendExportDataEmail(User user, File exportFile, LocalDate startDate, LocalDate endDate);
    
    /**
     * Send custom email
     * 
     * @param to the recipient email address
     * @param subject the email subject
     * @param body the email body
     * @return true if email was sent successfully
     */
    boolean sendCustomEmail(String to, String subject, String body);
    
    /**
     * Send custom email with attachments
     * 
     * @param to the recipient email address
     * @param subject the email subject
     * @param body the email body
     * @param attachments the attachments
     * @return true if email was sent successfully
     */
    boolean sendCustomEmailWithAttachments(String to, String subject, String body, List<File> attachments);
    
    /**
     * Send custom email with template
     * 
     * @param to the recipient email address
     * @param subject the email subject
     * @param templateName the template name
     * @param templateVariables the template variables
     * @return true if email was sent successfully
     */
    boolean sendCustomEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateVariables);
    
    /**
     * Send bulk email
     * 
     * @param recipients the recipient email addresses
     * @param subject the email subject
     * @param body the email body
     * @return the number of emails sent successfully
     */
    int sendBulkEmail(List<String> recipients, String subject, String body);
    
    /**
     * Send bulk email with template
     * 
     * @param recipients the recipient email addresses
     * @param subject the email subject
     * @param templateName the template name
     * @param templateVariables the template variables
     * @return the number of emails sent successfully
     */
    int sendBulkEmailWithTemplate(List<String> recipients, String subject, String templateName, Map<String, Object> templateVariables);
    
    /**
     * Get email template
     * 
     * @param templateName the template name
     * @return the template content
     */
    String getEmailTemplate(String templateName);
    
    /**
     * Create email template
     * 
     * @param templateName the template name
     * @param templateContent the template content
     * @return true if template was created successfully
     */
    boolean createEmailTemplate(String templateName, String templateContent);
    
    /**
     * Update email template
     * 
     * @param templateName the template name
     * @param templateContent the template content
     * @return true if template was updated successfully
     */
    boolean updateEmailTemplate(String templateName, String templateContent);
    
    /**
     * Delete email template
     * 
     * @param templateName the template name
     * @return true if template was deleted successfully
     */
    boolean deleteEmailTemplate(String templateName);
    
    /**
     * Get email sending status
     * 
     * @param emailId the email ID
     * @return the email status
     */
    String getEmailStatus(String emailId);
    
    /**
     * Get email sending history
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @return list of email history entries
     */
    List<Map<String, Object>> getEmailHistory(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Configure email settings
     * 
     * @param settings the email settings
     * @return true if settings were configured successfully
     */
    boolean configureEmailSettings(Map<String, String> settings);
    
    /**
     * Get email settings
     * 
     * @return the email settings
     */
    Map<String, String> getEmailSettings();
    
    /**
     * Test email configuration
     * 
     * @param settings the email settings
     * @return true if configuration is valid
     */
    boolean testEmailConfiguration(Map<String, String> settings);
}