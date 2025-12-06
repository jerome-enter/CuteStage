# StageView 아키텍처 문서

## 📐 전체 구조

StageView는 **MVVM 패턴**을 적용한 복잡한 UI 컴포넌트입니다.
ViewModel을 내부에 포함하여 상태 관리를 캡슐화하면서도, 외부에서는 간단한 API로 사용할 수 있습니다.

```
┌─────────────────────────────────────────────────────────────┐
│                      StageView                               │
│  (Entry Point - ViewModel 생성 및 Context 주입)              │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│                   StageViewModel                             │
│  - 17개 상태 → 5개 State 객체로 그룹화                       │
│  - 모든 비즈니스 로직 캡슐화                                  │
│  - Configuration Change 자동 대응                            │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│                StageViewContent                              │
│  (Stateless UI - 순수 렌더링만 담당)                         │
└─────────────────┬───────────────────────────────────────────┘
                  │
    ┌─────────────┼─────────────┬─────────────┐
    ▼             ▼             ▼             ▼
StageCharacter  StageSpeech  StageTypewriter  StageControls
(캐릭터)       (말풍선)      (타자기)         (UI 컨트롤)
```

---

## 📁 파일 구조 (총 8,229 lines)

### **핵심 파일** (ViewModel 패턴)

| 파일 | 크기 | 역할 |
|------|------|------|
| `StageView.kt` | 10KB (296 lines) | Entry point, ViewModel 생성, Context 주입 |
| `StageViewModel.kt` | 11KB (322 lines) | 상태 관리 + 비즈니스 로직 |
| `StageState.kt` | 1.4KB (52 lines) | 상태 그룹화 (5개 State 객체) |
| `StageEvent.kt` | 1.1KB (31 lines) | 이벤트 정의 (sealed class) |
| `StageModels.kt` | 2.5KB (103 lines) | 데이터 모델 (TheaterScript 등) |

### **UI 컴포넌트** (Stateless)

| 파일 | 크기 | 역할 |
|------|------|------|
| `StageCharacter.kt` | 5.4KB (158 lines) | 캐릭터 애니메이션 |
| `StageSpeechBubble.kt` | 8.0KB (244 lines) | 말풍선 (일반 + 상호작용) |
| `StageTypewriter.kt` | 3.7KB (106 lines) | 타자기 효과 + 음성 |
| `StageControls.kt` | 20KB (589 lines) | UI 컨트롤 (재생, 시나리오 선택 등) |

### **시나리오 & 시스템**

| 파일 | 크기 | 역할 |
|------|------|------|
| `StageTestScenario.kt` | 48KB | 테스트 시나리오 모음 |
| `StageFoolishTrick.kt` | 64KB | "폭삭 속았수다" 시나리오 |
| `StageLoveConfession.kt` | 31KB | "사랑고백" 시나리오 |
| `StageOksunMonologue.kt` | 21KB | "옥순의 혼잣말" 시나리오 |
| `StageSongScenario.kt` | 21KB | "하얀 바다새" 노래 시나리오 |
| `CharacterInteractionSystem.kt` | 10KB | 캐릭터 클릭 상호작용 |
| `CharacterAnimation.kt` | 7.8KB | 스프라이트 애니메이션 리소스 |
| `VoiceSoundEngine.kt` | 11KB | 3가지 음성 엔진 |
| `GeminiScenarioGenerator.kt` | 9.5KB | AI 시나리오 생성 |
| `ScenarioConverter.kt` | 8.2KB | Gemini → TheaterScript 변환 |

---

## 🎯 핵심 설계 원칙

### 1. **상태 그룹화** (17개 → 5개)

**Before:**

```kotlin
var currentScript by remember { mutableStateOf(script) }
var isPlaying by remember { mutableStateOf(false) }
var playbackSpeed by remember { mutableStateOf(1.0f) }
var currentSceneIndex by remember { mutableStateOf(0) }
var interactionDialogue by remember { mutableStateOf<String?>(null) }
var interactionCharacterId by remember { mutableStateOf<String?>(null) }
var interactionEmotion by remember { mutableStateOf(NORMAL) }
var maleClickCount by remember { mutableStateOf(0) }
var femaleClickCount by remember { mutableStateOf(0) }
var lastClickTime by remember { mutableStateOf(0L) }
// ... 7개 더
```

**After:**

```kotlin
data class StageState(
    val currentScript: TheaterScript? = null,
    val playbackState: PlaybackState = PlaybackState(),
    val interactionState: InteractionState = InteractionState(),
    val choiceState: ChoiceState = ChoiceState(),
    val aiGenerationState: AIGenerationState = AIGenerationState(),
)
```

### 2. **이벤트 기반 아키텍처**

```kotlin
sealed class StageEvent {
    object Play : StageEvent()
    object Stop : StageEvent()
    data class ChangePlaybackSpeed(val speed: Float) : StageEvent()
    data class CharacterClick(val character: CharacterState) : StageEvent()
    data class SelectChoice(val nextSceneIndex: Int) : StageEvent()
    object GenerateAIScenario : StageEvent()
    // ...
}
```

### 3. **단방향 데이터 흐름**

```
User Interaction
      ↓
  StageEvent
      ↓
StageViewModel.handleEvent()
      ↓
  State 업데이트
      ↓
StageViewContent Recompose
```

---

## 🔄 상태 관리 상세

### **PlaybackState** (재생 제어)

```kotlin
data class PlaybackState(
    val isPlaying: Boolean = false,
    val speed: Float = 1.0f,           // 1x, 1.5x, 2x
    val currentSceneIndex: Int = 0,
)
```

### **InteractionState** (캐릭터 상호작용)

```kotlin
data class InteractionState(
    val dialogue: String? = null,
    val characterId: String? = null,
    val emotion: EmotionType = NORMAL,
    val maleClickCount: Int = 0,
    val femaleClickCount: Int = 0,
    val lastClickTime: Long = 0L,
    val maleAngryCount: Int = 0,
    val femaleAngryCount: Int = 0,
)
```

**로직:**

- 5초 이상 지나면 클릭 카운트 리셋
- 클릭 횟수에 따라 감정 변화: NORMAL → HAPPY → ANNOYED → ANGRY
- 3번 화내면 리셋

### **ChoiceState** (선택지 분기)

```kotlin
data class ChoiceState(
    val isWaiting: Boolean = false,
    val choices: List<Choice>? = null,
)
```

### **AIGenerationState** (AI 생성)

```kotlin
data class AIGenerationState(
    val showDialog: Boolean = false,
    val userInput: String = "",
    val isGenerating: Boolean = false,
    val error: String? = null,
)
```

---

## 🎭 타임라인 진행 로직

```kotlin
LaunchedEffect(
    state.currentScript,
    state.playbackState.currentSceneIndex,
    state.playbackState.isPlaying,
    state.playbackState.speed,
    state.choiceState.isWaiting
) {
    if (isPlaying && script != null && !waitingForChoice) {
        currentScene?.let { scene ->
            // 재생 속도 적용한 안전한 delay
            delay(calculateSafeDelay(scene.durationMillis, speed))

            if (scene.isEnding || isLastScene) {
                onEvent(StageEvent.ScriptEnded)
            } else {
                onEvent(StageEvent.AdvanceScene)
            }
        }
    }
}
```

---

## ✅ ViewModel 도입 효과

### **Before (remember)**

- ❌ 17개의 분산된 상태 변수
- ❌ Configuration Change 시 모든 상태 손실
- ❌ 비즈니스 로직이 UI에 섞임
- ❌ 테스트 불가능
- ❌ 복잡한 콜백 함수 (8개 파라미터!)

### **After (ViewModel)**

- ✅ 5개 State 객체로 체계화
- ✅ Configuration Change 자동 대응
- ✅ 비즈니스 로직 완전 분리
- ✅ 단위 테스트 가능
- ✅ Event 기반 깔끔한 API

---

## 🔌 사용 예시

### **기본 사용**

```kotlin
@Composable
fun MyScreen() {
    StageView(
        script = myScript,
        onScriptEnd = { /* 종료 처리 */ }
    )
}
```

### **여러 인스턴스 독립적 사용**

```kotlin
@Composable
fun TwoStagesScreen() {
    Row {
        // 각각 독립된 ViewModel 인스턴스
        StageView(
            script = script1,
            modifier = Modifier.weight(1f)
        )
        StageView(
            script = script2,
            modifier = Modifier.weight(1f)
        )
    }
}
```

---

## 🧪 테스트 가능

```kotlin
@Test
fun `캐릭터를 5번 클릭하면 짜증난 표정이 나온다`() {
    val viewModel = StageViewModel(null) {}
    
    val character = CharacterState(
        id = "male",
        name = "상철",
        // ...
    )
    
    repeat(5) {
        viewModel.handleEvent(StageEvent.CharacterClick(character))
    }
    
    assertEquals(
        CharacterInteractionSystem.EmotionType.ANNOYED,
        viewModel.state.interactionState.emotion
    )
}
```

---

## 📊 성능 최적화

1. **remember + derivedStateOf**
   ```kotlin
   val currentScene = remember(state.currentScript, state.playbackState.currentSceneIndex) {
       state.currentScript?.scenes?.getOrNull(state.playbackState.currentSceneIndex)
   }
   ```

2. **key()로 애니메이션 보존**
   ```kotlin
   key(character.id) {
       AnimatedCharacter(character = character)
   }
   ```

3. **LaunchedEffect 의존성 최적화**
    - 필요한 상태만 dependencies에 포함
    - 불필요한 재시작 방지

---

## 🚀 확장 가능성

### **새로운 기능 추가 시**

1. **Event 추가** (`StageEvent.kt`)
   ```kotlin
   data class NewFeature(val data: String) : StageEvent()
   ```

2. **State 확장** (`StageState.kt`)
   ```kotlin
   data class NewFeatureState(val isActive: Boolean = false)
   ```

3. **로직 구현** (`StageViewModel.kt`)
   ```kotlin
   private fun handleNewFeature(data: String) {
       state = state.copy(newFeatureState = ...)
   }
   ```

4. **UI 추가** (`StageViewContent.kt`)
   ```kotlin
   if (state.newFeatureState.isActive) {
       NewFeatureUI(...)
   }
   ```

---

## 📝 요약

- **파일 수**: 24개
- **총 라인**: 8,229 lines
- **핵심 파일**: 5개 (ViewModel 패턴)
- **상태 객체**: 5개 (그룹화)
- **이벤트 타입**: 15개
- **테스트 가능**: ✅
- **재사용 가능**: ✅
- **Configuration Change 안전**: ✅

**StageView는 이제 엔터프라이즈급 아키텍처를 갖춘 재사용 가능한 UI 컴포넌트입니다!** 🎭
