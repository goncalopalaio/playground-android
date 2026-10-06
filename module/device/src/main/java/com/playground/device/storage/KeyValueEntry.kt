package com.playground.device.storage

import androidx.room.Entity

@Entity(tableName = "key_value_entries", primaryKeys = ["userId", "key"])
internal data class KeyValueEntry(
    val userId: String,
    val key: String,
    val value: String,
)
