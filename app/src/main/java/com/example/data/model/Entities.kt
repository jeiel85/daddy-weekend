package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_profile")
data class FamilyProfile(
    @PrimaryKey val id: Int = 1, // Single profile instance
    val childAgeGroup: String = "유치원", // 영유아, 유치원, 초등 저학년, 초등 고학년
    val defaultBudget: String = "3만원 이하", // 무료, 3만원 이하, 5만원 이하, 10만원 이하
    val preferredActivity: String = "체험" // 실내, 실외, 집 근처, 자연, 체험, 식사 포함
)

@Entity(tableName = "course_templates")
data class CourseTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val ageGroup: String, // 영유아, 유치원, 초등 저학년, 초등 고학년
    val durationType: String, // 1시간, 2~3시간, 반나절, 하루
    val budgetType: String, // 무료, 3만원 이하, 5만원 이하, 10만원 이하
    val placeType: String, // 실내, 실외, 집 근처, 자연, 체험, 식사 포함
    val dadEnergyLevel: String, // 피곤함, 보통, 에너지 있음
    val steps: String, // Delimited by "||"
    val preparation: String = "",
    val expectedCost: String = "",
    val memo: String = "",
    val isDefault: Boolean = false
)

@Entity(tableName = "saved_courses")
data class SavedCourse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val templateId: Int? = null,
    val title: String,
    val date: Long = System.currentTimeMillis(),
    val rating: Int = 5,
    val memo: String = "",
    val wantToRepeat: Boolean = false,
    val steps: String,
    val ageGroup: String = "",
    val durationType: String = "",
    val budgetType: String = "",
    val placeType: String = "",
    val dadEnergyLevel: String = "",
    val preparation: String = "",
    val expectedCost: String = ""
)
