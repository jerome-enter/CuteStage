# StageScreen과 PlayerScreen 통합 완료

> 작성일: 2024.12.07  
> 상태: ✅ 완료

---

## 🤔 문제 인식

### Before: 두 개의 거의 동일한 화면

**StageScreen.kt (270줄)**

```kotlin
@Composable
fun StageScreen(onScenarioSelectClick: (() -> Unit)?) {
    val viewModel: StageViewModel = hiltViewModel()
    val characters = extractCharactersFromScript(viewModel.state.currentScript)
    
    Scaffold {
        Column {
            StageView(viewModel = viewModel, onScenarioSelectClick = onScenarioSelectClick)
            CharacterIntroductionSection(characters)
        }
    }
}
```

**PlayerScreen.kt (350줄)**

```kotlin
@Composable
fun PlayerScreen(onScenarioSelectClick: (() -> Unit)?) {
    val viewModel: PlayerViewModel = hiltViewModel()
    val characters = extractCharactersFromScript(state.script)
    
    Scaffold {
        when {
            state.isLoading -> LoadingView()
            state.error != null -> ErrorView()
            state.script != null -> {
                Column {
                    StageView(script = state.script, onScenarioSelectClick = onScenarioSelectClick)
                    CharacterIntroductionSection(characters)
                }
            }
        }
    }
}
```

**중복:**

- Scaffold + TopAppBar (동일)
- Column + StageView (동일)
- CharacterIntroductionSection (동일)
- extractCharactersFromScript (동일)
- CharacterCard (동일)
- CharacterInfo (동일)

**총 중복 코드: ~250줄**

---

## ✅ 해결 방법

### StageScreen 하나로 통합

**수정된 StageScreen.kt:**

```kotlin
@Composable
fun StageScreen(
    script: TheaterScript? = null,  // ✅ 파라미터 추가
    onScenarioSelectClick: (() -> Unit)? = null
) {
    val viewModel: StageViewModel = hiltViewModel()
    
    // ✅ script 파라미터가 있으면 설정
    LaunchedEffect(script) {
        if (script != null) {
            viewModel.setInitialScript(script)
        }
    }
    
    val characters = extractCharactersFromScript(viewModel.state.currentScript)
    
    Scaffold {
        Column {
            StageView(viewModel = viewModel, onScenarioSelectClick = onScenarioSelectClick)
            CharacterIntroductionSection(characters)
        }
    }
}
```

**사용 방법:**

```kotlin
// 템플릿 시나리오 (기존 방식)
StageScreen(
    script = null,  // ViewModel의 current script 사용
    onScenarioSelectClick = onScenarioSelectClick
)

// 사용자 시나리오 (새 방식)
val script = convertedScript  // TimelineToScriptConverter 결과
StageScreen(
    script = script,  // 파라미터로 전달
    onScenarioSelectClick = onScenarioSelectClick
)
```

---

## 📝 변경 사항

### 1. StageScreen 수정

- `script: TheaterScript? = null` 파라미터 추가
- `LaunchedEffect(script)` 추가하여 script 설정
- 나머지 로직 동일

### 2. Navigation 수정

**Before:**

```kotlin
else -> {
    PlayerScreen(onScenarioSelectClick = ...)
}
```

**After:**

```kotlin
else -> {
    val viewModel: PlayerViewModel = hiltViewModel()
    val state = viewModel.state
    
    when {
        state.isLoading -> LoadingView()
        state.error != null -> ErrorView()
        state.script != null -> {
            StageScreen(
                script = state.script,
                onScenarioSelectClick = ...
            )
        }
    }
}
```

### 3. 삭제된 파일

- ❌ `PlayerScreen.kt` (350줄 삭제)

---

## 📊 코드 감소

### Before

```
StageScreen.kt:    270줄
PlayerScreen.kt:   350줄
────────────────────────
합계:              620줄
```

### After

```
StageScreen.kt:    280줄 (+10줄)
Navigation.kt:     +30줄 (로딩/에러 처리)
────────────────────────
합계:              310줄
```

**절감: 310줄 (50% 감소)** 🎉

---

## ✨ 장점

### 1. 코드 중복 제거

- ✅ 250줄 중복 코드 제거
- ✅ 하나의 화면만 유지보수
- ✅ 버그 수정 한 곳에서만

### 2. 일관성 향상

- ✅ 템플릿과 사용자 시나리오 완전히 동일한 로직
- ✅ 동일한 UI, 동일한 동작
- ✅ 차이 없음

### 3. 단순성

- ✅ 하나의 Screen만 이해하면 됨
- ✅ 코드 네비게이션 쉬움
- ✅ 새 개발자 온보딩 빠름

### 4. 확장성

- ✅ 새로운 기능 추가 시 한 곳에만
- ✅ 타입별 분기 없음
- ✅ 유연한 구조

---

## 🎬 사용 시나리오

### 템플릿 시나리오

```kotlin
// Navigation.kt
when (scenarioId) {
    "template_playground" -> {
        StageTestScenario.currentScenario = PLAYGROUND
        StageScreen(
            script = null,  // ViewModel의 script 사용
            onScenarioSelectClick = onScenarioSelectClick
        )
    }
}
```

**동작:**

1. StageScreen 진입
2. `script = null` → `LaunchedEffect` 스킵
3. ViewModel의 `currentScript` 사용 (StageTestScenario.currentScenario로 설정됨)
4. 재생 ✅

### 사용자 시나리오

```kotlin
// Navigation.kt
else -> {
    val viewModel: PlayerViewModel = hiltViewModel()
    
    when (state.script) {
        null -> LoadingView()
        else -> StageScreen(
            script = state.script,  // 변환된 script 전달
            onScenarioSelectClick = onScenarioSelectClick
        )
    }
}
```

**동작:**

1. PlayerViewModel이 TimelineItems 로드
2. TimelineToScriptConverter로 변환
3. StageScreen에 script 전달
4. `LaunchedEffect(script)` → `viewModel.setInitialScript(script)`
5. 재생 ✅

---

## 🔄 데이터 플로우

### 템플릿

```
StageTestScenario.createPlaygroundScenario()
  ↓
ViewModel.currentScript (자동 설정)
  ↓
StageScreen(script = null)
  ↓
viewModel.state.currentScript 사용
  ↓
재생 ✅
```

### 사용자

```
DB: TimelineItems
  ↓
PlayerViewModel: converter.convert()
  ↓
TheaterScript
  ↓
StageScreen(script = theaterScript)
  ↓
LaunchedEffect → viewModel.setInitialScript()
  ↓
재생 ✅
```

**✅ 최종적으로 모두 StageScreen!**

---

## 🎯 테스트

### 1. 템플릿 시나리오

```
1. 시나리오 목록 → "놀이터" 선택
2. StageScreen 표시 ✅
3. [시나리오 선택] 버튼 작동 ✅
```

### 2. 사용자 시나리오

```
1. [+ 새로 만들기] → 모듈 추가 → 저장
2. "첫 만남" 선택
3. 로딩 → StageScreen 표시 ✅
4. [시나리오 선택] 버튼 작동 ✅
```

### 3. 화면 전환

```
템플릿 → 사용자 → 템플릿 → 사용자
→ 모두 동일한 StageScreen ✅
```

---

## 📂 파일 구조 (정리됨)

### Before

```
app/src/main/java/com/example/cutestage/
├─ stage/
│  ├─ StageScreen.kt           (270줄)
│  └─ StageView.kt
└─ ui/
   └─ player/
      ├─ PlayerScreen.kt        (350줄) ← 중복!
      └─ PlayerViewModel.kt
```

### After

```
app/src/main/java/com/example/cutestage/
├─ stage/
│  ├─ StageScreen.kt           (280줄) ← 통합!
│  └─ StageView.kt
└─ ui/
   └─ player/
      └─ PlayerViewModel.kt     (변환만 담당)
```

**✅ 깔끔!**

---

## 🎉 결론

**StageScreen과 PlayerScreen을 하나로 통합 완료!**

✅ 310줄 코드 감소 (50%)  
✅ 중복 제거  
✅ 유지보수성 향상  
✅ 일관성 확보  
✅ 확장성 증가

**질문에 대한 답: 분리할 이유가 전혀 없었습니다!**

이제 모든 시나리오는 StageScreen 하나로 재생됩니다. 템플릿이든 사용자 생성이든 완전히 동일한 방식으로 작동합니다.

**완벽하게 통합되었습니다!** 🎊
