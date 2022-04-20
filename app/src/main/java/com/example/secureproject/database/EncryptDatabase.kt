package com.example.secureproject.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Dao
interface EncryptDAO {
    @Insert
    fun insert(encrypt: Encrypt): Long

    @Update
    fun update(encrypt: Encrypt)

    @Delete
    fun delete(encrypt: Encrypt)

    @Query("SELECT * FROM encrypt")
    fun getAll() : List<Encrypt>

    @Query("DELETE FROM encrypt")
    fun deleteAll()
}

@Database(version = 2, entities = [Encrypt::class], exportSchema = false)
abstract class EncryptDatabase : RoomDatabase() {
    abstract fun encryptDAO() : EncryptDAO

    companion object {
        private var INSTANCE : EncryptDatabase?=null

        fun getInstance(context: Context) : EncryptDatabase {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, EncryptDatabase::class.java, "database.db").addMigrations(
                    MIGRATION_1_2).build()
            }
            return INSTANCE!!
        }
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'encrypt' ADD COLUMN 'keyText' VARCHAR(30)")
            }
        }
    }
}