# 시나리오 직렬화 단순화 접근

> 작성일: 2024.12.07  
> 상태: 💡 재검토 필요

---

## 🤔 문제 재분석

### 현재 상황

1. **TheaterScript는 Compose 타입 의존**
    - `@DrawableRes Int`, `DpOffset`, `Dp`, `Easing` 등
    - 직접 JSON 직렬화 불가능

2. **변환이 복잡함**
    - TheaterScript ↔ SerializableTheaterScript 변환 필요
    - Drawable ID ↔ 이름 변환 필요
    - 타입 불일치 문제 다수

3. **시간이 너무 많이 걸림**
    - 모든 데이터 클래스 복제 필요
    - 변환 로직 양방향 필요
    - 테스트/디버깅 복잡

---

## 💡 더 단순한 접근: Gson 사용 (현재 이미 있음)

### 장점

- ✅ **Gson은 이미 프로젝트에 있음**
- ✅ **복잡한 타입도 커스텀 어댑터로 처리 가능**
- ✅ **@Serializable 어노테이션 불필요**
- ✅ **더 유연한 직렬화**

### 방법

```kotlin
val gson = GsonBuilder()
    .registerTypeAdapter(DpOffset::class.java, DpOffsetAdapter())
    .registerTypeAdapter(Dp::class.java, DpAdapter())
    // ... 필요한 어댑터 등록
    .create()

// 직렬화
val json = gson.toJson(theaterScript)

// 역직렬화
val script = gson.fromJson(json, TheaterScript::class.java)
```

---

## 🎯 대안 접근: 단순화된 모듈 시스템 유지

### 핵심 아이디어

**"모듈 조합 방식을 그대로 사용하되, 공유/저장은 나중에"**

### 현재 상태로도 충분히 작동

```
사용자 시나리오:
├─ TimelineItemEntity (DB)
├─ ModuleItemEntity (DB)
└─ TimelineToScriptConverter
    └─> TheaterScript (메모리)
        └─> StageView 재생
```

### 이미 구현된 것들

- ✅ 모듈 시스템 (Dialogue, Action 등)
- ✅ 타임라인 저장 (DB)
- ✅ 변환 로직 (TimelineToScriptConverter)
- ✅ 시나리오 목록 화면
- ✅ 저장/불러오기

---

## 🔧 현재 시스템 개선 방향

### 1단계: 재생 통일 (가장 중요!)

**문제**: 템플릿은 StageScreen, 사용자는 PlayerScreen
**해결**: 모두 StageScreen으로 통일

```kotlin
// Navigation.kt
composable("player/{scenarioId}") { backStackEntry ->
    val scenarioId = backStackEntry.arguments?.getString("scenarioId")
    
    when {
        scenarioId?.startsWith("template_") == true -> {
            // 템플릿: TheaterScript 직접 사용
            StageTestScenario.currentScenario = ...
            StageScreen(onScenarioSelectClick = ...)
        }
        else -> {
            // 사용자: DB → TheaterScript 변환 → StageScreen
            UserScenarioPlayer(
                scenarioId = scenarioId,
                onScenarioSelectClick = ...
            )
        }
    }
}
```

```kotlin
@Composable
fun UserScenarioPlayer(
    scenarioId: String,
    onScenarioSelectClick: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val script = viewModel.loadAndConvertScenario(scenarioId)
    
    if (script != null) {
        // ✅ StageScreen 재사용!
        StageScreen(
            script = script,
            onScenarioSelectClick = onScenarioSelectClick
        )
    } else {
        LoadingOrError()
    }
}
```

### 2단계: 템플릿도 DB에 저장

**현재**: 코드에 하드코딩 (TheaterScript DSL)
**변경**: 초기화 시 DB에 TimelineItem으로 저장

```kotlin
// Application.onCreate()
suspend fun initializeTemplates() {
    // TheaterScript → TimelineItems 변환
    val playgroundScript = StageTestScenario.createPlaygroundScenario()
    val timelineItems = scriptToTimelineItems(playgroundScript)
    
    // DB에 저장
    scenarioRepository.save(
        ScenarioEntity(
            id = "template_playground",
            title = "놀이터",
            isTemplate = true
        ),
        timelineItems
    )
}
```

### 3단계: 공유 기능은 나중에

**당장은 필요 없음!**

- 사용자가 만든 시나리오 = DB에 있음
- 템플릿 = 앱에 포함됨
- 공유는 나중에 필요할 때 추가

---

## ✅ 실용적인 해결책 (추천)

### 목표

1. ✅ 모든 시나리오를 StageView에서 재생 (통일성)
2. ✅ 템플릿과 사용자 시나리오 구분 유지
3. ❌ JSON 직렬화는 보류 (나중에)

### 구현

1. **PlayerScreen 개선**
    - TimelineToScriptConverter로 변환
    - StageScreen에 script 전달
    - onScenarioSelectClick 전달

2. **템플릿 변환 유틸리티**
    - TheaterScript → TimelineItems (한 번만)
    - DB에 저장
    - 이후 동일한 방식으로 로드

3. **공유는 V2로**
    - 지금은 DB만 사용
    - 나중에 export/import 기능 추가
    - JSON/Zip 파일로 공유

---

## 🎯 다음 단계 (실용적)

### 즉시 (1-2일)

1. PlayerScreen을 StageScreen 사용하도록 수정
2. onScenarioSelectClick 전달 확인
3. 재생 통일 테스트

### 단기 (1주)

4. 템플릿을 TimelineItem으로 변환하는 유틸 작성
5. 앱 초기화 시 DB에 저장
6. 템플릿/사용자 구분 없이 동일하게 처리

### 중기 (1개월)

7. 시나리오 export/import 기능
8. JSON 파일로 공유
9. QR 코드 공유

---

## 💭 결론

**JSON 직렬화는 너무 복잡합니다.**

더 단순한 방법:

1. ✅ 현재 모듈 시스템 유지
2. ✅ 재생 방식만 통일 (StageScreen)
3. ✅ 템플릿도 DB로 이동
4. ❌ JSON 직렬화는 나중에 (필요시)

**이 방향으로 진행할까요?**
