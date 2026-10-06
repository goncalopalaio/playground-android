package com.playground.device.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_users")
internal data class StoreUser(@PrimaryKey val userId: String)
