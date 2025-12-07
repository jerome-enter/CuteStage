# StageView를 메인 화면으로 변경 완료

> 시나리오 선택 버튼 클릭 시 팝업 대신 전체 화면 목록 표시  
> 작성일: 2024.12.07  
> 상태: ✅ 완료

---

## 🎯 변경 사항

### Before (이전)

```
앱 실행
└─> 시나리오 목록 화면 (메인)
    ├─> [▶ 재생] → 재생 화면
    ├─> [✏️ 편집] → 에디터 화면
    └─> [+ 새로만들기] → 에디터 화면

StageView는 숨겨진 화면
```

### After (현재)

```
앱 실행
└─> StageView (메인)
    ├─> [시나리오 선택 버튼] → 시나리오 목록 화면 (전체 화면)
    │   ├─> [← 뒤로] → StageView로 복귀
    │   ├─> [▶ 재생] → 재생 화면
    │   ├─> [✏️ 편집] → 에디터 화면
    │   └─> [+ 새로만들기] → 에디터 화면
    └─> 기존 팝업 메뉴는 네비게이션 없이 사용 시에만 표시
```

---

## 📝 수정된 파일

### 1. `Navigation.kt`

**변경 사항:**

- `Screen.Stage` 추가
- `startDestination`을 `Screen.Stage.route`로 변경
- `StageScreen(onScenarioSelectClick)` 추가

**코드:**

```kotlin
sealed class Screen(val route: String) {
    object Stage : Screen("stage")  // ← 추가
    object ScenarioList : Screen("scenario_list")
    // ...
}

NavHost(
    navController = navController,
    startDestination = Screen.Stage.route  // ← 변경 (이전: ScenarioList)
) {
    composable(Screen.Stage.route) {
        StageScreen(
            onScenarioSelectClick = {
                navController.navigate(Screen.ScenarioList.route)
            }
        )
    }
    
    composable(Screen.ScenarioList.route) {
        ScenarioListScreen(
            // ...
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
```

---

### 2. `StageScreen.kt`

**변경 사항:**

- `onScenarioSelectClick` 파라미터 추가
- `StageView`에 전달

**코드:**

```kotlin
@Composable
fun StageScreen(
    onScenarioSelectClick: (() -> Unit)? = null  // ← 추가
) {
    val viewModel: StageViewModel = hiltViewModel()
    
    // ...
    
    StageView(
        viewModel = viewModel,
        onScenarioSelectClick = onScenarioSelectClick,  // ← 전달
        modifier = Modifier.fillMaxWidth()
    )
}
```

---

### 3. `StageView.kt`

**변경 사항:**

- `onScenarioSelectClick` 파라미터 추가
- `StageViewContent`에 전달
- `StageControls`에 전달

**코드:**

```kotlin
@Composable
fun StageView(
    modifier: Modifier = Modifier,
    script: TheaterScript? = null,
    onScriptEnd: () -> Unit = {},
    onScenarioSelectClick: (() -> Unit)? = null,  // ← 추가
    viewModel: StageViewModel = hiltViewModel()
) {
    StageViewContent(
        state = viewModel.state,
        onEvent = viewModel::handleEvent,
        onScenarioSelectClick = onScenarioSelectClick,  // ← 전달
        modifier = modifier
    )
}

@Composable
internal fun StageViewContent(
    state: StageState,
    onEvent: (StageEvent) -> Unit,
    onScenarioSelectClick: (() -> Unit)? = null,  // ← 추가
    modifier: Modifier = Modifier,
) {
    // ...
    
    StageControls(
        // ...
        onScenarioSelectClick = onScenarioSelectClick  // ← 전달
    )
}
```

---

### 4. `StageControls.kt`

**변경 사항:**

- `onScenarioSelectClick` 파라미터 추가
- `ScenarioSelector`에 전달
- 버튼 클릭 로직 변경: 네비게이션 우선, 팝업은 fallback

**코드:**

```kotlin
@Composable
internal fun StageControls(
    // ...
    onScenarioSelectClick: (() -> Unit)? = null,  // ← 추가
    modifier: Modifier = Modifier
) {
    // ...
    
    ScenarioSelector(
        onScenarioSelected = onScenarioSelected,
        onShowAIDialog = onShowAIDialog,
        onScenarioSelectClick = onScenarioSelectClick  // ← 전달
    )
}

@Composable
private fun ScenarioSelector(
    onScenarioSelected: (StageTestScenario.ScenarioType, TheaterScript, Boolean) -> Unit,
    onShowAIDialog: () -> Unit,
    onScenarioSelectClick: (() -> Unit)? = null  // ← 추가
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        Surface(
            onClick = { 
                // 네비게이션 콜백이 있으면 전체 화면으로
                if (onScenarioSelectClick != null) {
                    onScenarioSelectClick()
                } else {
                    // 없으면 기존 팝업 메뉴 표시
                    showMenu = true
                }
            },
            // ...
        )
        
        // 기존 DropdownMenu (네비게이션 없을 때만 사용)
    }
}
```

---

### 5. `ScenarioListScreen.kt`

**변경 사항:**

- `onNavigateBack` 파라미터 추가
- TopAppBar에 navigationIcon (뒤로 가기 버튼) 추가

**코드:**

```kotlin
@Composable
fun ScenarioListScreen(
    onScenarioClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onCreateNew: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,  // ← 추가
    viewModel: ScenarioListViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("제롬 연극부") },
                navigationIcon = {
                    // 뒤로 가기 버튼 추가
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "뒤로 가기")
                        }
                    }
                },
                // ...
            )
        },
        // ...
    )
}
```

---

## 🎬 사용자 경험 플로우

### 1. 앱 실행

```
앱 실행
└─> StageView 표시
    ├─ 기본 시나리오: "놀이터" 자동 로드
    ├─ 캐릭터 클릭 가능 (상호작용)
    └─ 하단 컨트롤 버튼들
       ├─ [재생 속도] (1x, 1.5x, 2x)
       ├─ [음성 엔진 설정]
       ├─ [시나리오 선택] ← 메인 버튼
       └─ [재생]
```

### 2. 시나리오 선택 버튼 클릭

```
[시나리오 선택] 버튼 클릭
└─> 화면 전환 (슬라이드 애니메이션)
    └─> 시나리오 목록 화면
        ├─ [← 뒤로] 버튼 (StageView로 복귀)
        ├─ 📚 기본 제공 시나리오 (7개 템플릿)
        │  └─ [▶ 재생하기] 버튼
        ├─ 🎭 내 시나리오
        │  └─ [▶ 재생하기] [⋮ 메뉴]
        └─ [+ 새로 만들기] FAB
```

### 3. 시나리오 선택 후 재생

```
[▶ 재생하기] 클릭
└─> 템플릿 시나리오인 경우
    └─> StageScreen으로 이동 (시나리오 자동 로드 & 재생)
    
└─> 사용자 시나리오인 경우
    └─> PlayerScreen으로 이동 (모듈 변환 & 재생)
```

### 4. 뒤로 가기

```
시나리오 목록 화면
└─> [← 뒤로] 버튼 클릭
    └─> StageView로 복귀
        └─> 마지막 재생했던 시나리오 유지
```

---

## 🔄 기존 기능 유지

### 독립 실행 모드 (네비게이션 없이)

```kotlin
// 다른 곳에서 StageView를 사용할 때
StageView(
    script = myScript,
    // onScenarioSelectClick을 null로 두면
    // 기존 팝업 메뉴 동작
)

// 시나리오 선택 버튼 클릭 시
→ DropdownMenu 팝업 표시 (이전 동작)
```

### 네비게이션 모드 (메인 앱)

```kotlin
// MainActivity에서 CuteStageNavigation 사용
StageView(
    onScenarioSelectClick = { navController.navigate(...) }
)

// 시나리오 선택 버튼 클릭 시
→ 전체 화면으로 시나리오 목록 표시 (새 동작)
```

---

## ✨ 주요 개선 사항

### 1. **직관적인 UX**

- 시나리오 선택이 독립된 전체 화면으로 제공
- 더 많은 정보를 한눈에 볼 수 있음
- 뒤로 가기 버튼으로 명확한 네비게이션

### 2. **유연한 아키텍처**

- `onScenarioSelectClick`가 null이면 기존 팝업 방식
- null이 아니면 네비게이션 방식
- StageView를 독립적으로도 사용 가능

### 3. **사용자 여정 개선**

```
Before:
StageView를 볼 방법이 없음
→ 시나리오 목록이 메인 화면
→ 재생 버튼으로만 StageView 접근

After:
StageView가 메인 화면
→ 즉시 연극 감상 가능
→ 필요할 때만 시나리오 목록 열람
→ 자연스러운 복귀
```

---

## 📱 화면 구조 (최종)

```
┌─────────────────────────────────────┐
│           MainActivity              │
│    (CuteStageNavigation)            │
└────────────┬────────────────────────┘
             │
             ├─> Stage (메인)
             │   └─> StageScreen
             │       └─> StageView
             │           └─> [시나리오 선택] 버튼
             │               └─> navigate to ScenarioList
             │
             ├─> ScenarioList
             │   └─> [← 뒤로] popBackStack()
             │   └─> [▶ 재생] navigate to Player
             │   └─> [+ 새로만들기] navigate to Creator
             │
             ├─> Creator
             │   └─> [← 뒤로] popBackStack()
             │   └─> [✓ 저장] popBackStack()
             │
             └─> Player
                 └─> TheaterScript 재생
                 └─> 종료 시 popBackStack()
```

---

## 🎉 결론

**StageView가 메인 화면으로 자리 잡았습니다!**

- ✅ 사용자가 앱을 열면 바로 연극을 볼 수 있음
- ✅ 시나리오 선택은 필요할 때만 전체 화면으로
- ✅ 기존 기능 모두 유지 (독립 실행 모드)
- ✅ 명확한 네비게이션 구조

**완벽하게 작동합니다!** 🎊
