package com.jeiel85.daddyweekend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeiel85.daddyweekend.data.model.SavedCourse
import com.jeiel85.daddyweekend.ui.component.*
import com.jeiel85.daddyweekend.ui.theme.*
import com.jeiel85.daddyweekend.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToConditions: () -> Unit,
    onNavigateToResults: (String, String, String, String, String) -> Unit,
    onNavigateToMyCourses: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSavedDetail: (Int) -> Unit
) {
    val activeAgeGroup by viewModel.selectedAgeGroup.collectAsStateWithLifecycle()
    val activeEnergy by viewModel.selectedDadEnergy.collectAsStateWithLifecycle()
    val savedCourses by viewModel.savedCourses.collectAsStateWithLifecycle()

    val ageGroups = listOf("영유아", "유치원", "초등 저학년", "초등 고학년")
    val energyLevels = listOf("낮음", "보통", "충분함") // Map standard energy options

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_content")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Welcoming Card
        item {
            CourseHeader(
                title = "주말 아빠 코스",
                subtitle = "아이와의 소중한 주말, 아빠 고민을 해결합니다.",
                icon = Icons.Default.Weekend
            )
        }

        // Parent Helper "이번 주말 뭐하지?" Filter Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_filter_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = CardStrokeDefaults.borderStroke()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "⚡ 이번 주말 뭐하지? (빠른 탐색)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 1. Age Choice
                    Column {
                        Text(
                            text = "아이 연령 선택",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ageGroups.forEach { age ->
                                val isSelected = age == activeAgeGroup
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .clickable { viewModel.setAgeGroup(age) }
                                        .padding(vertical = 12.dp)
                                        .testTag("age_select_$age"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = age,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // 2. Dad Energy Choice
                    Column {
                        Text(
                            text = "오늘 아빠 체력 게이지",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            energyLevels.forEach { level ->
                                val isSelected = level == activeEnergy
                                val itemColor = when (level) {
                                    "낮음" -> MaterialTheme.colorScheme.error.copy(alpha = if (isSelected) 1f else 0.15f)
                                    "보통" -> MaterialTheme.colorScheme.secondary.copy(alpha = if (isSelected) 1f else 0.15f)
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = if (isSelected) 1f else 0.15f)
                                }
                                val textColor = if (isSelected) {
                                    if (level == "낮음") MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(itemColor)
                                        .clickable { viewModel.setDadEnergy(level) }
                                        .padding(vertical = 12.dp)
                                        .testTag("energy_select_$level"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = when (level) {
                                                "낮음" -> Icons.Default.Battery2Bar
                                                "보통" -> Icons.Default.Battery5Bar
                                                else -> Icons.Default.BatteryChargingFull
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) textColor else {
                                                when (level) {
                                                    "낮음" -> MaterialTheme.colorScheme.error
                                                    "보통" -> MaterialTheme.colorScheme.secondary
                                                    else -> MaterialTheme.colorScheme.primary
                                                }
                                            },
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = level,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Launch Recommend result button
                    Button(
                        onClick = {
                            // Instant route with default filters chosen
                            onNavigateToResults(
                                activeAgeGroup,
                                "전체", // leniency
                                "전체", // leniency
                                "전체", // leniency
                                activeEnergy
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("quick_recommend_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Celebration,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "지금 자녀 맞춤 추천 코스 보기",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 4. Custom 상세 검색 Button
                    OutlinedButton(
                        onClick = onNavigateToConditions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("detailed_search_button"),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "예산/장소/시간 골라서 정밀 추천",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Saved Recent Courses Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🏡 최근 저장한 가족 코스",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToMyCourses) {
                    Text(
                        text = "전체보기",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (savedCourses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = CardStrokeDefaults.borderStroke()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentLate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "아직 저장한 코스가 없습니다.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "추천 코스를 둘러보고 아빠 마음에 드는 일정을 책갈피 하세요!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Display top 2 recently saved
            val recent = savedCourses.take(2)
            items(recent) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSavedDetail(course.id) }
                        .testTag("recent_saved_item_${course.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = CardStrokeDefaults.borderStroke()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (course.wantToRepeat) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = course.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                StatusBadge(text = course.ageGroup, category = BadgeCategory.AGE)
                                StatusBadge(text = course.durationType, category = BadgeCategory.DURATION)
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Detail",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // Fast Quick Shortcut Menu Grid
        item {
            Text(
                text = "📁 빠른 바로가기 메뉴",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Templates list card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTemplates() }
                        .testTag("goto_templates_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "코스 템플릿 목록",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "8대 추천 모음",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                // Settings card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSettings() }
                        .testTag("goto_settings_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "기본값 & 설정",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "앱 초기화 및 기본 정보",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Supportive word to relieve guilt
        item {
            DadsTipCard(modifier = Modifier.padding(top = 10.dp))
        }
    }
}
