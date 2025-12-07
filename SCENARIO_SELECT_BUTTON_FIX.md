# 시나리오 선택 버튼 두번째 클릭 문제 해결

> 작성일: 2024.12.07  
> 상태: ✅ 완료

---

## 🐛 문제

### 증상

```
1. StageView에서 [시나리오 선택] 버튼 클릭
   → ✅ 정상 동작: 시나리오 목록 화면으로 이동

2. 템플릿 시나리오 재생 (예: "놀이터")
   → StageView가 다시 표시됨

3. [시나리오 선택] 버튼 다시 클릭
   → ❌ 반응 없음! (클릭이 안됨)
```

### 원인

Player 화면에서 템플릿 시나리오를 재생할 때, `StageScreen()`을 **콜백 없이** 호출했기 때문

```kotlin
// Navigation.kt - Player composable
when (scenarioId) {
    "template_playground" -> {
        StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
        StageScreen()  // ❌ onScenarioSelectClick = null
    }
    // ...
}
```

이렇게 되면:

- `StageScreen(onScenarioSelectClick = null)`
- → `StageView(onScenarioSelectClick = null)`
- → `StageControls(onScenarioSelectClick = null)`
- → `ScenarioSelector(onScenarioSelectClick = null)`
- → `Surface(onClick = { null?.invoke() })` ← **아무 일도 안 함!**

---

## ✅ 해결

### 수정된 코드

**Navigation.kt - Player composable:**

```kotlin
// 시나리오 재생 화면
composable(
    route = "player/{scenarioId}",
    arguments = listOf(
        navArgument("scenarioId") {
            type = NavType.StringType
        }
    )
) { backStackEntry ->
    val scenarioId = backStackEntry.arguments?.getString("scenarioId")

    // 템플릿 시나리오면 기존 StageScreen 사용 (콜백 포함!)
    when (scenarioId) {
        "template_playground" -> {
            StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
            StageScreen(
                onScenarioSelectClick = {
                    navController.navigate(Screen.ScenarioList.route)
                }
            )
        }

        "template_basic" -> {
            StageTestScenario.currentScenario = StageTestScenario.ScenarioType.BASIC
            StageScreen(
                onScenarioSelectClick = {
                    navController.navigate(Screen.ScenarioList.route)
                }
            )
        }

        // ... 나머지 템플릿들도 동일하게 콜백 추가

        else -> {
            // 사용자 생성 시나리오는 PlayerScreen 사용
            PlayerScreen()
        }
    }
}
```

---

## 🎬 수정 전/후 비교

### Before (문제 발생)

```
메인 Stage
├─ StageScreen(onScenarioSelectClick = λ) ← 콜백 있음 ✅
└─> [시나리오 선택] 클릭 → 시나리오 목록
    └─> 템플릿 선택 → Player
        └─> StageScreen() ← 콜백 없음 ❌
            └─> [시나리오 선택] 클릭 → 반응 없음 💥
```

### After (정상 동작)

```
메인 Stage
├─ StageScreen(onScenarioSelectClick = λ) ← 콜백 있음 ✅
└─> [시나리오 선택] 클릭 → 시나리오 목록
    └─> 템플릿 선택 → Player
        └─> StageScreen(onScenarioSelectClick = λ) ← 콜백 있음 ✅
            └─> [시나리오 선택] 클릭 → 정상 동작 ✅
```

---

## 🎯 테스트 시나리오

### 1. 메인 화면에서 시나리오 선택

```
1. 앱 실행 → StageView (메인)
2. [시나리오 선택] 버튼 클릭
   → ✅ 시나리오 목록 화면으로 이동
```

### 2. 템플릿 시나리오 재생 후 시나리오 선택

```
1. 시나리오 목록에서 "놀이터" 선택
2. StageView가 표시됨 (놀이터 시나리오 재생)
3. [시나리오 선택] 버튼 클릭
   → ✅ 시나리오 목록 화면으로 이동 (정상!)
```

### 3. 반복 테스트

```
1. 시나리오 목록 → "부부싸움" 선택
2. StageView 표시
3. [시나리오 선택] 클릭
   → ✅ 정상 동작

4. 시나리오 목록 → "나는솔로" 선택
5. StageView 표시
6. [시나리오 선택] 클릭
   → ✅ 정상 동작

... 무한 반복 가능 ✅
```

---

## 📋 수정된 파일

- `app/src/main/java/com/example/cutestage/navigation/Navigation.kt`
    - Player composable의 모든 템플릿 케이스에 `onScenarioSelectClick` 콜백 추가
    - 7개 템플릿 모두 수정

---

## 🎉 결론

**모든 경로에서 시나리오 선택 버튼이 정상 작동합니다!**

✅ 메인 화면에서 클릭 → 정상  
✅ 템플릿 재생 후 클릭 → 정상  
✅ 반복 사용 → 정상  
✅ 모든 템플릿 → 정상

**완벽하게 수정되었습니다!** 🎊
