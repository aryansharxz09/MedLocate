package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RequestEntity::class, SavedHospitalEntity::class, NotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MedLocateDatabase : RoomDatabase() {
    abstract fun requestDao(): RequestDao
    abstract fun savedHospitalDao(): SavedHospitalDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: MedLocateDatabase? = null

        fun getDatabase(context: Context): MedLocateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedLocateDatabase::class.java,
                    "medlocate_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
