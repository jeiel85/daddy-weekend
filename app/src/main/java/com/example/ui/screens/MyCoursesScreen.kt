package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.SavedCourse
import com.example.ui.component.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCoursesScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val savedCourses by viewModel.savedCourses.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCourse by remember { mutableStateOf<SavedCourse?>(null) }
    var expandedCourseId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "내 가족 코스 첩",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("my_courses_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("add_custom_course_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "등록")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "새 코스 등록",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (savedCourses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "No courses",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "아직 등록되거나 저장된 코스가 없습니다.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "주말 아빠 코스 제안에서 맘에 든 코스를 책갈피 하거나, 우리 가족만의 소수정예 비밀 놀이 실천북을 아래 '새 코스 등록' 버튼을 눌러 채워보세요!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("saved_courses_list"),
                    contentPadding = PaddingValues(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "아이와 함께했던 즐거운 추억과 기대를 기록 중입니다.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    items(savedCourses, key = { it.id }) { course ->
                        val isExpanded = expandedCourseId == course.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("saved_course_card_${course.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (course.wantToRepeat) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                                } else Color.White
                            ),
                            shape = RoundedCornerShape(18.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (course.wantToRepeat) 2.dp else 1.dp,
                                color = if (course.wantToRepeat) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Top status bar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Tags row
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (course.ageGroup.isNotBlank()) {
                                            StatusBadge(
                                                text = course.ageGroup,
                                                category = BadgeCategory.AGE
                                            )
                                        }
                                        if (course.durationType.isNotBlank()) {
                                            StatusBadge(
                                                text = course.durationType,
                                                category = BadgeCategory.DURATION
                                            )
                                        }
                                    }

                                    // Hearts/Star toggle
                                    IconButton(
                                        onClick = {
                                            viewModel.updateSavedCourseNotes(
                                                id = course.id,
                                                rating = course.rating,
                                                memo = course.memo,
                                                wantToRepeat = !course.wantToRepeat
                                            )
                                        },
                                        modifier = Modifier.testTag("repeat_toggle_${course.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (course.wantToRepeat) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Repeat toggle",
                                            tint = if (course.wantToRepeat) CoralAccent else MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                // Interactive Title click for Expand/Collapse
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedCourseId = if (isExpanded) null else course.id
                                        }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = course.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Display ratings star
                                    RatingBar(rating = course.rating)
                                }

                                // Personal Memo section
                                if (course.memo.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                            .padding(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(
                                                imageVector = Icons.Default.NoteAlt,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = course.memo,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // Expanded view detailing Schedule and preparations
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.padding(top = 12.dp)
                                    ) {
                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                                        // Timeline Steps
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text(
                                                text = "🧭 코스 스케줄 안내",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            val steps = course.steps.split("||").filter { it.isNotBlank() }
                                            steps.forEachIndexed { idx, s ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(end = 12.dp, top = 2.dp)
                                                            .size(22.dp)
                                                            .background(
                                                                MaterialTheme.colorScheme.secondary,
                                                                shape = CircleShape
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = "${idx + 1}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    Text(
                                                        text = s,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }

                                        // Additional info rows
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            if (course.preparation.isNotBlank()) {
                                                Text(
                                                    text = "🎒 준비했던 것: ${course.preparation}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            if (course.expectedCost.isNotBlank()) {
                                                Text(
                                                    text = "🪙 총 지출 금액: ${course.expectedCost}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        // Edit & Delete actions
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { editingCourse = course },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .testTag("edit_saved_btn_${course.id}"),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "메모/별점 수정",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            OutlinedButton(
                                                onClick = { viewModel.deleteSavedCourse(course.id) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .testTag("delete_saved_btn_${course.id}"),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = CoralAccent
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    CoralAccent.copy(alpha = 0.5f)
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "코스첩에서 삭제",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Dialog Form
        if (showAddDialog) {
            AddCustomCourseDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, steps, rating, memo, checkRepeat, age, duration, budget, place, energy, prep, cost ->
                    viewModel.saveCustomCourse(
                        title = title,
                        stepsList = steps,
                        rating = rating,
                        memo = memo,
                        wantToRepeat = checkRepeat,
                        ageGroup = age,
                        durationType = duration,
                        budgetType = budget,
                        placeType = place,
                        dadEnergyLevel = energy,
                        preparation = prep,
                        expectedCost = cost
                    )
                    showAddDialog = false
                }
            )
        }

        // Edit Dialog Form
        if (editingCourse != null) {
            val course = editingCourse!!
            EditCourseNotesDialog(
                course = course,
                onDismiss = { editingCourse = null },
                onSave = { rating, memo, wantToRepeat ->
                    viewModel.updateSavedCourseNotes(course.id, rating, memo, wantToRepeat)
                    editingCourse = null
                }
            )
        }
    }
}

// Dialog for registered manual custom app courses
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddCustomCourseDialog(
    onDismiss: () -> Unit,
    onSave: (
        title: String, steps: List<String>, rating: Int, memo: String, wantToRepeat: Boolean,
        age: String, duration: String, budget: String, place: String, energy: String,
        prep: String, cost: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var stepsRaw by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var memo by remember { mutableStateOf("") }
    var wantToRepeat by remember { mutableStateOf(false) }

    var ageGroup by remember { mutableStateOf("유치원") }
    var durationType by remember { mutableStateOf("2~3시간") }
    var budgetType by remember { mutableStateOf("3만원 이하") }
    var placeType by remember { mutableStateOf("체험") }
    var dadEnergyLevel by remember { mutableStateOf("보통") }
    var preparation by remember { mutableStateOf("") }
    var expectedCost by remember { mutableStateOf("") }

    val ageOptions = listOf("영유아", "유치원", "초등 저학년", "초등 고학년")
    val durationOptions = listOf("1시간", "2~3시간", "반나절", "하루")
    val energyOptions = listOf("피곤함", "보통", "에너지 있음")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val stepsList = stepsRaw.split("\n").filter { it.isNotBlank() }
                        onSave(
                            title, stepsList, rating, memo, wantToRepeat,
                            ageGroup, durationType, budgetType, placeType, dadEnergyLevel,
                            preparation, expectedCost
                        )
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("dialog_add_confirm_btn")
            ) {
                Text("기록하기", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", style = MaterialTheme.typography.bodyLarge)
            }
        },
        title = {
            Text(
                text = "✍ 우리 가족 주말 등록",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "직접 즐긴 가족 데이트나, 추후에 떠나볼 꿀맛 플랜 목록을 기록해 두세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("코스 이름") },
                    placeholder = { Text("예: 홍대 책방 투어 데이트") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_add_title_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Newline Steps
                OutlinedTextField(
                    value = stepsRaw,
                    onValueChange = { stepsRaw = it },
                    label = { Text("코스 일정 순서 (한 줄에 하나의 단계)") },
                    placeholder = { Text("문구 완구점 방문\n즉석 짜장면 시식\n하늘공원 산책") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3
                )

                // Ratings Scale
                Column {
                    Text(
                        text = "아빠 마음 점수 (만족도)",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingBar(rating = rating, onRatingChanged = { rating = it })
                }

                // Custom Memo
                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("우리가족 특별 메모") },
                    placeholder = { Text("예: 아이가 핫도그를 아주 사랑했음.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Quick Tag selections
                Column {
                    Text(text = "아이 연령 선택", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ageOptions.forEach { age ->
                            val isSel = ageGroup == age
                            FilterChip(
                                selected = isSel,
                                onClick = { ageGroup = age },
                                label = { Text(age) }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = wantToRepeat,
                        onCheckedChange = { wantToRepeat = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "❤️ 나중에 꼭 다시 갈 코스!",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Options specs inputs
                OutlinedTextField(
                    value = preparation,
                    onValueChange = { preparation = it },
                    label = { Text("필요한 준비물") },
                    placeholder = { Text("예: 돗자리, 현금") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = expectedCost,
                    onValueChange = { expectedCost = it },
                    label = { Text("비용 예산 정보") },
                    placeholder = { Text("예: 25,000원") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

// Dialog for editing saved courses memo / rating notes etc.
@Composable
fun EditCourseNotesDialog(
    course: SavedCourse,
    onDismiss: () -> Unit,
    onSave: (rating: Int, memo: String, wantToRepeat: Boolean) -> Unit
) {
    var rating by remember { mutableIntStateOf(course.rating) }
    var memo by remember { mutableStateOf(course.memo) }
    var wantToRepeat by remember { mutableStateOf(course.wantToRepeat) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onSave(rating, memo, wantToRepeat) },
                modifier = Modifier.testTag("dialog_edit_confirm_btn")
            ) {
                Text("기록 수정", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", style = MaterialTheme.typography.bodyLarge)
            }
        },
        title = {
            Text(
                text = "수정: 주말의 회고록",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "'${course.title}' 코스를 마무리한 후 느낀 점을 간단히 수정해보세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Ratings Scale
                Column {
                    Text(
                        text = "아빠 별점",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingBar(rating = rating, onRatingChanged = { rating = it })
                }

                // Custom Memo
                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("가족 고유 메모") },
                    placeholder = { Text("아이 행동, 좋았던 순간 등 기록...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_edit_memo_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Repeat Star check
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = wantToRepeat,
                        onCheckedChange = { wantToRepeat = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "❤️ 나중에 우리 또 가자! (다시 가고픈 코스 선언)",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
