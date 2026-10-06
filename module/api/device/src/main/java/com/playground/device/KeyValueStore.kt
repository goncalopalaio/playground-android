package com.playground.device

/** Persistent storage for string values, indexed by unique string keys. */
interface KeyValueStore {
    /** Returns null when the key does not exist. */
    suspend fun get(key: String): String?

    /** Inserts a value or replaces the existing value for the key. */
    suspend fun put(key: String, value: String)

    /** Removes the key if it exists. */
    suspend fun remove(key: String)

    /** Removes all entries. */
    suspend fun clear()
}
