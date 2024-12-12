package com.gp.playground.injection

import com.gp.device.AndroidDeviceInformation
import com.gp.device.DeviceInformation
import com.gp.domain.GetNameUseCase
import com.gp.logger.LogcatLogger
import com.gp.logger.Logger
import com.gp.playground.vm.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Main application module dependencies.
 */
val appModule = module {
    /** Singletons **/
    single<Logger> { LogcatLogger() }
    single<DeviceInformation> { AndroidDeviceInformation() }

    /** Factories **/
    factory<GetNameUseCase> { GetNameUseCase(get()) }

    /** ViewModels **/
    viewModel { MainViewModel(get(), get()) }
}
