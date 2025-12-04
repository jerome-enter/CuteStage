package com.example.cutestage.stage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Stage 독립 실행 Activity
 *
 * LazyColumn 구조 안에서 StageView를 호출하여
 * 스크롤 가능한 환경에서 연극 무대를 감상할 수 있습니다.
 *
 * 주요 기능:
 * - 여러 시나리오를 순차적으로 배치
 * - 스크롤하여 다양한 무대 감상
 * - 각 무대는 독립적으로 작동
 */
@Composable
fun StageScreen() {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            StageTopBar()
        },
        containerColor = Color(0xFF1A1A1A), // 어두운 배경
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Stage View
            item {
                StageView(
                    script = StageTestScenario.createTestScript(),
                    modifier = Modifier.fillMaxWidth(),
                )
            } // 푸터
            item {
                StageFooter()
            }
        }
    }
}

/**
 * 상단 앱바
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StageTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "🎭 연극 무대",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF2A2A2A),
            titleContentColor = Color.White,
        ),
    )
}

/**
 * 푸터 섹션
 */
@Composable
private fun StageFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Divider(
            color = Color.Gray.copy(alpha = 0.3f),
            modifier = Modifier.padding(vertical = 16.dp),
        )
        Text(
            text = "🎭 Stage View Demo",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "동물의 숲 스타일 음성 엔진 지원",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "캐릭터 애니메이션 & 상호작용",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
        )
    }
}
