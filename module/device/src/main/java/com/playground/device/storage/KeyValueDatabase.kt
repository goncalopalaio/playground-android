package com.playground.device.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [KeyValueEntry::class, StoreUser::class], version = 4, exportSchema = true)
internal abstract class KeyValueDatabase : RoomDatabase() {
    abstract fun keyValueDao(): KeyValueDao
    abstract fun storeUserDao(): StoreUserDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE key_value_entries ADD COLUMN updatedOrder INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS store_users (userId TEXT NOT NULL PRIMARY KEY)")
                db.execSQL("INSERT INTO store_users (userId) SELECT DISTINCT userId FROM key_value_entries WHERE userId != ''")
            }
        }

        // Version 1 entries have no owner. Preserve them under the reserved empty user ID.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE key_value_entries_new (userId TEXT NOT NULL, `key` TEXT NOT NULL, value TEXT NOT NULL, PRIMARY KEY(userId, `key`))")
                db.execSQL("INSERT INTO key_value_entries_new (userId, `key`, value) SELECT '', `key`, value FROM key_value_entries")
                db.execSQL("DROP TABLE key_value_entries")
                db.execSQL("ALTER TABLE key_value_entries_new RENAME TO key_value_entries")
            }
        }
    }
}
