package com.playground.injection

import com.playground.api.remote.UsersApi
import com.playground.space.UsersSource
import com.playground.api.remote.PhotosApi
import com.playground.device.AndroidDeviceInformation
import com.playground.device.DeviceInformation
import com.playground.device.KeyValueStores
import com.playground.device.RoomKeyValueStores
import com.playground.domain.GetNameUseCase
import com.playground.domain.GetPhotosUseCase
import com.playground.logger.LogcatLogger
import com.playground.logger.Logger
import com.playground.vm.UserStoreViewModel
import com.playground.vm.UsersViewModel
import com.playground.vm.MainViewModel
import com.playground.space.PhotosSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Main application module dependencies.
 */
val appModule = module {
    /** Singletons **/
    single<Logger> { LogcatLogger() }
    single<DeviceInformation> { AndroidDeviceInformation() }
    single<KeyValueStores> { RoomKeyValueStores(androidContext()) }
    single<PhotosApi> { PhotosSource() }
    single<UsersApi> { UsersSource() }


    /** Factories **/
    factory<GetNameUseCase> { GetNameUseCase(get()) }
    factory<GetPhotosUseCase> { GetPhotosUseCase(get()) }

    /** ViewModels **/
    viewModel { MainViewModel(get(), get()) }
    viewModel { UsersViewModel(get()) }
    viewModel { parameters -> UserStoreViewModel(get(), parameters.get(), get()) }
}
