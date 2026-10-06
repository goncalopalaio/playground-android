package com.playground.device.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "key_value_entries")
internal data class KeyValueEntry(
    @PrimaryKey val key: String,
    val value: String,
)
