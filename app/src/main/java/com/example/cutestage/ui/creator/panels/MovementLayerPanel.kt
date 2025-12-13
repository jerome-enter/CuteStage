package com.example.cutestage.ui.creator.panels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cutestage.stage.CharacterGender
import com.example.cutestage.stage.beat.*
import com.example.cutestage.ui.creator.StageMiniMap

/**
 * 이동 레이어 패널
 *
 * 캐릭터 이동 목록 및 인라인 편집기
 */
@Composable
fun MovementLayerPanel(
    beat: LayeredBeat,
    beatIndex: Int,
    characters: List<CharacterInfo>,
    backgroundLocation: StageLocation,
    onAddMovement: (String, StagePosition?, StagePosition, Float, Float) -> Unit, // fromPos, toPos, startTime, endTime
    onRemoveMovement: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var selectedCharacterId by remember { mutableStateOf("") }
    var useManualStart by remember { mutableStateOf(false) }
    var fromPosition by remember { mutableStateOf<StagePosition?>(null) }
    var toPosition by remember { mutableStateOf(StagePosition.CENTER) }
    var startTime by remember { mutableStateOf(0f) }
    var endTime by remember { mutableStateOf(1f) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp),  // ✅ 상단 여백 추가
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = beat.movementLayer.movements.sortedBy { it.startTime },
            key = { it.id }
        ) { movement ->
            val character = characters.find { it.id == movement.characterId }
            MovementItemCard(
                movement = movement,
                characterName = character?.name ?: "Unknown",
                onRemove = { onRemoveMovement(movement.id) }
            )
        }

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
                    Text("이동 추가")
                }
            } else {
                InlineMovementEditor(
                    beat = beat,
                    characters = characters,
                    backgroundLocation = backgroundLocation,
                    selectedCharacterId = selectedCharacterId,
                    useManualStart = useManualStart,
                    fromPosition = fromPosition,
                    toPosition = toPosition,
                    startTime = startTime,
                    endTime = endTime,
                    onCharacterChange = { selectedCharacterId = it },
                    onManualStartChange = { useManualStart = it },
                    onFromPositionChange = { fromPosition = it },
                    onToPositionChange = { toPosition = it },
                    onStartTimeChange = { newStartTime ->
                        startTime = newStartTime
                        // 시작 시간이 끝 시간보다 뒤로 가면 끝 시간도 조정
                        if (newStartTime > endTime) {
                            endTime = newStartTime
                        }
                    },
                    onEndTimeChange = { endTime = it },
                    onCancel = {
                        isEditing = false
                        selectedCharacterId = ""
                        useManualStart = false
                        fromPosition = null
                        toPosition = StagePosition.CENTER
                        startTime = 0f
                        endTime = 1f
                    },
                    onAdd = {
                        if (selectedCharacterId.isNotEmpty()) {
                            val actualFrom = if (useManualStart) fromPosition else null
                            onAddMovement(
                                selectedCharacterId,
                                actualFrom,
                                toPosition,
                                startTime,
                                endTime
                            )
                            selectedCharacterId = ""
                            useManualStart = false
                            fromPosition = null
                            toPosition = StagePosition.CENTER
                            startTime = 0f
                            endTime = 1f
                            isEditing = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MovementItemCard(
    movement: MovementEntry,
    characterName: String,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
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
                        "${movement.startTime}~${movement.endTime}초",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "목표: (${(movement.toPosition.x * 100).toInt()}%, ${(movement.toPosition.y * 100).toInt()}%)",
                    style = MaterialTheme.typography.bodySmall
                )
                if (movement.fromPosition != null) {
                    Text(
                        "시작: (${(movement.fromPosition!!.x * 100).toInt()}%, ${(movement.fromPosition!!.y * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }
                if (movement.autoWalk) {
                    Text(
                        "자동 걷기 애니메이션 · 소요 ${String.format("%.1f", movement.duration())}초",
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

/**
 * 인라인 이동 편집기
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineMovementEditor(
    beat: LayeredBeat,
    characters: List<CharacterInfo>,
    backgroundLocation: StageLocation,
    selectedCharacterId: String,
    useManualStart: Boolean,
    fromPosition: StagePosition?,
    toPosition: StagePosition,
    startTime: Float,
    endTime: Float,
    onCharacterChange: (String) -> Unit,
    onManualStartChange: (Boolean) -> Unit,
    onFromPositionChange: (StagePosition?) -> Unit,
    onToPositionChange: (StagePosition) -> Unit,
    onStartTimeChange: (Float) -> Unit,
    onEndTimeChange: (Float) -> Unit,
    onCancel: () -> Unit,
    onAdd: () -> Unit
) {
    var expandedCharacter by remember { mutableStateOf(false) }
    var touchPosition by remember { mutableStateOf(toPosition) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "새 이동 추가",
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

        // 캐릭터가 선택되었을 때만 나머지 입력 필드 표시
        if (selectedCharacterId.isNotEmpty()) {
            // 시작 위치 (선택적)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = useManualStart,
                onCheckedChange = onManualStartChange
            )
            Text(
                "시작 위치 수동 지정",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 목표 위치 선택 (미니맵)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "목표 위치 (터치하여 지정)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${(touchPosition.x * 100).toInt()}%, ${(touchPosition.y * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 미니맵
            StageMiniMap(
                selectedPosition = touchPosition,
                characterName = characters.find { it.id == selectedCharacterId }?.name,
                backgroundLocation = backgroundLocation,
                onPositionChange = {
                    touchPosition = it
                    onToPositionChange(it)
                }
            )
        }

        // 시작/끝 시간
        Column {
            Text(
                "시작 시간: ${String.format("%.1f", startTime)}초",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = startTime,
                onValueChange = { newStartTime ->
                    onStartTimeChange(newStartTime)
                    // 시작 시간이 끝 시간보다 뒤로 가면 끝 시간을 시작 시간과 같게 조정
                    if (newStartTime > endTime) {
                        onEndTimeChange(newStartTime)
                    }
                },
                valueRange = 0f..10f,
                modifier = Modifier.fillMaxWidth()
            )
        }

            Column {
                Text(
                    "끝 시간: ${String.format("%.1f", endTime)}초",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Slider(
                    value = endTime,
                    onValueChange = onEndTimeChange,
                    valueRange = startTime.coerceAtLeast(0.1f)..10f,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                "이동 소요: ${String.format("%.1f", (endTime - startTime).coerceAtLeast(0f))}초",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // 대사 타임라인 (참고용)
        if (beat.dialogueLayer.dialogues.isNotEmpty()) {
            DialogueTimeline(
                dialogues = beat.dialogueLayer.dialogues.sortedBy { it.startTime },
                totalDuration = beat.calculateDuration()
            )
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
                enabled = selectedCharacterId.isNotEmpty()
            ) {
                Text("추가")
            }
        }
        } // 캐릭터 선택 시에만 보이는 영역 끝
    }
}
