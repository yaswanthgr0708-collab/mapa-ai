package com.practice.mapa.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.mapa.data.catalog.CatalogRepository
import com.practice.mapa.data.catalog.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CatalogUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isGridLayout: Boolean = true,
    val searchQuery: String = "",
    val activeCategories: Set<String> = setOf("Electronics", "Clothing", "Books", "Home"),
    val minPrice: Float = 0f,
    val maxPrice: Float = 500f,
    val hasMore: Boolean = true
)

@OptIn(FlowPreview::class)
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repo: CatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState

    private val _searchInput = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchInput
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update { it.copy(searchQuery = query) }
                    resetAndLoad()
                }
        }
        loadInitial()
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            val page = repo.getProducts(
                query = state.searchQuery,
                categories = state.activeCategories.toList(),
                minPrice = state.minPrice.toDouble(),
                maxPrice = state.maxPrice.toDouble(),
                offset = 0
            )
            _uiState.update { it.copy(
                products = page,
                isLoading = false,
                hasMore = page.size == CatalogRepository.PAGE_SIZE
            )}
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || !state.hasMore) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val current = _uiState.value
            val page = repo.getProducts(
                query = current.searchQuery,
                categories = current.activeCategories.toList(),
                minPrice = current.minPrice.toDouble(),
                maxPrice = current.maxPrice.toDouble(),
                offset = current.products.size
            )
            _uiState.update { it.copy(
                products = it.products + page,
                isLoading = false,
                hasMore = page.size == CatalogRepository.PAGE_SIZE
            )}
        }
    }

    fun onSearchQuery(query: String) {
        _searchInput.value = query
    }

    fun toggleLayout() {
        _uiState.update { it.copy(isGridLayout = !it.isGridLayout) }
    }

    fun applyFilters(categories: Set<String>, minPrice: Float, maxPrice: Float) {
        _uiState.update { it.copy(
            activeCategories = categories.ifEmpty { setOf("Electronics", "Clothing", "Books", "Home") },
            minPrice = minPrice,
            maxPrice = maxPrice
        )}
        resetAndLoad()
    }

    fun removeCategoryFilter(category: String) {
        val all = setOf("Electronics", "Clothing", "Books", "Home")
        val newCategories = (_uiState.value.activeCategories - category).ifEmpty { all }
        _uiState.update { it.copy(activeCategories = newCategories) }
        resetAndLoad()
    }

    fun clearPriceFilter() {
        _uiState.update { it.copy(minPrice = 0f, maxPrice = 500f) }
        resetAndLoad()
    }

    private fun resetAndLoad() {
        _uiState.update { it.copy(products = emptyList(), hasMore = true) }
        loadInitial()
    }
}
