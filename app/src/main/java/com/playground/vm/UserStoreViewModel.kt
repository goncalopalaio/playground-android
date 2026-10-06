package com.playground.vm

import com.playground.api.remote.UsersApi
import com.playground.data.remote.User
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Job
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
    val user: User? = null,
    val isUserLoading: Boolean = false,
    val userError: String? = null,
    val isBusy: Boolean = false,
    val error: String? = null,
)

class UserStoreViewModel(private val stores: KeyValueStores, val userId: String, private val usersApi: UsersApi) : ViewModel() {
    private val _state = MutableStateFlow(UserStoreUiState())
    val state = _state.asStateFlow()

    private var userRequest: Job? = null

    init { refresh() }

    fun updateKey(key: String) { _state.update { it.copy(key = key, error = null) } }
    fun updateValue(value: String) { _state.update { it.copy(value = value, error = null) } }
    fun editEntry(entry: KeyValueItem) {
        _state.update { it.copy(key = entry.key, value = entry.value, error = null) }
    }

    fun refresh() = loadEntries(save = false)
    fun save() = loadEntries(save = true)

    private fun loadUser(id: String?) {
        userRequest?.cancel()
        _state.update { it.copy(user = null, userError = null, isUserLoading = id != null) }
        if (id == null) return
        userRequest = viewModelScope.launch {
            try {
                require(id.isNotBlank())
                val user = usersApi.user(id)
                _state.update { it.copy(user = user) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _state.update { it.copy(userError = "Could not load user for id '$id'. Check the value and try again.") }
            } finally {
                if (currentCoroutineContext().isActive) {
                    _state.update { it.copy(isUserLoading = false) }
                }
            }
        }
    }

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
                loadUser(entries.firstOrNull { it.key == "id" }?.value)
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
