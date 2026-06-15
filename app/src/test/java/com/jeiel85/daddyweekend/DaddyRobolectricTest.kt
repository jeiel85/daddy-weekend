package com.jeiel85.daddyweekend

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.jeiel85.daddyweekend.data.dao.CourseTemplateDao
import com.jeiel85.daddyweekend.data.database.AppDatabase
import com.jeiel85.daddyweekend.data.model.CourseTemplate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34]) // Run on a stable supported SDK in Robolectric
class DaddyRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var templateDao: CourseTemplateDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        templateDao = db.courseTemplateDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeTemplateAndReadInList() = runBlocking {
        val template = CourseTemplate(
            id = 100,
            title = "로보렉트릭 주말 아빠 여행",
            ageGroup = "초등 저학년",
            durationType = "반나절",
            budgetType = "무료",
            placeType = "자연",
            dadEnergyLevel = "보통",
            steps = "캠핑 가기||고기 굽기",
            preparation = "텐트",
            expectedCost = "무료",
            isDefault = false
        )
        templateDao.insertTemplate(template)
        
        val allTemplates = templateDao.getAllTemplates().first()
        assertEquals(1, allTemplates.size)
        assertEquals("로보렉트릭 주말 아빠 여행", allTemplates[0].title)
    }
}
