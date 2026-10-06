package com.playground.device.storage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
internal interface KeyValueDao {
    @Query("SELECT value FROM key_value_entries WHERE `key` = :key")
    suspend fun get(key: String): String?

    @Upsert
    suspend fun put(entry: KeyValueEntry)

    @Query("DELETE FROM key_value_entries WHERE `key` = :key")
    suspend fun remove(key: String)

    @Query("DELETE FROM key_value_entries")
    suspend fun clear()
}
