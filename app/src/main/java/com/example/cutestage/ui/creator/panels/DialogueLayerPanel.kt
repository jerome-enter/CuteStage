package com.example.cutestage.ui.creator.panels

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cutestage.stage.CharacterGender
import com.example.cutestage.stage.beat.*

/**
 * 대사 레이어 패널 (인라인 편집)
 *
 * 대사 타임라인, 대사 목록, 인라인 대사 편집기 포함
 */
@Composable
fun DialogueLayerPanel(
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
        contentPadding = PaddingValues(top = 16.dp),  // ✅ 상단 여백 추가
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 타임라인 시각화
        if (beat.dialogueLayer.dialogues.isNotEmpty()) {
            item {
                Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                    DialogueTimeline(
                        dialogues = beat.dialogueLayer.dialogues.sortedBy { it.startTime },
                        totalDuration = beat.calculateDuration()
                    )
                }
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
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("대사 추가")
                }
            } else {
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
                            onAddDialogue(
                                selectedCharacterId,
                                dialogueText,
                                selectedEmotion,
                                selectedAction
                            )
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

/**
 * 대사 타임라인 시각화
 */
@Composable
fun DialogueTimeline(
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

            dialogues.forEach { dialogue ->
                val duration = dialogue.calculateDuration()
                val startRatio = if (totalDuration > 0) dialogue.startTime / totalDuration else 0f
                val durationRatio = if (totalDuration > 0) duration / totalDuration else 0f

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
fun DialogueItemCard(
    dialogue: DialogueEntry,
    onRemove: () -> Unit
) {
    val duration = dialogue.calculateDuration()
    val endTime = dialogue.startTime + duration

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
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

/**
 * 인라인 대사 편집기
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineDialogueEditor(
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
    val estimatedDuration = ((dialogueText.length * 0.15f + 1f) / 1.3f).coerceAtLeast(1.2f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "새 대사 추가",
            style = MaterialTheme.typography.titleSmall,
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
                                    borderColor = if (isSelected) Color.Black else Color(0xFFCCCCCC),
                                    enabled = true,
                                    selected = isSelected
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                            )
                        }
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
                            val isSelected = if (action == DialogueActionType.NONE) {
                                selectedAction == null || selectedAction == DialogueActionType.NONE
                            } else {
                                selectedAction == action
                            }
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
