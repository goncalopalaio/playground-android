package com.playground.device.storage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
internal interface KeyValueDao {
    @Query("SELECT value FROM key_value_entries WHERE userId = :userId AND `key` = :key")
    suspend fun get(userId: String, key: String): String?

    @Upsert
    suspend fun put(entry: KeyValueEntry)

    @Query("DELETE FROM key_value_entries WHERE userId = :userId AND `key` = :key")
    suspend fun remove(userId: String, key: String)

    @Query("DELETE FROM key_value_entries WHERE userId = :userId")
    suspend fun clear(userId: String)
}
