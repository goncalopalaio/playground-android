package com.gp.playground.vm

import androidx.lifecycle.ViewModel
import com.gp.domain.GetNameUseCase
import com.gp.logger.Logger

class MainViewModel(private val logger: Logger, private val getNameUseCase: GetNameUseCase) : ViewModel() {

    init {

    }

    override fun onCleared() {
        super.onCleared()
    }

    fun name() = getNameUseCase()
}
