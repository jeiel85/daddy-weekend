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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CourseTemplate
import com.example.ui.component.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateManagerScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val templates by viewModel.templates.collectAsStateWithLifecycle()

    var showAddTemplateDialog by remember { mutableStateOf(false) }
    var editingTemplate by remember { mutableStateOf<CourseTemplate?>(null) }
    var expandedTemplateId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "코스 설계 도서관",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("templates_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTemplateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("add_template_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.LibraryAdd, contentDescription = "추가")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "도감 설계도 추가",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("templates_list"),
                contentPadding = PaddingValues(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "기본 제공되는 명품 추천 템플릿과, 아빠가 손수 설계한 코스 도감들을 모아둔 장소입니다.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Default vs Custom grouping
                val defaults = templates.filter { it.isDefault }
                val customs = templates.filter { !it.isDefault }

                // CUSTOM DESIGN CHANCES
                if (customs.isNotEmpty()) {
                    item {
                        Text(
                            text = "🛠️ 아빠가 커스텀 설계한 코스 (${customs.size}개)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    items(customs, key = { "custom_${it.id}" }) { template ->
                        val isExpanded = expandedTemplateId == template.id
                        TemplateCardItem(
                            template = template,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedTemplateId = if (isExpanded) null else template.id
                            },
                            onEdit = { editingTemplate = template },
                            onDelete = { viewModel.deleteCustomTemplate(template) },
                            onBookmark = { viewModel.saveCourseFromTemplate(template) }
                        )
                    }
                }

                // DEFAULT BUILT-IN TEMPLATES
                item {
                    Text(
                        text = "📖 기본 안심 추천 코스 (${defaults.size}개)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(defaults, key = { "default_${it.id}" }) { template ->
                    val isExpanded = expandedTemplateId == template.id
                    TemplateCardItem(
                        template = template,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedTemplateId = if (isExpanded) null else template.id
                        },
                        onEdit = null, // Disable editing default templates
                        onDelete = null,
                        onBookmark = { viewModel.saveCourseFromTemplate(template) }
                    )
                }
            }
        }

        // Add Template Dialog Form
        if (showAddTemplateDialog) {
            AddEditTemplateDialog(
                onDismiss = { showAddTemplateDialog = false },
                onSave = { title, age, duration, budget, place, energy, steps, prep, cost, memo ->
                    viewModel.addCustomTemplate(
                        title = title,
                        ageGroup = age,
                        durationType = duration,
                        budgetType = budget,
                        placeType = place,
                        dadEnergyLevel = energy,
                        stepsList = steps,
                        preparation = prep,
                        expectedCost = cost,
                        memo = memo
                    )
                    showAddTemplateDialog = false
                }
            )
        }

        // Edit Template Dialog Form
        if (editingTemplate != null) {
            val templateToEdit = editingTemplate!!
            AddEditTemplateDialog(
                existingTemplate = templateToEdit,
                onDismiss = { editingTemplate = null },
                onSave = { title, age, duration, budget, place, energy, steps, prep, cost, memo ->
                    viewModel.updateCustomTemplate(
                        templateToEdit.copy(
                            title = title,
                            ageGroup = age,
                            durationType = duration,
                            budgetType = budget,
                            placeType = place,
                            dadEnergyLevel = energy,
                            steps = steps.filter { it.isNotBlank() }.joinToString("||"),
                            preparation = prep,
                            expectedCost = cost,
                            memo = memo
                        )
                    )
                    editingTemplate = null
                }
            )
        }
    }
}

@Composable
fun TemplateCardItem(
    template: CourseTemplate,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    onBookmark: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("template_card_${template.id}"),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardStrokeDefaults.borderStroke()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(text = template.ageGroup, category = BadgeCategory.AGE)
                    StatusBadge(text = template.dadEnergyLevel, category = BadgeCategory.ENERGY)
                }

                if (template.isDefault) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "공식 추천",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Title line for Expand Collapse action
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = template.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "상세보기",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⏱️ ${template.durationType}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "🪙 ${template.budgetType}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "📍 ${template.placeType}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                    // Timeline checklist steps
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "🧭 미션 예정 행동망",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        val steps = template.steps.split("||").filter { it.isNotBlank() }
                        steps.forEachIndexed { i, s ->
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 8.dp, top = 2.dp)
                                        .size(18.dp)
                                        .background(
                                            MaterialTheme.colorScheme.secondary,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${i + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = s,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    if (template.preparation.isNotBlank() || template.expectedCost.isNotBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (template.preparation.isNotBlank()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "🎒 준비사항",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.outline,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = template.preparation,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            if (template.expectedCost.isNotBlank()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "🪙 예상 경비",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.outline,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = template.expectedCost,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    if (template.memo.isNotBlank()) {
                        Column {
                            Text(
                                text = "💡 아이 저격 꿀팁",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = template.memo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Buttons row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onBookmark,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(46.dp)
                                .testTag("template_bookmark_btn_${template.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("내 일정 도감에 복사")
                        }

                        if (onEdit != null) {
                            IconButton(
                                onClick = onEdit,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .testTag("template_edit_btn_${template.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        if (onDelete != null) {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                    .testTag("template_delete_btn_${template.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = CoralAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Dialog for adding or editing templates
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTemplateDialog(
    existingTemplate: CourseTemplate? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String, age: String, duration: String, budget: String, place: String, energy: String,
        steps: List<String>, prep: String, cost: String, memo: String
    ) -> Unit
) {
    var title by remember { mutableStateOf(existingTemplate?.title ?: "") }
    var stepsRaw by remember { mutableStateOf(existingTemplate?.steps?.split("||")?.joinToString("\n") ?: "") }
    var ageGroup by remember { mutableStateOf(existingTemplate?.ageGroup ?: "유치원") }
    var durationType by remember { mutableStateOf(existingTemplate?.durationType ?: "2~3시간") }
    var budgetType by remember { mutableStateOf(existingTemplate?.budgetType ?: "3만원 이하") }
    var placeType by remember { mutableStateOf(existingTemplate?.placeType ?: "체험") }
    var dadEnergyLevel by remember { mutableStateOf(existingTemplate?.dadEnergyLevel ?: "보통") }
    var preparation by remember { mutableStateOf(existingTemplate?.preparation ?: "") }
    var expectedCost by remember { mutableStateOf(existingTemplate?.expectedCost ?: "") }
    var memo by remember { mutableStateOf(existingTemplate?.memo ?: "") }

    val ageOptions = listOf("영유아", "유치원", "초등 저학년", "초등 고학년")
    val durationOptions = listOf("1시간", "2~3시간", "반나절", "하루")
    val budgetOptions = listOf("무료", "3만원 이하", "5만원 이하", "10만원 이하")
    val placeOptions = listOf("실내", "실외", "집 근처", "자연", "체험", "식사 포함")
    val energyOptions = listOf("피곤함", "보통", "에너지 있음")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val stepsList = stepsRaw.split("\n").filter { it.isNotBlank() }
                        onSave(
                            title, ageGroup, durationType, budgetType, placeType, dadEnergyLevel,
                            stepsList, preparation, expectedCost, memo
                        )
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("dialog_submit_template_btn")
            ) {
                Text(
                    text = if (existingTemplate == null) "설계도 저장" else "설계도 수정",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", style = MaterialTheme.typography.bodyLarge)
            }
        },
        title = {
            Text(
                text = if (existingTemplate == null) "🛠️ 코스 설계 도면 구상" else "✏️ 설계도 수정",
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
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("설계 코스 이름") },
                    placeholder = { Text("예: 한강 자전거 & 컵짜장 데이트") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_template_title_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Newline schedule steps
                OutlinedTextField(
                    value = stepsRaw,
                    onValueChange = { stepsRaw = it },
                    label = { Text("동선 계획 (한 줄에 한 일정)") },
                    placeholder = { Text("따릉이 대여하기\n강바람 맞으며 돗자리 휴식\n편의점 봉지 한강라면 격파") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3
                )

                // Category parameters selection
                Column {
                    Text(text = "아이 매칭 나이구간", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ageOptions.forEach { tag ->
                            val isS = tag == ageGroup
                            FilterChip(
                                selected = isS,
                                onClick = { ageGroup = tag },
                                label = { Text(tag) }
                            )
                        }
                    }
                }

                Column {
                    Text(text = "아빠 희망 체력수위", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        energyOptions.forEach { tag ->
                            val isS = tag == dadEnergyLevel
                            FilterChip(
                                selected = isS,
                                onClick = { dadEnergyLevel = tag },
                                label = { Text(tag) }
                            )
                        }
                    }
                }

                Column {
                    Text(text = "소요 시간", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        durationOptions.forEach { tag ->
                            val isS = tag == durationType
                            FilterChip(
                                selected = isS,
                                onClick = { durationType = tag },
                                label = { Text(tag) }
                            )
                        }
                    }
                }

                Column {
                    Text(text = "비용 등급", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        budgetOptions.forEach { tag ->
                            val isS = tag == budgetType
                            FilterChip(
                                selected = isS,
                                onClick = { budgetType = tag },
                                label = { Text(tag) }
                            )
                        }
                    }
                }

                Column {
                    Text(text = "장소 형태 성격", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        placeOptions.forEach { tag ->
                            val isS = tag == placeType
                            FilterChip(
                                selected = isS,
                                onClick = { placeType = tag },
                                label = { Text(tag) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = preparation,
                    onValueChange = { preparation = it },
                    label = { Text("준비물 목록") },
                    placeholder = { Text("예: 여벌옷, 생수") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = expectedCost,
                    onValueChange = { expectedCost = it },
                    label = { Text("구체적 경비 정보") },
                    placeholder = { Text("예: 8,000원") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("자녀 전용 심쿵 공략포인트") },
                    placeholder = { Text("아이 손 꼭 붙잡고 타기 등의 포인트") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
