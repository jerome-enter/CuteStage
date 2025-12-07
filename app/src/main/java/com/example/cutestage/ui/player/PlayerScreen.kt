package com.example.cutestage.ui.player

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cutestage.stage.*

/**
 * 사용자 생성 시나리오 재생 화면
 *
 * TimelineItems → TheaterScript 변환 후 StageView로 재생
 * ✅ StageScreen과 동일한 레이아웃 사용
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onScenarioSelectClick: (() -> Unit)? = null,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "제롬 연극부",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            when {
                state.isLoading -> {
                    // 로딩 중
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("시나리오 로딩 중...", color = Color.White)
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
                    // ✅ StageScreen과 동일한 레이아웃
                    UserScenarioStage(
                        script = state.script,
                        onScenarioSelectClick = onScenarioSelectClick
                    )
                }
            }
        }
    }
}

/**
 * 사용자 시나리오 재생 (StageScreen과 동일한 구조)
 */
@Composable
private fun UserScenarioStage(
    script: TheaterScript,
    onScenarioSelectClick: (() -> Unit)?
) {
    // 디버그 로그
    LaunchedEffect(script) {
        Log.d("PlayerScreen", "Script loaded: ${script.scenes.size} scenes")
        script.scenes.forEachIndexed { index, scene ->
            Log.d(
                "PlayerScreen",
                "Scene $index: ${scene.characters.size} chars, ${scene.dialogues.size} dialogues"
            )
        }
    }

    // 등장인물 추출
    val characters = remember(script) {
        extractCharactersFromScript(script)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ✅ StageView (고정 높이)
        key(script) {
            StageView(
                script = script,
                onScenarioSelectClick = onScenarioSelectClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ✅ 등장인물 소개 섹션 (스크롤 영역)
        if (characters.isNotEmpty()) {
            CharacterIntroductionSection(
                characters = characters,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

/**
 * 시나리오에서 등장인물 추출
 */
private fun extractCharactersFromScript(script: TheaterScript): List<CharacterInfo> {
    val characterMap = mutableMapOf<String, CharacterInfo>()

    script.scenes.forEach { scene ->
        scene.characters.forEach { character ->
            if (!characterMap.containsKey(character.id)) {
                val description = character.description ?: when {
                    character.name.contains("상철", ignoreCase = true) ||
                            character.name.contains(
                                "male",
                                ignoreCase = true
                            ) -> "무뚝뚝하지만 따뜻한 마음을 가진 남자"

                    character.name.contains("옥순", ignoreCase = true) ||
                            character.name.contains(
                                "female",
                                ignoreCase = true
                            ) -> "밝고 긍정적인 에너지를 가진 여자"

                    else -> "연극의 주인공"
                }

                characterMap[character.id] = CharacterInfo(
                    id = character.id,
                    name = character.name,
                    gender = character.spriteAnimation?.gender
                        ?: if (character.name.contains(
                                "male",
                                ignoreCase = true
                            )
                        ) CharacterGender.MALE
                        else CharacterGender.FEMALE,
                    imageRes = character.imageRes,
                    description = description
                )
            }
        }
    }

    return characterMap.values.toList()
}

/**
 * 등장인물 정보
 */
data class CharacterInfo(
    val id: String,
    val name: String,
    val gender: CharacterGender,
    val imageRes: Int,
    val description: String
)

/**
 * 등장인물 소개 섹션
 */
@Composable
private fun CharacterIntroductionSection(
    characters: List<CharacterInfo>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "🎭 등장인물",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(characters) { character ->
                CharacterCard(character = character)
            }
        }
    }
}

/**
 * 캐릭터 카드
 */
@Composable
private fun CharacterCard(
    character: CharacterInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 캐릭터 이미지
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (character.gender == CharacterGender.MALE)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                    .border(
                        width = 2.dp,
                        color = if (character.gender == CharacterGender.MALE)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = character.imageRes),
                    contentDescription = character.name,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // 캐릭터 정보
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // 성별 아이콘
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (character.gender == CharacterGender.MALE)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = if (character.gender == CharacterGender.MALE) "♂" else "♀",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (character.gender == CharacterGender.MALE)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = character.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
