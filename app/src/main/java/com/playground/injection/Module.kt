package com.playground.injection

import com.playground.api.space.SpaceApi
import com.playground.device.AndroidDeviceInformation
import com.playground.device.DeviceInformation
import com.playground.domain.GetNameUseCase
import com.playground.domain.GetSpaceCompanyUseCase
import com.playground.domain.GetSpaceLaunchesUseCase
import com.playground.domain.repository.SpaceRepository
import com.playground.logger.LogcatLogger
import com.playground.logger.Logger
import com.playground.vm.MainViewModel
import com.playground.space.SpaceSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Main application module dependencies.
 */
val appModule = module {
    /** Singletons **/
    single<Logger> { LogcatLogger() }
    single<DeviceInformation> { AndroidDeviceInformation() }
    single<SpaceApi> { SpaceSource() }

    single<SpaceRepository> { SpaceRepository() }

    /** Factories **/
    factory<GetNameUseCase> { GetNameUseCase(get()) }
    factory<GetSpaceLaunchesUseCase> { GetSpaceLaunchesUseCase(get()) }
    factory<GetSpaceCompanyUseCase> { GetSpaceCompanyUseCase(get()) }

    /** ViewModels **/
    viewModel { MainViewModel(get(), get(), get()) }
}
