package com.example.cutestage.ui.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * 시나리오 생성 화면
 *
 * 구성:
 * - 상단: 툴바 (뒤로가기, 제목, 저장)
 * - 중앙: 타임라인 (선택된 모듈들)
 * - 하단: 모듈 팔레트 (타입 탭 + 카테고리 + 모듈 카드)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioCreatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: ScenarioCreatorViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val moduleTypes by viewModel.moduleTypes.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val moduleItems by viewModel.moduleItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "시나리오 생성",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                },
                actions = {
                    // 저장 버튼
                    IconButton(
                        onClick = { viewModel.showSaveDialog() },
                        enabled = state.timelineItems.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Check, "저장")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 타임라인 영역 (30%)
            TimelineSection(
                items = state.timelineItems,
                onRemove = { viewModel.removeFromTimeline(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
            )

            Divider()

            // 모듈 팔레트 영역 (70%)
            ModulePaletteSection(
                moduleTypes = moduleTypes,
                categories = categories,
                moduleItems = moduleItems,
                selectedTypeId = state.selectedTypeId,
                selectedCategoryId = state.selectedCategoryId,
                onTypeSelect = { viewModel.selectModuleType(it) },
                onCategorySelect = { viewModel.selectCategory(it) },
                onModuleSelect = { viewModel.selectModuleItem(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            )
        }

        // 저장 다이얼로그
        if (state.showSaveDialog) {
            SaveScenarioDialog(
                title = state.saveDialogTitle,
                description = state.saveDialogDescription,
                moduleCount = state.timelineItems.size,
                onTitleChange = viewModel::updateSaveDialogTitle,
                onDescriptionChange = viewModel::updateSaveDialogDescription,
                onDismiss = viewModel::dismissSaveDialog,
                onSave = {
                    viewModel.saveScenario { _ ->
                        onNavigateBack()
                    }
                },
                isSaving = state.isSaving
            )
        }

        // 언락 다이얼로그
        if (state.showUnlockDialog && state.selectedModuleForUnlock != null) {
            UnlockDialog(
                module = state.selectedModuleForUnlock,
                onDismiss = { viewModel.dismissUnlockDialog() },
                onUnlock = { viewModel.unlockModule(state.selectedModuleForUnlock) }
            )
        }

        // 저장 성공 스낵바
        if (state.showSaveSuccess) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                viewModel.dismissSaveSuccess()
            }
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
 * 타임라인 섹션
 */
@Composable
private fun TimelineSection(
    items: List<TimelineModuleItem>,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🎬 타임라인",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "${items.size}개",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "아래에서 모듈을 선택하여\n타임라인에 추가하세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(items) { index, item ->
                        TimelineModuleCard(
                            item = item,
                            index = index,
                            onRemove = { onRemove(item.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 타임라인 모듈 카드
 */
@Composable
private fun TimelineModuleCard(
    item: TimelineModuleItem,
    index: Int,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 순서 번호
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )

                // 모듈 이름
                Text(
                    text = item.moduleItem.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 모듈 타입
                Text(
                    text = when (item.moduleItem.typeId) {
                        "dialogue" -> "💬 대사"
                        "action" -> "🏃 동작"
                        "scene" -> "🎬 장면"
                        "background" -> "🖼️ 배경"
                        "effect" -> "✨ 효과"
                        else -> item.moduleItem.typeId
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            // 삭제 버튼
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "제거",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * 모듈 팔레트 섹션
 */
@Composable
private fun ModulePaletteSection(
    moduleTypes: List<com.example.cutestage.data.module.ModuleTypeEntity>,
    categories: List<com.example.cutestage.data.module.ModuleCategoryEntity>,
    moduleItems: List<com.example.cutestage.data.module.ModuleItemEntity>,
    selectedTypeId: String?,
    selectedCategoryId: String?,
    onTypeSelect: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onModuleSelect: (com.example.cutestage.data.module.ModuleItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 모듈 타입이 로드되기 전에는 로딩 표시
        if (moduleTypes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        "모듈 로딩 중...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
        }

        // 모듈 타입 탭
        ScrollableTabRow(
            selectedTabIndex = moduleTypes.indexOfFirst { it.id == selectedTypeId }
                .coerceAtLeast(0),
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp
        ) {
            moduleTypes.forEach { type ->
                Tab(
                    selected = type.id == selectedTypeId,
                    onClick = { onTypeSelect(type.id) },
                    text = {
                        Text(
                            text = when (type.id) {
                                "dialogue" -> "💬 ${type.name}"
                                "action" -> "🏃 ${type.name}"
                                "scene" -> "🎬 ${type.name}"
                                "background" -> "🖼️ ${type.name}"
                                "effect" -> "✨ ${type.name}"
                                else -> type.name
                            }
                        )
                    }
                )
            }
        }

        // 카테고리 칩
        if (categories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = category.id == selectedCategoryId,
                        onClick = { onCategorySelect(category.id) },
                        label = { Text(category.name) }
                    )
                }
            }
        }

        Divider()

        // 모듈 아이템 그리드
        if (moduleItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "모듈이 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(moduleItems) { item ->
                    ModuleItemCard(
                        item = item,
                        onClick = { onModuleSelect(item) }
                    )
                }
            }
        }
    }
}

/**
 * 모듈 아이템 카드
 */
@Composable
private fun ModuleItemCard(
    item: com.example.cutestage.data.module.ModuleItemEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPremium)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 모듈 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 사용 횟수
                if (item.usageCount > 0) {
                    Text(
                        text = "사용 ${item.usageCount}회",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 프리미엄 표시 또는 추가 버튼
            if (item.isPremium) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "프리미엄",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${item.unlockCost}토큰",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "추가",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 저장 다이얼로그
 */
@Composable
private fun SaveScenarioDialog(
    title: String,
    description: String,
    moduleCount: Int,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("시나리오 저장")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 제목 입력
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("제목 *") },
                    placeholder = { Text("예: 첫 만남") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 설명 입력
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("설명 (선택)") },
                    placeholder = { Text("예: 공원에서의 첫 만남") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // 정보 표시
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "모듈 개수: ${moduleCount}개",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "예상 재생 시간: 약 ${moduleCount * 3}초",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !isSaving && title.isNotBlank()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("저장")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("취소")
            }
        }
    )
}

/**
 * 언락 다이얼로그
 */
@Composable
private fun UnlockDialog(
    module: com.example.cutestage.data.module.ModuleItemEntity,
    onDismiss: () -> Unit,
    onUnlock: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        title = {
            Text("프리미엄 모듈 언락")
        },
        text = {
            Column {
                Text("'${module.name}' 모듈을 언락하시겠습니까?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "필요 토큰: ${module.unlockCost}개",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "※ 현재는 무료로 언락됩니다 (토큰 시스템 구현 예정)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onUnlock) {
                Text("언락하기")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
