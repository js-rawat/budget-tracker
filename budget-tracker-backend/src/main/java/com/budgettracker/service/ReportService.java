package com.budgettracker.service;

import com.budgettracker.model.Category;
import com.budgettracker.model.User;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {
    
    /**
     * Generate monthly spending report
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateMonthlySpendingReport(User user, int year, int month, String currency);
    
    /**
     * Generate category breakdown report
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateCategoryBreakdownReport(User user, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Generate budget vs actual report
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateBudgetVsActualReport(User user, int year, int month, String currency);
    
    /**
     * Generate spending trend report
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateSpendingTrendReport(User user, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Generate category spending trend report
     * 
     * @param user the user
     * @param category the category
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateCategorySpendingTrendReport(User user, Category category, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Generate annual report
     * 
     * @param user the user
     * @param year the year
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateAnnualReport(User user, int year, String currency);
    
    /**
     * Generate savings report
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateSavingsReport(User user, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Generate budget utilization report
     * 
     * @param user the user
     * @param year the year
     * @param month the month (1-12)
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateBudgetUtilizationReport(User user, int year, int month, String currency);
    
    /**
     * Generate expense forecast report
     * 
     * @param user the user
     * @param months the number of months to forecast
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateExpenseForecastReport(User user, int months, String currency);
    
    /**
     * Generate income vs expense report
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @param currency the currency code
     * @return the report data
     */
    Map<String, Object> generateIncomeVsExpenseReport(User user, LocalDate startDate, LocalDate endDate, String currency);
    
    /**
     * Export report to PDF
     * 
     * @param reportData the report data
     * @param reportType the report type
     * @return the PDF file
     */
    File exportReportToPdf(Map<String, Object> reportData, String reportType);
    
    /**
     * Export report to Excel
     * 
     * @param reportData the report data
     * @param reportType the report type
     * @return the Excel file
     */
    File exportReportToExcel(Map<String, Object> reportData, String reportType);
    
    /**
     * Export report to CSV
     * 
     * @param reportData the report data
     * @param reportType the report type
     * @return the CSV file
     */
    File exportReportToCsv(Map<String, Object> reportData, String reportType);
    
    /**
     * Schedule report generation
     * 
     * @param user the user
     * @param reportType the report type
     * @param frequency the frequency
     * @param params the report parameters
     * @return true if schedule was set successfully
     */
    boolean scheduleReportGeneration(User user, String reportType, String frequency, Map<String, Object> params);
    
    /**
     * Cancel scheduled report
     * 
     * @param scheduleId the schedule ID
     * @return true if schedule was cancelled successfully
     */
    boolean cancelScheduledReport(String scheduleId);
    
    /**
     * Get scheduled reports
     * 
     * @param user the user
     * @return list of scheduled reports
     */
    List<Map<String, Object>> getScheduledReports(User user);
    
    /**
     * Get available report types
     * 
     * @return list of available report types
     */
    List<String> getAvailableReportTypes();
    
    /**
     * Get report parameters
     * 
     * @param reportType the report type
     * @return map of parameter name to parameter type
     */
    Map<String, String> getReportParameters(String reportType);
    
    /**
     * Generate custom report
     * 
     * @param user the user
     * @param reportConfig the report configuration
     * @return the report data
     */
    Map<String, Object> generateCustomReport(User user, Map<String, Object> reportConfig);
    
    /**
     * Save report template
     * 
     * @param user the user
     * @param templateName the template name
     * @param reportConfig the report configuration
     * @return true if template was saved successfully
     */
    boolean saveReportTemplate(User user, String templateName, Map<String, Object> reportConfig);
    
    /**
     * Get report templates
     * 
     * @param user the user
     * @return list of report templates
     */
    List<Map<String, Object>> getReportTemplates(User user);
    
    /**
     * Delete report template
     * 
     * @param templateId the template ID
     * @return true if template was deleted successfully
     */
    boolean deleteReportTemplate(String templateId);
    
    /**
     * Generate report from template
     * 
     * @param user the user
     * @param templateId the template ID
     * @param params the override parameters
     * @return the report data
     */
    Map<String, Object> generateReportFromTemplate(User user, String templateId, Map<String, Object> params);
    
    /**
     * Get report generation history
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @return list of report generation history entries
     */
    List<Map<String, Object>> getReportGenerationHistory(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get report by ID
     * 
     * @param reportId the report ID
     * @return the report data
     */
    Map<String, Object> getReportById(String reportId);
    
    /**
     * Share report
     * 
     * @param reportId the report ID
     * @param email the recipient email
     * @param expiryDays the number of days until the shared link expires
     * @return the shared report URL
     */
    String shareReport(String reportId, String email, int expiryDays);
}