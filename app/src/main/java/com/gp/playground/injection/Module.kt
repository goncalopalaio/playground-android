package com.gp.playground.injection

import com.gp.api.space.SpaceApi
import com.gp.device.AndroidDeviceInformation
import com.gp.device.DeviceInformation
import com.gp.domain.GetNameUseCase
import com.gp.domain.GetSpaceCompanyUseCase
import com.gp.domain.GetSpaceLaunchesUseCase
import com.gp.domain.repository.SpaceRepository
import com.gp.logger.LogcatLogger
import com.gp.logger.Logger
import com.gp.playground.vm.MainViewModel
import com.gp.space.SpaceSource
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
