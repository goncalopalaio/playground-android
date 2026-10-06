package com.playground.space

import arrow.core.Either
import com.playground.common.failure
import com.playground.common.success
import kotlinx.coroutines.CancellationException
import com.playground.api.remote.PostsApi
import com.playground.data.remote.Post
import com.playground.api.remote.UsersApi
import com.playground.data.remote.User
import com.playground.space.api.JsonPlaceholderApi
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class UsersSource : UsersApi, PostsApi {
    private val service by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .build()
            .create(JsonPlaceholderApi::class.java)
    }

    override suspend fun posts(userId: Int): Either<List<Post>, Exception> = try {
        service.posts(userId).map { remote ->
            Post(remote.userId, remote.id, remote.title, remote.body)
        }.success()
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        exception.failure()
    }

    override suspend fun user(id: String): Either<User, Exception> = try {
        val remote = service.user(id)
        User(remote.id, remote.name, remote.username, remote.email, remote.phone, remote.website).success()
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        exception.failure()
    }
}
