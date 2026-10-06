package com.playground.device.storage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import androidx.room.Transaction

@Dao
internal interface KeyValueDao {
    @Query("SELECT value FROM key_value_entries WHERE userId = :userId AND `key` = :key")
    suspend fun get(userId: String, key: String): String?

    @Upsert
    suspend fun upsert(entry: KeyValueEntry)

    @Query("SELECT COALESCE(MAX(updatedOrder), 0) + 1 FROM key_value_entries")
    suspend fun nextUpdateOrder(): Long

    @Transaction
    suspend fun put(entry: KeyValueEntry) {
        upsert(entry.copy(updatedOrder = nextUpdateOrder()))
    }

    @Query("SELECT * FROM key_value_entries WHERE userId = :userId ORDER BY updatedOrder DESC, `key` ASC")
    suspend fun getEntries(userId: String): List<KeyValueEntry>

    @Query("DELETE FROM key_value_entries WHERE userId = :userId AND `key` = :key")
    suspend fun remove(userId: String, key: String)

    @Query("DELETE FROM key_value_entries WHERE userId = :userId")
    suspend fun clear(userId: String)
}
