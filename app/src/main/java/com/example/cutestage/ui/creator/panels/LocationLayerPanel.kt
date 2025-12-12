package com.example.cutestage.ui.creator.panels

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cutestage.stage.beat.LayeredBeat
import com.example.cutestage.stage.beat.StageLocation

/**
 * 장소 레이어 패널
 *
 * 가로 스크롤 장소 선택 + 선택된 장소 미리보기
 */
@Composable
fun LocationLayerPanel(
    beat: LayeredBeat,
    beatIndex: Int,
    onLocationChange: (StageLocation) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))  // ✅ 상단 여백 추가

        // 장소 선택 가로 스크롤
        Text(
            "장소 선택",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(StageLocation.values().toList()) { location ->
                LocationCard(
                    location = location,
                    isSelected = beat.locationLayer.location == location,
                    onClick = { onLocationChange(location) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 선택된 장소 미리보기
        LocationPreview(
            location = beat.locationLayer.location
        )
    }
}

/**
 * 선택된 장소 미리보기
 */
@Composable
private fun LocationPreview(
    location: StageLocation
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "선택된 장소",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // 배경 이미지
                Image(
                    painter = painterResource(location.backgroundRes),
                    contentDescription = location.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 하단 정보 오버레이
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = location.emoji,
                            style = MaterialTheme.typography.displaySmall
                        )
                        Column {
                            Text(
                                text = location.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "현재 비트의 배경으로 설정됨",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationCard(
    location: StageLocation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)  // ✅ 가로 스크롤용 고정 너비
            .height(100.dp)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(
                    3.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = location.emoji,
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = location.displayName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ==================== 프리뷰 ====================

@Preview(showBackground = true)
@Composable
private fun LocationCardPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LocationCard(
                location = StageLocation.STAGE_FLOOR,
                isSelected = false,
                onClick = {}
            )
            LocationCard(
                location = StageLocation.PARK,
                isSelected = true,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
private fun LocationLayerPanelPreview() {
    MaterialTheme {
        LocationLayerPanel(
            beat = LayeredBeat(
                id = "preview",
                name = "Preview Beat",
                duration = 5f
            ),
            beatIndex = 0,
            onLocationChange = {}
        )
    }
}
