package com.playground.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playground.device.KeyValueItem
import com.playground.device.KeyValueStores
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserStoreUiState(
    val key: String = "",
    val value: String = "",
    val entries: List<KeyValueItem> = emptyList(),
    val isBusy: Boolean = false,
    val error: String? = null,
)

class UserStoreViewModel(private val stores: KeyValueStores, val userId: String) : ViewModel() {
    private val _state = MutableStateFlow(UserStoreUiState())
    val state = _state.asStateFlow()

    init { refresh() }

    fun updateKey(key: String) { _state.update { it.copy(key = key, error = null) } }
    fun updateValue(value: String) { _state.update { it.copy(value = value, error = null) } }
    fun editEntry(entry: KeyValueItem) {
        _state.update { it.copy(key = entry.key, value = entry.value, error = null) }
    }

    fun refresh() = loadEntries(save = false)
    fun save() = loadEntries(save = true)

    private fun loadEntries(save: Boolean) {
        val draft = _state.value
        if (draft.isBusy || (save && draft.key.isBlank())) return
        _state.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            try {
                val store = stores.forUser(userId)
                if (save) store.put(draft.key, draft.value)
                val entries = store.getEntries()
                _state.update { it.copy(entries = entries) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _state.update { it.copy(error = "Could not ${if (save) "save" else "load"} values. Please try again.") }
            } finally {
                _state.update { it.copy(isBusy = false) }
            }
        }
    }
}
