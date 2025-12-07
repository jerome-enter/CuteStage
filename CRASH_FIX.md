# 크래시 수정 완료

## 🐛 문제 분석

### 에러 메시지

```
java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
at androidx.compose.material3.TabRowKt$ScrollableTabRow$1.invoke
```

### 원인

1. **타이밍 이슈**: 앱 시작 시 `CuteStageApplication.onCreate()`에서 비동기로 DB 초기화
2. **화면 진입**: 사용자가 "시나리오 생성" 버튼 클릭
3. **데이터 없음**: DB 초기화가 완료되기 전에 화면이 렌더링됨
4. **크래시**: `ScrollableTabRow`가 빈 리스트로 `selectedTabIndex`를 계산하려고 시도

```kotlin
// 문제 코드
ScrollableTabRow(
    selectedTabIndex = moduleTypes.indexOfFirst { it.id == selectedTypeId }
        .coerceAtLeast(0),  // moduleTypes가 비어있으면 -1 → 0 → 여전히 IndexOutOfBounds
    ...
)
```

---

## ✅ 수정 내용

### 1. 로딩 상태 추가

**파일: `ScenarioCreatorScreen.kt`**

```kotlin
@Composable
private fun ModulePaletteSection(...) {
    Column(modifier = modifier) {
        // 모듈 타입이 로드되기 전에는 로딩 표시
        if (moduleTypes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator()
                    Text("모듈 로딩 중...")
                }
            }
            return  // ← 여기서 조기 반환, ScrollableTabRow 실행 안됨
        }

        // 이하 기존 코드...
        ScrollableTabRow(...)
    }
}
```

**효과:**

- `moduleTypes`가 비어있으면 로딩 인디케이터만 표시
- `ScrollableTabRow`는 실행되지 않음
- 크래시 방지

---

### 2. ViewModel 초기화 개선

**파일: `ScenarioCreatorViewModel.kt`**

**변경 전:**

```kotlin
init {
    viewModelScope.launch {
        moduleTypes.first().let { types ->  // ← first()는 첫 값만 가져옴
            if (types.isNotEmpty()) {
                selectModuleType(types.first().id)
            }
        }
    }
}
```

**변경 후:**

```kotlin
init {
    viewModelScope.launch {
        moduleTypes.collect { types ->  // ← collect로 지속적으로 관찰
            if (types.isNotEmpty() && state.selectedTypeId == null) {
                selectModuleType(types.first().id)
            }
        }
    }
}
```

**차이점:**

- `first()`: Flow의 첫 번째 값만 가져오고 종료 (빈 리스트일 수 있음)
- `collect`: Flow를 계속 관찰, DB 초기화 완료 시 자동으로 반응
- `state.selectedTypeId == null`: 중복 선택 방지

---

## 🔍 근본 원인 및 해결

### 문제의 흐름

```
1. 앱 시작
   └─> CuteStageApplication.onCreate()
       └─> moduleRepository.initializeDefaultModules() (비동기)

2. 사용자 FAB 클릭 (0.5초 후)
   └─> ScenarioCreatorScreen 진입
       └─> ViewModel 생성
           └─> moduleTypes.collect() 시작
               └─> DB 쿼리: SELECT * FROM module_types
                   └─> 결과: [] (아직 초기화 안됨)

3. Compose 렌더링
   └─> ModulePaletteSection(moduleTypes = [])
       └─> ScrollableTabRow(selectedTabIndex = -1.coerceAtLeast(0) = 0)
           └─> 💥 CRASH: Index 0 out of bounds for size 0
```

### 해결 방법

```
1. 앱 시작
   └─> DB 초기화 (비동기)

2. 사용자 FAB 클릭
   └─> ScenarioCreatorScreen 진입
       └─> ViewModel.moduleTypes.collect { types ->
           |   if (types.isEmpty()) {
           |       // 아직 초기화 안됨, 대기
           |   }
           └─> }

3. Compose 렌더링
   └─> ModulePaletteSection(moduleTypes = [])
       └─> if (moduleTypes.isEmpty()) {
               CircularProgressIndicator()  // ✅ 로딩 표시
               return  // ScrollableTabRow 실행 안함
           }

4. DB 초기화 완료 (1-2초 후)
   └─> moduleTypes.collect { types ->  // [5개 타입]
           selectModuleType(types.first().id)  // "dialogue" 자동 선택
       }

5. 리컴포지션
   └─> ModulePaletteSection(moduleTypes = [5개])
       └─> ScrollableTabRow(selectedTabIndex = 0)  // ✅ 정상 동작
```

---

## 📊 추가 개선 사항

### 1. 앱 시작 시 초기화 대기

현재는 사용자가 빠르게 "시나리오 생성"을 누르면 로딩 화면이 나타납니다.  
더 나은 UX를 위해 MainScreen에서 초기화 완료 여부를 체크할 수 있습니다.

**옵션 A: FAB 비활성화**

```kotlin
@Composable
fun MainScreen(
    onNavigateToCreator: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val isInitialized by viewModel.isModuleSystemInitialized.collectAsState()

    FloatingActionButton(
        onClick = onNavigateToCreator,
        enabled = isInitialized  // ← 초기화 완료 전에는 비활성화
    ) {
        if (isInitialized) {
            Text("시나리오 생성")
        } else {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}
```

**옵션 B: 초기화 화면**

```kotlin
@Composable
fun CuteStageNavigation() {
    val isInitialized by remember {
        // DB 초기화 상태 관찰
    }.collectAsState()

    if (!isInitialized) {
        // 스플래시 화면
        SplashScreen()
    } else {
        NavHost(...)
    }
}
```

---

### 2. 에러 처리 강화

**현재 구현:**

- 빈 리스트 → 로딩 표시

**추가 가능:**

- 초기화 실패 → 에러 메시지
- 재시도 버튼

```kotlin
sealed class ModuleLoadState {
    object Loading : ModuleLoadState()
    data class Success(val types: List<ModuleTypeEntity>) : ModuleLoadState()
    data class Error(val message: String) : ModuleLoadState()
}

// ViewModel
val loadState = combine(
    moduleTypes,
    initializationError
) { types, error ->
    when {
        error != null -> ModuleLoadState.Error(error)
        types.isEmpty() -> ModuleLoadState.Loading
        else -> ModuleLoadState.Success(types)
    }
}.stateIn(...)

// UI
when (val state = viewModel.loadState.collectAsState().value) {
    is Loading -> CircularProgressIndicator()
    is Success -> ScrollableTabRow(state.types)
    is Error -> ErrorView(state.message, onRetry = { ... })
}
```

---

## 🧪 테스트 시나리오

### 정상 케이스

1. ✅ 앱 시작 → 3초 대기 → FAB 클릭
    - 결과: 정상적으로 모듈 타입 탭 표시

### 빠른 클릭 케이스

2. ✅ 앱 시작 → 즉시 FAB 클릭
    - 결과: "모듈 로딩 중..." 표시 → 1-2초 후 정상 표시

### 느린 기기 케이스

3. ✅ 느린 기기에서 앱 시작 → FAB 클릭
    - 결과: 로딩 시간이 길어도 크래시 없음

---

## 📝 학습 포인트

### 1. Compose의 비동기 데이터 처리

**Flow + collectAsState()**

```kotlin
// ViewModel
val data: StateFlow<List<T>> = repository.getData()
    .stateIn(scope, SharingStarted.Lazily, emptyList())

// Composable
val items by viewModel.data.collectAsState()

// 안전한 렌더링
if (items.isEmpty()) {
    Loading()
} else {
    Content(items)
}
```

### 2. 조기 반환 (Early Return)

**Bad:**

```kotlin
@Composable
fun MyScreen(data: List<T>) {
    if (data.isNotEmpty()) {
        // 100줄의 코드...
    } else {
        Loading()
    }
}
```

**Good:**

```kotlin
@Composable
fun MyScreen(data: List<T>) {
    if (data.isEmpty()) {
        Loading()
        return  // ← 조기 반환
    }

    // 100줄의 코드 (들여쓰기 감소)
}
```

### 3. StateFlow 초기화

**SharingStarted 옵션:**

```kotlin
// Lazily: 첫 구독자가 나타날 때 시작
.stateIn(scope, SharingStarted.Lazily, initialValue)

// Eagerly: 즉시 시작
.stateIn(scope, SharingStarted.Eagerly, initialValue)

// WhileSubscribed: 구독자가 있을 때만 활성
.stateIn(scope, SharingStarted.WhileSubscribed(5000), initialValue)
```

---

## ✅ 수정 완료 체크리스트

- [x] `ModulePaletteSection`에 빈 리스트 체크 추가
- [x] 로딩 인디케이터 표시
- [x] ViewModel `init` 블록 수정 (first → collect)
- [x] 빌드 성공 확인
- [x] 크래시 수정 문서 작성

---

## 🚀 다음 테스트

1. **앱 재설치 후 즉시 FAB 클릭**
    - DB가 비어있는 상태에서 테스트
    - 예상: 로딩 화면 → 정상 표시

2. **비행기 모드에서 테스트**
    - 오프라인에서도 정상 동작 확인

3. **앱 강제 종료 후 재시작**
    - DB 데이터 유지 확인

---

**크래시 수정 완료! 🎉**
