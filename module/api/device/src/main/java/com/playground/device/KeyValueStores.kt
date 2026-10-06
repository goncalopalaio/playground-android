package com.playground.device

/** Provides isolated stores for stable user IDs supplied by the application. */
interface KeyValueStores {
    /**
     * Registers the user if needed and returns a store bound to this user, including after account changes.
     * The ID must be nonblank. Use the same ID across sessions to retrieve saved values.
     */
    suspend fun forUser(userId: String): KeyValueStore

    /** Creates a user with an empty store. Returns false if the user already exists. */
    suspend fun createUser(userId: String): Boolean

    /** Returns all registered user IDs in ascending order, including users with empty stores. */
    suspend fun getUsers(): List<String>

    /** Atomically removes all users and all stored values, including unassigned legacy entries. */
    suspend fun removeAllUsers()
}
