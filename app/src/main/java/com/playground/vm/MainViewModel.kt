package com.playground.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playground.data.remote.Photo
import com.playground.domain.GetNameUseCase
import com.playground.domain.GetPhotosUseCase
import com.playground.logger.log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

sealed class UiState {
    data object Loading : UiState()
    data class Dashboard(val name: String, val photos: List<Photo>) : UiState()
    data class Error(val message: String) : UiState()
}

interface UiEffect

interface UiEvent

class MainViewModel(
    private val getNameUseCase: GetNameUseCase,
    private val getPhotosUseCase: GetPhotosUseCase,
) : ViewModel() {
    private val _effect: Channel<UiEffect> = Channel() // Do not keep previous values.
    val effect = _effect.receiveAsFlow()

    init {
        log { "init | this=$this" }
    }

    val state: StateFlow<UiState> = combine(
        getPhotosUseCase(),
        getName(),
    ) { photosResult, name ->
        photosResult.fold(
            { photos -> UiState.Dashboard(name, photos) },
            { UiState.Error("Unable to load photos. Please try again later.") },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    fun event(event: UiEvent) {
    }

    override fun onCleared() {
        super.onCleared()
        log { "onCleared | this=$this" }
    }

    private fun getName(): Flow<String> = flow {
        emit(getNameUseCase())
    }
}
