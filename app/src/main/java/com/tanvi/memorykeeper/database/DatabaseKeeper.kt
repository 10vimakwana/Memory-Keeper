package com.tanvi.memorykeeper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tanvi.memorykeeper.dataclass.Note

@Database(entities = [EntityKeeper::class, Note::class], version = 1)
@TypeConverters(Convert::class)

abstract class DatabaseKeeper : RoomDatabase() {
    abstract fun getDao(): KeeperDao

    companion object {
        @Volatile
        private var instance: DatabaseKeeper? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            DatabaseKeeper::class.java,
            "roomdb"
        ).build()
    }

}