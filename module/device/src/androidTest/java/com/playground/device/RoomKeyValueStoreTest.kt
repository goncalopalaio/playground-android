package com.playground.device

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.playground.device.storage.KeyValueDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomKeyValueStoreTest {
    private lateinit var context: Context
    private lateinit var store: RoomKeyValueStore
    private val databaseName = "key_value_store_test.db"

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(databaseName)
        store = RoomKeyValueStore(Room.inMemoryDatabaseBuilder(context, KeyValueDatabase::class.java).build())
    }

    @After
    fun tearDown() {
        store.close()
        context.deleteDatabase(databaseName)
    }

    @Test
    fun readsMissingKeysAndReplacesExistingValues() = runTest {
        assertNull(store.get("missing"))
        store.put("name", "first")
        store.put("other", "untouched")
        assertEquals("first", store.get("name"))
        store.put("name", "second")
        assertEquals("second", store.get("name"))
        assertEquals("untouched", store.get("other"))
    }

    @Test
    fun removesOnlyTheRequestedKeyAndClearsAllEntries() = runTest {
        store.put("first", "one")
        store.put("second", "two")
        store.remove("missing")
        store.remove("first")
        assertNull(store.get("first"))
        assertEquals("two", store.get("second"))
        store.clear()
        assertNull(store.get("second"))
        store.clear()
        store.put("after-clear", "three")
        assertEquals("three", store.get("after-clear"))
    }

    @Test
    fun roundTripsEmptyAndUnicodeValuesAndSqlCharactersInKeys() = runTest {
        store.put("", "")
        store.put("' OR 1=1 --", "Olá 🌍")
        assertEquals("", store.get(""))
        assertEquals("Olá 🌍", store.get("' OR 1=1 --"))
        assertNull(store.get("different"))
    }

    @Test
    fun persistsValuesAfterClosingAndReopeningTheDatabase() = runTest {
        store.close()
        store = openPersistentStore()
        store.put("saved", "value")
        store.close()
        store = openPersistentStore()
        assertEquals("value", store.get("saved"))
    }

    private fun openPersistentStore() = RoomKeyValueStore(
        Room.databaseBuilder(context, KeyValueDatabase::class.java, databaseName).build()
    )
}
