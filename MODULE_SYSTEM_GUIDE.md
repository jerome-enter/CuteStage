# 모듈 시스템 가이드

> CuteStage 모듈식 시나리오 제작 시스템  
> 작성일: 2024  
> 상태: Phase 1 완료 (데이터 레이어)

---

## 📋 목차

1. [개요](#개요)
2. [아키텍처](#아키텍처)
3. [데이터 구조](#데이터-구조)
4. [사용 방법](#사용-방법)
5. [다음 단계](#다음-단계)

---

## 개요

### 완료된 작업 ✅

**Phase 1: 데이터 레이어 구축 완료**

1. **Room Database 설정**
    - `CuteStageDatabase` 생성
    - 모듈 관련 4개 엔티티 정의
    - TypeConverters 구현

2. **모듈 엔티티 구현**
    - `ModuleTypeEntity`: 모듈 타입 (대사, 동작, 장면 등)
    - `ModuleCategoryEntity`: 카테고리 (인사, 갈등, 로맨스 등)
    - `ModuleItemEntity`: 개별 모듈 아이템
    - `UnlockedModuleEntity`: 언락된 프리미엄 모듈

3. **모듈 콘텐츠 타입 정의**
    - `DialogueContent`: 대사 콘텐츠
    - `ActionContent`: 동작 콘텐츠
    - `SceneContent`: 장면 콘텐츠
    - `BackgroundContent`: 배경 콘텐츠
    - `EffectContent`: 효과 콘텐츠

4. **Repository 구현**
    - `ModuleRepository`: 모듈 CRUD 및 언락 관리
    - 초기 샘플 데이터 로딩 (5개 대사, 3개 동작)
    - Content JSON 직렬화/역직렬화

5. **Hilt 통합**
    - `DatabaseModule`: DB 의존성 주입
    - `CuteStageApplication`: 앱 시작 시 자동 초기화

---

## 아키텍처

### 레이어 구조

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│  (ViewModel, Composable, Screen)    │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│         Domain Layer (추후)         │
│  (UseCase, Business Logic)          │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│          Data Layer ✅               │
│  (Repository, DAO, Database)        │
└─────────────────────────────────────┘
```

### 데이터베이스 ERD

```
┌─────────────────┐
│  ModuleType     │
│  (대사,동작...) │
└────────┬────────┘
         │ 1:N
         ▼
┌─────────────────┐
│ ModuleCategory  │
│  (인사,갈등...) │
└────────┬────────┘
         │ 1:N
         ▼
┌─────────────────┐      ┌──────────────────┐
│  ModuleItem     │◄─────┤ UnlockedModule   │
│  (개별 모듈)    │ 1:1  │  (언락 기록)     │
└─────────────────┘      └──────────────────┘
```

---

## 데이터 구조

### 1. ModuleTypeEntity (모듈 타입)

**역할**: 모듈의 최상위 분류 (개발자 관리)

```kotlin
@Entity(tableName = "module_types")
data class ModuleTypeEntity(
    val id: String,                  // "dialogue", "action", "scene"
    val name: String,                // "대사", "동작", "장면"
    val icon: String,                // "ic_dialogue"
    val colorHex: String,            // "#FF5722"
    val isActive: Boolean = true,    // 활성화 여부
    val sortOrder: Int = 0           // 표시 순서
)
```

**현재 데이터** (5개):

```
dialogue  - 대사
action    - 동작
scene     - 장면
background- 배경
effect    - 효과
```

---

### 2. ModuleCategoryEntity (카테고리)

**역할**: 모듈 타입 내의 하위 분류

```kotlin
@Entity(tableName = "module_categories")
data class ModuleCategoryEntity(
    val id: String,                  // "dialogue_greeting"
    val typeId: String,              // "dialogue"
    val name: String,                // "인사"
    val sortOrder: Int = 0
)
```

**현재 데이터** (20개):

**Dialogue (5개)**

- `dialogue_greeting` - 인사
- `dialogue_daily` - 일상
- `dialogue_conflict` - 갈등
- `dialogue_reconcile` - 화해
- `dialogue_romance` - 로맨스

**Action (4개)**

- `action_move` - 이동
- `action_emotion` - 감정
- `action_gesture` - 제스처
- `action_interaction` - 상호작용

**Scene (4개)**

- `scene_meet` - 만남
- `scene_conflict` - 갈등
- `scene_climax` - 클라이맥스
- `scene_ending` - 결말

**Background (4개)**

- `bg_indoor` - 실내
- `bg_outdoor` - 실외
- `bg_nature` - 자연
- `bg_urban` - 도시

**Effect (4개)**

- `effect_transition` - 전환
- `effect_emphasis` - 강조
- `effect_mood` - 분위기
- `effect_sound` - 사운드

---

### 3. ModuleItemEntity (개별 모듈)

**역할**: 실제 사용 가능한 모듈 아이템

```kotlin
@Entity(tableName = "module_items")
data class ModuleItemEntity(
    val id: String,
    val typeId: String,
    val categoryId: String,
    val name: String,                    // "안녕하세요"
    val thumbnailUrl: String? = null,
    val isPremium: Boolean = false,      // 프리미엄 여부
    val unlockCost: Int = 0,             // 토큰 비용
    val contentJson: String,             // JSON 콘텐츠
    val tags: String = "[]",             // 태그 (JSON 배열)
    val usageCount: Int = 0,             // 사용 횟수
    val createdAt: Long,
    val updatedAt: Long
)
```

**현재 샘플 데이터** (8개):

**Dialogue (5개)**

```
dialogue_hello          - "안녕하세요" (무료)
dialogue_hi             - "안녕" (무료)
dialogue_long_time      - "오랜만이에요" (무료)
dialogue_love_confession- "사랑해요" (프리미엄, 50토큰)
dialogue_angry          - "화났어!" (무료)
```

**Action (3개)**

```
action_walk             - "걷기" (무료)
action_wave             - "손 흔들기" (무료)
action_hug              - "포옹하기" (프리미엄, 50토큰)
```

---

### 4. ModuleContent (콘텐츠 데이터)

**역할**: 각 모듈 타입의 실제 데이터 구조 (JSON으로 저장)

#### DialogueContent

```kotlin
data class DialogueContent(
    val text: String,                     // 대사 텍스트
    val characterId: String,              // 발화자 ID
    val emotion: EmotionType,             // NEUTRAL, HAPPY, SAD, ANGRY, ...
    val bubbleStyle: BubbleStyle,         // NORMAL, SHOUT, WHISPER, THOUGHT
    val typingSpeedMs: Long = 50L,
    val voicePitch: Float = 1.0f,
    val delayMillis: Long = 0L
)
```

**예시 JSON**:

```json
{
  "text": "안녕하세요!",
  "characterId": "hero",
  "emotion": "HAPPY",
  "bubbleStyle": "NORMAL",
  "typingSpeedMs": 50,
  "voicePitch": 1.0,
  "delayMillis": 0
}
```

#### ActionContent

```kotlin
data class ActionContent(
    val characterId: String,
    val animationType: AnimationType,     // WALK, RUN, WAVE, HUG, ...
    val startPositionX: Float,            // 0.0 ~ 1.0 (화면 비율)
    val startPositionY: Float,
    val endPositionX: Float?,
    val endPositionY: Float?,
    val duration: Float = 1.0f,           // 초
    val emotion: EmotionType
)
```

**예시 JSON**:

```json
{
  "characterId": "hero",
  "animationType": "WALK",
  "startPositionX": 0.2,
  "startPositionY": 0.5,
  "endPositionX": 0.8,
  "endPositionY": 0.5,
  "duration": 2.0,
  "emotion": "NEUTRAL"
}
```

#### SceneContent

```kotlin
data class SceneContent(
    val backgroundId: String,
    val timeOfDay: TimeOfDay,             // DAWN, MORNING, DAY, ...
    val weather: Weather,                 // CLEAR, RAINY, SNOWY, ...
    val bgmId: String?,
    val ambientSound: String?,
    val initialCharacters: List<CharacterPosition>,
    val mood: SceneMood                   // NEUTRAL, ROMANTIC, TENSE, ...
)
```

#### EffectContent

```kotlin
data class EffectContent(
    val effectType: EffectType,           // FADE_IN, FLASH, HEART, RAIN, ...
    val duration: Float = 1.0f,
    val intensity: Float = 1.0f,
    val soundId: String?,
    val color: String?
)
```

---

### 5. UnlockedModuleEntity (언락 기록)

**역할**: 사용자가 언락한 프리미엄 모듈 추적

```kotlin
@Entity(tableName = "unlocked_modules")
data class UnlockedModuleEntity(
    val moduleItemId: String,
    val unlockedAt: Long,
    val unlockMethod: UnlockMethod        // TOKEN, SUBSCRIPTION, EVENT, DEFAULT
)

enum class UnlockMethod {
    TOKEN,          // 토큰으로 구매
    SUBSCRIPTION,   // 구독으로 언락
    EVENT,          // 이벤트 보상
    DEFAULT         // 기본 제공
}
```

---

## 사용 방법

### Repository 주입 및 사용

```kotlin
@HiltViewModel
class EditorViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository
) : ViewModel() {

    // 1. 모듈 타입 목록 가져오기
    val moduleTypes = moduleRepository.getAllModuleTypes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 2. 특정 타입의 카테고리 가져오기
    fun loadCategories(typeId: String) {
        viewModelScope.launch {
            moduleRepository.getCategoriesByType(typeId)
                .collect { categories ->
                    // UI 업데이트
                }
        }
    }

    // 3. 특정 카테고리의 모듈 아이템 가져오기
    fun loadModuleItems(categoryId: String) {
        viewModelScope.launch {
            moduleRepository.getModuleItemsByCategory(categoryId)
                .collect { items ->
                    // UI 업데이트
                }
        }
    }

    // 4. 모듈 언락 여부 확인
    suspend fun checkUnlock(itemId: String): Boolean {
        return moduleRepository.isModuleUnlocked(itemId)
    }

    // 5. 모듈 언락하기
    suspend fun unlockModule(itemId: String) {
        moduleRepository.unlockModule(itemId, UnlockMethod.TOKEN)
    }

    // 6. 모듈 콘텐츠 파싱
    fun parseDialogue(item: ModuleItemEntity): DialogueContent {
        return moduleRepository.parseModuleContent<DialogueContent>(item.contentJson)
    }
}
```

### Composable에서 사용

```kotlin
@Composable
fun ModulePaletteScreen(
    viewModel: EditorViewModel = hiltViewModel()
) {
    val moduleTypes by viewModel.moduleTypes.collectAsState()

    LazyColumn {
        items(moduleTypes) { type ->
            ModuleTypeCard(
                type = type,
                onClick = { viewModel.loadCategories(type.id) }
            )
        }
    }
}
```

---

## 다음 단계

### Phase 2: UI 레이어 구축 (1-2주)

**목표**: 사용자가 모듈을 보고 선택할 수 있는 UI

#### 구현할 화면

1. **홈 화면** (`HomeScreen`)
   ```
   - 내 시나리오 목록
   - 템플릿 갤러리
   - [+ 새로 만들기] 버튼
   ```

2. **에디터 화면** (`EditorScreen`)
   ```
   ┌─────────────────────────────────┐
   │ [저장] [미리보기]    토큰: 150  │ ← 툴바
   ├─────────────────────────────────┤
   │ ┌─┐  ┌─┐  ┌─┐                  │
   │ │1│  │2│  │3│  ...              │ ← 타임라인
   │ └─┘  └─┘  └─┘                  │
   ├─────────────────────────────────┤
   │                                 │
   │      [스테이지 미리보기]         │ ← StageView
   │                                 │
   ├─────────────────────────────────┤
   │ [대사] [동작] [장면] [배경]     │ ← 모듈 팔레트 탭
   │                                 │
   │ ┌───┐ ┌───┐ ┌───┐ ┌───┐       │
   │ │안녕│ │반가│ │사랑│🔒│오래│   │ ← 모듈 카드
   │ └───┘ └───┘ └───┘ └───┘       │
   └─────────────────────────────────┘
   ```

3. **모듈 팔레트** (`ModulePalette`)
    - 탭으로 모듈 타입 전환
    - 카테고리별 필터링
    - 검색 기능
    - 프리미엄 모듈 🔒 표시

4. **모듈 상세** (`ModuleDetailDialog`)
    - 모듈 미리보기
    - 언락 버튼 (프리미엄)
    - 사용하기 버튼

#### 구현 순서

```
1주차:
├─ HomeScreen (기본 레이아웃)
├─ EditorScreen (레이아웃)
└─ ModulePalette (타입별 탭)

2주차:
├─ 모듈 카드 Composable
├─ 프리미엄 모듈 UI (🔒, 토큰 표시)
├─ 모듈 상세 다이얼로그
└─ 검색 기능
```

---

### Phase 3: 타임라인 시스템 (2-3주)

**목표**: 드래그 앤 드롭으로 모듈을 배치하고 순서 조정

#### 타임라인 데이터 구조

```kotlin
@Entity(tableName = "scenarios")
data class ScenarioEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val genre: String,
    val thumbnailPath: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "timeline_items",
    foreignKeys = [
        ForeignKey(
            entity = ScenarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["scenarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ModuleItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["moduleItemId"]
        )
    ]
)
data class TimelineItemEntity(
    @PrimaryKey val id: String,
    val scenarioId: String,
    val moduleItemId: String,
    val startTime: Float,                 // 시작 시간 (초)
    val duration: Float,                  // 지속 시간 (초)
    val layerIndex: Int = 0,              // 레이어 (동시 실행용)
    val parametersJson: String = "{}",    // 커스터마이징 파라미터
    val sortOrder: Int                    // 순서
)
```

#### 타임라인 파라미터 (인스턴스별 커스터마이징)

```kotlin
data class TimelineParameters(
    // Dialogue용
    val customText: String? = null,       // 대사 텍스트 수정
    val textSpeed: Float = 1.0f,

    // Action용
    val speedMultiplier: Float = 1.0f,
    val customEndPosition: Position? = null,

    // 공통
    val volume: Float = 1.0f,
    val opacity: Float = 1.0f
)
```

---

### Phase 4: 재생 엔진 통합 (1주)

**목표**: 타임라인의 모듈을 실제로 재생

#### TheaterScript 변환

```kotlin
class ScenarioToScriptConverter @Inject constructor(
    private val moduleRepository: ModuleRepository
) {
    suspend fun convert(scenarioId: String): TheaterScript {
        // 1. Scenario 로드
        val scenario = scenarioRepository.getScenarioById(scenarioId)

        // 2. TimelineItem 목록 로드
        val timelineItems = timelineRepository.getTimelineItems(scenarioId)

        // 3. 각 TimelineItem을 ModuleItem으로 변환
        val scenes = timelineItems.groupBy { it.startTime }
            .map { (time, items) ->
                buildScene(time, items)
            }

        return TheaterScript(scenes = scenes)
    }

    private suspend fun buildScene(
        time: Float,
        items: List<TimelineItemEntity>
    ): SceneState {
        val characters = mutableListOf<CharacterState>()
        val dialogues = mutableListOf<DialogueState>()

        items.forEach { item ->
            val module = moduleRepository.getModuleItemById(item.moduleItemId)
            when (module?.typeId) {
                "dialogue" -> {
                    val content = moduleRepository.parseModuleContent<DialogueContent>(
                        module.contentJson
                    )
                    dialogues.add(convertToDialogueState(content, item))
                }
                "action" -> {
                    // Action 처리
                }
                // ...
            }
        }

        return SceneState(
            characters = characters,
            dialogues = dialogues,
            durationMillis = calculateDuration(items)
        )
    }
}
```

---

### Phase 5: 토큰 시스템 (1주)

**목표**: 광고 시청 → 토큰 획득 → 프리미엄 모듈 언락

#### UserProfile (DataStore)

```kotlin
data class UserProfile(
    val tokenBalance: Int = 0,
    val isPremiumSubscriber: Boolean = false,
    val tutorialCompleted: Boolean = false
)

class UserRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.dataStore

    fun getUserProfile(): Flow<UserProfile> = dataStore.data
        .map { prefs ->
            UserProfile(
                tokenBalance = prefs[TOKEN_BALANCE] ?: 0,
                isPremiumSubscriber = prefs[IS_PREMIUM] ?: false,
                tutorialCompleted = prefs[TUTORIAL_COMPLETED] ?: false
            )
        }

    suspend fun addTokens(amount: Int) {
        dataStore.edit { prefs ->
            val current = prefs[TOKEN_BALANCE] ?: 0
            prefs[TOKEN_BALANCE] = current + amount
        }
    }

    suspend fun spendTokens(amount: Int): Boolean {
        var success = false
        dataStore.edit { prefs ->
            val current = prefs[TOKEN_BALANCE] ?: 0
            if (current >= amount) {
                prefs[TOKEN_BALANCE] = current - amount
                success = true
            }
        }
        return success
    }
}
```

---

## 개발 팁

### 1. JSON 직렬화/역직렬화

```kotlin
// 저장할 때
val dialogueContent = DialogueContent(
    text = "안녕하세요",
    characterId = "hero",
    emotion = EmotionType.HAPPY
)
val json = moduleRepository.serializeModuleContent(dialogueContent)

val moduleItem = ModuleItemEntity(
    id = "dialogue_hello",
    typeId = "dialogue",
    categoryId = "dialogue_greeting",
    name = "안녕하세요",
    contentJson = json,
    ...
)
moduleRepository.insertModuleItem(moduleItem)

// 불러올 때
val moduleItem = moduleRepository.getModuleItemById("dialogue_hello")
val content = moduleRepository.parseModuleContent<DialogueContent>(
    moduleItem.contentJson
)
println(content.text) // "안녕하세요"
```

### 2. Flow를 State로 변환

```kotlin
@Composable
fun ModuleList(viewModel: EditorViewModel) {
    val items by viewModel.moduleItems.collectAsStateWithLifecycle()

    LazyColumn {
        items(items) { item ->
            ModuleCard(item)
        }
    }
}
```

### 3. 프리미엄 모듈 체크

```kotlin
@Composable
fun ModuleCard(
    item: ModuleItemEntity,
    onUnlock: (String) -> Unit
) {
    val isUnlocked = remember(item.id) {
        // Repository에서 확인
    }

    Card(
        onClick = {
            if (item.isPremium && !isUnlocked) {
                onUnlock(item.id)
            } else {
                // 사용하기
            }
        }
    ) {
        Row {
            Text(item.name)
            if (item.isPremium && !isUnlocked) {
                Icon(Icons.Default.Lock)
                Text("${item.unlockCost} 토큰")
            }
        }
    }
}
```

---

## 테스트 방법

### 데이터베이스 초기화 확인

1. 앱 실행
2. Logcat에서 확인:
   ```
   D/CuteStage: Initializing default modules...
   D/CuteStage: Inserted 5 module types
   D/CuteStage: Inserted 20 categories
   D/CuteStage: Inserted 8 sample modules
   ```

3. Database Inspector (Android Studio)
    - `View > Tool Windows > App Inspection`
    - `Database Inspector` 탭
    - `cutestage_db` 선택
    - 각 테이블 확인

### Repository 테스트

```kotlin
@HiltAndroidTest
class ModuleRepositoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: ModuleRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testGetAllModuleTypes() = runTest {
        val types = repository.getAllModuleTypes().first()
        assertEquals(5, types.size)
        assertTrue(types.any { it.id == "dialogue" })
    }

    @Test
    fun testUnlockModule() = runTest {
        val itemId = "dialogue_love_confession"
        assertFalse(repository.isModuleUnlocked(itemId))

        repository.unlockModule(itemId, UnlockMethod.TOKEN)

        assertTrue(repository.isModuleUnlocked(itemId))
    }
}
```

---

## 문제 해결

### Q1: Room 스키마 export 경고

**문제**:

```
Schema export directory was not provided
```

**해결**:
`app/build.gradle.kts`에 추가:

```kotlin
android {
    defaultConfig {
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }
}
```

### Q2: Kapt language version 경고

**문제**:

```
Kapt currently doesn't support language version 2.0+
```

**해결**: 무시해도 됨 (정상 동작). Kotlin 2.0은 KSP 권장하지만 Hilt가 아직 Kapt 사용 중.

### Q3: 데이터베이스 초기화 안됨

**확인 사항**:

1. `@HiltAndroidApp` 어노테이션 있는지
2. `AndroidManifest.xml`에 `android:name=".CuteStageApplication"` 있는지
3. Hilt 의존성 주입 정상인지

---

## 마무리

### ✅ Phase 1 완료 체크리스트

- [x] Room Database 설정
- [x] 모듈 엔티티 4개 구현
- [x] 모듈 콘텐츠 타입 5개 정의
- [x] ModuleDao 모든 쿼리 구현
- [x] ModuleRepository 구현
- [x] Hilt 통합
- [x] 초기 샘플 데이터 (8개 모듈)
- [x] 빌드 성공

### 다음 세션 시작 시

1. 이 문서 읽기
2. `DEVELOPMENT_ROADMAP.md` 참고
3. Phase 2 (UI 레이어) 시작

**Happy Coding! 🎭✨**
