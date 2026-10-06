package com.playground.device.storage

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "key_value_entries", primaryKeys = ["userId", "key"])
internal data class KeyValueEntry(
    val userId: String,
    val key: String,
    val value: String,
    @ColumnInfo(defaultValue = "0") val updatedOrder: Long = 0,
)
