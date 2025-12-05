package com.example.cutestage.stage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cutestage.R

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
 * - bg.png를 전체 배경으로 사용
 * - 투명한 상단 앱바
 */
@Composable
fun StageScreen() {
    val listState = rememberLazyListState()

    // Box로 배경 이미지와 콘텐츠를 겹침
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // 화면에 꽉 차도록
        )

        // 하단 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg_bottom),
            contentDescription = "Bottom Background",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth // 가로는 꽉 채우고 세로는 비율 유지
        )

        // Scaffold with transparent background (타이틀 없음)
        Scaffold(
            containerColor = Color.Transparent, // 투명 배경으로 bg.png가 보이도록
        ) { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // 상단 여백 56dp
                item {
                    Spacer(modifier = Modifier.height(5.dp))
                }

                // Stage View
                item {
                    StageView(
                        script = StageTestScenario.createTestScript(), // PLAYGROUND 시나리오 자동 로드
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // 푸터
                item {
                    StageFooter()
                }
            }
        }
    }
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
//        Text(
//            text = "🎭 Stage View Demo",
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color.Gray,
//        )
//        Spacer(modifier = Modifier.height(4.dp))
//        Text(
//            text = "동물의 숲 스타일 음성 엔진 지원",
//            style = MaterialTheme.typography.bodySmall,
//            color = Color.Gray,
//        )
//        Spacer(modifier = Modifier.height(4.dp))
//        Text(
//            text = "캐릭터 애니메이션 & 상호작용",
//            style = MaterialTheme.typography.bodySmall,
//            color = Color.Gray,
//        )
    }
}
