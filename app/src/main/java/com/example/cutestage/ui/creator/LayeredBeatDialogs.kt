package com.example.cutestage.ui.creator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cutestage.stage.CharacterGender
import com.example.cutestage.stage.beat.*

/**
 * 캐릭터 추가 다이얼로그
 */
@Composable
fun AddCharacterDialog(
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
                        // 남성
                        FilterChip(
                            selected = selectedGender == CharacterGender.MALE,
                            onClick = { selectedGender = CharacterGender.MALE },
                            label = {
                                Text(
                                    "♂ 남성",
                                    fontWeight = if (selectedGender == CharacterGender.MALE) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedGender == CharacterGender.MALE)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        Color(0xFF666666)
                                )
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (selectedGender == CharacterGender.MALE) 2.dp else 1.dp,
                                color = if (selectedGender == CharacterGender.MALE)
                                    Color.Black
                                else
                                    Color(0xFFCCCCCC)
                            )
                        )
                        // 여성
                        FilterChip(
                            selected = selectedGender == CharacterGender.FEMALE,
                            onClick = { selectedGender = CharacterGender.FEMALE },
                            label = {
                                Text(
                                    "♀ 여성",
                                    fontWeight = if (selectedGender == CharacterGender.FEMALE) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedGender == CharacterGender.FEMALE)
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    else
                                        Color(0xFF666666)
                                )
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (selectedGender == CharacterGender.FEMALE) 2.dp else 1.dp,
                                color = if (selectedGender == CharacterGender.FEMALE)
                                    Color.Black
                                else
                                    Color(0xFFCCCCCC)
                            )
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
 * 대사 추가 다이얼로그
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialogueDialog(
    characters: List<CharacterInfo>,
    editState: DialogueEditState,
    onDismiss: () -> Unit,
    onCharacterChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onEmotionChange: (DialogueEmotion) -> Unit,
    onActionChange: (DialogueActionType?) -> Unit,
    onAdd: () -> Unit
) {
    // 예상 재생 시간 계산 (1.3배 빠르게)
    val estimatedDuration = ((editState.text.length * 0.15f + 1f) / 1.3f).coerceAtLeast(1.2f)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("대사 추가") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 캐릭터 선택
                item {
                    Column {
                        Text("캐릭터", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = editState.selectedCharacterName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("선택") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                characters.forEach { character ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${if (character.gender == CharacterGender.MALE) "♂" else "♀"} ${character.name}"
                                            )
                                        },
                                        onClick = {
                                            onCharacterChange(character.id)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // 대사 입력
                item {
                    OutlinedTextField(
                        value = editState.text,
                        onValueChange = onTextChange,
                        label = { Text("대사") },
                        placeholder = { Text("예: 안녕하세요!") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 감정 선택
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("감정", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "↓ 스크롤 가능",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Box {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .height(120.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                items(DialogueEmotion.values().toList()) { emotion ->
                                    FilterChip(
                                        selected = editState.emotion == emotion,
                                        onClick = { onEmotionChange(emotion) },
                                        label = {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    emotion.emoji,
                                                    fontWeight = if (editState.emotion == emotion) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(
                                                    emotion.displayName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (editState.emotion == emotion) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (editState.emotion == emotion)
                                                        MaterialTheme.colorScheme.onSecondaryContainer
                                                    else
                                                        Color(0xFF666666)
                                                )
                                            }
                                        },
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (editState.emotion == emotion) 2.dp else 1.dp,
                                            color = if (editState.emotion == emotion)
                                                Color.Black
                                            else
                                                Color(0xFFCCCCCC)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // 하단 페이드 그라디언트 (스크롤 힌트)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }

                // 동작 선택 (옵션)
                item {
                    Column {
                        Text("동작 (선택)", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        // 여러 줄로 자동 배치
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val actions = DialogueActionType.values().toList()
                            val chunked = actions.chunked(2) // 한 줄에 2개씩

                            chunked.forEach { rowActions ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    rowActions.forEach { action ->
                                        FilterChip(
                                            selected = editState.action == action,
                                            onClick = { onActionChange(if (editState.action == action) null else action) },
                                            label = {
                                                Text(
                                                    "${action.emoji} ${action.displayName}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (editState.action == action) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (editState.action == action)
                                                        MaterialTheme.colorScheme.onTertiaryContainer
                                                    else
                                                        Color(0xFF666666)
                                                )
                                            },
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (editState.action == action) 2.dp else 1.dp,
                                                color = if (editState.action == action)
                                                    Color.Black
                                                else
                                                    Color(0xFFCCCCCC)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    // 빈 공간 채우기
                                    if (rowActions.size < 2) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                // 예상 재생 시간 표시
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⏱️", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "예상 재생 시간: ${String.format("%.1f", estimatedDuration)}초",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAdd,
                enabled = editState.selectedCharacterId.isNotEmpty() && editState.text.isNotEmpty()
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
 * 이동 추가 다이얼로그 (미니맵 방식)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovementDialog(
    characters: List<CharacterInfo>,
    editState: MovementEditState,
    backgroundLocation: StageLocation = StageLocation.STAGE_FLOOR,
    onDismiss: () -> Unit,
    onCharacterChange: (String) -> Unit,
    onPositionChange: (StagePosition) -> Unit,
    onStartTimeChange: (Float) -> Unit,
    onAdd: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("이동 추가") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 캐릭터 선택
                Column {
                    Text("캐릭터", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    var expanded by remember { mutableStateOf(false) }
                    val selectedCharacter =
                        characters.find { it.id == editState.selectedCharacterId }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCharacter?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("선택") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            characters.forEach { character ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${if (character.gender == CharacterGender.MALE) "♂" else "♀"} ${character.name}"
                                        )
                                    },
                                    onClick = {
                                        onCharacterChange(character.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 미니맵 방식 위치 선택
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("위치 (터치하여 지정)", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "(${(editState.position.x * 100).toInt()}%, ${(editState.position.y * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // 미니 스테이지 맵 (배경 이미지 포함)
                    StageMiniMap(
                        selectedPosition = editState.position,
                        backgroundLocation = backgroundLocation,
                        onPositionChange = onPositionChange
                    )
                }

                // 시작 시간
                Column {
                    Text(
                        "시작 시간: ${editState.startTime}초",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Slider(
                        value = editState.startTime,
                        onValueChange = onStartTimeChange,
                        valueRange = 0f..10f,
                        steps = 19,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAdd,
                enabled = editState.selectedCharacterId.isNotEmpty()
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
 * 스테이지 미니맵 (터치로 위치 지정)
 * StageView와 동일한 크기:
 * - width: fillMaxWidth - 20.dp (좌우 패딩 10dp씩)
 * - height: 300.dp
 */
@Composable
internal fun StageMiniMap(
    selectedPosition: StagePosition,
    characterName: String? = null,  // ✅ 선택된 캐릭터 이름
    facingDirection: FacingDirection = FacingDirection.RIGHT,  // ✅ 바라보는 방향
    backgroundLocation: StageLocation = StageLocation.STAGE_FLOOR,
    onPositionChange: (StagePosition) -> Unit
) {
    var localPosition by remember { mutableStateOf(selectedPosition) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()  // ✅ 패딩 제거 - 부모에서 처리
            .height(300.dp)  // StageView와 동일한 높이
            .clip(RoundedCornerShape(16.dp))  // StageView와 동일한 radius
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // 배경 이미지
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(backgroundLocation.backgroundRes),
            contentDescription = "Stage Background",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,  // ✅ StageView와 동일하게 Crop 사용
            modifier = Modifier.fillMaxSize()
        )
        
        // 반투명 오버레이
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
        
        // 배경 격자
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color.Gray.copy(alpha = 0.3f)
            // 수직선 (3등분)
            for (i in 1..2) {
                val x = size.width * i / 3
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            // 수평선 (3등분)
            for (i in 1..2) {
                val y = size.height * i / 3
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // 선택된 위치 표시 (이름 버블 + 핀)
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val pinX = localPosition.x
            val pinY = localPosition.y
            val boxWidth = constraints.maxWidth.toFloat()
            val boxHeight = constraints.maxHeight.toFloat()

            // 핀의 크기 (40dp 원 + 화살표 20dp)
            val pinRadius = 20.dp
            val arrowWidth = 20.dp

            // Row 전체 너비 계산 (왼쪽 화살표 + 핀 + 오른쪽 화살표)
            val totalWidth = when (facingDirection) {
                FacingDirection.LEFT -> arrowWidth + pinRadius * 2 + 4.dp  // 화살표 + 핀 + 간격
                FacingDirection.RIGHT -> pinRadius * 2 + 4.dp + arrowWidth  // 핀 + 간격 + 화살표
            }

            // Column으로 이름과 핀을 수직 배치
            // ✅ 클릭한 위치가 핀의 중앙이 되도록 offset 조정
            Column(
                modifier = Modifier
                    .offset {
                        androidx.compose.ui.unit.IntOffset(
                            x = (pinX * boxWidth - (totalWidth / 2).toPx()).toInt(),  // 중앙 정렬
                            y = (pinY * boxHeight - pinRadius.toPx()).toInt()  // 핀 중심이 클릭 위치
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 1. 캐릭터 이름 버블 (위)
                if (characterName != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 4.dp,
                        modifier = Modifier.offset(y = (-50).dp)  // 전체를 위로
                    ) {
                        Text(
                            text = characterName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 2. 핀 + 화살표 (아래)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.offset(y = (-50).dp)  // 전체를 위로
                ) {
                    // 왼쪽 화살표 (왼쪽 보기일 때)
                    if (facingDirection == FacingDirection.LEFT) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width * 0.7f, size.height * 0.2f)
                                lineTo(size.width * 0.3f, size.height * 0.5f)
                                lineTo(size.width * 0.7f, size.height * 0.8f)
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF6200EE),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 3.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                                )
                            )
                        }
                    }

                    // 핀 원
                    Canvas(modifier = Modifier.size(40.dp)) {
                        // 핀 원
                        drawCircle(
                            color = Color(0xFF6200EE),
                            radius = 20.dp.toPx(),
                            center = Offset(size.width / 2, size.height / 2),
                            alpha = 0.7f
                        )
                        // 핀 중심점
                        drawCircle(
                            color = Color.White,
                            radius = 8.dp.toPx(),
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }

                    // 오른쪽 화살표 (오른쪽 보기일 때)
                    if (facingDirection == FacingDirection.RIGHT) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width * 0.3f, size.height * 0.2f)
                                lineTo(size.width * 0.7f, size.height * 0.5f)
                                lineTo(size.width * 0.3f, size.height * 0.8f)
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF6200EE),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 3.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                                )
                            )
                        }
                    }
                }
            }
        }
        
        // 터치 인식 레이어 (최상단)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // 터치 위치를 0~1 범위로 정규화
                        val x = (offset.x / size.width).coerceIn(0f, 1f)
                        val y = (offset.y / size.height).coerceIn(0f, 1f)
                        val newPosition = StagePosition(x, y)
                        localPosition = newPosition
                        onPositionChange(newPosition)
                    }
                }
        )
    }
}

/**
 * 캐릭터 라이브러리 다이얼로그
 */
@Composable
fun CharacterLibraryDialog(
    characterLibrary: List<com.example.cutestage.data.character.CharacterLibraryEntity>,
    onDismiss: () -> Unit,
    onCharacterSelect: (com.example.cutestage.data.character.CharacterLibraryEntity) -> Unit,
    onAddNew: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("캐릭터 라이브러리") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 즐겨찾기
                val favorites = characterLibrary.filter { it.isFavorite }
                if (favorites.isNotEmpty()) {
                    item {
                        Text(
                            "⭐ 즐겨찾기",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(favorites) { character ->
                        CharacterLibraryItem(
                            character = character,
                            onClick = { onCharacterSelect(character) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // 최근 사용
                val recent = characterLibrary
                    .filter { !it.isFavorite }
                    .sortedByDescending { it.lastUsedAt }
                    .take(5)
                if (recent.isNotEmpty()) {
                    item {
                        Text(
                            "🕐 최근 사용",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    items(recent) { character ->
                        CharacterLibraryItem(
                            character = character,
                            onClick = { onCharacterSelect(character) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // 전체
                val others = characterLibrary
                    .filter { !it.isFavorite && !recent.contains(it) }
                    .sortedBy { it.name }
                if (others.isNotEmpty()) {
                    item {
                        Text(
                            "📋 전체",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(others) { character ->
                        CharacterLibraryItem(
                            character = character,
                            onClick = { onCharacterSelect(character) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onAddNew()
                onDismiss()
            }) {
                Text("+ 새로 만들기")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        }
    )
}

/**
 * 캐릭터 라이브러리 아이템
 */
@Composable
private fun CharacterLibraryItem(
    character: com.example.cutestage.data.character.CharacterLibraryEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (character.gender == CharacterGender.MALE)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (character.gender == CharacterGender.MALE) "♂" else "♀",
                    style = MaterialTheme.typography.titleMedium
                )
                Column {
                    Text(
                        character.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    character.role?.let { role ->
                        Text(
                            role,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (character.usageCount > 0) {
                Text(
                    "${character.usageCount}회",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 저장 다이얼로그
 * @param isEditMode 편집 모드인지 여부 (true면 덮어쓰기/새로저장, false면 저장만)
 * @param onOverwrite 덮어쓰기 (편집 모드만)
 * @param onSaveAsNew 새로저장 (편집 모드에서는 별도 저장, 신규 모드에서는 기본 저장)
 */
@Composable
fun SaveLayeredBeatScenarioDialog(
    title: String,
    description: String,
    beatCount: Int,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onOverwrite: (() -> Unit)? = null,
    isEditMode: Boolean = false,
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

                Text(
                    "비트 개수: ${beatCount}개",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            if (isEditMode && onOverwrite != null) {
                // 편집 모드: 덮어쓰기 + 새로저장
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onOverwrite,
                        enabled = !isSaving && title.isNotBlank()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("덮어쓰기")
                        }
                    }
                    Button(
                        onClick = onSave,
                        enabled = !isSaving && title.isNotBlank()
                    ) {
                        Text("새로저장")
                    }
                }
            } else {
                // 신규 모드: 저장만
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
