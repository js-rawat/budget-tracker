package com.budgettracker.repository;

import com.budgettracker.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, UUID> {
    
    Optional<CurrencyRate> findByFromCurrencyAndToCurrencyAndDate(
            String fromCurrency, String toCurrency, LocalDate date);
    
    List<CurrencyRate> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
    
    @Query("SELECT cr FROM CurrencyRate cr WHERE cr.fromCurrency = :fromCurrency AND cr.toCurrency = :toCurrency " +
           "AND cr.date = (SELECT MAX(cr2.date) FROM CurrencyRate cr2 WHERE cr2.fromCurrency = :fromCurrency " +
           "AND cr2.toCurrency = :toCurrency AND cr2.date <= :date)")
    Optional<CurrencyRate> findLatestRateBeforeDate(
            @Param("fromCurrency") String fromCurrency, 
            @Param("toCurrency") String toCurrency, 
            @Param("date") LocalDate date);
    
    @Query("SELECT cr FROM CurrencyRate cr WHERE cr.fromCurrency = :fromCurrency AND cr.toCurrency = :toCurrency " +
           "ORDER BY cr.date DESC")
    List<CurrencyRate> findRateHistoryOrderByDateDesc(
            @Param("fromCurrency") String fromCurrency, 
            @Param("toCurrency") String toCurrency);
    
    @Query("SELECT cr FROM CurrencyRate cr WHERE cr.fromCurrency = :fromCurrency AND cr.toCurrency = :toCurrency " +
           "AND cr.date BETWEEN :startDate AND :endDate ORDER BY cr.date")
    List<CurrencyRate> findRatesByDateRange(
            @Param("fromCurrency") String fromCurrency, 
            @Param("toCurrency") String toCurrency, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
}