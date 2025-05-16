import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Grid,
  Paper,
  Typography,
  Button,
  Card,
  CardContent,
  CardActions,
  Divider,
  CircularProgress,
  Alert,
  IconButton,
  Tabs,
  Tab,
} from '@mui/material';
import {
  Add as AddIcon,
  ArrowForward as ArrowForwardIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  AccountBalance as AccountBalanceIcon,
  Receipt as ReceiptIcon,
} from '@mui/icons-material';
import { LineChart } from '@mui/x-charts/LineChart';
import { PieChart } from '@mui/x-charts/PieChart';
import { format } from 'date-fns';

import { useAuth } from '../../context/AuthContext';
import { useCurrency } from '../../context/CurrencyContext';
import axios from 'axios';

function Dashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { formatAmount, preferredCurrency } = useCurrency();
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [dashboardData, setDashboardData] = useState(null);
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth());
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [tabValue, setTabValue] = useState(0);
  
  // Fetch dashboard data
  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      try {
        const response = await axios.get('/api/dashboard', {
          params: {
            month: selectedMonth + 1, // API expects 1-12 for months
            year: selectedYear,
          },
        });
        setDashboardData(response.data);
        setError(null);
      } catch (err) {
        console.error('Failed to fetch dashboard data:', err);
        setError('Failed to load dashboard data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [selectedMonth, selectedYear]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleMonthChange = (month) => {
    let newMonth = month;
    let newYear = selectedYear;
    
    if (month < 0) {
      newMonth = 11;
      newYear = selectedYear - 1;
    } else if (month > 11) {
      newMonth = 0;
      newYear = selectedYear + 1;
    }
    
    setSelectedMonth(newMonth);
    setSelectedYear(newYear);
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box mt={3}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  if (!dashboardData) {
    return (
      <Box mt={3}>
        <Alert severity="info">No data available for the selected period.</Alert>
      </Box>
    );
  }

  const {
    summary,
    budgets,
    recentTransactions,
    expensesByCategory,
    monthlyTrends,
  } = dashboardData;

  // Prepare data for charts
  const expensePieData = expensesByCategory.map(category => ({
    id: category.name,
    value: category.amount,
    label: category.name,
    color: category.color || undefined,
  }));

  const trendLineData = {
    income: monthlyTrends.map(trend => trend.income),
    expense: monthlyTrends.map(trend => trend.expense),
    xAxis: monthlyTrends.map(trend => format(new Date(trend.year, trend.month - 1), 'MMM')),
  };

  return (
    <Box>
      {/* Header with month selector */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Dashboard
        </Typography>
        <Box display="flex" alignItems="center">
          <IconButton onClick={() => handleMonthChange(selectedMonth - 1)}>
            <ArrowForwardIcon style={{ transform: 'rotate(180deg)' }} />
          </IconButton>
          <Typography variant="h6" sx={{ mx: 2 }}>
            {format(new Date(selectedYear, selectedMonth), 'MMMM yyyy')}
          </Typography>
          <IconButton onClick={() => handleMonthChange(selectedMonth + 1)}>
            <ArrowForwardIcon />
          </IconButton>
        </Box>
      </Box>

      {/* Summary Cards */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Paper elevation={2} sx={{ p: 2, height: '100%' }}>
            <Typography variant="subtitle2" color="textSecondary">Income</Typography>
            <Box display="flex" alignItems="center" mt={1}>
              <TrendingUpIcon color="success" sx={{ mr: 1 }} />
              <Typography variant="h5" component="div">
                {formatAmount(summary.income)}
              </Typography>
            </Box>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Paper elevation={2} sx={{ p: 2, height: '100%' }}>
            <Typography variant="subtitle2" color="textSecondary">Expenses</Typography>
            <Box display="flex" alignItems="center" mt={1}>
              <TrendingDownIcon color="error" sx={{ mr: 1 }} />
              <Typography variant="h5" component="div">
                {formatAmount(summary.expenses)}
              </Typography>
            </Box>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Paper elevation={2} sx={{ p: 2, height: '100%' }}>
            <Typography variant="subtitle2" color="textSecondary">Balance</Typography>
            <Box display="flex" alignItems="center" mt={1}>
              <AccountBalanceIcon color="primary" sx={{ mr: 1 }} />
              <Typography variant="h5" component="div" color={summary.balance >= 0 ? 'success.main' : 'error.main'}>
                {formatAmount(summary.balance)}
              </Typography>
            </Box>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Paper elevation={2} sx={{ p: 2, height: '100%' }}>
            <Typography variant="subtitle2" color="textSecondary">Budget Utilization</Typography>
            <Box display="flex" alignItems="center" mt={1}>
              <Box position="relative" display="inline-flex" mr={1}>
                <CircularProgress
                  variant="determinate"
                  value={Math.min(summary.budgetUtilization, 100)}
                  color={summary.budgetUtilization > 100 ? 'error' : 'primary'}
                />
                <Box
                  top={0}
                  left={0}
                  bottom={0}
                  right={0}
                  position="absolute"
                  display="flex"
                  alignItems="center"
                  justifyContent="center"
                >
                  <Typography variant="caption" component="div" color="text.secondary">
                    {`${Math.round(summary.budgetUtilization)}%`}
                  </Typography>
                </Box>
              </Box>
              <Typography variant="h5" component="div">
                {formatAmount(summary.budgetRemaining)}
              </Typography>
            </Box>
          </Paper>
        </Grid>
      </Grid>

      {/* Tabs for different sections */}
      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={tabValue} onChange={handleTabChange} aria-label="dashboard tabs">
          <Tab label="Overview" id="tab-0" />
          <Tab label="Budgets" id="tab-1" />
          <Tab label="Transactions" id="tab-2" />
        </Tabs>
      </Box>

      {/* Tab Content */}
      <Box role="tabpanel" hidden={tabValue !== 0} id="tabpanel-0">
        {tabValue === 0 && (
          <Grid container spacing={3}>
            {/* Expense by Category Chart */}
            <Grid item xs={12} md={6}>
              <Paper elevation={2} sx={{ p: 2, height: '100%' }}>
                <Typography variant="h6" gutterBottom>
                  Expenses by Category
                </Typography>
                <Box height={300} display="flex" justifyContent="center" alignItems="center">
                  {expensePieData.length > 0 ? (
                    <PieChart
                      series={[
                        {
                          data: expensePieData,
                          highlightScope: { faded: 'global', highlighted: 'item' },
                          faded: { innerRadius: 30, additionalRadius: -30, color: 'gray' },
                        },
                      ]}
                      height={280}
                    />
                  ) : (
                    <Typography color="textSecondary">No expense data available</Typography>
                  )}
                </Box>
              </Paper>
            </Grid>

            {/* Monthly Trends Chart */}
            <Grid item xs={12} md={6}>
              <Paper elevation={2} sx={{ p: 2, height: '100%' }}>
                <Typography variant="h6" gutterBottom>
                  Monthly Trends
                </Typography>
                <Box height={300} display="flex" justifyContent="center" alignItems="center">
                  {trendLineData.income.length > 0 ? (
                    <LineChart
                      xAxis={[{ data: trendLineData.xAxis, scaleType: 'band' }]}
                      series={[
                        {
                          data: trendLineData.income,
                          label: 'Income',
                          color: '#4caf50',
                        },
                        {
                          data: trendLineData.expense,
                          label: 'Expenses',
                          color: '#f44336',
                        },
                      ]}
                      height={280}
                    />
                  ) : (
                    <Typography color="textSecondary">No trend data available</Typography>
                  )}
                </Box>
              </Paper>
            </Grid>
          </Grid>
        )}
      </Box>

      <Box role="tabpanel" hidden={tabValue !== 1} id="tabpanel-1">
        {tabValue === 1 && (
          <Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="h6">Budgets</Typography>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => navigate('/budgets/new')}
              >
                New Budget
              </Button>
            </Box>
            <Grid container spacing={2}>
              {budgets.length > 0 ? (
                budgets.map((budget) => (
                  <Grid item xs={12} sm={6} md={4} key={budget.id}>
                    <Card>
                      <CardContent>
                        <Typography variant="h6" component="div">
                          {budget.name}
                        </Typography>
                        <Typography color="textSecondary" gutterBottom>
                          {budget.category.name}
                        </Typography>
                        <Box mt={2} mb={1}>
                          <Box display="flex" justifyContent="space-between">
                            <Typography variant="body2">
                              {formatAmount(budget.spent)} / {formatAmount(budget.amount)}
                            </Typography>
                            <Typography variant="body2">
                              {Math.round((budget.spent / budget.amount) * 100)}%
                            </Typography>
                          </Box>
                          <Box
                            sx={{
                              mt: 1,
                              height: 10,
                              width: '100%',
                              bgcolor: 'grey.300',
                              borderRadius: 5,
                              position: 'relative',
                            }}
                          >
                            <Box
                              sx={{
                                height: '100%',
                                borderRadius: 5,
                                bgcolor: budget.spent > budget.amount ? 'error.main' : 'primary.main',
                                width: `${Math.min((budget.spent / budget.amount) * 100, 100)}%`,
                              }}
                            />
                          </Box>
                        </Box>
                      </CardContent>
                      <CardActions>
                        <Button size="small" onClick={() => navigate(`/budgets/${budget.id}`)}>
                          View Details
                        </Button>
                      </CardActions>
                    </Card>
                  </Grid>
                ))
              ) : (
                <Grid item xs={12}>
                  <Alert severity="info">
                    No budgets found for this month. Create a new budget to start tracking your expenses.
                  </Alert>
                </Grid>
              )}
            </Grid>
          </Box>
        )}
      </Box>

      <Box role="tabpanel" hidden={tabValue !== 2} id="tabpanel-2">
        {tabValue === 2 && (
          <Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="h6">Recent Transactions</Typography>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => navigate('/transactions/new')}
              >
                New Transaction
              </Button>
            </Box>
            <Paper elevation={2}>
              {recentTransactions.length > 0 ? (
                recentTransactions.map((transaction, index) => (
                  <React.Fragment key={transaction.id}>
                    <Box p={2} display="flex" alignItems="center">
                      <Box
                        sx={{
                          bgcolor: transaction.type === 'INCOME' ? 'success.light' : 'error.light',
                          borderRadius: '50%',
                          p: 1,
                          mr: 2,
                        }}
                      >
                        {transaction.type === 'INCOME' ? (
                          <TrendingUpIcon color="success" />
                        ) : (
                          <TrendingDownIcon color="error" />
                        )}
                      </Box>
                      <Box flexGrow={1}>
                        <Typography variant="body1">{transaction.description}</Typography>
                        <Typography variant="body2" color="textSecondary">
                          {transaction.category.name} • {format(new Date(transaction.transactionDate), 'MMM dd, yyyy')}
                        </Typography>
                      </Box>
                      <Typography
                        variant="body1"
                        color={transaction.type === 'INCOME' ? 'success.main' : 'error.main'}
                      >
                        {transaction.type === 'INCOME' ? '+' : '-'} {formatAmount(transaction.amount)}
                      </Typography>
                    </Box>
                    {index < recentTransactions.length - 1 && <Divider />}
                  </React.Fragment>
                ))
              ) : (
                <Box p={3}>
                  <Alert severity="info">
                    No transactions found for this month. Add a new transaction to start tracking your finances.
                  </Alert>
                </Box>
              )}
              {recentTransactions.length > 0 && (
                <Box p={2} display="flex" justifyContent="center">
                  <Button
                    endIcon={<ArrowForwardIcon />}
                    onClick={() => navigate('/transactions')}
                  >
                    View All Transactions
                  </Button>
                </Box>
              )}
            </Paper>
          </Box>
        )}
      </Box>
    </Box>
  );
}

export default Dashboard;