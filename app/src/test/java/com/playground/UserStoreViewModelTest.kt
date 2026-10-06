package com.playground

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
                override suspend fun user(id: String): User {
                    requests += id
                    return User(id.toInt(), "Name $id", "username", "email", "phone", "website")
                }
            }
            val vm = UserStoreViewModel(Stores(mapOf("id" to "1")), "local", api)
            advanceUntilIdle()
            assertEquals(1, vm.state.value.user?.id)
            vm.updateKey("id")
            vm.updateValue("2")
            vm.save()
            advanceUntilIdle()
            assertEquals(listOf("1", "2"), requests)
            assertEquals(2, vm.state.value.user?.id)
            assertFalse(vm.state.value.isUserLoading)
        } finally { Dispatchers.resetMain() }
    }

    @Test
    fun skipsMissingIdAndPreservesEntriesOnLookupFailure() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var requests = 0
            val api = object : UsersApi {
                override suspend fun user(id: String): User {
                    requests++
                    throw IllegalStateException("Network unavailable")
                }
            }
            val vm = UserStoreViewModel(Stores(emptyMap()), "local", api)
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
