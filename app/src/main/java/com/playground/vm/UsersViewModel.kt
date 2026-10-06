package com.playground.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playground.device.KeyValueStores
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsersUiState(
    val userId: String = "",
    val users: List<String> = emptyList(),
    val isBusy: Boolean = false,
    val error: String? = null,
)

class UsersViewModel(private val stores: KeyValueStores) : ViewModel() {
    private val _state = MutableStateFlow(UsersUiState())
    val state = _state.asStateFlow()

    fun updateUserId(userId: String) {
        _state.update { it.copy(userId = userId, error = null) }
    }

    fun refresh() {
        if (_state.value.isBusy) return
        _state.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            try {
                val users = stores.getUsers()
                _state.update { it.copy(users = users) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _state.update { it.copy(error = "Could not load users. Please try again.") }
            } finally {
                _state.update { it.copy(isBusy = false) }
            }
        }
    }

    fun createUser() {
        val userId = _state.value.userId.trim()
        if (userId.isBlank() || _state.value.isBusy) return
        _state.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            try {
                val created = stores.createUser(userId)
                val users = stores.getUsers()
                _state.update {
                    it.copy(
                        users = users,
                        userId = if (created) "" else it.userId,
                        error = if (created) null else "This user already exists.",
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _state.update { it.copy(error = "Could not create user. Please try again.") }
            } finally {
                _state.update { it.copy(isBusy = false) }
            }
        }
    }
}
