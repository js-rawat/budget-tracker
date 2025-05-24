// Budget Tracker Charts

// Store chart instances to destroy and recreate them
const chartInstances = {};

// Dashboard charts
async function initDashboardCharts() {
    try {
        // Get monthly overview data
        const today = new Date();
        const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth() - 5, 1);
        const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);
        
        const response = await this.fetchWithAuth(
            `/api/reports/trends?start_date=${this.formatDateForInput(firstDayOfMonth)}T00:00:00&end_date=${this.formatDateForInput(lastDayOfMonth)}T23:59:59&currency=${this.userPreferences.default_currency}`
        );
        
        const trendsData = await response.json();
        
        // Create monthly overview chart
        const monthlyOverviewCtx = document.getElementById('monthlyOverviewChart');
        if (monthlyOverviewCtx) {
            if (chartInstances.monthlyOverview) {
                chartInstances.monthlyOverview.destroy();
            }
            
            chartInstances.monthlyOverview = new Chart(monthlyOverviewCtx, {
                type: 'bar',
                data: {
                    labels: trendsData.labels,
                    datasets: trendsData.datasets
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        title: {
                            display: true,
                            text: 'Budget vs Actual (Last 6 Months)'
                        },
                        legend: {
                            position: 'top'
                        }
                    }
                }
            });
        }
    } catch (error) {
        console.error('Error initializing dashboard charts:', error);
    }
}

// Monthly report charts
async function loadMonthlyReport() {
    try {
        this.isLoading = true;
        
        if (!this.reportFilters.month) {
            const today = new Date();
            this.reportFilters.month = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;
        }
        
        const [year, month] = this.reportFilters.month.split('-');
        
        const response = await this.fetchWithAuth(
            `/api/reports/monthly/${year}/${month}?currency=${this.reportFilters.currency}`
        );
        
        const reportData = await response.json();
        
        // Create income pie chart
        createPieChart('incomePieChart', 'Income by Category', reportData.income_by_category);
        
        // Create expense pie chart
        createPieChart('expensePieChart', 'Expense by Category', reportData.expense_by_category);
        
        // Create budget vs actual chart
        createBarChart('budgetVsActualChart', 'Budget vs Actual', reportData.budget_vs_actual);
        
        // Create daily transactions chart
        createLineChart('dailyTransactionsChart', 'Daily Transactions', reportData.daily_transactions);
        
        // Create net income/expense chart
        createBarChart('netIncomeExpenseChart', 'Net Income/Expense', reportData.net_income_expense);
    } catch (error) {
        console.error('Error loading monthly report:', error);
    } finally {
        this.isLoading = false;
    }
}

// Budget trends chart
async function loadBudgetTrends() {
    try {
        this.isLoading = true;
        
        if (!this.trendFilters.start_date || !this.trendFilters.end_date) {
            const today = new Date();
            const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth() - 5, 1);
            const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);
            
            this.trendFilters.start_date = this.formatDateForInput(firstDayOfMonth);
            this.trendFilters.end_date = this.formatDateForInput(lastDayOfMonth);
        }
        
        let url = `/api/reports/trends?start_date=${this.trendFilters.start_date}T00:00:00&end_date=${this.trendFilters.end_date}T23:59:59&currency=${this.trendFilters.currency}`;
        
        if (this.trendFilters.category_id) {
            url += `&category_id=${this.trendFilters.category_id}`;
        }
        
        if (this.trendFilters.subcategory_id) {
            url += `&subcategory_id=${this.trendFilters.subcategory_id}`;
        }
        
        const response = await this.fetchWithAuth(url);
        const trendsData = await response.json();
        
        createLineChart('budgetTrendsChart', 'Budget Trends', trendsData);
    } catch (error) {
        console.error('Error loading budget trends:', error);
    } finally {
        this.isLoading = false;
    }
}

// Helper functions for creating charts
function createPieChart(canvasId, title, data) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;
    
    if (chartInstances[canvasId]) {
        chartInstances[canvasId].destroy();
    }
    
    const labels = data.map(item => item.label);
    const values = data.map(item => item.value);
    
    // Generate colors
    const colors = generateColors(data.length);
    
    chartInstances[canvasId] = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: colors.background,
                borderColor: colors.border,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: title
                },
                legend: {
                    position: 'right'
                }
            }
        }
    });
}

function createBarChart(canvasId, title, data) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;
    
    if (chartInstances[canvasId]) {
        chartInstances[canvasId].destroy();
    }
    
    chartInstances[canvasId] = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.labels,
            datasets: data.datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: title
                },
                legend: {
                    position: 'top'
                }
            }
        }
    });
}

function createLineChart(canvasId, title, data) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;
    
    if (chartInstances[canvasId]) {
        chartInstances[canvasId].destroy();
    }
    
    chartInstances[canvasId] = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: data.datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: title
                },
                legend: {
                    position: 'top'
                }
            }
        }
    });
}

// Generate colors for charts
function generateColors(count) {
    const backgroundColors = [];
    const borderColors = [];
    
    const baseColors = [
        'rgba(255, 99, 132, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(255, 206, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
        'rgba(153, 102, 255, 0.2)',
        'rgba(255, 159, 64, 0.2)'
    ];
    
    const baseBorders = [
        'rgba(255, 99, 132, 1)',
        'rgba(54, 162, 235, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(75, 192, 192, 1)',
        'rgba(153, 102, 255, 1)',
        'rgba(255, 159, 64, 1)'
    ];
    
    for (let i = 0; i < count; i++) {
        backgroundColors.push(baseColors[i % baseColors.length]);
        borderColors.push(baseBorders[i % baseBorders.length]);
    }
    
    return {
        background: backgroundColors,
        border: borderColors
    };
}

// Add methods to app
document.addEventListener('alpine:init', () => {
    Alpine.data('app', () => ({
        ...app(),
        initDashboardCharts,
        loadMonthlyReport,
        loadBudgetTrends
    }));
});