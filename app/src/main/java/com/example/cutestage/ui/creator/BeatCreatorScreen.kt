package com.example.cutestage.ui.creator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cutestage.stage.CharacterGender
import com.example.cutestage.stage.beat.*

/**
 * Beat 기반 시나리오 생성 화면
 *
 * 구성:
 * - 상단: 캐릭터 설정
 * - 중앙: 타임라인 (Beat 시퀀스)
 * - 하단: Beat 템플릿 팔레트
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeatCreatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: BeatCreatorViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Beat 시나리오 생성",
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
                    // 미리보기 버튼
                    IconButton(
                        onClick = { viewModel.showPreview() },
                        enabled = state.beats.isNotEmpty()
                    ) {
                        Icon(Icons.Default.PlayArrow, "미리보기")
                    }
                    // 저장 버튼
                    IconButton(
                        onClick = { viewModel.showSaveDialog() },
                        enabled = state.beats.isNotEmpty()
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
            // 캐릭터 설정 영역 (10%)
            CharacterSetupSection(
                characters = state.characters,
                onAddCharacter = { viewModel.showAddCharacterDialog() },
                onRemoveCharacter = { viewModel.removeCharacter(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.15f)
            )

            Divider()

            // 타임라인 영역 (35%)
            BeatTimelineSection(
                beats = state.beats,
                onRemove = { viewModel.removeBeat(it) },
                onReorder = { from, to -> viewModel.reorderBeats(from, to) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
            )

            Divider()

            // Beat 템플릿 팔레트 영역 (50%)
            BeatTemplatePaletteSection(
                characters = state.characters,
                selectedCategory = state.selectedTemplateCategory,
                onCategorySelect = { viewModel.selectTemplateCategory(it) },
                onTemplateSelect = { template -> viewModel.addBeat(template) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            )
        }

        // 캐릭터 추가 다이얼로그
        if (state.showAddCharacterDialog) {
            BeatCreatorAddCharacterDialog(
                onDismiss = { viewModel.dismissAddCharacterDialog() },
                onAdd = { name, gender -> viewModel.addCharacter(name, gender) }
            )
        }

        // 저장 다이얼로그
        if (state.showSaveDialog) {
            SaveBeatScenarioDialog(
                title = state.saveDialogTitle,
                description = state.saveDialogDescription,
                beatCount = state.beats.size,
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
 * 캐릭터 설정 섹션
 */
@Composable
private fun CharacterSetupSection(
    characters: List<CharacterInfo>,
    onAddCharacter: () -> Unit,
    onRemoveCharacter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "👥 등장인물",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onAddCharacter,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, "캐릭터 추가", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (characters.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+ 버튼을 눌러 캐릭터를 추가하세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(characters) { character ->
                        CharacterChip(
                            character = character,
                            onRemove = { onRemoveCharacter(character.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 캐릭터 칩
 */
@Composable
private fun CharacterChip(
    character: CharacterInfo,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (character.gender == CharacterGender.MALE)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (character.gender == CharacterGender.MALE) "♂" else "♀",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = character.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    "제거",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Beat 타임라인 섹션
 */
@Composable
private fun BeatTimelineSection(
    beats: List<Beat>,
    onRemove: (Int) -> Unit,
    onReorder: (Int, Int) -> Unit,
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
                    "${beats.size}개 비트 (약 ${beats.sumOf { it.duration.toInt() }}초)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (beats.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "아래에서 Beat 템플릿을 선택하여\n타임라인에 추가하세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(beats) { index, beat ->
                        BeatCard(
                            beat = beat,
                            index = index,
                            onRemove = { onRemove(index) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Beat 카드
 */
@Composable
private fun BeatCard(
    beat: Beat,
    index: Int,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(120.dp),
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
                    text = "#${index + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )

                // Beat 이름
                Text(
                    text = beat.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Beat 정보
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "${beat.layers.characters.size}명",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${beat.duration.toInt()}초",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
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
 * Beat 템플릿 팔레트 섹션
 */
@Composable
private fun BeatTemplatePaletteSection(
    characters: List<CharacterInfo>,
    selectedCategory: BeatTemplateCategory,
    onCategorySelect: (BeatTemplateCategory) -> Unit,
    onTemplateSelect: (Beat) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 카테고리 탭
        ScrollableTabRow(
            selectedTabIndex = BeatTemplateCategory.values().indexOf(selectedCategory),
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp
        ) {
            BeatTemplateCategory.values().forEach { category ->
                Tab(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelect(category) },
                    text = {
                        Text(text = "${category.emoji} ${category.displayName}")
                    }
                )
            }
        }

        Divider()

        // 템플릿 리스트
        if (characters.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "먼저 캐릭터를 추가해주세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val templates = generateTemplatesForCategory(selectedCategory, characters)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(templates) { template ->
                    BeatTemplateCard(
                        template = template,
                        onClick = { onTemplateSelect(template) }
                    )
                }
            }
        }
    }
}

/**
 * Beat 템플릿 카드
 */
@Composable
private fun BeatTemplateCard(
    template: Beat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 템플릿 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (template.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = template.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 템플릿 상세 정보
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "👥 ${template.layers.characters.size}명",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "⏱️ ${template.duration.toInt()}초",
                        style = MaterialTheme.typography.labelSmall
                    )
                    if (template.layers.dialogues.isNotEmpty()) {
                        Text(
                            text = "💬 ${template.layers.dialogues.size}개",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // 추가 버튼
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "추가",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * 캐릭터 추가 다이얼로그 (Beat Creator용)
 */
@Composable
private fun BeatCreatorAddCharacterDialog(
    onDismiss: () -> Unit,
    onAdd: (String, CharacterGender) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(CharacterGender.MALE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("캐릭터 추가") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("이름") },
                    placeholder = { Text("예: 철수") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text("성별", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedGender == CharacterGender.MALE,
                            onClick = { selectedGender = CharacterGender.MALE },
                            label = { Text("♂ 남성") }
                        )
                        FilterChip(
                            selected = selectedGender == CharacterGender.FEMALE,
                            onClick = { selectedGender = CharacterGender.FEMALE },
                            label = { Text("♀ 여성") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name.trim(), selectedGender)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("추가")
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
 * 저장 다이얼로그
 */
@Composable
private fun SaveBeatScenarioDialog(
    title: String,
    description: String,
    beatCount: Int,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("시나리오 저장") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("제목 *") },
                    placeholder = { Text("예: 첫 만남") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("설명 (선택)") },
                    placeholder = { Text("예: 공원에서의 첫 만남") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "비트 개수: ${beatCount}개",
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
 * 카테고리별 템플릿 생성
 */
private fun generateTemplatesForCategory(
    category: BeatTemplateCategory,
    characters: List<CharacterInfo>
): List<Beat> {
    return when (category) {
        BeatTemplateCategory.MEETING -> {
            if (characters.size >= 2) {
                listOf(
                    BeatTemplates.firstMeeting(characters[0], characters[1]),
                    BeatTemplates.awkwardSilence(characters[0], characters[1])
                )
            } else {
                emptyList()
            }
        }

        BeatTemplateCategory.CONFLICT -> {
            if (characters.size >= 2) {
                listOf(
                    BeatTemplates.confrontation(characters[0], characters[1]),
                    BeatTemplates.stepBack(characters[0])
                )
            } else {
                emptyList()
            }
        }

        BeatTemplateCategory.EMOTION -> {
            if (characters.size >= 2) {
                listOf(
                    BeatTemplates.confession(characters[0], characters[1]),
                    BeatTemplates.celebration(characters.take(3))
                )
            } else {
                emptyList()
            }
        }

        BeatTemplateCategory.FAREWELL -> {
            if (characters.size >= 2) {
                listOf(
                    BeatTemplates.farewell(characters[0], characters[1])
                )
            } else {
                emptyList()
            }
        }

        BeatTemplateCategory.SOLO -> {
            if (characters.isNotEmpty()) {
                listOf(
                    BeatTemplates.entrance(characters[0]),
                    BeatTemplates.monologue(characters[0])
                )
            } else {
                emptyList()
            }
        }
    }
}
