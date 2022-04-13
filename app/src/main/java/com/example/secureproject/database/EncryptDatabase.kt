package com.example.secureproject.database

import android.content.Context
import androidx.room.*

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

@Database(entities = [Encrypt::class], version = 1)
abstract class EncryptDatabase : RoomDatabase() {
    abstract fun encryptDAO() : EncryptDAO

    companion object {
        private var INSTANCE : EncryptDatabase?=null

        fun getInstance(context: Context) : EncryptDatabase {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, EncryptDatabase::class.java, "database.db").build()
            }
            return INSTANCE!!
        }
    }
}