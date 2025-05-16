import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from './AuthContext';

// Create context
const CurrencyContext = createContext();

// Custom hook to use the currency context
export const useCurrency = () => {
  return useContext(CurrencyContext);
};

// Provider component
export const CurrencyProvider = ({ children }) => {
  const { user, isAuthenticated } = useAuth();
  
  const [currencies, setCurrencies] = useState([]);
  const [preferredCurrency, setPreferredCurrency] = useState(null);
  const [secondaryCurrency, setSecondaryCurrency] = useState(null);
  const [exchangeRates, setExchangeRates] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Load available currencies
  useEffect(() => {
    const loadCurrencies = async () => {
      try {
        const response = await axios.get('/api/currencies');
        setCurrencies(response.data);
        setError(null);
      } catch (err) {
        console.error('Failed to load currencies:', err);
        setError('Failed to load currencies');
      }
    };

    loadCurrencies();
  }, []);

  // Load user currency preferences
  useEffect(() => {
    const loadUserCurrencyPreferences = async () => {
      if (!isAuthenticated || !user) {
        // Set default currencies if user is not authenticated
        setPreferredCurrency(currencies.find(c => c.code === 'ZAR') || null);
        setSecondaryCurrency(currencies.find(c => c.code === 'INR') || null);
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get('/api/users/currency-preferences');
        setPreferredCurrency(response.data.preferredCurrency);
        setSecondaryCurrency(response.data.secondaryCurrency);
        setError(null);
      } catch (err) {
        console.error('Failed to load currency preferences:', err);
        setError('Failed to load currency preferences');
        
        // Set default currencies if there's an error
        setPreferredCurrency(currencies.find(c => c.code === 'ZAR') || null);
        setSecondaryCurrency(currencies.find(c => c.code === 'INR') || null);
      } finally {
        setLoading(false);
      }
    };

    if (currencies.length > 0) {
      loadUserCurrencyPreferences();
    }
  }, [isAuthenticated, user, currencies]);

  // Load exchange rates
  useEffect(() => {
    const loadExchangeRates = async () => {
      if (currencies.length === 0) return;
      
      try {
        const response = await axios.get('/api/currencies/exchange-rates');
        setExchangeRates(response.data);
        setError(null);
      } catch (err) {
        console.error('Failed to load exchange rates:', err);
        setError('Failed to load exchange rates');
      }
    };

    loadExchangeRates();
    
    // Refresh exchange rates every hour
    const interval = setInterval(loadExchangeRates, 3600000);
    return () => clearInterval(interval);
  }, [currencies]);

  // Update user currency preferences
  const updateCurrencyPreferences = async (preferred, secondary) => {
    if (!isAuthenticated) return false;
    
    setLoading(true);
    try {
      const response = await axios.put('/api/users/currency-preferences', {
        preferredCurrencyCode: preferred.code,
        secondaryCurrencyCode: secondary.code,
      });
      
      setPreferredCurrency(response.data.preferredCurrency);
      setSecondaryCurrency(response.data.secondaryCurrency);
      setError(null);
      return true;
    } catch (err) {
      console.error('Failed to update currency preferences:', err);
      setError('Failed to update currency preferences');
      return false;
    } finally {
      setLoading(false);
    }
  };

  // Format amount according to currency
  const formatAmount = (amount, currencyCode = preferredCurrency?.code) => {
    if (!amount && amount !== 0) return '';
    
    const currency = currencies.find(c => c.code === currencyCode) || preferredCurrency;
    if (!currency) return amount.toString();
    
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency.code,
      minimumFractionDigits: currency.decimalPlaces,
      maximumFractionDigits: currency.decimalPlaces,
    }).format(amount);
  };

  // Convert amount between currencies
  const convertAmount = (amount, fromCurrency, toCurrency) => {
    if (!amount && amount !== 0) return 0;
    if (!fromCurrency || !toCurrency || fromCurrency === toCurrency) return amount;
    
    const rate = exchangeRates[`${fromCurrency}_${toCurrency}`];
    if (!rate) return amount;
    
    return amount * rate;
  };

  // Convert and format amount
  const convertAndFormatAmount = (amount, fromCurrency, toCurrency) => {
    const convertedAmount = convertAmount(amount, fromCurrency, toCurrency);
    return formatAmount(convertedAmount, toCurrency);
  };

  // Clear error
  const clearError = () => {
    setError(null);
  };

  // Context value
  const value = {
    currencies,
    preferredCurrency,
    secondaryCurrency,
    exchangeRates,
    loading,
    error,
    updateCurrencyPreferences,
    formatAmount,
    convertAmount,
    convertAndFormatAmount,
    clearError,
  };

  return <CurrencyContext.Provider value={value}>{children}</CurrencyContext.Provider>;
};