# 시나리오 형식 통일 필요성 분석

> 작성일: 2024.12.07  
> 상태: ⚠️ 문제 발견

---

## 🐛 현재 문제점

### 1. 두 가지 형식의 시나리오

#### 기본 제공 시나리오 (템플릿)

```kotlin
// StageTestScenario.kt
theaterScript {
    scene {
        character("male") at position(100.dp, 300.dp)
        dialogue("안녕하세요") at position(80.dp, 200.dp)
    }
}
```

- ✅ **TheaterScript 형식** (코틀린 DSL)
- ✅ StageView에서 직접 재생
- ❌ JSON 직렬화 불가능
- ❌ 공유 불가능
- ❌ 저장 불가능

#### 사용자 생성 시나리오 (모듈 조합)

```kotlin
// TimelineItemEntity + ModuleItemEntity
TimelineItemEntity(
    scenarioId = "user_001",
    moduleItemId = "dialogue_hello",
    order = 0
)
```

- ✅ **DB에 저장 가능** (Room)
- ✅ JSON 직렬화 가능
- ✅ 공유 가능
- ❌ PlayerScreen (별도 화면)에서 재생
- ❌ TimelineToScriptConverter로 변환 후 재생

---

### 2. 재생 방식의 차이

#### 템플릿 시나리오

```
ScenarioList → Player Route
└─> StageScreen (onScenarioSelectClick 있음)
    └─> StageView + 시나리오 선택 버튼
```

#### 사용자 시나리오

```
ScenarioList → Player Route
└─> PlayerScreen
    └─> TimelineToScriptConverter
        └─> StageView (onScenarioSelectClick 없음!)
```

**문제:**

- 사용자 시나리오는 시나리오 선택 버튼이 없음! 💥
- 재생 화면이 다름 (일관성 없음)
- UX가 다름

---

### 3. 구조적 문제

```
┌──────────────────────────────────────┐
│         TheaterScript                │
│      (코틀린 DSL, 메모리만)          │
├──────────────────────────────────────┤
│  ✅ 기본 템플릿 (7개)                │
│  ❌ 저장 불가                        │
│  ❌ JSON 변환 불가                   │
│  ❌ 공유 불가                        │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│    TimelineItem + ModuleItem         │
│      (Room DB, JSON 가능)            │
├──────────────────────────────────────┤
│  ✅ 사용자 생성                      │
│  ✅ 저장 가능                        │
│  ✅ JSON 변환 가능                   │
│  ✅ 공유 가능                        │
│  ❌ 변환 오버헤드                    │
└──────────────────────────────────────┘
```

---

## ✅ 해결 방안

### 목표

1. **통일된 JSON 형식** - 모든 시나리오가 같은 구조
2. **동일한 재생 방식** - 모두 StageView에서 재생
3. **공유 가능** - JSON으로 직렬화/역직렬화

---

### 제안 1: TheaterScript를 JSON 직렬화 가능하게 (추천 ✅)

#### 장점

- 기존 코드 최소 변경
- TheaterScript 기반 DSL 유지
- 변환 로직 단순화

#### 구조

```kotlin
@Serializable
data class TheaterScript(
    val scenes: List<SceneState>,
    val debug: Boolean = false
)

@Serializable
data class SceneState(
    val characters: List<CharacterState>,
    val dialogues: List<DialogueState>,
    val backgroundRes: Int = R.drawable.stage_floor,
    val durationMillis: Long,
    val isEnding: Boolean = false
)
```

#### 저장 방식

```kotlin
// ScenarioEntity
data class ScenarioEntity(
    @PrimaryKey val id: String,
    val title: String,
    val scriptJson: String,  // ← TheaterScript를 JSON으로
    val isTemplate: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
```

#### 변환

```kotlin
// 저장
val scriptJson = Json.encodeToString(theaterScript)
scenarioDao.insert(ScenarioEntity(..., scriptJson = scriptJson))

// 로드
val theaterScript = Json.decodeFromString<TheaterScript>(scriptJson)
stageView.setScript(theaterScript)
```

---

### 제안 2: 모듈 시스템을 TheaterScript로 변환 (현재 방식)

#### 문제점

- 변환 오버헤드
- 복잡한 변환 로직 (TimelineToScriptConverter)
- 정보 손실 가능성
- 템플릿과 구조가 다름

#### 현재 흐름

```
사용자 시나리오:
TimelineItem (DB)
  ↓ TimelineToScriptConverter
TheaterScript (메모리)
  ↓ StageView
재생

템플릿:
TheaterScript (코드)
  ↓ StageView
재생
```

**문제**: 시작점이 다름!

---

## 📋 통합 방안 (추천)

### 1단계: TheaterScript를 JSON 직렬화 가능하게

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
```

```kotlin
// StageModels.kt
import kotlinx.serialization.Serializable

@Serializable
data class TheaterScript(
    val scenes: List<SceneState>,
    val debug: Boolean = false
)

@Serializable
data class SceneState(
    val characters: List<SerializableCharacter>,
    val dialogues: List<SerializableDialogue>,
    val backgroundName: String = "stage_floor",  // Int 대신 String
    val durationMillis: Long,
    val isEnding: Boolean = false
)

@Serializable
data class SerializableCharacter(
    val id: String,
    val name: String,
    val imageName: String,  // Int 대신 String
    val positionX: Float,   // Dp 대신 Float
    val positionY: Float,
    val size: Float,
    val alpha: Float = 1f
)
```

---

### 2단계: ScenarioEntity 구조 변경

```kotlin
@Entity(tableName = "scenarios")
data class ScenarioEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val scriptJson: String,  // ← TheaterScript JSON
    val thumbnailPath: String? = null,
    val isTemplate: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
```

**삭제:**

- ❌ TimelineItemEntity (더 이상 불필요)
- ❌ TimelineToScriptConverter (더 이상 불필요)

---

### 3단계: 템플릿을 DB에 저장

```kotlin
// Application.onCreate()
suspend fun initializeTemplateScenarios() {
    val templates = listOf(
        StageTestScenario.createPlaygroundScenario(),
        StageTestScenario.createBasicScenario(),
        // ...
    )
    
    templates.forEach { script ->
        val json = Json.encodeToString(script)
        scenarioDao.insert(
            ScenarioEntity(
                id = "template_playground",
                title = "놀이터",
                scriptJson = json,
                isTemplate = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
```

---

### 4단계: 통일된 재생 방식

```kotlin
// Navigation.kt
composable("player/{scenarioId}") { backStackEntry ->
    val scenarioId = backStackEntry.arguments?.getString("scenarioId")
    
    // 모든 시나리오 동일하게 처리
    PlayerScreen(
        scenarioId = scenarioId,
        onScenarioSelectClick = {
            navController.navigate(Screen.ScenarioList.route) {
                popUpTo(Screen.Stage.route) { inclusive = false }
                launchSingleTop = true
            }
        }
    )
}
```

```kotlin
// PlayerScreen.kt
@Composable
fun PlayerScreen(
    scenarioId: String,
    onScenarioSelectClick: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    // DB에서 시나리오 로드
    val scenario = viewModel.loadScenario(scenarioId)
    
    if (scenario != null) {
        val script = Json.decodeFromString<TheaterScript>(scenario.scriptJson)
        
        // 모두 StageView에서 재생
        StageScreen(
            script = script,
            onScenarioSelectClick = onScenarioSelectClick
        )
    }
}
```

---

### 5단계: 시나리오 생성 방식 변경

```kotlin
// Creator에서 TheaterScript 직접 생성
val script = theaterScript {
    timelineItems.forEach { item ->
        scene {
            // 모듈 내용을 DSL로 변환
            when (item.moduleType) {
                "dialogue" -> dialogue(item.text) at position(...)
                "action" -> character(...) at position(...)
            }
        }
    }
}

// JSON으로 저장
val scriptJson = Json.encodeToString(script)
scenarioRepository.save(ScenarioEntity(..., scriptJson = scriptJson))
```

---

## 🎯 최종 구조 (통일)

```
모든 시나리오:
├─ ScenarioEntity (DB)
│  ├─ id
│  ├─ title
│  ├─ scriptJson ← TheaterScript JSON
│  └─ isTemplate
│
└─ JSON 형식 예시:
   {
     "scenes": [
       {
         "characters": [
           {
             "id": "male_1",
             "name": "상철",
             "imageName": "male_1_idle_1",
             "positionX": 100.0,
             "positionY": 300.0,
             "size": 80.0
           }
         ],
         "dialogues": [
           {
             "text": "안녕하세요",
             "positionX": 80.0,
             "positionY": 200.0,
             "speakerName": "상철"
           }
         ],
         "durationMillis": 3000
       }
     ]
   }
```

---

## ✨ 장점

### 1. 통일성

- ✅ 모든 시나리오가 같은 형식 (TheaterScript JSON)
- ✅ 템플릿과 사용자 생성 구분 없음
- ✅ 동일한 재생 방식 (StageView)

### 2. 공유 가능

- ✅ JSON 파일로 내보내기
- ✅ QR 코드로 공유
- ✅ 클라우드 업로드/다운로드

### 3. 단순화

- ❌ TimelineItemEntity 삭제
- ❌ TimelineToScriptConverter 삭제
- ❌ PlayerScreen 단순화
- ✅ 코드 복잡도 감소

### 4. 확장성

- ✅ 시나리오 편집 쉬움
- ✅ 버전 관리 가능
- ✅ 백업/복원 가능

---

## 🚀 마이그레이션 계획

### Phase 1: TheaterScript Serializable 추가

1. kotlinx-serialization 의존성 추가
2. @Serializable 어노테이션 추가
3. Int → String 변환 (리소스 ID)

### Phase 2: ScenarioEntity 구조 변경

1. scriptJson 필드 추가
2. TimelineItemEntity 제거
3. Migration 작성

### Phase 3: 템플릿 DB 저장

1. 기존 템플릿 JSON 변환
2. DB에 insert
3. 앱 초기화 시 자동 생성

### Phase 4: Creator 수정

1. 모듈 조합 → TheaterScript DSL
2. 저장 시 JSON 직렬화

### Phase 5: 통합 테스트

1. 템플릿 재생 테스트
2. 사용자 시나리오 생성/재생
3. 공유 기능 테스트

---

## 🎉 결론

**현재 문제:**

- ❌ 템플릿과 사용자 시나리오 형식 다름
- ❌ 재생 방식 다름
- ❌ 공유 불가능

**해결 후:**

- ✅ 모든 시나리오 TheaterScript JSON 통일
- ✅ 모두 StageView에서 재생
- ✅ JSON 파일로 공유 가능
- ✅ 단순하고 일관된 구조

**진행할까요?**
