package com.example.groceyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceyapp.data.model.*
import com.example.groceyapp.data.repository.PantryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

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
    
    private val _pantryItemCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val pantryItemCounts: StateFlow<Map<Int, Int>> = _pantryItemCounts.asStateFlow()
    
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
            
            Log.d("PantryViewModel", "Loading pantries...")
            val result = repository.getAllPantries()
            
            when (result) {
                is ApiResult.Success -> {
                    Log.d("PantryViewModel", "Pantries loaded successfully: ${result.data.size} pantries")
                    result.data.forEach { pantry ->
                        Log.d("PantryViewModel", "Pantry: ${pantry.name}, id: ${pantry.id}, metadata: ${pantry.metadata}")
                    }
                    _pantries.value = result.data
                    // Load item counts for each pantry
                    result.data.forEach { pantry ->
                        pantry.id?.let { id ->
                            viewModelScope.launch {
                                Log.d("PantryViewModel", "Loading items for pantry $id")
                                val itemsResult = repository.getAllPantryItems(id)
                                when (itemsResult) {
                                    is ApiResult.Success -> {
                                        Log.d("PantryViewModel", "Pantry $id has ${itemsResult.data.size} items")
                                        _pantryItemCounts.value = _pantryItemCounts.value.toMutableMap().apply {
                                            this[id] = itemsResult.data.size
                                        }
                                        Log.d("PantryViewModel", "Updated pantryItemCounts: $_pantryItemCounts")
                                    }
                                    is ApiResult.Error -> {
                                        Log.e("PantryViewModel", "Error loading items for pantry $id: ${itemsResult.message}")
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }
                is ApiResult.Error -> {
                    Log.e("PantryViewModel", "Error loading pantries: ${result.message}")
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
            Log.d("PantryViewModel", "loadPantryItems called for pantryId=$pantryId")
            val result = repository.getAllPantryItems(pantryId)
            
            when (result) {
                is ApiResult.Success -> {
                    Log.d("PantryViewModel", "Loaded ${result.data.size} items for pantry $pantryId")
                    result.data.forEach { item ->
                        Log.d("PantryViewModel", "  Item ${item.id}: ${item.product?.name}, quantity=${item.quantity}")
                    }
                    _pantryItems.value = result.data
                }
                is ApiResult.Error -> {
                    Log.e("PantryViewModel", "Error loading pantry items: ${result.message}")
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
            
            Log.d("PantryViewModel", "Creating pantry: name=$name, metadata=$metadata")
            
            val pantryCreate = PantryCreate(
                name = name,
                metadata = metadata
            )
            
            val result = repository.createPantry(pantryCreate)
            
            when (result) {
                is ApiResult.Success -> {
                    Log.d("PantryViewModel", "Pantry created successfully: ${result.data}")
                    loadPantries()
                    onSuccess(result.data)
                }
                is ApiResult.Error -> {
                    Log.e("PantryViewModel", "Error creating pantry: ${result.message}")
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
                    if (_selectedPantry.value?.id == id) {
                        _selectedPantry.value = null
                    }
                    loadPantries() // Reload the list
                    onSuccess() // Call success after reload starts
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
                    // Update item count
                    viewModelScope.launch {
                        val itemsResult = repository.getAllPantryItems(pantryId)
                        when (itemsResult) {
                            is ApiResult.Success -> {
                                _pantryItemCounts.value = _pantryItemCounts.value.toMutableMap().apply {
                                    this[pantryId] = itemsResult.data.size
                                }
                            }
                            else -> {}
                        }
                    }
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
            Log.d("PantryViewModel", "updateItemQuantity called: pantryId=$pantryId, itemId=$itemId, quantity=$quantity")
            
            // Find the current item to get its unit
            val currentItem = _pantryItems.value.find { it.id == itemId }
            val unit = currentItem?.unit ?: "units"
            
            Log.d("PantryViewModel", "Using unit: $unit for item $itemId")
            
            val result = repository.updatePantryItem(
                pantryId,
                itemId,
                PantryItemUpdate(quantity = quantity, unit = unit)
            )
            
            when (result) {
                is ApiResult.Success -> {
                    Log.d("PantryViewModel", "Item quantity updated successfully, reloading items...")
                    loadPantryItems(pantryId)
                    onSuccess()
                }
                is ApiResult.Error -> {
                    Log.e("PantryViewModel", "Error updating item quantity: ${result.message}")
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
                    // Update item count
                    viewModelScope.launch {
                        val itemsResult = repository.getAllPantryItems(pantryId)
                        when (itemsResult) {
                            is ApiResult.Success -> {
                                _pantryItemCounts.value = _pantryItemCounts.value.toMutableMap().apply {
                                    this[pantryId] = itemsResult.data.size
                                }
                            }
                            else -> {}
                        }
                    }
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
