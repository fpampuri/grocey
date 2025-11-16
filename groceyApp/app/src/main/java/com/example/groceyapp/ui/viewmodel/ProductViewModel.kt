package com.example.groceyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceyapp.data.model.*
import com.example.groceyapp.data.repository.CategoryRepository
import com.example.groceyapp.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Product ViewModel
 * Manages products and categories
 */
class ProductViewModel : ViewModel() {
    
    private val productRepository = ProductRepository()
    private val categoryRepository = CategoryRepository()
    
    // Products state
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    // Categories state
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Loading & error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Load all products
     */
    fun loadProducts(searchQuery: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = productRepository.getAllProducts(searchQuery)
            
            when (result) {
                is ApiResult.Success -> {
                    _products.value = result.data
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Load all categories
     */
    fun loadCategories(searchQuery: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = categoryRepository.getAllCategories(searchQuery)
            
            when (result) {
                is ApiResult.Success -> {
                    _categories.value = result.data
                    // Ensure the protected default category exists
                    ensureMiscCategoryExists(result.data)
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Smart search for categories and products
     * Shows categories that match by name OR contain products matching the query
     * For categories matched by name, shows ALL their products
     */
    fun searchCategoriesAndProducts(searchQuery: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Store the search query
            _searchQuery.value = searchQuery ?: ""
            
            if (searchQuery.isNullOrBlank()) {
                // No search query - load all categories and products
                loadCategories(null)
                loadProducts(null)
                _isLoading.value = false
                return@launch
            }
            
            // Search categories by name
            val categoriesResult = categoryRepository.getAllCategories(searchQuery)
            
            // Search products by name
            val productsResult = productRepository.getAllProducts(searchQuery)
            
            // Get all products (to show complete categories when matched by name)
            val allProductsResult = productRepository.getAllProducts(null)
            
            // Get all categories (without filter) to show categories with matching products
            val allCategoriesResult = categoryRepository.getAllCategories(null)
            
            when {
                categoriesResult is ApiResult.Success && 
                productsResult is ApiResult.Success && 
                allProductsResult is ApiResult.Success &&
                allCategoriesResult is ApiResult.Success -> {
                    
                    val matchingProducts = productsResult.data
                    val matchingCategories = categoriesResult.data
                    val allProducts = allProductsResult.data
                    val allCategories = allCategoriesResult.data
                    
                    // Get category IDs from matching products
                    val categoryIdsFromProducts = matchingProducts
                        .mapNotNull { it.category?.id }
                        .toSet()
                    
                    // Get category IDs from matching categories
                    val categoryIdsFromCategories = matchingCategories
                        .mapNotNull { it.id }
                        .toSet()
                    
                    // Combine both sets
                    val allMatchingCategoryIds = categoryIdsFromProducts + categoryIdsFromCategories
                    
                    // Filter all categories to only show matching ones
                    val filteredCategories = allCategories.filter { category ->
                        category.id in allMatchingCategoryIds
                    }
                    
                    // For products: show ALL products from matching categories
                    // This ensures categories matched by name show all their products
                    val filteredProducts = allProducts.filter { product ->
                        product.category?.id in allMatchingCategoryIds
                    }
                    
                    _categories.value = filteredCategories
                    _products.value = filteredProducts
                }
                categoriesResult is ApiResult.Error -> {
                    _errorMessage.value = categoriesResult.message
                }
                productsResult is ApiResult.Error -> {
                    _errorMessage.value = productsResult.message
                }
                allProductsResult is ApiResult.Error -> {
                    _errorMessage.value = allProductsResult.message
                }
                allCategoriesResult is ApiResult.Error -> {
                    _errorMessage.value = allCategoriesResult.message
                }
                else -> {}
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Ensures the default "Miscellaneous" category exists for the current user.
     * If missing, creates it with a protected metadata marker and a generic icon.
     */
    private suspend fun ensureMiscCategoryExists(existing: List<Category>) {
        val hasMisc = existing.any { category ->
            category.name.equals(com.example.groceyapp.Constants.MISCELLANEOUS_CATEGORY_NAME, ignoreCase = true) ||
                (category.metadata?.get(com.example.groceyapp.Constants.MISC_CATEGORY_META_KEY) as? String)
                    ?.equals(com.example.groceyapp.Constants.MISC_CATEGORY_META_VALUE, ignoreCase = true) == true
        }
        if (!hasMisc) {
            val meta = mapOf(
                "icon" to com.example.groceyapp.Constants.MISC_CATEGORY_ICON_NAME,
                com.example.groceyapp.Constants.MISC_CATEGORY_META_KEY to com.example.groceyapp.Constants.MISC_CATEGORY_META_VALUE
            )
            when (val created = categoryRepository.createCategory(CategoryCreate(
                name = com.example.groceyapp.Constants.MISCELLANEOUS_CATEGORY_NAME,
                metadata = meta
            ))) {
                is ApiResult.Success -> {
                    // Refresh categories after creating the default
                    when (val refreshed = categoryRepository.getAllCategories()) {
                        is ApiResult.Success -> _categories.value = refreshed.data
                        is ApiResult.Error -> _errorMessage.value = refreshed.message
                        else -> {}
                    }
                }
                is ApiResult.Error -> {
                    // Non-fatal: keep working even if creation failed
                    _errorMessage.value = created.message
                }
                else -> {}
            }
        }
    }
    
    /**
     * Create a new product
     */
    fun createProduct(
        name: String,
        categoryId: Int? = null,
        metadata: Map<String, Any>? = null,
        onSuccess: (Product) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val productCreate = ProductCreate(
                name = name,
                category = categoryId?.let { CategoryReference(it) },
                metadata = metadata
            )
            
            val result = productRepository.createProduct(productCreate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadProducts()
                    onSuccess(result.data)
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Update an existing product
     */
    fun updateProduct(
        id: Int,
        name: String? = null,
        categoryId: Int? = null,
        metadata: Map<String, Any>? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val productUpdate = ProductUpdate(
                name = name,
                category = categoryId?.let { CategoryReference(it) },
                metadata = metadata
            )
            
            val result = productRepository.updateProduct(id, productUpdate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadProducts()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Delete a product
     */
    fun deleteProduct(id: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = productRepository.deleteProduct(id)
            
            when (result) {
                is ApiResult.Success -> {
                    loadProducts()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Create a new category
     */
    fun createCategory(
        name: String,
        metadata: Map<String, Any>? = null,
        onSuccess: (Category) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val categoryCreate = CategoryCreate(
                name = name,
                metadata = metadata
            )
            
            val result = categoryRepository.createCategory(categoryCreate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadCategories()
                    onSuccess(result.data)
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Update an existing category
     */
    fun updateCategory(
        id: Int,
        name: String? = null,
        metadata: Map<String, Any>? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val categoryUpdate = CategoryUpdate(
                name = name,
                metadata = metadata
            )
            
            val result = categoryRepository.updateCategory(id, categoryUpdate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadCategories()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Delete a category
     */
    fun deleteCategory(id: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = categoryRepository.deleteCategory(id)
            
            when (result) {
                is ApiResult.Success -> {
                    loadCategories()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
