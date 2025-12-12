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
    onAddMovement: (String, StagePosition, Float) -> Unit,
    onRemoveMovement: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var selectedCharacterId by remember { mutableStateOf("") }
    var selectedPosition by remember { mutableStateOf(StagePosition.CENTER) }
    var startTime by remember { mutableStateOf(0f) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
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
                    selectedPosition = selectedPosition,
                    startTime = startTime,
                    onCharacterChange = { selectedCharacterId = it },
                    onPositionChange = { selectedPosition = it },
                    onStartTimeChange = { startTime = it },
                    onCancel = {
                        isEditing = false
                        selectedCharacterId = ""
                        selectedPosition = StagePosition.CENTER
                        startTime = 0f
                    },
                    onAdd = {
                        if (selectedCharacterId.isNotEmpty()) {
                            onAddMovement(selectedCharacterId, selectedPosition, startTime)
                            selectedCharacterId = ""
                            selectedPosition = StagePosition.CENTER
                            startTime = 0f
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
    selectedPosition: StagePosition,
    startTime: Float,
    onCharacterChange: (String) -> Unit,
    onPositionChange: (StagePosition) -> Unit,
    onStartTimeChange: (Float) -> Unit,
    onCancel: () -> Unit,
    onAdd: () -> Unit
) {
    var expandedCharacter by remember { mutableStateOf(false) }
    var touchPosition by remember { mutableStateOf(selectedPosition) }

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

        // 위치 선택 (미니맵)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "위치 (터치하여 지정)",
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
                backgroundLocation = backgroundLocation,
                onPositionChange = {
                    touchPosition = it
                    onPositionChange(it)
                }
            )
        }

        // 시작 시간
        Column {
            Text(
                "시작 시간: ${String.format("%.1f", startTime)}초",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = startTime,
                onValueChange = onStartTimeChange,
                valueRange = 0f..10f,
                modifier = Modifier.fillMaxWidth()
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
    }
}
