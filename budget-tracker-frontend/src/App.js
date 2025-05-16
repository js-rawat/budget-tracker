import React, { useState, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Box, CircularProgress } from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';

// Layout components
import Layout from './components/layout/Layout';

// Auth components
import Login from './components/auth/Login';
import Register from './components/auth/Register';

// Dashboard components
import Dashboard from './components/dashboard/Dashboard';

// Budget components
import BudgetList from './components/budget/BudgetList';
import BudgetForm from './components/budget/BudgetForm';
import BudgetDetail from './components/budget/BudgetDetail';

// Transaction components
import TransactionList from './components/transaction/TransactionList';
import TransactionForm from './components/transaction/TransactionForm';

// Category components
import CategoryList from './components/category/CategoryList';
import CategoryForm from './components/category/CategoryForm';

// Report components
import Reports from './components/report/Reports';

// Settings components
import Settings from './components/settings/Settings';

// Context
import { AuthProvider } from './context/AuthContext';
import { CurrencyProvider } from './context/CurrencyContext';

// Auth guard for protected routes
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  return token ? children : <Navigate to="/login" />;
};

function App() {
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Simulate initial loading
    const timer = setTimeout(() => {
      setLoading(false);
    }, 1000);
    return () => clearTimeout(timer);
  }, []);

  if (loading) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <AuthProvider>
        <CurrencyProvider>
          <Routes>
            {/* Auth routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            
            {/* Protected routes */}
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <Layout />
                </ProtectedRoute>
              }
            >
              <Route index element={<Dashboard />} />
              
              {/* Budget routes */}
              <Route path="budgets" element={<BudgetList />} />
              <Route path="budgets/new" element={<BudgetForm />} />
              <Route path="budgets/edit/:id" element={<BudgetForm />} />
              <Route path="budgets/:id" element={<BudgetDetail />} />
              
              {/* Transaction routes */}
              <Route path="transactions" element={<TransactionList />} />
              <Route path="transactions/new" element={<TransactionForm />} />
              <Route path="transactions/edit/:id" element={<TransactionForm />} />
              
              {/* Category routes */}
              <Route path="categories" element={<CategoryList />} />
              <Route path="categories/new" element={<CategoryForm />} />
              <Route path="categories/edit/:id" element={<CategoryForm />} />
              
              {/* Report routes */}
              <Route path="reports" element={<Reports />} />
              
              {/* Settings routes */}
              <Route path="settings" element={<Settings />} />
            </Route>
            
            {/* Fallback route */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </CurrencyProvider>
      </AuthProvider>
    </LocalizationProvider>
  );
}

export default App;