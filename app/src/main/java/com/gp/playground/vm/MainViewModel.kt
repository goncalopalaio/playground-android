package com.gp.playground.vm

import androidx.lifecycle.ViewModel
import com.gp.domain.GetNameUseCase
import com.gp.logger.log

class MainViewModel(private val getNameUseCase: GetNameUseCase) : ViewModel() {

    init {
        log { "init | this=$this" }
    }

    override fun onCleared() {
        super.onCleared()
        log { "onCleared | this=$this" }
    }

    fun name() = getNameUseCase()
}
