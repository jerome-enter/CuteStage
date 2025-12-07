# 시나리오 저장 기능 구현 진행 상황

> 단계적 구현 중  
> 현재 진행: Step 3/6 완료

---

## ✅ 완료된 단계

### Step 1: DB 스키마 확장 ✅

- [x] `ScenarioEntity` 생성
- [x] `TimelineItemEntity` 생성
- [x] Database 버전 v1 → v2 업그레이드
- [x] `ScenarioDao` 구현
- [x] DatabaseModule에 DAO 추가

### Step 2: ScenarioRepository 구현 ✅

- [x] CRUD 메서드 구현
- [x] `createScenario()` - 새 시나리오 생성
- [x] `updateScenario()` - 기존 시나리오 업데이트
- [x] `deleteScenario()` - 시나리오 삭제
- [x] `getScenarioWithTimeline()` - 타임라인과 함께 조회
- [x] Duration 추정 로직

### Step 3: 저장 다이얼로그 UI ✅

- [x] ViewModel에 저장 관련 상태 추가
- [x] `showSaveDialog()` 함수
- [x] `saveScenario(onSuccess)` 함수
- [x] `loadScenarioForEdit()` 함수 (편집 모드)
- [x] SaveScenarioDialog Composable 생성
    - 제목 입력 필드
    - 설명 입력 필드
    - 모듈 개수 표시
    - 저장 중 로딩 표시
- [x] 빌드 성공 확인

---

## 📊 현재 작동 방식

### 저장 플로우

```
1. 사용자가 타임라인에 모듈 추가
   ├─ 💬 안녕하세요
   ├─ 🏃 걷기
   └─ 💬 반가워요

2. [✓ 저장] 버튼 클릭
   └─> SaveScenarioDialog 표시

3. 다이얼로그에서 정보 입력
   ├─ 제목: "첫 만남"
   ├─ 설명: "공원에서의 첫 만남"
   └─ 모듈 개수: 3개 (자동)
       예상 시간: 약 9초 (자동)

4. [저장] 버튼 클릭
   └─> viewModel.saveScenario()
       ├─> scenarioRepository.createScenario()
       │   ├─> ScenarioEntity INSERT
       │   └─> TimelineItemEntity 3개 INSERT
       └─> onSuccess(scenarioId)
           └─> onNavigateBack()  // 홈으로 이동
```

### DB 구조

```sql
-- scenarios 테이블
CREATE TABLE scenarios (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    moduleCount INTEGER,
    estimatedDuration INTEGER,
    createdAt INTEGER,
    updatedAt INTEGER,
    isTemplate INTEGER
);

-- timeline_items 테이블
CREATE TABLE timeline_items (
    id TEXT PRIMARY KEY,
    scenarioId TEXT,  -- FOREIGN KEY
    moduleItemId TEXT,
    order INTEGER,
    customParametersJson TEXT,
    FOREIGN KEY(scenarioId) REFERENCES scenarios(id) ON DELETE CASCADE
);
```

---

## 🔜 다음 단계

### Step 4: 시나리오 목록 화면 (Next)

**목표: 저장된 시나리오를 볼 수 있게**

```kotlin
// 1. ScenarioListScreen.kt 생성
@Composable
fun ScenarioListScreen(
    onScenarioClick: (String) -> Unit,  // 재생
    onEditClick: (String) -> Unit,      // 편집
    onDeleteClick: (String) -> Unit,    // 삭제
    onCreateNew: () -> Unit             // 새로 만들기
)

// 2. ScenarioListViewModel.kt 생성
@HiltViewModel
class ScenarioListViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository
) {
    val scenarios = scenarioRepository.getUserScenarios()
        .stateIn(...)
}

// 3. Navigation 업데이트
sealed class Screen {
    object Main
    object ScenarioList  // ← 추가
    object Creator
}
```

**UI 레이아웃:**

```
┌─────────────────────────────────────┐
│ 제롬 연극부              [+ 새로만들기]│
├─────────────────────────────────────┤
│                                     │
│ ┌───────────────────────────────┐  │
│ │ 📝 첫 만남                ⋮  │  │
│ │ 3개 모듈 · 2024.12.07        │  │
│ │ [▶ 재생]                     │  │
│ └───────────────────────────────┘  │
│                                     │
│ [비어있을 때]                       │
│ 아직 시나리오가 없어요              │
└─────────────────────────────────────┘
```

### Step 5: 재생 연결

**목표: 시나리오 클릭 시 StageView에서 재생**

```kotlin
// TimelineToScriptConverter.kt
class TimelineToScriptConverter @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val scenarioRepository: ScenarioRepository
) {
    suspend fun convert(scenarioId: String): TheaterScript {
        val timeline = scenarioRepository.getTimelineItems(scenarioId)
        
        val scenes = timeline.map { item ->
            val module = moduleRepository.getModuleItemById(item.moduleItemId)
            when (module?.typeId) {
                "dialogue" -> buildDialogueScene(module)
                "action" -> buildActionScene(module)
                // ...
            }
        }
        
        return TheaterScript(scenes = scenes)
    }
}
```

### Step 6: 편집 & 삭제

**목표: 메뉴에서 편집/삭제 가능**

```kotlin
// 편집: Creator 재진입
onEditClick = { scenarioId ->
    navController.navigate("creator?scenarioId=$scenarioId")
}

// 삭제: 확인 다이얼로그 후 삭제
onDeleteClick = { scenarioId ->
    showDeleteDialog {
        viewModel.deleteScenario(scenarioId)
    }
}
```

---

## 🧪 테스트 방법

### 현재 구현 테스트

1. **앱 실행**
2. **시나리오 생성 진입**
    - FAB "시나리오 생성" 클릭
3. **모듈 추가**
    - "안녕하세요" 추가
    - "걷기" 추가
    - "반가워요" 추가
4. **저장 버튼 클릭**
    - ✓ 버튼 활성화 확인 (3개 모듈)
5. **다이얼로그 확인**
    - 제목 입력: "첫 만남"
    - 설명 입력: "공원에서의 첫 만남"
    - "모듈 개수: 3개" 표시
    - "예상 재생 시간: 약 9초" 표시
6. **저장 실행**
    - [저장] 버튼 클릭
    - 로딩 인디케이터 확인
    - 홈 화면으로 이동 확인

### DB 확인 (Android Studio Database Inspector)

```sql
-- scenarios 테이블 확인
SELECT * FROM scenarios;

-- timeline_items 테이블 확인
SELECT * FROM timeline_items;
```

---

## 💡 개선 아이디어

### 저장 성공 피드백

```kotlin
// 현재: 그냥 홈으로 이동
onSuccess(scenarioId) -> onNavigateBack()

// 개선: Snackbar 표시
onSuccess(scenarioId) -> {
    showSnackbar("'첫 만남'이 저장되었습니다")
    onNavigateBack()
}
```

### 자동 저장

```kotlin
// 30초마다 자동 저장 (임시)
LaunchedEffect(state.timelineItems) {
    delay(30000)
    if (state.timelineItems.isNotEmpty()) {
        autoSave()
    }
}
```

### 제목 자동 제안

```kotlin
// 첫 번째 대사를 제목으로 제안
val suggestedTitle = state.timelineItems
    .firstOrNull { it.moduleItem.typeId == "dialogue" }
    ?.moduleItem?.name
    ?: "새 시나리오"
```

---

## 📁 생성된 파일

```
app/src/main/java/com/example/cutestage/
├─ data/
│  ├─ scenario/
│  │  ├─ ScenarioEntities.kt         ✅ (Step 1)
│  │  ├─ ScenarioDao.kt              ✅ (Step 1)
│  │  └─ ScenarioRepository.kt       ✅ (Step 2)
│  └─ CuteStageDatabase.kt           🔄 (v2로 업그레이드)
├─ di/
│  └─ DatabaseModule.kt               🔄 (ScenarioDao 추가)
└─ ui/
   └─ creator/
      ├─ ScenarioCreatorScreen.kt    🔄 (SaveDialog 추가)
      └─ ScenarioCreatorViewModel.kt 🔄 (저장 로직 추가)
```

---

## 다음 작업 시작

**Step 4부터 계속:**

```bash
# 다음 명령어:
"Step 4: 시나리오 목록 화면을 구현해줘"
```

---

**진행 상황: 50% (3/6 단계 완료)** 🎉
