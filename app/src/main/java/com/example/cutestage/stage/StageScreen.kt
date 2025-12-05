package com.example.cutestage.stage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
 * - StageView는 상단에 고정
 * - bg_bottom.png만 스크롤되는 콘텐츠
 * - bottom.png는 화면 하단에 고정
 * - bg.png를 전체 배경으로 사용
 */
@Composable
fun StageScreen() {
    val listState = rememberLazyListState()

    // Box로 배경 이미지와 콘텐츠를 겹침
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // 검은색 배경 추가
    ) {
        // 콘텐츠 레이아웃
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.top),
                modifier = Modifier.fillMaxWidth(),
                contentDescription = "Top",
                contentScale = ContentScale.Crop // 화면에 꽉 차도록
            )

            // StageView (고정, 스크롤되지 않음)
            StageView(
                script = StageTestScenario.createTestScript(), // PLAYGROUND 시나리오 자동 로드
                modifier = Modifier.fillMaxWidth(),
            )

            // 스크롤 가능한 영역 (bg_bottom만 스크롤)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 남은 공간 전부 사용
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 70.dp), // bottom.png 높이만큼 여백
                    verticalArrangement = Arrangement.Bottom, // 하단부터 콘텐츠 배치
                ) {
                    // 하단 배경 이미지 (스크롤되는 콘텐츠)
                    item {
                        Image(
                            painter = painterResource(id = R.drawable.bg_bottom),
                            contentDescription = "Bottom Background",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth // 가로는 꽉 채우고 세로는 비율 유지
                        )
                    }
                }
            }
        }

        // 화면 하단에 고정되는 bottom.png (스크롤되지 않음)
        Image(
            painter = painterResource(id = R.drawable.bottom),
            contentDescription = "Fixed Bottom",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth // 가로는 꽉 채우고 세로는 비율 유지
        )
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
