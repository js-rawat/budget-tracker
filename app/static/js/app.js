// Budget Tracker Application

function app() {
    return {
        // Authentication state
        isAuthenticated: false,
        isLoading: false,
        token: null,
        user: null,
        
        // Navigation
        currentPage: 'dashboard',
        showRegisterForm: false,
        
        // Forms
        loginForm: {
            username: '',
            password: ''
        },
        registerForm: {
            username: '',
            password: '',
            default_currency: 'ZAR'
        },
        
        // Data
        categories: [],
        subcategories: [],
        budgets: [],
        transactions: [],
        currencyRates: [],
        
        // Selected items
        selectedCategory: null,
        categorySubcategories: [],
        filteredCategories: [],
        
        // Form states
        editingCategory: false,
        editingSubcategory: false,
        editingBudget: false,
        editingTransaction: false,
        editingCurrencyRate: false,
        
        // Form data
        categoryForm: {
            name: '',
            type: 'expense'
        },
        subcategoryForm: {
            name: '',
            category_id: null
        },
        budgetForm: {
            category_id: '',
            subcategory_id: '',
            amount: 0,
            currency: 'ZAR',
            start_date: '',
            end_date: '',
            period_type: 'monthly'
        },
        transactionForm: {
            category_id: '',
            subcategory_id: '',
            amount: 0,
            currency: 'ZAR',
            transaction_date: '',
            description: '',
            type: 'expense'
        },
        currencyRateForm: {
            from_currency: 'ZAR',
            to_currency: 'INR',
            rate: 0,
            effective_date: ''
        },
        
        // Filters
        transactionFilters: {
            type: '',
            category_id: '',
            start_date: '',
            end_date: ''
        },
        reportFilters: {
            month: '',
            currency: 'ZAR'
        },
        trendFilters: {
            start_date: '',
            end_date: '',
            category_id: '',
            subcategory_id: '',
            currency: 'ZAR'
        },
        
        // User preferences
        userPreferences: {
            default_currency: 'ZAR'
        },
        
        // Dashboard data
        dashboardData: {
            totalBudget: 0,
            totalSpent: 0,
            percentageSpent: 0,
            recentTransactions: []
        },
        
        // Initialize the application
        init() {
            // Set default dates for filters
            const today = new Date();
            const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
            const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);
            
            this.transactionFilters.start_date = this.formatDateForInput(firstDayOfMonth);
            this.transactionFilters.end_date = this.formatDateForInput(lastDayOfMonth);
            
            this.reportFilters.month = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;
            
            this.trendFilters.start_date = this.formatDateForInput(new Date(today.getFullYear(), today.getMonth() - 5, 1));
            this.trendFilters.end_date = this.formatDateForInput(lastDayOfMonth);
            
            // Check for stored token
            const token = localStorage.getItem('token');
            if (token) {
                this.token = token;
                this.isAuthenticated = true;
                this.loadUserData();
            }
        },
        
        // Authentication methods
        async login() {
            this.isLoading = true;
            try {
                const formData = new FormData();
                formData.append('username', this.loginForm.username);
                formData.append('password', this.loginForm.password);
                
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    body: formData
                });
                
                if (!response.ok) {
                    throw new Error('Login failed');
                }
                
                const data = await response.json();
                this.token = data.access_token;
                localStorage.setItem('token', this.token);
                this.isAuthenticated = true;
                
                // Reset form
                this.loginForm.username = '';
                this.loginForm.password = '';
                
                // Load user data
                await this.loadUserData();
            } catch (error) {
                console.error('Login error:', error);
                alert('Login failed. Please check your credentials.');
            } finally {
                this.isLoading = false;
            }
        },
        
        async register() {
            this.isLoading = true;
            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(this.registerForm)
                });
                
                if (!response.ok) {
                    throw new Error('Registration failed');
                }
                
                // Reset form and show login
                this.registerForm.username = '';
                this.registerForm.password = '';
                this.registerForm.default_currency = 'ZAR';
                this.showRegisterForm = false;
                
                alert('Registration successful! Please login.');
            } catch (error) {
                console.error('Registration error:', error);
                alert('Registration failed. Please try again.');
            } finally {
                this.isLoading = false;
            }
        },
        
        logout() {
            localStorage.removeItem('token');
            this.token = null;
            this.isAuthenticated = false;
            this.user = null;
            this.currentPage = 'dashboard';
        },
        
        // Data loading methods
        async loadUserData() {
            try {
                // Load user info
                const userResponse = await this.fetchWithAuth('/api/auth/me');
                this.user = await userResponse.json();
                
                // Load user preferences
                const preferencesResponse = await this.fetchWithAuth('/api/settings/user/preferences');
                this.userPreferences = await preferencesResponse.json();
                
                // Load categories
                await this.loadCategories();
                
                // Load dashboard data
                await this.loadDashboardData();
                
                // Set default currency from user preferences
                this.transactionForm.currency = this.userPreferences.default_currency;
                this.budgetForm.currency = this.userPreferences.default_currency;
                this.reportFilters.currency = this.userPreferences.default_currency;
                this.trendFilters.currency = this.userPreferences.default_currency;
            } catch (error) {
                console.error('Error loading user data:', error);
                if (error.message === 'Unauthorized') {
                    this.logout();
                }
            }
        },
        
        async loadCategories() {
            try {
                const response = await this.fetchWithAuth('/api/categories');
                this.categories = await response.json();
                this.filterCategoriesByType();
            } catch (error) {
                console.error('Error loading categories:', error);
            }
        },
        
        async loadSubcategories(categoryId) {
            if (!categoryId) return;
            
            try {
                const response = await this.fetchWithAuth(`/api/categories/${categoryId}/subcategories`);
                this.categorySubcategories = await response.json();
                
                // If viewing subcategories in modal
                if (this.selectedCategory && this.selectedCategory.id === categoryId) {
                    this.subcategories = this.categorySubcategories;
                    document.getElementById('subcategoriesModal').showModal();
                }
            } catch (error) {
                console.error('Error loading subcategories:', error);
            }
        },
        
        async loadBudgets() {
            try {
                const response = await this.fetchWithAuth('/api/budgets');
                this.budgets = await response.json();
            } catch (error) {
                console.error('Error loading budgets:', error);
            }
        },
        
        async loadTransactions() {
            try {
                let url = '/api/transactions?';
                
                if (this.transactionFilters.type) {
                    url += `transaction_type=${this.transactionFilters.type}&`;
                }
                
                if (this.transactionFilters.category_id) {
                    url += `category_id=${this.transactionFilters.category_id}&`;
                }
                
                if (this.transactionFilters.start_date) {
                    url += `start_date=${this.transactionFilters.start_date}T00:00:00&`;
                }
                
                if (this.transactionFilters.end_date) {
                    url += `end_date=${this.transactionFilters.end_date}T23:59:59&`;
                }
                
                const response = await this.fetchWithAuth(url);
                this.transactions = await response.json();
            } catch (error) {
                console.error('Error loading transactions:', error);
            }
        },
        
        async loadCurrencyRates() {
            try {
                const response = await this.fetchWithAuth('/api/settings/currencies/rates');
                this.currencyRates = await response.json();
            } catch (error) {
                console.error('Error loading currency rates:', error);
            }
        },
        
        async loadDashboardData() {
            try {
                // Load recent transactions
                const transactionsResponse = await this.fetchWithAuth('/api/transactions?limit=5');
                this.dashboardData.recentTransactions = await transactionsResponse.json();
                
                // Load budget summary
                const today = new Date();
                const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
                const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);
                
                const budgetSummaryResponse = await this.fetchWithAuth(
                    `/api/budgets/summary?start_date=${this.formatDateForInput(firstDayOfMonth)}T00:00:00&end_date=${this.formatDateForInput(lastDayOfMonth)}T23:59:59&currency=${this.userPreferences.default_currency}`
                );
                
                const budgetSummary = await budgetSummaryResponse.json();
                this.dashboardData.totalBudget = budgetSummary.total_budget;
                this.dashboardData.totalSpent = budgetSummary.total_actual;
                this.dashboardData.percentageSpent = budgetSummary.overall_percentage;
                
                // Initialize charts
                this.initDashboardCharts();
            } catch (error) {
                console.error('Error loading dashboard data:', error);
            }
        },
        
        // Helper methods
        fetchWithAuth(url, options = {}) {
            if (!this.token) {
                return Promise.reject(new Error('Unauthorized'));
            }
            
            const headers = {
                'Authorization': `Bearer ${this.token}`,
                ...options.headers
            };
            
            return fetch(url, {
                ...options,
                headers
            }).then(response => {
                if (response.status === 401) {
                    throw new Error('Unauthorized');
                }
                return response;
            });
        },
        
        formatDate(dateString) {
            if (!dateString) return '';
            const date = new Date(dateString);
            return date.toLocaleDateString();
        },
        
        formatDateForInput(date) {
            return date.toISOString().split('T')[0];
        },
        
        formatCurrency(amount, currency = this.userPreferences.default_currency) {
            return `${currency} ${parseFloat(amount).toFixed(2)}`;
        },
        
        formatDateRange(startDate, endDate) {
            return `${this.formatDate(startDate)} - ${this.formatDate(endDate)}`;
        },
        
        getCategoryName(categoryId) {
            const category = this.categories.find(c => c.id === categoryId);
            return category ? category.name : 'Unknown';
        },
        
        getSubcategoryName(subcategoryId) {
            for (const category of this.categories) {
                const subcategory = this.categorySubcategories.find(s => s.id === subcategoryId);
                if (subcategory) {
                    return subcategory.name;
                }
            }
            return 'Unknown';
        },
        
        filterCategoriesByType() {
            this.filteredCategories = this.categories.filter(c => c.type === this.transactionForm.type);
        },
        
        // UI methods
        initDashboardCharts() {
            // Implementation will be in charts.js
        },
        
        // Form handling methods
        resetCategoryForm() {
            this.categoryForm = {
                name: '',
                type: 'expense'
            };
            this.editingCategory = false;
        },
        
        resetSubcategoryForm() {
            this.subcategoryForm = {
                name: '',
                category_id: this.selectedCategory ? this.selectedCategory.id : null
            };
            this.editingSubcategory = false;
        },
        
        resetBudgetForm() {
            const today = new Date();
            const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
            const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);
            
            this.budgetForm = {
                category_id: '',
                subcategory_id: '',
                amount: 0,
                currency: this.userPreferences.default_currency,
                start_date: this.formatDateForInput(firstDayOfMonth),
                end_date: this.formatDateForInput(lastDayOfMonth),
                period_type: 'monthly'
            };
            this.editingBudget = false;
        },
        
        resetTransactionForm() {
            this.transactionForm = {
                category_id: '',
                subcategory_id: '',
                amount: 0,
                currency: this.userPreferences.default_currency,
                transaction_date: this.formatDateForInput(new Date()),
                description: '',
                type: 'expense'
            };
            this.editingTransaction = false;
            this.filterCategoriesByType();
        },
        
        resetCurrencyRateForm() {
            this.currencyRateForm = {
                from_currency: 'ZAR',
                to_currency: 'INR',
                rate: 0,
                effective_date: this.formatDateForInput(new Date())
            };
            this.editingCurrencyRate = false;
        }
    };
}