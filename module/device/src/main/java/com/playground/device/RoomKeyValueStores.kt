package com.playground.device

import android.content.Context
import androidx.room.Room
import com.playground.device.storage.KeyValueDatabase
import com.playground.device.storage.StoreUser

/** Owns one shared database. Close only when all user stores are no longer needed. */
class RoomKeyValueStores internal constructor(
    private val database: KeyValueDatabase,
) : KeyValueStores, AutoCloseable {
    constructor(context: Context) : this(
        Room.databaseBuilder(
            context.applicationContext,
            KeyValueDatabase::class.java,
            "key_value_store.db",
        ).addMigrations(KeyValueDatabase.MIGRATION_1_2, KeyValueDatabase.MIGRATION_2_3, KeyValueDatabase.MIGRATION_3_4).build()
    )

    override suspend fun forUser(userId: String): KeyValueStore {
        createUser(userId)
        return RoomKeyValueStore(database.keyValueDao(), userId)
    }

    override suspend fun createUser(userId: String): Boolean {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        return database.storeUserDao().insert(StoreUser(userId)) != -1L
    }

    override suspend fun getUsers(): List<String> = database.storeUserDao().getUsers()

    override suspend fun removeAllUsers() = database.storeUserDao().removeAllUsers()

    override fun close() = database.close()
}
