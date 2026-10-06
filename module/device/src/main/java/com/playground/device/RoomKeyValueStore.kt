package com.playground.device

import android.content.Context
import androidx.room.Room
import com.playground.device.storage.KeyValueDatabase
import com.playground.device.storage.KeyValueEntry

/** Reuse one instance for the application and close it when it is no longer needed. */
class RoomKeyValueStore internal constructor(
    private val database: KeyValueDatabase,
) : KeyValueStore, AutoCloseable {

    constructor(context: Context) : this(
        Room.databaseBuilder(
            context.applicationContext,
            KeyValueDatabase::class.java,
            "key_value_store.db",
        ).build()
    )

    private val dao = database.keyValueDao()

    override suspend fun get(key: String): String? = dao.get(key)

    override suspend fun put(key: String, value: String) = dao.put(KeyValueEntry(key, value))

    override suspend fun remove(key: String) = dao.remove(key)

    override suspend fun clear() = dao.clear()

    override fun close() = database.close()
}
