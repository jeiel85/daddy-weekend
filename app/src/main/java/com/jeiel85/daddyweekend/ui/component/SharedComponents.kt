package com.jeiel85.daddyweekend.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeiel85.daddyweekend.ui.theme.*

@Composable
fun CourseHeader(
    title: String,
    subtitle: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("course_header"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RatingBar(
    rating: Int,
    modifier: Modifier = Modifier,
    onRatingChanged: ((Int) -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarOutline,
                contentDescription = "Star $i",
                tint = if (i <= rating) GoldMuted else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(36.dp)
                    .clickable(enabled = onRatingChanged != null) {
                        onRatingChanged?.invoke(i)
                    }
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    category: BadgeCategory,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (category) {
        BadgeCategory.AGE -> {
            Pair(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                MaterialTheme.colorScheme.primary
            )
        }
        BadgeCategory.DURATION -> {
            Pair(
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                MaterialTheme.colorScheme.tertiary
            )
        }
        BadgeCategory.ENERGY -> {
            val color = when (text) {
                "피곤함", "낮음" -> CoralAccent
                "보통" -> FieldGreen
                else -> Color(0xFF1E88E5) // Blue
            }
            Pair(color.copy(alpha = 0.1f), color)
        }
        BadgeCategory.BUDGET -> {
            Pair(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

enum class BadgeCategory {
    AGE, DURATION, ENERGY, BUDGET
}

@Composable
fun DadsTipCard(
    modifier: Modifier = Modifier
) {
    val tips = listOf(
        "아빠, 거창하지 않아도 괜찮아요. 자녀에게 가장 소중한 건 넓은 놀이공원이 아니라 아빠의 시선입니다.",
        "체력이 낮을 때는 무리하지 마세요. 집에서 이불 텐트 치고 누워있는 것만으로도 아이에겐 큰 탐험지입니다.",
        "하루에 1시간만 온전히 함께해도 우리 아이의 마음엔 아빠의 사랑이 평생의 우주로 기억됩니다.",
        "아이와 걸을 때는 아빠가 한 걸음 양보해 주세요. 아이의 속도에 맞춰 기어가는 벌레를 관찰하는 것도 좋습니다.",
        "이번 주말, 완벽함을 추구하기보다 웃음 지을 수 있는 따사로운 단 한 조각의 기억을 선물하세요."
    )
    val randomTip = tips.random()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardStrokeDefaults.borderStroke()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = CoralAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "아빠를 위한 응원 한마디",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "\"$randomTip\"",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )
        }
    }
}

object CardStrokeDefaults {
    @Composable
    fun borderStroke() = androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    )
}
