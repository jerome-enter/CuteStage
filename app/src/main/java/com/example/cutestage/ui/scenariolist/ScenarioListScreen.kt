package com.example.cutestage.ui.scenariolist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cutestage.data.scenario.ScenarioEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * 시나리오 목록 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioListScreen(
    onScenarioClick: (String) -> Unit,    // 재생
    onEditClick: (String) -> Unit,        // 편집
    onCreateNew: () -> Unit,              // 새로 만들기
    onNavigateBack: (() -> Unit)? = null, // 뒤로 가기 (StageScreen으로 복귀)
    viewModel: ScenarioListViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val userScenarios by viewModel.userScenarios.collectAsState()
    val templateScenarios by viewModel.templateScenarios.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "시나리오 선택",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "뒤로 가기")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNew,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, "새로 만들기")
                    Text("새로 만들기")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 시나리오 목록
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 템플릿 섹션
                if (templateScenarios.isNotEmpty()) {
                    item {
                        Text(
                            "📚 기본 제공 시나리오",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                    }

                    items(templateScenarios) { scenario ->
                        ScenarioCard(
                            scenario = scenario,
                            isTemplate = true,
                            onPlayClick = { onScenarioClick(scenario.id) },
                            onEditClick = { }, // 템플릿은 편집 불가
                            onDeleteClick = { } // 템플릿은 삭제 불가
                        )
                    }

                    // 구분선
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // 내 시나리오 섹션
                item {
                    Text(
                        "🎭 내 시나리오",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (userScenarios.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "아직 만든 시나리오가 없어요",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "첫 시나리오를 만들어보세요!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(userScenarios) { scenario ->
                        ScenarioCard(
                            scenario = scenario,
                            isTemplate = false,
                            onPlayClick = { onScenarioClick(scenario.id) },
                            onEditClick = { onEditClick(scenario.id) },
                            onDeleteClick = { viewModel.showDeleteDialog(scenario) }
                        )
                    }
                }
            }
        }

        // 삭제 확인 다이얼로그
        if (state.showDeleteDialog && state.scenarioToDelete != null) {
            DeleteConfirmDialog(
                scenario = state.scenarioToDelete,
                onConfirm = { viewModel.deleteScenario(state.scenarioToDelete.id) },
                onDismiss = { viewModel.dismissDeleteDialog() }
            )
        }

        // 에러 스낵바
        state.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // TODO: Snackbar 표시
                viewModel.clearError()
            }
        }
    }
}

/**
 * 비어있을 때 표시
 */
@Composable
private fun EmptyState(
    onCreateNew: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "🎬",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "아직 시나리오가 없어요",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "첫 시나리오를 만들어보세요!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateNew,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("시나리오 만들기")
        }
    }
}

/**
 * 시나리오 카드
 */
@Composable
private fun ScenarioCard(
    scenario: ScenarioEntity,
    isTemplate: Boolean = false,
    onPlayClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 헤더 (제목 + 메뉴)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isTemplate) "📚 ${scenario.title}" else "📝 ${scenario.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 템플릿이 아닐 때만 메뉴 표시
                if (!isTemplate) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "메뉴",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("✏️ 편집") },
                                onClick = {
                                    showMenu = false
                                    onEditClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🗑️ 삭제") },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // 설명 (있을 경우)
            if (scenario.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scenario.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 정보 (모듈 개수, 날짜, 시간)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "🎬 ${scenario.moduleCount}개 모듈",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "⏱️ 약 ${scenario.estimatedDuration}초",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    formatDate(scenario.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 재생 버튼
            Button(
                onClick = onPlayClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "재생",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("재생하기")
            }
        }
    }
}

/**
 * 삭제 확인 다이얼로그
 */
@Composable
private fun DeleteConfirmDialog(
    scenario: ScenarioEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("시나리오 삭제")
        },
        text = {
            Text("'${scenario.title}'을(를) 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("삭제")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

/**
 * 날짜 포맷팅
 */
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM.dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
