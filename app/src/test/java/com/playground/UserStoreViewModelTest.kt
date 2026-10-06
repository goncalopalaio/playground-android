package com.playground

import arrow.core.Either
import com.playground.common.failure
import com.playground.common.success
import com.playground.api.remote.PostsApi
import com.playground.data.remote.Post
import com.playground.api.remote.UsersApi
import com.playground.data.remote.User
import com.playground.device.KeyValueItem
import com.playground.device.KeyValueStore
import com.playground.device.KeyValueStores
import com.playground.vm.UserStoreViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserStoreViewModelTest {
    private val postsApi = object : PostsApi {
        override suspend fun posts(userId: Int) = listOf(Post(userId, 10, "First", "Body 1"), Post(userId, 20, "Second", "Body 2")).success()
    }

    private class Stores(initial: Map<String, String>) : KeyValueStores {
        val values = initial.toMutableMap()
        override suspend fun forUser(userId: String) = object : KeyValueStore {
            override suspend fun get(key: String) = values[key]
            override suspend fun put(key: String, value: String) { values[key] = value }
            override suspend fun getEntries() = values.map { KeyValueItem(it.key, it.value) }
            override suspend fun remove(key: String) { values.remove(key) }
            override suspend fun clear() { values.clear() }
        }
        override suspend fun createUser(userId: String) = true
        override suspend fun getUsers() = listOf("local")
        override suspend fun removeAllUsers() { values.clear() }
    }

    @Test
    fun loadsExistingIdAndUpdatesAfterSaving() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val requests = mutableListOf<String>()
            val api = object : UsersApi {
                override suspend fun user(id: String): Either<User, Exception> {
                    requests += id
                    return User(id.toInt(), "Name $id", "username", "email", "phone", "website").success()
                }
            }
            val vm = UserStoreViewModel(Stores(mapOf("id" to "1")), "local", api, postsApi)
            advanceUntilIdle()
            assertEquals(1, vm.state.value.user?.id)
            assertNull(vm.state.value.posts)
            vm.loadPosts()
            advanceUntilIdle()
            assertEquals(listOf(Post(1, 10, "First", "Body 1"), Post(1, 20, "Second", "Body 2")), vm.state.value.posts)
            assertFalse(vm.state.value.isPostsLoading)
            vm.updateKey("id")
            vm.updateValue("2")
            vm.save()
            advanceUntilIdle()
            assertEquals(listOf("1", "2"), requests)
            assertEquals(2, vm.state.value.user?.id)
            assertNull(vm.state.value.posts)
            assertFalse(vm.state.value.isUserLoading)
        } finally { Dispatchers.resetMain() }
    }

    @Test
    fun postsRequireSuccessfulUserAndCanRetryAfterFailure() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var requests = 0
            val api = object : UsersApi {
                override suspend fun user(id: String) = User(1, "Name", "username", "email", "phone", "website").success()
            }
            val posts = object : PostsApi {
                override suspend fun posts(userId: Int): Either<List<Post>, Exception> {
                    assertEquals(1, userId)
                    requests++
                    if (requests == 1) return IllegalStateException("Network unavailable").failure()
                    return listOf(Post(userId, 1, "Recovered", "Body")).success()
                }
            }
            val vm = UserStoreViewModel(Stores(mapOf("id" to "1")), "local", api, posts)
            vm.loadPosts()
            assertEquals(0, requests)
            advanceUntilIdle()
            vm.loadPosts()
            vm.loadPosts()
            advanceUntilIdle()
            assertEquals(1, requests)
            assertNotNull(vm.state.value.postsError)
            assertFalse(vm.state.value.isPostsLoading)
            assertNotNull(vm.state.value.user)
            vm.loadPosts()
            advanceUntilIdle()
            assertEquals(listOf(Post(1, 1, "Recovered", "Body")), vm.state.value.posts)
            assertNull(vm.state.value.postsError)
        } finally { Dispatchers.resetMain() }
    }

    @Test
    fun skipsMissingIdAndPreservesEntriesOnLookupFailure() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var requests = 0
            val api = object : UsersApi {
                override suspend fun user(id: String): Either<User, Exception> {
                    requests++
                    return IllegalStateException("Network unavailable").failure()
                }
            }
            val vm = UserStoreViewModel(Stores(emptyMap()), "local", api, postsApi)
            advanceUntilIdle()
            assertEquals(0, requests)
            vm.updateKey("id")
            vm.updateValue("99")
            vm.save()
            advanceUntilIdle()
            assertEquals(listOf(KeyValueItem("id", "99")), vm.state.value.entries)
            assertNotNull(vm.state.value.userError)
            assertNull(vm.state.value.user)
            assertFalse(vm.state.value.isUserLoading)
        } finally { Dispatchers.resetMain() }
    }
}
