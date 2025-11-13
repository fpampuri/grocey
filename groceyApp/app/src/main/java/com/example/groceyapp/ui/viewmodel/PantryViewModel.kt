package com.example.groceyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceyapp.data.model.*
import com.example.groceyapp.data.repository.PantryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Pantry ViewModel
 * Manages pantries and their items
 */
class PantryViewModel : ViewModel() {
    
    private val repository = PantryRepository()
    
    // Pantries state
    private val _pantries = MutableStateFlow<List<Pantry>>(emptyList())
    val pantries: StateFlow<List<Pantry>> = _pantries.asStateFlow()
    
    private val _selectedPantry = MutableStateFlow<Pantry?>(null)
    val selectedPantry: StateFlow<Pantry?> = _selectedPantry.asStateFlow()
    
    // Pantry items state
    private val _pantryItems = MutableStateFlow<List<PantryItem>>(emptyList())
    val pantryItems: StateFlow<List<PantryItem>> = _pantryItems.asStateFlow()
    
    // Loading & error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Load all pantries
     */
    fun loadPantries() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.getAllPantries()
            
            when (result) {
                is ApiResult.Success -> {
                    _pantries.value = result.data
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
     * Load a specific pantry
     */
    fun loadPantry(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = repository.getPantry(id)
            
            when (result) {
                is ApiResult.Success -> {
                    _selectedPantry.value = result.data
                    loadPantryItems(id)
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
     * Load items for a specific pantry
     */
    fun loadPantryItems(pantryId: Int) {
        viewModelScope.launch {
            val result = repository.getAllPantryItems(pantryId)
            
            when (result) {
                is ApiResult.Success -> {
                    _pantryItems.value = result.data
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    
    /**
     * Create a new pantry
     */
    fun createPantry(
        name: String,
        metadata: Map<String, Any>? = null,
        onSuccess: (Pantry) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val pantryCreate = PantryCreate(
                name = name,
                metadata = metadata
            )
            
            val result = repository.createPantry(pantryCreate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadPantries()
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
     * Update an existing pantry
     */
    fun updatePantry(
        id: Int,
        name: String? = null,
        metadata: Map<String, Any>? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val pantryUpdate = PantryUpdate(
                name = name,
                metadata = metadata
            )
            
            val result = repository.updatePantry(id, pantryUpdate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadPantries()
                    if (_selectedPantry.value?.id == id) {
                        _selectedPantry.value = result.data
                    }
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
     * Delete a pantry
     */
    fun deletePantry(id: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.deletePantry(id)
            
            when (result) {
                is ApiResult.Success -> {
                    loadPantries()
                    if (_selectedPantry.value?.id == id) {
                        _selectedPantry.value = null
                    }
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
     * Add item to pantry
     */
    fun addItemToPantry(
        pantryId: Int,
        productId: Int,
        quantity: Double = 1.0,
        unit: String = "kg",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _errorMessage.value = null
            
            val itemCreate = PantryItemCreate(
                product = ProductReference(productId),
                quantity = quantity,
                unit = unit
            )
            
            val result = repository.createPantryItem(pantryId, itemCreate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadPantryItems(pantryId)
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    
    /**
     * Update pantry item quantity
     */
    fun updateItemQuantity(
        pantryId: Int,
        itemId: Int,
        quantity: Double,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.updatePantryItem(
                pantryId,
                itemId,
                PantryItemUpdate(quantity = quantity)
            )
            
            when (result) {
                is ApiResult.Success -> {
                    loadPantryItems(pantryId)
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    
    /**
     * Remove item from pantry
     */
    fun removeItemFromPantry(
        pantryId: Int,
        itemId: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.deletePantryItem(pantryId, itemId)
            
            when (result) {
                is ApiResult.Success -> {
                    loadPantryItems(pantryId)
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    
    /**
     * Share pantry with users
     */
    fun sharePantry(
        pantryId: Int,
        emails: List<String>,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _errorMessage.value = null
            
            val result = repository.sharePantry(pantryId, emails)
            
            when (result) {
                is ApiResult.Success -> {
                    loadPantry(pantryId)
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Clear selected pantry
     */
    fun clearSelectedPantry() {
        _selectedPantry.value = null
        _pantryItems.value = emptyList()
    }
}
