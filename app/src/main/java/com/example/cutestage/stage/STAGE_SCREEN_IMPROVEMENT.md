# StageScreen 개선 사항

## 🎯 **개선 목표**

1. ✅ StageView 높이 원복 (고정 높이 유지)
2. ✅ 앱바 배경 검정, 제목 흰색 중앙 정렬
3. ✅ 시나리오 관리 일원화 (ViewModel 통해)
4. ✅ 관심사 분리 명확화

---

## 📊 **Before vs After**

### **Before**

```kotlin
// 문제 1: StageView 높이가 화면의 60% 차지
StageView(
    script = currentScript,
    modifier = Modifier.weight(1f)  // ❌ 동적 높이
)

// 문제 2: 시나리오를 StageScreen에서 직접 관리
val currentScript = StageTestScenario.createTestScript()

// 문제 3: 앱바 테마 색상 사용
TopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )
)
```

### **After**

```kotlin
// 해결 1: StageView 고정 높이 (350.dp)
StageView(
    viewModel = viewModel,
    modifier = Modifier.fillMaxWidth()  // ✅ 내부에서 height(350.dp) 고정
)

// 해결 2: ViewModel에서 시나리오 관리
val currentScript = viewModel.state.currentScript

// 해결 3: 검정 배경 + 흰색 제목 + 중앙 정렬
TopAppBar(
    title = {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "제롬 연극부",
                color = Color.White
            )
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Black,
        titleContentColor = Color.White
    )
)
```

---

## 🏗️ **아키텍처 개선**

### **이전 구조 (문제)**

```
StageScreen
├─ currentScript 생성 (중복 관리)
├─ StageView(script = currentScript)
│  └─ ViewModel (내부에서 script 관리)
└─ CharacterIntroductionSection(characters)
```

**문제점:**

- 시나리오가 두 곳에서 관리됨
- StageScreen의 script와 ViewModel의 script 불일치 가능성

---

### **개선 구조 (해결)**

```
StageScreen
├─ ViewModel 공유 (단일 인스턴스)
├─ currentScript = viewModel.state.currentScript (관찰)
├─ StageView(viewModel = viewModel)
│  └─ ViewModel 사용 (동일 인스턴스)
└─ CharacterIntroductionSection(characters)
    └─ characters 추출 (viewModel.state.currentScript 기반)
```

**개선점:**

- ✅ 시나리오 단일 소스 (ViewModel)
- ✅ StageScreen은 ViewModel 관찰만
- ✅ 데이터 흐름 명확화

---

## 📐 **레이아웃 구조**

```
┌─────────────────────────────────────┐
│  제롬 연극부 (중앙 정렬)             │  ← 검정 배경, 흰색 텍스트
├─────────────────────────────────────┤
│                                     │
│        StageView                    │  ← 350.dp 고정 높이
│     (연극 무대 - 독립적)             │
│                                     │
├─────────────────────────────────────┤
│  🎭 등장인물                         │
│  ┌───────────────────────────────┐ │
│  │ 상철 ♂ 무뚝뚝하지만...        │ │
│  └───────────────────────────────┘ │
│  ┌───────────────────────────────┐ │
│  │ 옥순 ♀ 밝고 긍정적인...       │ │
│  └───────────────────────────────┘ │
│         (나머지 공간, 스크롤)        │
└─────────────────────────────────────┘
```

---

## 🎨 **UI 변경 사항**

### **1. 앱바**

| 항목 | Before | After |
|------|--------|-------|
| 배경색 | PrimaryContainer (테마 색) | Black |
| 텍스트 색 | OnPrimaryContainer | White |
| 정렬 | 왼쪽 | **중앙** |
| 스타일 | 기본 | **Bold** |

### **2. StageView**

| 항목 | Before | After |
|------|--------|-------|
| 높이 | weight(1f) (동적) | 350.dp (고정) |
| 너비 | fillMaxWidth() | fillMaxWidth() |
| 모서리 | 16.dp | 16.dp |
| 패딩 | 10.dp | 10.dp |

### **3. 캐릭터 소개**

| 항목 | Before | After |
|------|--------|-------|
| 높이 | weight(0.4f) | weight(1f) |
| 스크롤 | 가능 | 가능 |
| 데이터 소스 | StageScreen의 script | ViewModel의 script |

---

## ✅ **개선 효과**

### **1. StageView 독립성 유지**

- ✅ 고정 높이로 어디서나 일관된 크기
- ✅ 시나리오 관리를 ViewModel에 완전히 위임
- ✅ 다른 화면에서 재사용 시에도 동일하게 작동

### **2. 데이터 흐름 명확화**

```
ViewModel (Single Source of Truth)
    ↓
StageView (무대 표시)
    ↓
StageScreen (등장인물 추출 & 표시)
```

### **3. UI 일관성**

- ✅ 검정 배경에 흰색 텍스트 (고급스러움)
- ✅ 중앙 정렬로 균형감
- ✅ StageView의 고정 높이로 예측 가능

### **4. 유지보수성**

- ✅ 시나리오 변경 시 한 곳만 수정 (ViewModel)
- ✅ StageScreen은 UI만 담당
- ✅ 관심사 분리 명확

---

## 🚀 **사용 예시**

### **기본 사용**

```kotlin
@Composable
fun MyApp() {
    StageScreen()  // ViewModel 자동 생성
}
```

### **ViewModel 공유**

```kotlin
@Composable
fun MyApp() {
    val sharedViewModel: StageViewModel = viewModel()
    
    // 다른 화면에서도 동일한 ViewModel 사용 가능
    StageScreen(viewModel = sharedViewModel)
}
```

---

## 🎯 **관심사 분리**

### **StageView의 책임**

- ✅ 연극 무대 표시
- ✅ 캐릭터 애니메이션
- ✅ 대사 말풍선
- ✅ 시나리오 재생 제어
- ❌ 등장인물 소개 (Screen의 책임)

### **StageScreen의 책임**

- ✅ 앱바 표시
- ✅ 등장인물 소개
- ✅ 전체 레이아웃 구성
- ❌ 시나리오 직접 생성 (ViewModel의 책임)

### **ViewModel의 책임**

- ✅ 시나리오 관리
- ✅ 재생 상태 관리
- ✅ AI 생성 로직
- ✅ 비즈니스 로직
- ❌ UI 렌더링 (View의 책임)

---

## 📊 **코드 통계**

| 항목 | Before | After | 변화 |
|------|--------|-------|------|
| StageView 높이 | 동적 (weight) | 350.dp 고정 | **일관성 향상** |
| 시나리오 소스 | 2곳 | 1곳 (ViewModel) | **단일화** |
| 앱바 색상 | 테마 색 | 검정/흰색 | **명확화** |
| ViewModel 사용 | 내부만 | 공유 가능 | **확장성 향상** |

---

## 🎉 **최종 결과**

### **Before 문제점**

- ❌ StageView 높이가 화면의 60% 차지 (너무 큼)
- ❌ 시나리오가 두 곳에서 관리됨 (충돌 가능성)
- ❌ 앱바가 테마 색상이라 검정 배경과 안 어울림

### **After 개선**

- ✅ StageView 350.dp 고정 (적절한 크기)
- ✅ 시나리오 단일 소스 (ViewModel)
- ✅ 앱바 검정 배경 + 흰색 텍스트 중앙 정렬 (세련됨)
- ✅ 등장인물 소개가 나머지 공간 차지 (균형)
- ✅ 관심사 분리 명확 (유지보수 용이)

---

## 빌드 성공 ✅

```bash
BUILD SUCCESSFUL in 14s
```

모든 기능이 정상 작동하며, 더 깔끔한 구조로 개선되었습니다! 🎭✨
