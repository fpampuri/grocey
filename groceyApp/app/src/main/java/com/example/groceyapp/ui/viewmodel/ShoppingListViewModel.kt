package com.example.groceyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceyapp.data.model.*
import com.example.groceyapp.data.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Shopping List ViewModel
 * Manages shopping lists and their items
 */
class ShoppingListViewModel : ViewModel() {
    
    private val repository = ShoppingListRepository()
    
    // Shopping lists state
    private val _lists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val lists: StateFlow<List<ShoppingList>> = _lists.asStateFlow()
    
    private val _selectedList = MutableStateFlow<ShoppingList?>(null)
    val selectedList: StateFlow<ShoppingList?> = _selectedList.asStateFlow()
    
    // List items state
    private val _listItems = MutableStateFlow<List<ListItem>>(emptyList())
    val listItems: StateFlow<List<ListItem>> = _listItems.asStateFlow()
    
    // Loading & error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Load all shopping lists
     */
    fun loadShoppingLists() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.getAllShoppingLists()
            
            when (result) {
                is ApiResult.Success -> {
                    _lists.value = result.data
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
     * Load a specific shopping list
     */
    fun loadShoppingList(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = repository.getShoppingList(id)
            
            when (result) {
                is ApiResult.Success -> {
                    _selectedList.value = result.data
                    loadListItems(id)
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
     * Load items for a specific list
     */
    fun loadListItems(listId: Int) {
        viewModelScope.launch {
            val result = repository.getAllListItems(listId)
            
            when (result) {
                is ApiResult.Success -> {
                    _listItems.value = result.data
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    
    /**
     * Create a new shopping list
     */
    fun createShoppingList(
        name: String,
        description: String? = null,
        recurring: Boolean = false,
        metadata: Map<String, Any>? = null,
        onSuccess: (ShoppingList) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val listCreate = ShoppingListCreate(
                name = name,
                description = description,
                recurring = recurring,
                metadata = metadata
            )
            
            val result = repository.createShoppingList(listCreate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadShoppingLists()  // Refresh list
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
     * Update an existing shopping list
     */
    fun updateShoppingList(
        id: Int,
        name: String? = null,
        description: String? = null,
        recurring: Boolean? = null,
        metadata: Map<String, Any>? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val listUpdate = ShoppingListUpdate(
                name = name,
                description = description,
                recurring = recurring,
                metadata = metadata
            )
            
            val result = repository.updateShoppingList(id, listUpdate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadShoppingLists()
                    if (_selectedList.value?.id == id) {
                        _selectedList.value = result.data
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
     * Delete a shopping list
     */
    fun deleteShoppingList(id: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.deleteShoppingList(id)
            
            when (result) {
                is ApiResult.Success -> {
                    loadShoppingLists()
                    if (_selectedList.value?.id == id) {
                        _selectedList.value = null
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
     * Add item to shopping list
     */
    fun addItemToList(
        listId: Int,
        productId: Int,
        quantity: Double = 1.0,
        unit: String = "kg",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _errorMessage.value = null
            
            val itemCreate = ListItemCreate(
                product = ProductReference(productId),
                quantity = quantity,
                unit = unit
            )
            
            val result = repository.createListItem(listId, itemCreate)
            
            when (result) {
                is ApiResult.Success -> {
                    loadListItems(listId)
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
     * Update list item quantity
     */
    fun updateItemQuantity(
        listId: Int,
        itemId: Int,
        quantity: Double,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.updateListItem(
                listId,
                itemId,
                ListItemUpdate(quantity = quantity)
            )
            
            when (result) {
                is ApiResult.Success -> {
                    loadListItems(listId)
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
     * Mark item as purchased
     */
    fun markItemAsPurchased(
        listId: Int,
        itemId: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.markItemAsPurchased(listId, itemId)
            
            when (result) {
                is ApiResult.Success -> {
                    loadListItems(listId)
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
     * Remove item from list
     */
    fun removeItemFromList(
        listId: Int,
        itemId: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.deleteListItem(listId, itemId)
            
            when (result) {
                is ApiResult.Success -> {
                    loadListItems(listId)
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
     * Share list with users
     */
    fun shareList(
        listId: Int,
        emails: List<String>,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _errorMessage.value = null
            
            val result = repository.shareShoppingList(listId, emails)
            
            when (result) {
                is ApiResult.Success -> {
                    loadShoppingList(listId)
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
     * Clear selected list
     */
    fun clearSelectedList() {
        _selectedList.value = null
        _listItems.value = emptyList()
    }
}
