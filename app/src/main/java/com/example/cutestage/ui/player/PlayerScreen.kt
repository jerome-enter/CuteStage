package com.example.cutestage.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cutestage.stage.StageView
import com.example.cutestage.stage.StageViewModel

/**
 * 시나리오 재생 화면
 */
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    stageViewModel: StageViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                // 로딩 중
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("시나리오 로딩 중...")
                }
            }

            state.error != null -> {
                // 에러 표시
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "❌",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        state.error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            state.script != null -> {
                // 시나리오 재생
                // StageViewModel에 스크립트 설정
                stageViewModel.setInitialScript(state.script)
                StageView(viewModel = stageViewModel)
            }
        }
    }
}
