package com.example.data.dao

import androidx.room.*
import com.example.data.model.CourseTemplate
import com.example.data.model.FamilyProfile
import com.example.data.model.SavedCourse
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyProfileDao {
    @Query("SELECT * FROM family_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<FamilyProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: FamilyProfile)
}

@Dao
interface CourseTemplateDao {
    @Query("SELECT * FROM course_templates ORDER BY id ASC")
    fun getAllTemplates(): Flow<List<CourseTemplate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: CourseTemplate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTemplates(templates: List<CourseTemplate>)

    @Delete
    suspend fun deleteTemplate(template: CourseTemplate)

    @Query("SELECT * FROM course_templates WHERE id = :id LIMIT 1")
    suspend fun getTemplateById(id: Int): CourseTemplate?
}

@Dao
interface SavedCourseDao {
    @Query("SELECT * FROM saved_courses ORDER BY date DESC")
    fun getAllSavedCourses(): Flow<List<SavedCourse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCourse(savedCourse: SavedCourse)

    @Delete
    suspend fun deleteSavedCourse(savedCourse: SavedCourse)

    @Query("DELETE FROM saved_courses WHERE id = :id")
    suspend fun deleteSavedCourseById(id: Int)

    @Query("SELECT * FROM saved_courses WHERE id = :id LIMIT 1")
    suspend fun getSavedCourseById(id: Int): SavedCourse?
}
