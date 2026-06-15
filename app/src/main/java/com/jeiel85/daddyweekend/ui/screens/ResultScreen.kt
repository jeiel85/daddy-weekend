package com.jeiel85.daddyweekend.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeiel85.daddyweekend.data.model.CourseTemplate
import com.jeiel85.daddyweekend.ui.component.*
import com.jeiel85.daddyweekend.ui.theme.*
import com.jeiel85.daddyweekend.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: MainViewModel,
    age: String,
    duration: String,
    budget: String,
    place: String,
    energy: String,
    onNavigateBack: () -> Unit,
    onNavigateToMyCourses: () -> Unit
) {
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val savedCourses by viewModel.savedCourses.collectAsStateWithLifecycle()

    // Calculate recommendations dynamically
    val filterResult = remember(templates, age, duration, budget, place, energy) {
        viewModel.getFilteredRecommendations(age, duration, budget, place, energy)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "아빠를 위한 주말 제안",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("results_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (templates.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val courses = filterResult.courses
            var activeIndex by remember { mutableIntStateOf(0) }
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(courses) {
                // Ensure active index resets if the courses list changes
                activeIndex = 0
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
                ) {
                    // Match type notification message (to relieve guilt and show smart matching!)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (filterResult.isExact) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.40f)
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = CardStrokeDefaults.borderStroke()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (filterResult.isExact) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (filterResult.isExact) FieldGreen else EarthBrown,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = filterResult.message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (filterResult.isExact) DarkGreen else EarthBrown
                                )
                            }
                        }
                    }

                    // Browsing tab indicator if multiple courses match
                    if (courses.size > 1) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "💡 이런 주말 코스는 어떠세요? (총 ${courses.size}개 추천)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    courses.forEachIndexed { idx, item ->
                                        val isActive = idx == activeIndex
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isActive) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                )
                                                .clickable { activeIndex = idx }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "제안 ${idx + 1}",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isActive) MaterialTheme.colorScheme.onPrimary
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Current Selected Recommendation Course Presentation Card
                    if (activeIndex in courses.indices) {
                        val activeCourse = courses[activeIndex]
                        val isAlreadySaved = savedCourses.any { it.title == activeCourse.title }

                        item {
                            CourseDetailDisplay(
                                course = activeCourse,
                                isSaved = isAlreadySaved,
                                onSave = {
                                    viewModel.saveCourseFromTemplate(activeCourse)
                                },
                                onViewSavedCourses = onNavigateToMyCourses
                            )
                        }
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CourseDetailDisplay(
    course: CourseTemplate,
    isSaved: Boolean,
    onSave: () -> Unit,
    onViewSavedCourses: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("course_detail_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardStrokeDefaults.borderStroke()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title block
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "오늘 아빠랑 주말 모험",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Specs badges bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusBadge(text = course.ageGroup, category = BadgeCategory.AGE)
                StatusBadge(text = course.durationType, category = BadgeCategory.DURATION)
                StatusBadge(text = course.dadEnergyLevel, category = BadgeCategory.ENERGY)
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Sequence Timeline
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🧭 모험 일정 코스 순서",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val steps = course.steps.split("||").filter { it.isNotBlank() }
                steps.forEachIndexed { idx, step ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        MaterialTheme.colorScheme.secondary,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (idx < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                        )
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Preparation, Costs & Kids points grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Costs
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = "비용",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "예상 비용",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = course.expectedCost.ifEmpty { "무료 혹은 개인 선택" },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Prep
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Luggage,
                        contentDescription = "준비물",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "아빠 준비물",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = course.preparation.ifEmpty { "가벼운 옷차림, 휴대폰만 있으면 끝!" },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Child Point
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "꿀팁",
                        tint = CoralAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "아이가 좋아할 공감 포인트!",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (course.isDefault) {
                                "아빠가 자기 눈높이에 맞춰 조잘거려 주고 맛난 것을 사줬다는 것 자체에 폭풍 감동을 느낍니다."
                            } else {
                                course.memo.ifEmpty { "아빠와 수수한 추억 하나를 쌓는 즐거움!" }
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions panel
            if (isSaved) {
                Button(
                    onClick = onViewSavedCourses,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("results_saved_completed_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FieldGreen
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.BookmarkAdded, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "저장 완료! '내 가족 코스'로 가기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("save_recommendation_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "이 코스 책갈피에 저장하기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
