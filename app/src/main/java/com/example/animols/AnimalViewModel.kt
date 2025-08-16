package com.example.animols

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AnimalViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals

    private val _selectedAnimal = MutableStateFlow<Animal?>(null)
    val selectedAnimal: StateFlow<Animal?> = _selectedAnimal

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // NEW: explicitly control dropdown visibility
    private val _showSuggestions = MutableStateFlow(false)
    val showSuggestions: StateFlow<Boolean> = _showSuggestions

    init {
        _query
            .debounce(400)
            .distinctUntilChanged()
            .onEach { q ->
                // Only produce suggestions in search mode AND when dropdown should be open
                if (_selectedAnimal.value != null || !_showSuggestions.value) {
                    return@onEach
                }
                if (q.isBlank()) {
                    _animals.value = emptyList()
                    _error.value = null
                } else {
                    fetchSuggestions(q)
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
        _showSuggestions.value = newQuery.isNotBlank() && _selectedAnimal.value == null
    }

    fun onSuggestionClicked(name: String) {
        _query.value = name
        _showSuggestions.value = false        // CLOSE DROPDOWN
        // keep _animals as-is (optional) or clear:
        // _animals.value = emptyList()
    }

    fun searchAnimal(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            _error.value = "Please enter an animal name"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _selectedAnimal.value = null
            _showSuggestions.value = false     // CLOSE DROPDOWN
            try {
                val response = RetrofitInstance.api.getAnimal(trimmed)
                _animals.value = response

                val match = response.firstOrNull { it.name.equals(trimmed, ignoreCase = true) }
                    ?: response.firstOrNull()

                if (match != null) {
                    _selectedAnimal.value = match
                    _error.value = null
                } else {
                    _selectedAnimal.value = null
                    _error.value = "No matches for \"$trimmed\""
                }
            } catch (e: Exception) {
                _animals.value = emptyList()
                _selectedAnimal.value = null
                _error.value = "Network error: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSearch() {
        _selectedAnimal.value = null
        _query.value = ""
        _animals.value = emptyList()
        _error.value = null
        _showSuggestions.value = false        // CLOSE DROPDOWN
    }

    private fun fetchSuggestions(q: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getAnimal(q.trim())
                _animals.value = response
                _error.value = if (response.isEmpty()) "No matches for \"$q\"" else null
            } catch (e: Exception) {
                _animals.value = emptyList()
                _error.value = "Network error: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}
