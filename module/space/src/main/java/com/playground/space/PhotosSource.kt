package com.playground.space

import com.playground.api.remote.PhotosApi
import com.playground.common.failure
import com.playground.common.success
import com.playground.data.remote.Photo
import com.playground.space.api.JsonPlaceholderApi
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CancellationException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

class PhotosSource : PhotosApi {
    private val service by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
            .baseUrl(BASE_URL)
            .build()
            .create(JsonPlaceholderApi::class.java)
    }

    override suspend fun photos() = try {
        service.photos().map {
            Photo(it.albumId, it.id, it.title, it.url, it.thumbnailUrl)
        }.success()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        e.failure()
    }
}
