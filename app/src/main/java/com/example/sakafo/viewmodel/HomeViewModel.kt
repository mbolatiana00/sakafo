package com.example.sakafo.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sakafo.data.Api.model.MenuItem
import com.example.sakafo.data.Api.model.Restaurant
import com.example.sakafo.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── UI States ─────────────────────────────────────────────────────────────────

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val restaurants: List<Restaurant>,
        val menuItems: List<MenuItem>,
        val categories: List<String>,
        val selectedCategory: String,
        val searchQuery: String,
        val isLoadingMore: Boolean = false,
        val hasMorePages: Boolean = true,
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class HomeViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private var currentPage      = 1
    private var selectedCategory = "Tout"
    private var searchQuery      = ""

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val categories  = repository.getCategories()
                val restaurants = repository.getRestaurants(page = 1, limit = 20)
                val menuItems   = repository.getAllMenuItems(page = 1)

                _uiState.value = HomeUiState.Success(
                    restaurants      = restaurants,
                    menuItems        = menuItems,
                    categories       = categories,
                    selectedCategory = "Tout",
                    searchQuery      = "",
                    hasMorePages     = menuItems.size >= 20,
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    // Filtre par catégorie
    fun selectCategory(category: String) {
        selectedCategory = category
        currentPage      = 1
        viewModelScope.launch {
            val current = _uiState.value as? HomeUiState.Success ?: return@launch
            _uiState.value = HomeUiState.Loading
            try {
                val menuItems = repository.getAllMenuItems(
                    page     = 1,
                    category = if (category == "Tout") null else category,
                    search   = searchQuery.ifBlank { null },
                )
                _uiState.value = current.copy(
                    menuItems        = menuItems,
                    selectedCategory = category,
                    isLoadingMore    = false,
                    hasMorePages     = menuItems.size >= 20,
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    // Recherche
    fun search(query: String) {
        searchQuery = query
        currentPage = 1
        viewModelScope.launch {
            val current = _uiState.value as? HomeUiState.Success ?: return@launch
            try {
                val menuItems = repository.getAllMenuItems(
                    page     = 1,
                    category = if (selectedCategory == "Tout") null else selectedCategory,
                    search   = query.ifBlank { null },
                )
                _uiState.value = current.copy(
                    menuItems    = menuItems,
                    searchQuery  = query,
                    hasMorePages = menuItems.size >= 20,
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Erreur filtre")
            }
        }
    }

    // Pagination — charge la page suivante
    fun loadMoreMenuItems() {
        val current = _uiState.value as? HomeUiState.Success ?: return
        if (current.isLoadingMore || !current.hasMorePages) return

        currentPage++
        viewModelScope.launch {
            _uiState.value = current.copy(isLoadingMore = true)
            try {
                val more = repository.getAllMenuItems(
                    page     = currentPage,
                    category = if (selectedCategory == "Tout") null else selectedCategory,
                    search   = searchQuery.ifBlank { null },
                )
                _uiState.value = current.copy(
                    menuItems     = current.menuItems + more,
                    isLoadingMore = false,
                    hasMorePages  = more.size >= 20,
                )
            } catch (e: Exception) {
                currentPage--  // rollback
                _uiState.value = current.copy(isLoadingMore = false)
            }
        }
    }

    fun retry() = loadInitialData()
}

// ── Factory ───────────────────────────────────────────────────────────────────

class HomeViewModelFactory(
    private val repository: RestaurantRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(repository) as T
    }
}