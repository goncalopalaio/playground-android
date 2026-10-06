package com.playground.device.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface StoreUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: StoreUser): Long

    @Query("SELECT userId FROM store_users ORDER BY userId ASC")
    suspend fun getUsers(): List<String>

    @Query("DELETE FROM key_value_entries")
    suspend fun deleteAllValues()

    @Query("DELETE FROM store_users")
    suspend fun deleteAllUsers()

    @Transaction
    suspend fun removeAllUsers() {
        deleteAllValues()
        deleteAllUsers()
    }
}
