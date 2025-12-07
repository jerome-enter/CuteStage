package com.example.cutestage.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cutestage.stage.StageScreen

/**
 * 메인 화면
 *
 * - StageScreen (연극 재생)
 * - FloatingActionButton (시나리오 생성으로 이동)
 */
@Composable
fun MainScreen(
    onNavigateToCreator: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 기존 StageScreen을 그대로 사용 (자체적으로 Scaffold 포함)
        StageScreen()

        // FAB을 Box 안에 배치 (StageScreen의 Scaffold와 독립적)
        FloatingActionButton(
            onClick = onNavigateToCreator,
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "시나리오 생성"
                )
                Text("시나리오 생성")
            }
        }
    }
}
