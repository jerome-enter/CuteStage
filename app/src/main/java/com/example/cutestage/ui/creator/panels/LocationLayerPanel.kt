package com.example.cutestage.ui.creator.panels

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cutestage.stage.beat.LayeredBeat
import com.example.cutestage.stage.beat.StageLocation

/**
 * 장소 레이어 패널
 *
 * Beat의 배경 장소를 선택하는 그리드 UI
 */
@Composable
fun LocationLayerPanel(
    beat: LayeredBeat,
    beatIndex: Int,
    onLocationChange: (StageLocation) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(StageLocation.values().toList()) { location ->
            LocationCard(
                location = location,
                isSelected = beat.locationLayer.location == location,
                onClick = { onLocationChange(location) }
            )
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
            .fillMaxWidth()
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
