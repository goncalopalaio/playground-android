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
    private lateinit var stores: RoomKeyValueStores
    private lateinit var store: KeyValueStore
    private val databaseName = "key_value_store_test.db"

    @Before
    fun setUp() = runTest {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(databaseName)
        stores = RoomKeyValueStores(Room.inMemoryDatabaseBuilder(context, KeyValueDatabase::class.java).build())
        store = stores.forUser("alice")
    }

    @After
    fun tearDown() {
        stores.close()
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
        stores.close()
        stores = openPersistentStores()
        store = stores.forUser("alice")
        store.put("saved", "value")
        stores.close()
        stores = openPersistentStores()
        store = stores.forUser("alice")
        assertEquals("value", store.get("saved"))
    }

    @Test
    fun usersCanShareKeysWithoutSharingValues() = runTest {
        val bob = stores.forUser("bob")
        store.put("name", "Alice")
        assertNull(bob.get("name"))
        bob.put("name", "Bob")
        store.put("name", "Alice updated")
        assertEquals("Alice updated", stores.forUser("alice").get("name"))
        assertEquals("Bob", bob.get("name"))
        store.remove("name")
        assertEquals("Bob", bob.get("name"))
        store.put("other", "value")
        store.clear()
        assertNull(store.get("other"))
        assertEquals("Bob", bob.get("name"))
    }

    @Test
    fun rejectsBlankUserIds() = runTest {
        for (userId in listOf("", " ", "\t")) {
            try {
                stores.forUser(userId)
                org.junit.Assert.fail("Expected blank user ID to be rejected")
            } catch (_: IllegalArgumentException) {
                // Expected.
            }
        }
    }

    @Test
    fun migratesLegacyEntriesWithoutAssigningThemToAUser() = runTest {
        stores.close()
        context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null).use { legacy ->
            legacy.execSQL("CREATE TABLE key_value_entries (`key` TEXT NOT NULL PRIMARY KEY, value TEXT NOT NULL)")
            legacy.execSQL("INSERT INTO key_value_entries (`key`, value) VALUES ('legacy', 'saved')")
            legacy.version = 1
        }
        val database = Room.databaseBuilder(context, KeyValueDatabase::class.java, databaseName)
            .addMigrations(KeyValueDatabase.MIGRATION_1_2, KeyValueDatabase.MIGRATION_2_3, KeyValueDatabase.MIGRATION_3_4)
            .build()
        stores = RoomKeyValueStores(database)
        store = stores.forUser("alice")
        assertNull(store.get("legacy"))
        assertNull(stores.forUser("bob").get("legacy"))
        assertEquals("saved", database.keyValueDao().get("", "legacy"))
        store.put("legacy", "Alice's value")
        assertEquals("saved", database.keyValueDao().get("", "legacy"))
        assertEquals("Alice's value", store.get("legacy"))
    }

    @Test
    fun listsEmptyUsersInOrderAndIgnoresDuplicateCreation() = runTest {
        assertEquals(listOf("alice"), stores.getUsers())
        org.junit.Assert.assertTrue(stores.createUser("charlie"))
        org.junit.Assert.assertTrue(stores.createUser("bob"))
        org.junit.Assert.assertFalse(stores.createUser("bob"))
        assertEquals(listOf("alice", "bob", "charlie"), stores.getUsers())
        val bob = stores.forUser("bob")
        assertNull(bob.get("name"))
        bob.put("name", "Bob")
        org.junit.Assert.assertFalse(stores.createUser("bob"))
        assertEquals("Bob", bob.get("name"))
        bob.clear()
        assertEquals(listOf("alice", "bob", "charlie"), stores.getUsers())
    }

    @Test
    fun persistsUsersWithNoValuesAcrossReopening() = runTest {
        stores.close()
        stores = openPersistentStores()
        assertEquals(emptyList<String>(), stores.getUsers())
        stores.createUser("empty-user")
        stores.close()
        stores = openPersistentStores()
        assertEquals(listOf("empty-user"), stores.getUsers())
        assertNull(stores.forUser("empty-user").get("missing"))
    }

    @Test
    fun migratesExistingUserIdsAndPreservesTheirValues() = runTest {
        stores.close()
        context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null).use { legacy ->
            legacy.execSQL("CREATE TABLE key_value_entries (userId TEXT NOT NULL, `key` TEXT NOT NULL, value TEXT NOT NULL, PRIMARY KEY(userId, `key`))")
            legacy.execSQL("INSERT INTO key_value_entries VALUES ('bob', 'name', 'Bob'), ('bob', 'theme', 'dark'), ('alice', 'name', 'Alice'), ('', 'legacy', 'saved')")
            legacy.version = 2
        }
        stores = openPersistentStores()
        assertEquals(listOf("alice", "bob"), stores.getUsers())
        assertEquals("Bob", stores.forUser("bob").get("name"))
        assertEquals("dark", stores.forUser("bob").get("theme"))
        assertEquals("Alice", stores.forUser("alice").get("name"))
    }

    @Test
    fun rejectsBlankIdsWhenCreatingUsers() = runTest {
        for (userId in listOf("", " ", "\t")) {
            try {
                stores.createUser(userId)
                org.junit.Assert.fail("Expected blank user ID to be rejected")
            } catch (_: IllegalArgumentException) {
                // Expected.
            }
        }
        assertEquals(listOf("alice"), stores.getUsers())
    }

    @Test
    fun removesAllUsersAndValuesAndCanBeRepeated() = runTest {
        val bob = stores.forUser("bob")
        stores.createUser("empty-user")
        store.put("name", "Alice")
        bob.put("name", "Bob")
        stores.removeAllUsers()
        assertEquals(emptyList<String>(), stores.getUsers())
        assertNull(store.get("name"))
        assertNull(bob.get("name"))
        stores.removeAllUsers()
        org.junit.Assert.assertTrue(stores.createUser("alice"))
        val recreated = stores.forUser("alice")
        assertNull(recreated.get("name"))
        recreated.put("name", "New Alice")
        assertEquals("New Alice", recreated.get("name"))
    }

    @Test
    fun userRemovalPersistsAcrossReopening() = runTest {
        stores.close()
        stores = openPersistentStores()
        stores.forUser("alice").put("name", "Alice")
        stores.createUser("empty-user")
        stores.removeAllUsers()
        stores.close()
        stores = openPersistentStores()
        assertEquals(emptyList<String>(), stores.getUsers())
        assertNull(stores.forUser("alice").get("name"))
    }

    @Test
    fun removesUnassignedLegacyValuesAlongWithUsers() = runTest {
        stores.close()
        val database = Room.inMemoryDatabaseBuilder(context, KeyValueDatabase::class.java).build()
        stores = RoomKeyValueStores(database)
        database.keyValueDao().put(com.playground.device.storage.KeyValueEntry("", "legacy", "saved"))
        stores.createUser("alice")
        stores.removeAllUsers()
        assertNull(database.keyValueDao().get("", "legacy"))
        assertEquals(emptyList<String>(), stores.getUsers())
    }

    @Test
    fun listsOnlyThisUsersEntriesWithLatestUpdatesFirst() = runTest {
        store.put("first", "one")
        stores.forUser("bob").put("private", "Bob")
        store.put("second", "two")
        assertEquals(listOf(KeyValueItem("second", "two"), KeyValueItem("first", "one")), store.getEntries())
        store.put("first", "updated")
        assertEquals(listOf(KeyValueItem("first", "updated"), KeyValueItem("second", "two")), store.getEntries())
        store.remove("first")
        assertEquals(listOf(KeyValueItem("second", "two")), store.getEntries())
    }

    private fun openPersistentStores() = RoomKeyValueStores(
        Room.databaseBuilder(context, KeyValueDatabase::class.java, databaseName).addMigrations(KeyValueDatabase.MIGRATION_1_2, KeyValueDatabase.MIGRATION_2_3, KeyValueDatabase.MIGRATION_3_4).build()
    )
}
