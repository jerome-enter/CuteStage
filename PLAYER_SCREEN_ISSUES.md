# PlayerScreen 문제 분석

> 작성일: 2024.12.07  
> 상태: 🐛 문제 발견

---

## 🐛 발견된 문제들

### 1. StageView 크기 문제

**증상**: 등장인물 소개 영역이 보이지 않음

**원인**:

```kotlin
// StageView.kt
Box(
    modifier = modifier
        .padding(10.dp)
        .fillMaxWidth()
        .height(300.dp)  // ← 고정 크기 300dp
        .clip(RoundedCornerShape(16.dp))
)
```

**비교**:

- **StageScreen**: Scaffold 안에 StageView + 등장인물 소개 섹션
- **PlayerScreen**: Scaffold 안에 StageView만 (fillMaxSize)

**결과**: PlayerScreen에서는 StageView만 크게 표시되고 등장인물 소개가 없음

---

### 2. 이전 시나리오가 재생됨

**증상**: 새로 만든 시나리오를 선택해도 이전 시나리오가 재생됨

**가능한 원인**:

1. `StageViewModel`이 Hilt로 공유되어 이전 script가 남아있음
2. `LaunchedEffect(script)`가 제대로 동작하지 않음
3. `script`가 null이거나 잘못된 값

**확인 필요**:

```kotlin
// PlayerViewModel
suspend fun convert(scenarioId: String): TheaterScript? {
    // 변환이 제대로 되는지?
    // 캐릭터와 대사가 있는지?
}
```

---

### 3. 캐릭터/대사 없음 문제

**증상**: 모듈로 만든 시나리오에 캐릭터나 대사가 표시되지 않음

**TimelineToScriptConverter 검토**:

```kotlin
private fun buildDialogueScene(moduleItem: ModuleItemEntity, order: Int): SceneState {
    val content = parseModuleContent<DialogueContent>(moduleItem.contentJson)
    
    // ✅ 캐릭터 생성
    val character = CharacterState(
        id = content.characterId,
        name = content.characterId,
        imageRes = getDefaultCharacterImage(content.characterId),
        position = DpOffset(150.dp, 300.dp),  // 고정 위치
        size = 80.dp,
        alpha = 1f
    )
    
    // ✅ 대사 생성
    val dialogue = DialogueState(
        text = content.text,
        position = DpOffset(120.dp, 200.dp),
        speakerName = content.characterId,
        typingSpeedMs = content.typingSpeedMs
    )
    
    return SceneState(
        characters = listOf(character),  // ✅ 포함됨
        dialogues = listOf(dialogue),    // ✅ 포함됨
        durationMillis = (content.text.length * content.typingSpeedMs) + 1000L
    )
}
```

**로직상으로는 문제 없음!**

---

## 🔍 근본 원인 추정

### 가설 1: ViewModel 공유 문제

```kotlin
@Composable
fun StageView(
    script: TheaterScript? = null,
    viewModel: StageViewModel = hiltViewModel()  // ← 공유됨
) {
    LaunchedEffect(script) {
        viewModel.setInitialScript(script)
    }
}
```

**문제**:

- Hilt ViewModel은 NavHost 스코프에서 공유됨
- 이전 화면의 script가 남아있을 수 있음
- `script` 파라미터가 null이면 이전 script 유지

**해결책**:

```kotlin
LaunchedEffect(script) {
    if (script != null) {
        viewModel.setInitialScript(script)
    }
}
```

이것만으로는 부족! script가 null일 때도 처리 필요.

---

### 가설 2: script가 null

```kotlin
// PlayerViewModel
val script = converter.convert(scenarioId)
state = state.copy(script = script)  // script가 null일 수 있음
```

**확인 필요**:

1. TimelineItems가 DB에 제대로 저장되었는지
2. convert()가 제대로 동작하는지
3. scenes가 비어있지 않은지

---

### 가설 3: 등장인물 소개 섹션 없음

**StageScreen**:

```kotlin
Column {
    StageView(...)          // 상단
    CharacterIntroduction   // 하단 스크롤
}
```

**PlayerScreen**:

```kotlin
Box {
    StageView(...)  // 전체 fillMaxSize
}
```

**결과**: 레이아웃이 다름!

---

## ✅ 해결 방법

### 방법 1: PlayerScreen을 StageScreen과 동일하게 수정 (추천)

```kotlin
@Composable
fun PlayerScreen(
    onScenarioSelectClick: (() -> Unit)? = null,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state = viewModel.state
    
    Scaffold(
        topBar = { /* TopAppBar */ }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).background(Color.Black)) {
            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView()
                state.script != null -> {
                    // ✅ StageScreen처럼 Column 사용
                    Column(modifier = Modifier.fillMaxSize()) {
                        StageView(
                            script = state.script,
                            onScenarioSelectClick = onScenarioSelectClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // 등장인물 소개
                        CharacterIntroductionSection(
                            characters = extractCharacters(state.script),
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    }
                }
            }
        }
    }
}
```

---

### 방법 2: StageView를 강제로 재초기화

```kotlin
@Composable
fun StageView(
    script: TheaterScript? = null,
    onScenarioSelectClick: (() -> Unit)? = null,
    viewModel: StageViewModel = hiltViewModel()
) {
    // ✅ script가 변경될 때마다 강제 재설정
    LaunchedEffect(script) {
        viewModel.setInitialScript(script)
        if (script != null) {
            viewModel.handleEvent(StageEvent.Play)  // 자동 재생
        }
    }
    
    // ...
}
```

---

### 방법 3: PlayerScreen에서 별도 ViewModel 키 사용

```kotlin
@Composable
fun PlayerScreen(
    onScenarioSelectClick: (() -> Unit)? = null,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state = viewModel.state
    
    state.script?.let { script ->
        // ✅ 시나리오 ID를 키로 사용하여 별도 ViewModel
        key(viewModel.scenarioId) {
            StageView(
                script = script,
                onScenarioSelectClick = onScenarioSelectClick
            )
        }
    }
}
```

---

## 🎯 즉시 적용할 수정

### 1. PlayerScreen 레이아웃 수정

StageScreen과 동일한 구조 사용

### 2. script null 체크 강화

```kotlin
LaunchedEffect(script) {
    viewModel.setInitialScript(script)
}

// 또는
DisposableEffect(Unit) {
    viewModel.setInitialScript(script)
    onDispose {
        viewModel.clearScript()  // 정리
    }
}
```

### 3. 디버그 로그 추가

```kotlin
// PlayerViewModel
Log.d("PlayerScreen", "Loaded script with ${script?.scenes?.size} scenes")

// StageView
LaunchedEffect(script) {
    Log.d("StageView", "Setting script: ${script?.scenes?.size} scenes")
    viewModel.setInitialScript(script)
}
```

---

## 📝 다음 단계

1. **즉시**: PlayerScreen을 StageScreen 구조로 수정
2. **디버깅**: 로그 추가하여 script 전달 확인
3. **테스트**: 시나리오 생성 → 재생 → 다른 시나리오 재생

진행할까요?
