package com.gp.playground.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.common.successOrNull
import com.gp.domain.GetNameUseCase
import com.gp.domain.GetSpaceCompanyUseCase
import com.gp.domain.GetSpaceLaunchesUseCase
import com.gp.logger.log
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
    data class Dashboard(val name: String, val stats: String) : UiState()
}

interface UiEffect

interface UiEvent

class MainViewModel(
    private val getNameUseCase: GetNameUseCase,
    private val getSpaceLaunchesUseCase: GetSpaceLaunchesUseCase,
    private val getSpaceCompanyUseCase: GetSpaceCompanyUseCase,
) : ViewModel() {
    private val _effect: Channel<UiEffect> = Channel() // Do not keep previous values.
    val effect = _effect.receiveAsFlow()

    init {
        log { "init | this=$this" }
    }

    val state: StateFlow<UiState> = combine(
        getSpaceCompanyUseCase(),
        getSpaceLaunchesUseCase(),
        getName(),
    ) { companyResult, launchesResult, name ->
        log { "companyResult=$companyResult, launchesResult=$launchesResult" }

        val company = companyResult.successOrNull()
        val launches = launchesResult.successOrNull()

        val state = UiState.Dashboard(
            name = name,
            """
                companyName: ${company?.companyName}
                employees: ${company?.employees}
                
                launches: ${launches?.size}
            """.trimIndent()
        )

        log { "state=$state" }
        state
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
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
