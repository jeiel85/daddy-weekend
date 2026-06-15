package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CourseTemplateDao
import com.example.data.dao.FamilyProfileDao
import com.example.data.dao.SavedCourseDao
import com.example.data.model.CourseTemplate
import com.example.data.model.FamilyProfile
import com.example.data.model.SavedCourse

@Database(
    entities = [FamilyProfile::class, CourseTemplate::class, SavedCourse::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun familyProfileDao(): FamilyProfileDao
    abstract fun courseTemplateDao(): CourseTemplateDao
    abstract fun savedCourseDao(): SavedCourseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weekend_dad_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
