package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CourseTemplate
import com.example.data.model.FamilyProfile
import com.example.data.model.SavedCourse
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    val profile: StateFlow<FamilyProfile?> = repository.familyProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val templates: StateFlow<List<CourseTemplate>> = repository.allTemplates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedCourses: StateFlow<List<SavedCourse>> = repository.allSavedCourses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Condition States for Quick/Main Filters
    private val _selectedAgeGroup = MutableStateFlow("유치원")
    val selectedAgeGroup: StateFlow<String> = _selectedAgeGroup.asStateFlow()

    private val _selectedDuration = MutableStateFlow("2~3시간")
    val selectedDuration: StateFlow<String> = _selectedDuration.asStateFlow()

    private val _selectedBudget = MutableStateFlow("3만원 이하")
    val selectedBudget: StateFlow<String> = _selectedBudget.asStateFlow()

    private val _selectedPlaceType = MutableStateFlow("체험")
    val selectedPlaceType: StateFlow<String> = _selectedPlaceType.asStateFlow()

    private val _selectedDadEnergy = MutableStateFlow("보통") // 피곤함(낮음), 보통(보통), 에너지 있음(충분함)
    val selectedDadEnergy: StateFlow<String> = _selectedDadEnergy.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndPopulateDefaultTemplates()
            // On startup, pre-load configuration filter states from profile if saved
            profile.collect { prof ->
                prof?.let {
                    _selectedAgeGroup.value = it.childAgeGroup
                    _selectedBudget.value = it.defaultBudget
                    _selectedPlaceType.value = it.preferredActivity
                }
            }
        }
    }

    fun setAgeGroup(age: String) {
        _selectedAgeGroup.value = age
    }

    fun setDuration(duration: String) {
        _selectedDuration.value = duration
    }

    fun setBudget(budget: String) {
        _selectedBudget.value = budget
    }

    fun setPlaceType(place: String) {
        _selectedPlaceType.value = place
    }

    fun setDadEnergy(energy: String) {
        _selectedDadEnergy.value = energy
    }

    // Update global preferences
    fun updateProfilePrefs(age: String, budget: String, activity: String) {
        viewModelScope.launch {
            repository.updateProfile(
                FamilyProfile(
                    childAgeGroup = age,
                    defaultBudget = budget,
                    preferredActivity = activity
                )
            )
        }
    }

    // Save proposed Course Template to My Saved Courses
    fun saveCourseFromTemplate(template: CourseTemplate, customMemo: String = "") {
        viewModelScope.launch {
            repository.saveCourse(
                SavedCourse(
                    templateId = template.id,
                    title = template.title,
                    rating = 5,
                    memo = customMemo.ifEmpty { "아빠가 아이와 주말에 가보고 싶은 코스!" },
                    wantToRepeat = false,
                    steps = template.steps,
                    ageGroup = template.ageGroup,
                    durationType = template.durationType,
                    budgetType = template.budgetType,
                    placeType = template.placeType,
                    dadEnergyLevel = template.dadEnergyLevel,
                    preparation = template.preparation,
                    expectedCost = template.expectedCost
                )
            )
        }
    }

    // Insert direct custom user course template
    fun addCustomTemplate(
        title: String,
        ageGroup: String,
        durationType: String,
        budgetType: String,
        placeType: String,
        dadEnergyLevel: String,
        stepsList: List<String>,
        preparation: String,
        expectedCost: String,
        memo: String
    ) {
        viewModelScope.launch {
            repository.insertTemplate(
                CourseTemplate(
                    title = title,
                    ageGroup = ageGroup,
                    durationType = durationType,
                    budgetType = budgetType,
                    placeType = placeType,
                    dadEnergyLevel = dadEnergyLevel,
                    steps = stepsList.filter { it.isNotBlank() }.joinToString("||"),
                    preparation = preparation,
                    expectedCost = expectedCost,
                    memo = memo,
                    isDefault = false
                )
            )
        }
    }

    // Update existing course template (for custom edit page)
    fun updateCustomTemplate(template: CourseTemplate) {
        viewModelScope.launch {
            repository.insertTemplate(template)
        }
    }

    // Delete custom course template
    fun deleteCustomTemplate(template: CourseTemplate) {
        viewModelScope.launch {
            repository.deleteTemplate(template)
        }
    }

    // Save custom manual course directly to My Courses
    fun saveCustomCourse(
        title: String,
        stepsList: List<String>,
        rating: Int,
        memo: String,
        wantToRepeat: Boolean,
        ageGroup: String,
        durationType: String,
        budgetType: String,
        placeType: String,
        dadEnergyLevel: String,
        preparation: String,
        expectedCost: String
    ) {
        viewModelScope.launch {
            repository.saveCourse(
                SavedCourse(
                    title = title,
                    rating = rating,
                    memo = memo,
                    wantToRepeat = wantToRepeat,
                    steps = stepsList.filter { it.isNotBlank() }.joinToString("||"),
                    ageGroup = ageGroup,
                    durationType = durationType,
                    budgetType = budgetType,
                    placeType = placeType,
                    dadEnergyLevel = dadEnergyLevel,
                    preparation = preparation,
                    expectedCost = expectedCost
                )
            )
        }
    }

    // Update rating/memo of an already saved course
    fun updateSavedCourseNotes(id: Int, rating: Int, memo: String, wantToRepeat: Boolean) {
        viewModelScope.launch {
            val existing = repository.getSavedCourseById(id)
            if (existing != null) {
                repository.saveCourse(
                    existing.copy(
                        rating = rating,
                        memo = memo,
                        wantToRepeat = wantToRepeat
                    )
                )
            }
        }
    }

    // Delete saved course
    fun deleteSavedCourse(id: Int) {
        viewModelScope.launch {
            repository.deleteSavedCourseById(id)
        }
    }

    // Clear all data and reset to initial factory settings
    fun resetAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            // Reset state flows as well
            _selectedAgeGroup.value = "유치원"
            _selectedDuration.value = "2~3시간"
            _selectedBudget.value = "3만원 이하"
            _selectedPlaceType.value = "체험"
            _selectedDadEnergy.value = "보통"
        }
    }

    // Standard high quality filtering logic with fallback
    fun getFilteredRecommendations(
        age: String,
        duration: String,
        budget: String,
        place: String,
        energy: String
    ): FilterResult {
        val all = templates.value
        // Map Home and slider labels safely
        val targetEnergy = when (energy) {
            "낮음", "피곤함" -> "피곤함"
            "충분함", "에너지 있음" -> "에너지 있음"
            else -> "보통"
        }

        // 1. Exact match pass
        val exactMatches = all.filter {
            it.ageGroup == age &&
            it.durationType == duration &&
            it.budgetType == budget &&
            it.placeType == place &&
            it.dadEnergyLevel == targetEnergy
        }
        if (exactMatches.isNotEmpty()) {
            return FilterResult(exactMatches, isExact = true, message = "조건에 부합하는 맞춤 코스입니다.")
        }

        // 2. Soft match 1: Age + Energy exactly, other attributes lenient
        val ageAndEnergyMatches = all.filter {
            it.ageGroup == age && it.dadEnergyLevel == targetEnergy
        }
        if (ageAndEnergyMatches.isNotEmpty()) {
            return FilterResult(
                courses = ageAndEnergyMatches,
                isExact = false,
                message = "조건과 100% 일치하는 코스가 없어, 아이 연령(${age})과 아빠 체력 상태(${energy})에 어울리는 추천 코스를 보여드려요."
            )
        }

        // 3. Soft match 2: Age only
        val ageMatches = all.filter {
            it.ageGroup == age
        }
        if (ageMatches.isNotEmpty()) {
            return FilterResult(
                courses = ageMatches,
                isExact = false,
                message = "희망하시는 코스가 없어, 자녀의 연령인 '${age}' 맞춤형 코스들을 대신 가져왔습니다."
            )
        }

        // 4. Ultimate fallback: Return everything (or first 4)
        return FilterResult(
            courses = all,
            isExact = false,
            message = "일치하는 코스가 없어, 현재 등록된 전체 코스를 소개해 드립니다."
        )
    }
}

data class FilterResult(
    val courses: List<CourseTemplate>,
    val isExact: Boolean,
    val message: String
)

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
