package com.jeiel85.daddyweekend

import com.jeiel85.daddyweekend.data.model.CourseTemplate
import org.junit.Assert.assertEquals
import org.junit.Test

class DaddyUnitTest {

    @Test
    fun test_filterResult_exactMatch_check() {
        val course1 = CourseTemplate(
            id = 1,
            title = "테스트 코스 1",
            ageGroup = "유치원",
            durationType = "2~3시간",
            budgetType = "3만원 이하",
            placeType = "실내",
            dadEnergyLevel = "보통",
            steps = "1단계||2단계",
            preparation = "준비물",
            expectedCost = "10,000원",
            isDefault = true
        )
        
        val list = listOf(course1)
        
        val matches = list.filter {
            it.ageGroup == "유치원" &&
            it.durationType == "2~3시간" &&
            it.budgetType == "3만원 이하" &&
            it.placeType == "실내" &&
            it.dadEnergyLevel == "보통"
        }
        
        assertEquals(1, matches.size)
        assertEquals("테스트 코스 1", matches[0].title)
    }

    @Test
    fun test_filterResult_fallbackMatch_check() {
        val course = CourseTemplate(
            id = 2,
            title = "테스트 코스 2",
            ageGroup = "초등 저학년",
            durationType = "반나절",
            budgetType = "무료",
            placeType = "자연",
            dadEnergyLevel = "에너지 있음",
            steps = "1단계",
            preparation = "준비물",
            expectedCost = "무료",
            isDefault = true
        )
        val list = listOf(course)
        
        val ageMatches = list.filter { it.ageGroup == "초등 저학년" }
        
        assertEquals(1, ageMatches.size)
        assertEquals("테스트 코스 2", ageMatches[0].title)
    }
}
