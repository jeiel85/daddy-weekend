package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResults: (String, String, String, String, String) -> Unit
) {
    // Local state for exact filter selection to allow dads to play around before submitting
    var tempAge by remember { mutableStateOf("유치원") }
    var tempDuration by remember { mutableStateOf("2~3시간") }
    var tempBudget by remember { mutableStateOf("3만원 이하") }
    var tempPlace by remember { mutableStateOf("체험") }
    var tempEnergy by remember { mutableStateOf("보통") }

    val ageOptions = listOf("영유아", "유치원", "초등 저학년", "초등 고학년")
    val durationOptions = listOf("1시간", "2~3시간", "반나절", "하루")
    val budgetOptions = listOf("무료", "3만원 이하", "5만원 이하", "10만원 이하")
    val placeOptions = listOf("실내", "실외", "집 근처", "자연", "체험", "식사 포함")
    val energyOptions = listOf("피곤함", "보통", "에너지 있음")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "맞춤 코스 찾기",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("condition_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
            ) {
                // Header help description
                item {
                    Text(
                        text = "오늘 함께 보낼 자녀와의 알맞은 조율 조건을 선택하세요.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 1. Child Age group selection
                item {
                    SelectorSection(
                        title = "👶 아이 연령대",
                        options = ageOptions,
                        selected = tempAge,
                        onSelectedChange = { tempAge = it },
                        tagPrefix = "age"
                    )
                }

                // 2. Dad physical energy limit
                item {
                    SelectorSection(
                        title = "🔋 내 체력 소모 한도",
                        options = energyOptions,
                        selected = tempEnergy,
                        onSelectedChange = { tempEnergy = it },
                        tagPrefix = "energy"
                    )
                }

                // 3. Expected Elapsed time
                item {
                    SelectorSection(
                        title = "⏳ 소요 시간",
                        options = durationOptions,
                        selected = tempDuration,
                        onSelectedChange = { tempDuration = it },
                        tagPrefix = "duration"
                    )
                }

                // 4. Budget
                item {
                    SelectorSection(
                        title = "🪙 여유 예산 기준",
                        options = budgetOptions,
                        selected = tempBudget,
                        onSelectedChange = { tempBudget = it },
                        tagPrefix = "budget"
                    )
                }

                // 5. Place type character
                item {
                    SelectorSection(
                        title = "🌲 코스 장소/성격",
                        options = placeOptions,
                        selected = tempPlace,
                        onSelectedChange = { tempPlace = it },
                        tagPrefix = "place",
                        columns = 3
                    )
                }
            }

            // Bottom Action Bar for finding courses
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        onNavigateToResults(tempAge, tempDuration, tempBudget, tempPlace, tempEnergy)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(58.dp)
                        .navigationBarsPadding() // Protect Gesture System Navbar overlapping
                        .testTag("find_courses_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "조건 맞춤형 가족 코스 탐색",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorSection(
    title: String,
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit,
    tagPrefix: String,
    columns: Int = 2
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardStrokeDefaults.borderStroke(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = columns
            ) {
                options.forEach { option ->
                    val isSelected = option == selected
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize() // accessibility targets min 48dp thickness
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { onSelectedChange(option) }
                            .padding(vertical = 12.dp, horizontal = 12.dp)
                            .testTag("${tagPrefix}_pill_$option"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
