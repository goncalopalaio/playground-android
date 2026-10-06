package com.playground.device.storage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [KeyValueEntry::class], version = 1, exportSchema = true)
internal abstract class KeyValueDatabase : RoomDatabase() {
    abstract fun keyValueDao(): KeyValueDao
}
