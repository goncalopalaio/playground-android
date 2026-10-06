package com.playground

import com.playground.api.remote.PhotosApi
import com.playground.common.failure
import com.playground.common.success
import com.playground.common.successOrNull
import com.playground.data.remote.Photo
import com.playground.domain.GetPhotosUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPhotosUseCaseTest {
    @Test
    fun returnsFirstTenPhotosInResponseOrder() = runTest {
        val photos = (1..12).map { Photo(1, it, "Photo $it", "url/$it", "thumbnail/$it") }
        val api = object : PhotosApi {
            override suspend fun photos() = photos.success()
        }
        assertEquals(photos.take(10), GetPhotosUseCase(api)().first().successOrNull())
    }

    @Test
    fun preservesShortResponses() = runTest {
        val photos = listOf(Photo(1, 1, "Photo", "url", "thumbnail"))
        val api = object : PhotosApi {
            override suspend fun photos() = photos.success()
        }
        assertEquals(photos, GetPhotosUseCase(api)().first().successOrNull())
    }

    @Test
    fun preservesApiFailures() = runTest {
        val failure = IllegalStateException("Network unavailable")
        val api = object : PhotosApi {
            override suspend fun photos() = failure.failure()
        }
        assertEquals(failure.failure(), GetPhotosUseCase(api)().first())
    }
}
