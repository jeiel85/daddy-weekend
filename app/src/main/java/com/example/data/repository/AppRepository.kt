package com.example.data.repository

import com.example.data.dao.CourseTemplateDao
import com.example.data.dao.FamilyProfileDao
import com.example.data.dao.SavedCourseDao
import com.example.data.model.CourseTemplate
import com.example.data.model.FamilyProfile
import com.example.data.model.SavedCourse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(
    private val familyProfileDao: FamilyProfileDao,
    private val courseTemplateDao: CourseTemplateDao,
    private val savedCourseDao: SavedCourseDao
) {
    val familyProfile: Flow<FamilyProfile?> = familyProfileDao.getProfile()
    val allTemplates: Flow<List<CourseTemplate>> = courseTemplateDao.getAllTemplates()
    val allSavedCourses: Flow<List<SavedCourse>> = savedCourseDao.getAllSavedCourses()

    suspend fun updateProfile(profile: FamilyProfile) {
        familyProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun insertTemplate(template: CourseTemplate) {
        courseTemplateDao.insertTemplate(template)
    }

    suspend fun deleteTemplate(template: CourseTemplate) {
        courseTemplateDao.deleteTemplate(template)
    }

    suspend fun getTemplateById(id: Int): CourseTemplate? {
        return courseTemplateDao.getTemplateById(id)
    }

    suspend fun saveCourse(saved: SavedCourse) {
        savedCourseDao.insertSavedCourse(saved)
    }

    suspend fun deleteSavedCourse(saved: SavedCourse) {
        savedCourseDao.deleteSavedCourse(saved)
    }

    suspend fun deleteSavedCourseById(id: Int) {
        savedCourseDao.deleteSavedCourseById(id)
    }

    suspend fun getSavedCourseById(id: Int): SavedCourse? {
        return savedCourseDao.getSavedCourseById(id)
    }

    suspend fun checkAndPopulateDefaultTemplates() {
        val templates = allTemplates.firstOrNull() ?: emptyList()
        if (templates.isEmpty()) {
            val list = getDefaultTemplates()
            courseTemplateDao.insertAllTemplates(list)
            
            val profile = familyProfile.firstOrNull()
            if (profile == null) {
                familyProfileDao.insertOrUpdateProfile(FamilyProfile())
            }
        }
    }

    suspend fun clearAllData() {
        // Safe clear
        val saved = allSavedCourses.firstOrNull() ?: emptyList()
        saved.forEach { savedCourseDao.deleteSavedCourse(it) }
        
        val templates = allTemplates.firstOrNull() ?: emptyList()
        templates.forEach { courseTemplateDao.deleteTemplate(it) }
        
        courseTemplateDao.insertAllTemplates(getDefaultTemplates())
        familyProfileDao.insertOrUpdateProfile(FamilyProfile())
    }

    private fun getDefaultTemplates(): List<CourseTemplate> {
        return listOf(
            CourseTemplate(
                title = "집 앞 놀이터 수사대 + 홈 아이스크림 카페",
                ageGroup = "영유아",
                durationType = "1시간",
                budgetType = "무료",
                placeType = "집 근처",
                dadEnergyLevel = "피곤함",
                steps = "아이가 좋아하는 옷 입히고 출발하기||집 앞 놀이터에서 손잡고 기구 타기 및 모래성 쌓기||근처 슈퍼마켓에서 아이가 직접 아이스크림 고르게 하기||집 거실 소파에서 에어컨 틀고 맛있게 아이스크림 먹기",
                preparation = "물티슈, 자그마한 지갑(아이 결제 체험용)",
                expectedCost = "5,000원 이하",
                isDefault = true
            ),
            CourseTemplate(
                title = "비 오는 날, 도서관 책 도장 깨기 + 붕어빵 맛집",
                ageGroup = "유치원",
                durationType = "2~3시간",
                budgetType = "3만원 이하",
                placeType = "실내",
                dadEnergyLevel = "보통",
                steps = "노란 우산과 장화 신고 빗소리 들으며 동네 도서관 이동||도서관 유아실 코너에서 재미있는 그림책 3권 골라주기||책 그림 보며 소곤소곤 아빠 목소리로 재미있게 읽기||도서관 앞 간식 가판대에서 따뜻한 붕어빵 사서 하하 호호 먹기",
                preparation = "도서 대여 에코백, 우산, 장화",
                expectedCost = "8,000원 내외",
                isDefault = true
            ),
            CourseTemplate(
                title = "공원 보물찾기 + 야외 돗자리 피크닉",
                ageGroup = "초등 저학년",
                durationType = "반나절",
                budgetType = "무료",
                placeType = "자연",
                dadEnergyLevel = "보통",
                steps = "종이에 낙엽, 특이한 돌멩이, 은행잎 등 수집미션 적기||인근 공원 산책하며 아빠와 아이가 경쟁하듯 보물찾기 미션 수행||잔디밭 그늘 아래에 돗자리 펴고 누워 바람 맞으며 고요한 하늘 감상||집에서 싸온 보리차와 간편 도시락(또는 편의점 삼각김밥) 냠냠",
                preparation = "돗자리, 보물찾기 리스트 종이, 필기구, 빈 생수병",
                expectedCost = "무료 (김밥 제외)",
                isDefault = true
            ),
            CourseTemplate(
                title = "아빠는 눕방 수비수, 아이는 무한 슈팅 스타",
                ageGroup = "초등 저학년",
                durationType = "반나절",
                budgetType = "3만원 이하",
                placeType = "실외",
                dadEnergyLevel = "에너지 있음",
                steps = "동네 잔디밭이나 보도블록 넓은 공터 찾아가기||축구공 또는 원반던지기로 숨차게 놀이 시작하기||아빠는 골라인 중앙에 자리 잡고 누워서 손발로만 골키퍼 행동하기(체력 아끼기 최고)||편의점에 들러 좋아하는 주스와 컵라면으로 아빠-자녀 단짝 교감 나누기",
                preparation = "축구공 또는 원반, 이온음료",
                expectedCost = "5,000원 이하",
                isDefault = true
            ),
            CourseTemplate(
                title = "아빠 수면 보충용 드로잉 라이프 카페",
                ageGroup = "유치원",
                durationType = "1시간",
                budgetType = "3만원 이하",
                placeType = "식사 포함",
                dadEnergyLevel = "피곤함",
                steps = "아이 마음에 드는 색칠 공부 책과 mini 크레크레파스 준비||한적한 아파트 앞 조용한 디저트 카페 동반 입장||아이는 과일 주스, 아빠는 아이스 아메리카노 주문 후 아이에게 스케치북 전달||아이가 색칠에 초휘둥그레 집중하는 사이 아빠는 조용히 눈 붙이거나 스마트폰 휴식 취하기||카페 나선 길 아파트 가로수길 10분 도보 산책하며 바람 쐬고 복귀",
                preparation = "마음에 드는 애니 보석 스티커/색칠책, 미니 무독성 크레파스",
                expectedCost = "12,000원 선",
                isDefault = true
            ),
            CourseTemplate(
                title = "비밀의 이불 텐트 특공 요새 작전",
                ageGroup = "영유아",
                durationType = "1시간",
                budgetType = "무료",
                placeType = "실내",
                dadEnergyLevel = "피곤함",
                steps = "거실 소파와 높이 있는 식탁 의자를 멀찌감치 기둥으로 삼기||가장 큰 안방 극세사 이불을 머리 위로 펼쳐 지붕 기지 텐트 빌드하기||집의 모든 플래시(스마트폰 포함)를 비밀리에 켜서 오붓하게 요새 속으로 입성||아이가 제일 좋아하는 구연동화 듣거나 그림자놀이 10분 즐기기",
                preparation = "넓은 이불, 빨래 크립(고정용), 스마트폰 플래시",
                expectedCost = "무료",
                isDefault = true
            ),
            CourseTemplate(
                title = "동네 소품샵 문방구 투어 & 네컷 커플룩 촬영",
                ageGroup = "초등 고학년",
                durationType = "반나절",
                budgetType = "5만원 이하",
                placeType = "체험",
                dadEnergyLevel = "에너지 있음",
                steps = "동네 소문난 캐릭터 굿즈샵이나 아기자기한 팬시샵 가기||우리 아이 시선 강탈 스티커나 미니 메모장 예쁜 걸로 한 개 깜짝 선물하기||오락실로 이동하여 추억 속 레트로 격투 오락 혹은 공 던지기 2판 내기 하기||소형 네컷 사진부스 찾아 우스꽝스러운 안경과 소품 가발 쓰고 아빠-자녀 인생샷 찍기",
                preparation = "현금 천원짜리 서너 장(오락실 구동용)",
                expectedCost = "22,000원 내외",
                isDefault = true
            ),
            CourseTemplate(
                title = "아빠 역사 가이드 투어 + 시장 도넛 격파",
                ageGroup = "초등 고학년",
                durationType = "반나절",
                budgetType = "5만원 이하",
                placeType = "자연",
                dadEnergyLevel = "보통",
                steps = "도보로 이동 가능한 동네 전통 박물관 또는 역사 문화공원 입장||박물관 투어하며 전시물에 대단한 것 마냥 아빠표 픽션 덧붙이기||근방 오래된 전통시장 나들이 도보 이동||시장 대표 수제 꽈배기, 핫바, 달달 시원한 식혜 먹거리 격파",
                preparation = "편안한 신발, 시장용 카드 혹은 소액 현금",
                expectedCost = "15,000원 내외",
                isDefault = true
            )
        )
    }
}
