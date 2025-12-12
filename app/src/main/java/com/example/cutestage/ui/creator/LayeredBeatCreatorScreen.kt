package com.example.cutestage.ui.creator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cutestage.stage.CharacterGender
import com.example.cutestage.stage.beat.*

/**
 * 레이어 기반 Beat Creator 화면
 *
 * 구조:
 * - 상단: 캐릭터 설정
 * - 중앙 좌측: Beat 타임라인
 * - 중앙 우측: 레이어 편집 패널 (장소/대사/동작/이동)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayeredBeatCreatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: LayeredBeatCreatorViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "레이어 기반 시나리오 생성",
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
            // 캐릭터 설정 (15%)
            CharacterSetupSection(
                characters = state.characters,
                onAddCharacter = { viewModel.showAddCharacterDialog() },
                onShowLibrary = { viewModel.showCharacterLibraryDialog() },
                onRemoveCharacter = { viewModel.removeCharacter(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.18f)
            )

            Divider()

            // 메인 편집 영역 (85%) - 위아래 배치
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.82f)
            ) {
                // 상단: Beat 타임라인 (25%)
                BeatTimelineSection(
                    beats = state.beats,
                    selectedIndex = state.selectedBeatIndex,
                    onSelectBeat = { viewModel.selectBeat(it) },
                    onAddBeat = { viewModel.createNewBeat() },
                    onRemoveBeat = { viewModel.removeBeat(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.25f)
                )

                Divider()

                // 하단: 레이어 편집 패널 (75%)
                LayerEditSection(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f)
                )
            }
        }

        // 다이얼로그들
        if (state.showCharacterLibraryDialog) {
            CharacterLibraryDialog(
                characterLibrary = state.characterLibrary,
                onDismiss = { viewModel.dismissCharacterLibraryDialog() },
                onCharacterSelect = { character ->
                    viewModel.addCharacterFromLibrary(character)
                    viewModel.dismissCharacterLibraryDialog()
                },
                onAddNew = { viewModel.showAddCharacterDialog() }
            )
        }

        if (state.showAddCharacterDialog) {
            AddCharacterDialog(
                onDismiss = { viewModel.dismissAddCharacterDialog() },
                onAdd = { name, gender -> viewModel.addCharacter(name, gender) }
            )
        }

        if (state.showAddDialogueDialog) {
            AddDialogueDialog(
                characters = state.characters,
                editState = state.dialogueEditState,
                onDismiss = { viewModel.dismissAddDialogueDialog() },
                onCharacterChange = { viewModel.updateDialogueEditCharacter(it) },
                onTextChange = { viewModel.updateDialogueEditText(it) },
                onEmotionChange = { viewModel.updateDialogueEditEmotion(it) },
                onActionChange = { viewModel.updateDialogueEditAction(it) },
                onAdd = { viewModel.addDialogue() }
            )
        }

        if (state.showAddMovementDialog) {
            val selectedBeat = state.selectedBeatIndex?.let { state.beats.getOrNull(it) }
            AddMovementDialog(
                characters = state.characters,
                editState = state.movementEditState,
                backgroundLocation = selectedBeat?.locationLayer?.location
                    ?: StageLocation.STAGE_FLOOR,
                onDismiss = { viewModel.dismissAddMovementDialog() },
                onCharacterChange = { viewModel.updateMovementEditCharacter(it) },
                onPositionChange = { viewModel.updateMovementEditPosition(it) },
                onStartTimeChange = { viewModel.updateMovementEditStartTime(it) },
                onAdd = { viewModel.addMovement() }
            )
        }

        if (state.showSaveDialog) {
            SaveLayeredBeatScenarioDialog(
                title = state.saveDialogTitle,
                description = state.saveDialogDescription,
                beatCount = state.beats.size,
                onTitleChange = { viewModel.updateSaveDialogTitle(it) },
                onDescriptionChange = { viewModel.updateSaveDialogDescription(it) },
                onDismiss = { viewModel.dismissSaveDialog() },
                onSave = {
                    viewModel.saveScenario { _ ->
                        onNavigateBack()
                    }
                },
                isSaving = state.isSaving
            )
        }

        // 에러 표시
        state.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // TODO: Snackbar 표시
                viewModel.clearError()
            }
        }
    }
}

// ==================== 캐릭터 설정 섹션 ====================

@Composable
private fun CharacterSetupSection(
    characters: List<CharacterInfo>,
    onAddCharacter: () -> Unit,
    onShowLibrary: () -> Unit,
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
                // 라이브러리 버튼
                IconButton(
                    onClick = onShowLibrary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.List, "라이브러리", tint = MaterialTheme.colorScheme.secondary)
                }
                // 추가 버튼
                IconButton(
                    onClick = onAddCharacter,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, "새로 만들기", tint = MaterialTheme.colorScheme.primary)
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

// ==================== Beat 타임라인 섹션 ====================

@Composable
private fun BeatTimelineSection(
    beats: List<LayeredBeat>,
    selectedIndex: Int?,
    onSelectBeat: (Int) -> Unit,
    onAddBeat: () -> Unit,
    onRemoveBeat: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                    "🎬 비트 타임라인",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "${beats.size}개",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onAddBeat,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, "비트 추가", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (beats.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+ 버튼을 눌러 새 비트를 추가하세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                // 가로 스크롤 타임라인
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(beats) { index, beat ->
                        BeatTimelineCard(
                            beat = beat,
                            index = index,
                            isSelected = index == selectedIndex,
                            onClick = { onSelectBeat(index) },
                            onRemove = { onRemoveBeat(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BeatTimelineCard(
    beat: LayeredBeat,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 비트 번호와 이름
                Column {
                    Text(
                        text = "#${index + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = beat.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 레이어 요약
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LayerSummaryChip("📍", beat.locationLayer.location.emoji)
                    if (beat.dialogueLayer.dialogues.isNotEmpty()) {
                        LayerSummaryChip("💬", "${beat.dialogueLayer.dialogues.size}")
                    }
                    if (beat.movementLayer.movements.isNotEmpty()) {
                        LayerSummaryChip("🚶", "${beat.movementLayer.movements.size}")
                    }
                }

                // 재생 시간
                Text(
                    text = "${String.format("%.1f", beat.calculateDuration())}초",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
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
                    Icons.Default.Close,
                    "제거",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun LayerSummaryChip(icon: String, text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.height(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = icon, style = MaterialTheme.typography.labelSmall)
            Text(text = text, style = MaterialTheme.typography.labelSmall)
        }
    }
}

// ==================== 레이어 편집 섹션 ====================

@Composable
private fun LayerEditSection(
    state: LayeredBeatCreatorState,
    viewModel: LayeredBeatCreatorViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 레이어 탭
        ScrollableTabRow(
            selectedTabIndex = LayerTab.values().indexOf(state.selectedLayerTab),
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp
        ) {
            LayerTab.values().forEach { tab ->
                Tab(
                    selected = tab == state.selectedLayerTab,
                    onClick = { viewModel.selectLayerTab(tab) },
                    text = {
                        Text("${tab.emoji} ${tab.displayName}")
                    }
                )
            }
        }

        Divider()

        // 선택된 Beat가 없으면 안내 메시지
        if (state.selectedBeatIndex == null || state.beats.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "좌측에서 비트를 선택하세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val selectedBeat = state.beats[state.selectedBeatIndex]

            // 선택된 레이어에 따른 패널 표시
            when (state.selectedLayerTab) {
                LayerTab.LOCATION -> LocationLayerPanel(
                    beat = selectedBeat,
                    beatIndex = state.selectedBeatIndex,
                    onLocationChange = { viewModel.updateLocation(state.selectedBeatIndex, it) }
                )

                LayerTab.DIALOGUE -> DialogueLayerPanel(
                    beat = selectedBeat,
                    beatIndex = state.selectedBeatIndex,
                    characters = state.characters,
                    onAddDialogue = { charId, text, emotion, action ->
                        viewModel.addDialogueInline(
                            state.selectedBeatIndex,
                            charId,
                            text,
                            emotion,
                            action
                        )
                    },
                    onRemoveDialogue = { viewModel.removeDialogue(state.selectedBeatIndex, it) }
                )

                LayerTab.MOVEMENT -> MovementLayerPanel(
                    beat = selectedBeat,
                    beatIndex = state.selectedBeatIndex,
                    characters = state.characters,
                    onAddMovement = { viewModel.showAddMovementDialog() },
                    onRemoveMovement = { viewModel.removeMovement(state.selectedBeatIndex, it) }
                )
            }
        }
    }
}

// ==================== 장소 레이어 패널 ====================

@Composable
private fun LocationLayerPanel(
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

// ==================== 대사 레이어 패널 (인라인 편집) ====================

@Composable
private fun DialogueLayerPanel(
    beat: LayeredBeat,
    beatIndex: Int,
    characters: List<CharacterInfo>,
    onAddDialogue: (String, String, DialogueEmotion, DialogueActionType?) -> Unit,
    onRemoveDialogue: (String) -> Unit
) {
    // 인라인 편집 상태 관리
    var isEditing by remember { mutableStateOf(false) }
    var selectedCharacterId by remember { mutableStateOf("") }
    var dialogueText by remember { mutableStateOf("") }
    var selectedEmotion by remember { mutableStateOf(DialogueEmotion.CALM) }
    var selectedAction by remember { mutableStateOf<DialogueActionType?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 타임라인 시각화
        if (beat.dialogueLayer.dialogues.isNotEmpty()) {
            item {
                DialogueTimeline(
                    dialogues = beat.dialogueLayer.dialogues.sortedBy { it.startTime },
                    totalDuration = beat.calculateDuration()
                )
            }
        }

        // 대사 목록
        items(
            items = beat.dialogueLayer.dialogues.sortedBy { it.startTime },
            key = { it.id }
        ) { dialogue ->
            DialogueItemCard(
                dialogue = dialogue,
                onRemove = { onRemoveDialogue(dialogue.id) }
            )
        }

        // [+ 대사 추가] 버튼 또는 입력 폼
        item {
            if (!isEditing) {
                // [+ 대사 추가] 버튼
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("대사 추가")
                }
            } else {
                // 입력 폼
                InlineDialogueEditor(
                    characters = characters,
                    selectedCharacterId = selectedCharacterId,
                    dialogueText = dialogueText,
                    selectedEmotion = selectedEmotion,
                    selectedAction = selectedAction,
                    onCharacterChange = { selectedCharacterId = it },
                    onTextChange = { dialogueText = it },
                    onEmotionChange = { selectedEmotion = it },
                    onActionChange = { selectedAction = it },
                    onCancel = {
                        isEditing = false
                        selectedCharacterId = ""
                        dialogueText = ""
                        selectedEmotion = DialogueEmotion.CALM
                        selectedAction = null
                    },
                    onAdd = {
                        if (selectedCharacterId.isNotEmpty() && dialogueText.isNotEmpty()) {
                            onAddDialogue(selectedCharacterId, dialogueText, selectedEmotion, selectedAction)
                            // 입력 필드 초기화 및 폼 숨기기
                            selectedCharacterId = ""
                            dialogueText = ""
                            selectedEmotion = DialogueEmotion.CALM
                            selectedAction = null
                            isEditing = false
                        }
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InlineDialogueEditor(
    characters: List<CharacterInfo>,
    selectedCharacterId: String,
    dialogueText: String,
    selectedEmotion: DialogueEmotion,
    selectedAction: DialogueActionType?,
    onCharacterChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onEmotionChange: (DialogueEmotion) -> Unit,
    onActionChange: (DialogueActionType?) -> Unit,
    onCancel: () -> Unit,
    onAdd: () -> Unit
) {
    var expandedCharacter by remember { mutableStateOf(false) }

    // 예상 재생 시간 계산
    val estimatedDuration = ((dialogueText.length * 0.15f + 1f) / 1.3f).coerceAtLeast(1.2f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        // LazyColumn 안에 있으므로 verticalScroll 제거
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "새 대사 추가",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            // 캐릭터 선택
            ExposedDropdownMenuBox(
                expanded = expandedCharacter,
                onExpandedChange = { expandedCharacter = it }
            ) {
                OutlinedTextField(
                    value = characters.find { it.id == selectedCharacterId }?.name ?: "선택",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("캐릭터") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCharacter) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedCharacter,
                    onDismissRequest = { expandedCharacter = false }
                ) {
                    characters.forEach { character ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        if (character.gender == CharacterGender.MALE) "♂" else "♀",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(character.name)
                                }
                            },
                            onClick = {
                                onCharacterChange(character.id)
                                expandedCharacter = false
                            }
                        )
                    }
                }
            }

            // 감정 선택
            Column {
                Text(
                    "감정",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 감정 그리드 (스크롤 가능, 테두리)
                // 3x4 그리드로 고정 배치
                val emotions = DialogueEmotion.values().toList()
                val emotionRows = emotions.chunked(3)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    emotionRows.forEach { rowEmotions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowEmotions.forEach { emotion ->
                                val isSelected = selectedEmotion == emotion
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onEmotionChange(emotion) },
                                    label = {
                                        Text(
                                            "${emotion.emoji} ${emotion.displayName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                Color(0xFF666666)
                                        )
                                    },
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderWidth = if (isSelected) 2.dp else 1.dp,
                                        borderColor = if (isSelected) Color.Black else Color(
                                            0xFFCCCCCC
                                        ),
                                        enabled = true,
                                        selected = isSelected
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(32.dp)
                                )
                            }
                            // 마지막 행이 3개 미만이면 빈 공간
                            repeat(3 - rowEmotions.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // 동작 선택
            Column {
                Text(
                    "동작 (선택)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 2개씩 배치
                val actions = DialogueActionType.values().toList()
                val chunked = actions.chunked(2)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    chunked.forEach { rowActions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowActions.forEach { action ->
                                val isSelected = selectedAction == action
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        onActionChange(if (action == DialogueActionType.NONE) null else action)
                                    },
                                    label = {
                                        Text(
                                            "${action.emoji} ${action.displayName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                Color(0xFF666666)
                                        )
                                    },
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderWidth = if (isSelected) 2.dp else 1.dp,
                                        borderColor = if (isSelected) Color.Black else Color(0xFFCCCCCC),
                                        enabled = true,
                                        selected = isSelected
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // 홀수 개일 때 빈 공간
                            if (rowActions.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            OutlinedTextField(
                value = dialogueText,
                onValueChange = onTextChange,
                label = { Text("대사") },
                placeholder = { Text("대사를 입력하세요") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // 예상 재생 시간
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "예상 재생 시간: ${String.format("%.1f", estimatedDuration)}초",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 취소/추가 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("취소")
                }

                Button(
                    onClick = onAdd,
                    modifier = Modifier.weight(1f),
                    enabled = selectedCharacterId.isNotEmpty() && dialogueText.isNotEmpty()
                ) {
                    Text("추가")
                }
            }
        }
    }
}

/**
 * 대사 타임라인 시각화
 */
@Composable
private fun DialogueTimeline(
    dialogues: List<DialogueEntry>,
    totalDuration: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "대사 타임라인",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        // 타임라인 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp)
                )
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            // 시간 눈금
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val divisions = 5
                for (i in 0..divisions) {
                    val x = width * i / divisions
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // 대사 버블들
            dialogues.forEach { dialogue ->
                val duration = dialogue.calculateDuration()
                val startRatio = if (totalDuration > 0) dialogue.startTime / totalDuration else 0f
                val durationRatio = if (totalDuration > 0) duration / totalDuration else 0f

                // 이름 축약 (최대 4글자)
                val displayName = if (dialogue.characterName.length > 4) {
                    dialogue.characterName.take(3) + "…"
                } else {
                    dialogue.characterName
                }

                Surface(
                    modifier = Modifier
                        .offset(
                            x = (startRatio * 100).dp * 2.6f,
                            y = 4.dp
                        )
                        .width((durationRatio * 100).dp * 2.6f)
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 시간 표시
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "0s",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 9.sp
                )
                Text(
                    "${String.format("%.1f", totalDuration)}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun DialogueItemCard(
    dialogue: DialogueEntry,
    onRemove: () -> Unit
) {
    val duration = dialogue.calculateDuration()
    val endTime = dialogue.startTime + duration

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${dialogue.characterName} ${dialogue.emotion.emoji}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "⏱ ${String.format("%.1f", dialogue.startTime)}s ~ ${
                            String.format(
                                "%.1f",
                                endTime
                            )
                        }s",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    dialogue.text,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (dialogue.action != null && dialogue.action != DialogueActionType.NONE) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${dialogue.action.emoji} ${dialogue.action.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
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

// ==================== 이동 레이어 패널 ====================

@Composable
private fun MovementLayerPanel(
    beat: LayeredBeat,
    beatIndex: Int,
    characters: List<CharacterInfo>,
    onAddMovement: () -> Unit,
    onRemoveMovement: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onAddMovement,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("이동 추가")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (beat.movementLayer.movements.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "이동을 추가하세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(beat.movementLayer.movements.sortedBy { it.startTime }) { movement ->
                    val character = characters.find { it.id == movement.characterId }
                    MovementItemCard(
                        movement = movement,
                        characterName = character?.name ?: "Unknown",
                        onRemove = { onRemoveMovement(movement.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MovementItemCard(
    movement: MovementEntry,
    characterName: String,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$characterName 🚶",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${movement.startTime}초",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "위치: (${(movement.position.x * 100).toInt()}%, ${(movement.position.y * 100).toInt()}%)",
                    style = MaterialTheme.typography.bodySmall
                )
                if (movement.autoWalk) {
                    Text(
                        "자동 걷기 애니메이션",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
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
