package com.playground.device

import com.playground.device.storage.KeyValueDao
import com.playground.device.storage.KeyValueEntry

/** A store bound to one user; its database is owned by [RoomKeyValueStores]. */
internal class RoomKeyValueStore(
    private val dao: KeyValueDao,
    private val userId: String,
) : KeyValueStore {
    override suspend fun get(key: String): String? = dao.get(userId, key)

    override suspend fun put(key: String, value: String) = dao.put(KeyValueEntry(userId, key, value))

    override suspend fun remove(key: String) = dao.remove(userId, key)

    override suspend fun clear() = dao.clear(userId)
}
