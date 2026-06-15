package com.jeiel85.daddyweekend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
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
import com.jeiel85.daddyweekend.ui.component.*
import com.jeiel85.daddyweekend.ui.theme.*
import com.jeiel85.daddyweekend.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val profileState by viewModel.profile.collectAsStateWithLifecycle()

    var showResetConfirm by remember { mutableStateOf(false) }

    val ageOptions = listOf("영유아", "유치원", "초등 저학년", "초등 고학년")
    val budgetOptions = listOf("무료", "3만원 이하", "5만원 이하", "10만원 이하")
    val activityOptions = listOf("실내", "실외", "집 근처", "자연", "체험", "식사 포함")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "아빠 기본 설정",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        profileState?.let { currentProfile ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .testTag("settings_content"),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
            ) {
                // Header guidance card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "여기에 등록해 둔 값은 앱이 시작되거나 추천 필터를 돌릴 때 자동으로 기본값이 되어 아빠의 소중한 타이핑 시간을 덜어드립니다.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // 1. Def Child Age
                item {
                    SettingsSection(
                        title = "👶 자녀 기본 연령대",
                        options = ageOptions,
                        selected = currentProfile.childAgeGroup,
                        onSelect = {
                            viewModel.updateProfilePrefs(
                                age = it,
                                budget = currentProfile.defaultBudget,
                                activity = currentProfile.preferredActivity
                            )
                        }
                    )
                }

                // 2. Def Budget pref
                item {
                    SettingsSection(
                        title = "🪙 주요 예상 비용대 선호수준",
                        options = budgetOptions,
                        selected = currentProfile.defaultBudget,
                        onSelect = {
                            viewModel.updateProfilePrefs(
                                age = currentProfile.childAgeGroup,
                                budget = it,
                                activity = currentProfile.preferredActivity
                            )
                        }
                    )
                }

                // 3. Def place type pref
                item {
                    SettingsSection(
                        title = "🌲 선호 활동/장소 테마",
                        options = activityOptions,
                        selected = currentProfile.preferredActivity,
                        onSelect = {
                            viewModel.updateProfilePrefs(
                                age = currentProfile.childAgeGroup,
                                budget = currentProfile.defaultBudget,
                                activity = it
                            )
                        }
                    )
                }

                // 4. Reset card action
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CoralAccent.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "⚠️ 데이터 초기화 전방 지대",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CoralAccent
                            )
                            Text(
                                text = "지금껏 저장/기록한 소중한 가족 코스첩, 커스텀 등록 템플릿들이 모두 복구 불가하게 삭제되고, 앱 초기 추천 8종 템플릿 정보로 롤백됩니다.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { showResetConfirm = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("open_reset_dialog_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CoralAccent
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "앱 데이터 완전 초기화 수행",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Wipe Confirm dialog
        if (showResetConfirm) {
            AlertDialog(
                onDismissRequest = { showResetConfirm = false },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetAllData()
                            showResetConfirm = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoralAccent
                        ),
                        modifier = Modifier.testTag("confirm_reset_action_btn")
                    ) {
                        Text("네, 초기화합니다", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirm = false }) {
                        Text("취소", style = MaterialTheme.typography.bodyLarge)
                    }
                },
                title = {
                    Text(
                        text = "정말 초기화 하시겠습니까?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CoralAccent
                    )
                },
                text = {
                    Text(
                        text = "이 작업은 취소할 수 없으며, 모든 가족 추억 기록이 영구 폐기됩니다.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsSection(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEach { option ->
                    val isS = option == selected
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isS) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)
                            )
                            .clickable { onSelect(option) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("setting_chip_$option"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isS) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
