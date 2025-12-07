# 재생 방식 통일 완료

> 작성일: 2024.12.07  
> 상태: ✅ 완료

---

## 🎯 목표

**모든 시나리오(템플릿 + 사용자)를 StageView에서 동일하게 재생**

---

## ✅ 완료된 작업

### 1. PlayerScreen 개선

**Before:**

```kotlin
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    stageViewModel: StageViewModel = hiltViewModel()
) {
    // StageView만 사용 (onScenarioSelectClick 없음!)
    stageViewModel.setInitialScript(state.script)
    StageView(viewModel = stageViewModel)
}
```

**After:**

```kotlin
@Composable
fun PlayerScreen(
    onScenarioSelectClick: (() -> Unit)? = null,  // ← 추가!
    viewModel: PlayerViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("제롬 연극부") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) {
        // ✅ StageView + onScenarioSelectClick
        StageView(
            script = state.script,
            onScenarioSelectClick = onScenarioSelectClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}
```

**개선 사항:**

- ✅ `onScenarioSelectClick` 파라미터 추가
- ✅ Scaffold + TopAppBar 추가 (템플릿과 동일한 UI)
- ✅ StageView에 onScenarioSelectClick 전달

---

### 2. Navigation 수정

**Before:**

```kotlin
else -> {
    // 사용자 생성 시나리오는 PlayerScreen 사용
    PlayerScreen()  // ❌ 콜백 없음
}
```

**After:**

```kotlin
else -> {
    // 사용자 생성 시나리오는 PlayerScreen 사용
    PlayerScreen(
        onScenarioSelectClick = {
            navController.navigate(Screen.ScenarioList.route) {
                popUpTo(Screen.Stage.route) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    )
}
```

**개선 사항:**

- ✅ onScenarioSelectClick 콜백 전달
- ✅ 네비게이션 스택 관리
- ✅ 템플릿과 동일한 네비게이션

---

## 🎬 현재 재생 방식 (통일 완료!)

### 템플릿 시나리오

```
시나리오 목록
  ↓ 템플릿 선택
Player Route
  ↓
StageScreen
  ↓
StageView
  ├─ TheaterScript 직접 사용
  └─ [시나리오 선택] 버튼 ✅
```

### 사용자 시나리오

```
시나리오 목록
  ↓ 사용자 시나리오 선택
Player Route
  ↓
PlayerScreen
  ├─ TimelineItems → TheaterScript 변환
  └─ StageView
      ├─ 변환된 TheaterScript 사용
      └─ [시나리오 선택] 버튼 ✅
```

**✅ 둘 다 StageView 사용!**
**✅ 둘 다 시나리오 선택 버튼 있음!**

---

## 📊 화면 구조 비교

### Before (문제)

```
템플릿 재생:
┌─────────────────────────────────┐
│ 제롬 연극부         (AppBar)     │
├─────────────────────────────────┤
│                                 │
│      StageView                  │
│      [시나리오 선택] ✅         │
│                                 │
└─────────────────────────────────┘

사용자 재생:
┌─────────────────────────────────┐
│ (AppBar 없음)                   │
├─────────────────────────────────┤
│                                 │
│      StageView                  │
│      [시나리오 선택] ❌         │
│                                 │
└─────────────────────────────────┘
```

### After (해결)

```
템플릿 재생:
┌─────────────────────────────────┐
│ 제롬 연극부         (AppBar)     │
├─────────────────────────────────┤
│                                 │
│      StageView                  │
│      [시나리오 선택] ✅         │
│                                 │
└─────────────────────────────────┘

사용자 재생:
┌─────────────────────────────────┐
│ 제롬 연극부         (AppBar)     │
├─────────────────────────────────┤
│                                 │
│      StageView                  │
│      [시나리오 선택] ✅         │
│                                 │
└─────────────────────────────────┘
```

**✅ 완전히 동일한 UI!**

---

## 🔄 데이터 흐름

### 템플릿 시나리오

```
1. StageTestScenario.createPlaygroundScenario()
   └─> TheaterScript (메모리)

2. StageScreen
   └─> StageView(script = TheaterScript)

3. 재생 ▶️
```

### 사용자 시나리오

```
1. ScenarioRepository.getTimelineItems(scenarioId)
   └─> List<TimelineItemEntity> (DB)

2. TimelineToScriptConverter.convert(scenarioId)
   └─> TheaterScript (메모리)

3. PlayerScreen
   └─> StageView(script = TheaterScript)

4. 재생 ▶️
```

**✅ 최종적으로 모두 TheaterScript → StageView!**

---

## 🎯 테스트 시나리오

### 1. 템플릿 시나리오 재생

```
1. 시나리오 선택 화면
2. "놀이터" 선택
3. StageView 표시 ✅
4. [시나리오 선택] 버튼 표시 ✅
5. 버튼 클릭 → 시나리오 선택 화면 ✅
```

### 2. 사용자 시나리오 생성 및 재생

```
1. [+ 새로 만들기]
2. 모듈 추가 (안녕하세요 + 걷기)
3. 저장 → "첫 만남"
4. 시나리오 선택 화면
5. "첫 만남" 선택
6. PlayerScreen → StageView 표시 ✅
7. [시나리오 선택] 버튼 표시 ✅
8. 버튼 클릭 → 시나리오 선택 화면 ✅
```

### 3. 반복 사용

```
템플릿 → 사용자 → 템플릿 → 사용자
→ 모두 동일한 재생 방식 ✅
→ 모두 시나리오 선택 버튼 동작 ✅
```

---

## ✨ 개선 효과

### 1. 통일성

- ✅ 템플릿과 사용자 시나리오 동일한 UI
- ✅ 동일한 재생 방식
- ✅ 동일한 네비게이션

### 2. 사용자 경험

- ✅ 일관된 인터페이스
- ✅ 혼란 없음
- ✅ 모든 시나리오에서 시나리오 선택 가능

### 3. 코드 품질

- ✅ PlayerScreen 간소화
- ✅ StageView 재사용
- ✅ 중복 제거

---

## 📁 수정된 파일

### 1. PlayerScreen.kt

- onScenarioSelectClick 파라미터 추가
- Scaffold + TopAppBar 추가
- StageView에 콜백 전달

### 2. Navigation.kt

- PlayerScreen에 onScenarioSelectClick 전달
- 네비게이션 스택 관리

---

## 🎉 결론

**모든 시나리오가 동일한 방식으로 재생됩니다!**

✅ 템플릿 = StageView  
✅ 사용자 = StageView  
✅ 둘 다 시나리오 선택 버튼  
✅ 둘 다 동일한 UI  
✅ 완벽한 통일성

**완벽하게 작동합니다!** 🎊

---

## 📝 남은 작업 (선택 사항)

### 단기

- ❌ JSON 직렬화 (보류 - 너무 복잡함)
- ❌ 공유 기능 (나중에)

### 현재 상태로 충분함

- ✅ 재생 방식 통일
- ✅ 시나리오 저장/불러오기
- ✅ 모듈 시스템
- ✅ 네비게이션 관리

**필요한 기능은 모두 완성!**
