# CuteStage 개발 로드맵 및 방향성

> 제롬 연극부 - 모듈식 시나리오 제작 플랫폼  
> 작성일: 2024  
> 개발 형태: 1인 개발  
> 핵심 전략: 로컬 우선, 광고 기반 수익, 모듈식 콘텐츠

---

## 📋 목차

1. [프로젝트 현황 및 문제 인식](#프로젝트-현황-및-문제-인식)
2. [핵심 솔루션: 모듈식 시나리오 시스템](#핵심-솔루션-모듈식-시나리오-시스템)
3. [수익 모델](#수익-모델)
4. [개발 로드맵](#개발-로드맵)
5. [기술 스택](#기술-스택)
6. [데이터 구조 설계](#데이터-구조-설계)
7. [UI/UX 구조](#uiux-구조)
8. [콘텐츠 전략](#콘텐츠-전략)
9. [성장 전략](#성장-전략)
10. [제외 사항 (명확히)](#제외-사항-명확히)

---

## 프로젝트 현황 및 문제 인식

### ✅ 완료된 기능

- 기본 연극 재생 엔진
- StageView (캐릭터 애니메이션, 대사 표시)
- 고정 시나리오 재생
- 캐릭터 소개 섹션

### ❌ 예상되는 문제점

1. **고정 시나리오만 제공**
    - 사용자가 금방 질림
    - 재방문율 낮음

2. **AI 시나리오 생성의 한계**
    - 비용 문제 (사용자 거부감)
    - 품질 관리 어려움
    - 1인 개발에서 운영 부담

3. **마켓플레이스/소셜 기능의 한계**
    - 서버 운영 비용
    - 콘텐츠 관리/검수 인력 필요
    - 법적 책임 문제 (저작권 등)

### 💡 핵심 인사이트

> "사용자에게 **레고 블록**을 주자.  
> 미리 만들어진 모듈을 조합해서 무한한 시나리오를 만들 수 있게."

---

## 핵심 솔루션: 모듈식 시나리오 시스템

### 개념

- **모듈**: 재사용 가능한 최소 단위 (대사, 동작, 장면 등)
- **조합**: 사용자가 모듈을 선택해서 타임라인에 배치
- **커스터마이징**: 텍스트, 타이밍, 파라미터 수정 가능

### 모듈 구성 요소

#### 1. 모듈 타입 (개발자가 관리)

```
├─ Scene (장면)
│  └─ 배경, 분위기, 초기 캐릭터 배치
├─ Dialogue (대사)
│  └─ 텍스트, 말풍선 스타일, 음성 톤
├─ Action (동작)
│  └─ 캐릭터 애니메이션, 이동, 표정
├─ Background (배경)
│  └─ 이미지, 시간대, 날씨
└─ Effect (효과)
   └─ 사운드, 화면 효과, 트랜지션
```

#### 2. 모듈 계층 구조

```
모듈 타입 (Type)
└─ 카테고리 (Category)
   └─ 개별 모듈 (Module Item)
      └─ 인스턴스 (Timeline Instance)
```

**예시:**

```
Dialogue (타입)
├─ 인사 (카테고리)
│  ├─ "안녕하세요" (기본 모듈)
│  ├─ "오랜만이에요" (기본 모듈)
│  └─ "어이!" (프리미엄 모듈 - 50토큰)
├─ 갈등 (카테고리)
│  ├─ "왜 그랬어?"
│  └─ "이해할 수 없어"
└─ 화해 (카테고리)
   ├─ "미안해..."
   └─ "다시 시작하자"
```

### 모듈 관리 원칙

#### 개발자 권한

- ✅ 모듈 타입 추가/삭제
- ✅ 기본 모듈 제작
- ✅ 프리미엄 모듈 지정
- ✅ 카테고리 구성

#### 사용자 권한

- ✅ 기본 모듈 무제한 사용
- ✅ 대사 텍스트 수정
- ✅ 타이밍/파라미터 조절
- ✅ 모듈 조합 (타임라인 배치)
- ❌ 새 모듈 타입 생성 불가
- ❌ 이미지/사운드 업로드 불가

---

## 수익 모델

### 광고 기반 + 선택적 구독

#### 💰 수익 구조

```
1. 배너 광고 (항상 노출)
   └─ 위치: 시나리오 목록 하단, 에디터 하단
   
2. 리워드 광고 (선택 시청)
   └─ 시청 후 토큰 지급
   
3. 구독 (Premium)
   └─ 월 2,900원
   └─ 광고 제거 + 모든 프리미엄 모듈 언락
```

#### 🪙 토큰 이코노미

**토큰 획득:**

```
├─ 리워드 광고 1회 시청 = 10토큰
├─ 일일 출석 = 5토큰
├─ 시나리오 첫 완성 (최대 3개) = 20토큰/개
├─ 튜토리얼 완료 = 100토큰
└─ 뱃지 달성 = 10~50토큰
```

**토큰 사용:**

```
├─ 프리미엄 모듈 언락 = 50토큰
├─ 모듈 팩 언락 = 80토큰
├─ 시나리오 슬롯 추가 (10개 초과시) = 100토큰
└─ 고급 편집 기능 임시 해제 (24시간) = 30토큰
```

#### 💎 구독 혜택

```
월 2,900원:
├─ 광고 제거
├─ 모든 프리미엄 모듈 무제한 사용
├─ 시나리오 슬롯 무제한
├─ 고급 편집 기능 상시 사용
├─ 주간 독점 콘텐츠
└─ 우선 업데이트 체험
```

---

## 개발 로드맵

### Phase 1: 로컬 우선 MVP (1-2개월)

**목표: 완전히 오프라인으로 동작하는 기본 시스템**

#### 필수 구현 사항

```
✅ 모듈 시스템 (Room DB)
   ├─ 5개 모듈 타입
   ├─ 각 타입당 10-15개 기본 모듈
   └─ JSON 기반 모듈 정의

✅ 시나리오 에디터
   ├─ 타임라인 UI (드래그 앤 드롭)
   ├─ 모듈 팔레트 (하단 탭)
   ├─ 실시간 미리보기
   └─ 기본 편집 (대사 텍스트 수정)

✅ 10개 템플릿 시나리오
   ├─ 로맨스 (3개)
   ├─ 코미디 (3개)
   ├─ 드라마 (2개)
   └─ 액션 (2개)

✅ 로컬 저장/불러오기
   └─ 기본 10개 슬롯

✅ 광고 통합
   └─ AdMob 배너 광고
```

#### 제외 사항

```
❌ 서버 연동
❌ 로그인/회원가입
❌ 클라우드 백업
❌ 리워드 광고 (토큰 없음)
```

#### 주요 화면

```
1. 홈 화면
   ├─ 내 시나리오 목록
   ├─ 템플릿 갤러리
   └─ 배너 광고

2. 에디터 화면
   ├─ 상단: 툴바 (저장, 미리보기)
   ├─ 중앙: 타임라인 + 스테이지 뷰
   ├─ 하단: 모듈 팔레트 (탭)
   └─ 최하단: 배너 광고

3. 재생 화면
   └─ StageView (기존 기능 활용)
```

---

### Phase 2: 토큰 & 클라우드 백업 (3-4개월)

**목표: 수익화 + 데이터 안전성**

#### 추가 구현 사항

```
✅ 토큰 시스템
   ├─ DataStore로 토큰 관리
   ├─ 리워드 광고 통합
   └─ 프리미엄 모듈 언락

✅ Firebase 통합
   ├─ Anonymous Auth
   ├─ Firestore (시나리오 백업)
   └─ Remote Config (모듈 메타데이터)

✅ 고급 편집 기능
   ├─ 타이밍 세밀 조절
   ├─ 모듈 파라미터 수정
   └─ 복사/붙여넣기

✅ 일일 미션 & 뱃지
   ├─ 출석 체크
   ├─ 달성 뱃지 (로컬)
   └─ 토큰 보상
```

#### 사용자 플로우

```
1. 앱 실행
   └─> 튜토리얼 (100토큰 지급)

2. 템플릿 선택 & 수정
   └─> 저장 (20토큰 보상)

3. 프리미엄 모듈 발견
   └─> "50토큰 필요"
   └─> [광고 보기] 버튼
   └─> 리워드 광고 5회 시청
   └─> 언락 완료

4. 일일 접속
   └─> 출석 5토큰
```

---

### Phase 3: 구독 & 콘텐츠 확장 (5-6개월)

**목표: 지속 가능한 수익 + 콘텐츠 다양화**

#### 추가 구현 사항

```
✅ 구독 시스템
   ├─ Google Play Billing
   ├─ 광고 제거
   └─ 프리미엄 혜택

✅ 주간 업데이트
   ├─ Remote Config로 새 모듈 푸시
   ├─ 계절/이벤트 콘텐츠
   └─ 독점 템플릿 (구독자 전용)

✅ 동영상 내보내기
   ├─ 시나리오 → MP4 녹화
   ├─ 워터마크 자동 삽입
   └─ SNS 공유 (인스타/틱톡)

✅ 통계 & 분석
   ├─ 제작 시나리오 수
   ├─ 사용 모듈 통계
   └─ 플레이 타임
```

#### 콘텐츠 업데이트 사이클

```
매주 월요일:
├─ 새 시나리오 템플릿 1개
├─ 새 모듈 5-10개
└─ 이벤트 모듈 (명절, 기념일)

소요 시간: 개발자 1-2시간
```

---

### Phase 4: 고급 기능 (추후 검토)

**목표: 파워 유저 확보**

```
🔮 아이디어 (우선순위 낮음):
├─ AI 대사 다듬기 (유료, 건당 과금)
├─ 음성 녹음 (내 목소리로 더빙)
├─ 커스텀 캐릭터 (사진 → 캐릭터화)
└─ 협업 모드 (친구와 공동 제작)
```

---

## 기술 스택

### 앱 개발

```kotlin
// 아키텍처
├─ MVVM + Jetpack Compose
├─ Hilt (DI)
└─ Coroutines + Flow

// 로컬 저장
├─ Room Database
│  ├─ 시나리오 데이터
│  ├─ 모듈 정의
│  └─ 사용자 설정
├─ DataStore
│  ├─ 토큰
│  ├─ 구독 상태
│  └─ 설정값
└─ JSON (assets)
   └─ 기본 모듈 정의

// UI
├─ Jetpack Compose
├─ Material3
└─ Accompanist (권한, 애니메이션)
```

### 백엔드 (최소화)

```
// Firebase (무료 티어)
├─ Authentication (Anonymous)
├─ Firestore
│  └─ 시나리오 백업만 (쓰기 최소화)
├─ Remote Config
│  └─ 모듈 메타데이터, 업데이트 정보
└─ Analytics
   └─ 사용자 행동 분석

// 광고
└─ Google AdMob
   ├─ Banner
   └─ Rewarded Video
```

### 빌드 & 배포

```
├─ GitHub Actions (CI/CD)
├─ Google Play Store
└─ Firebase App Distribution (베타 테스트)
```

---

## 데이터 구조 설계

### 1. 모듈 시스템

#### ModuleType (모듈 타입)

```kotlin
@Entity(tableName = "module_types")
data class ModuleType(
    @PrimaryKey val id: String,          // "dialogue", "action", ...
    val name: String,                     // "대사", "동작"
    val icon: Int,                        // R.drawable.ic_dialogue
    val colorHex: String,                 // "#FF5722"
    val isActive: Boolean = true,         // 활성화 여부
    val sortOrder: Int = 0                // 표시 순서
)
```

#### ModuleCategory (카테고리)

```kotlin
@Entity(tableName = "module_categories")
data class ModuleCategory(
    @PrimaryKey val id: String,          // "greeting", "conflict"
    val typeId: String,                   // "dialogue"
    val name: String,                     // "인사", "갈등"
    val sortOrder: Int = 0
)
```

#### ModuleItem (개별 모듈)

```kotlin
@Entity(tableName = "module_items")
data class ModuleItem(
    @PrimaryKey val id: String,
    val typeId: String,
    val categoryId: String,
    val name: String,                     // "안녕하세요"
    val thumbnailUrl: String? = null,
    val isPremium: Boolean = false,       // 프리미엄 여부
    val unlockCost: Int = 0,              // 토큰 비용
    val contentJson: String,              // JSON 형태의 실제 데이터
    val tags: List<String> = emptyList(), // ["로맨스", "첫만남"]
    val usageCount: Int = 0,              // 사용 횟수 (인기도)
    val createdAt: Long = System.currentTimeMillis()
)
```

#### ModuleContent (타입별 실제 데이터)

```kotlin
// Dialogue 모듈
data class DialogueContent(
    val text: String,                     // "안녕하세요"
    val characterId: String,              // "sangchul"
    val emotion: Emotion,                 // HAPPY, SAD, ANGRY, ...
    val bubbleStyle: BubbleStyle,         // NORMAL, SHOUT, WHISPER
    val voiceTone: VoiceTone? = null      // (추후) 음성 톤
)

// Action 모듈
data class ActionContent(
    val characterId: String,
    val animationType: AnimationType,     // WALK, RUN, WAVE, ...
    val startPosition: Position,
    val endPosition: Position,
    val duration: Float,                  // 초 단위
    val emotion: Emotion
)

// Scene 모듈
data class SceneContent(
    val backgroundId: String,
    val timeOfDay: TimeOfDay,             // MORNING, AFTERNOON, ...
    val weather: Weather,                 // SUNNY, RAINY, ...
    val bgmId: String? = null,
    val initialCharacters: List<CharacterPosition>
)
```

---

### 2. 시나리오 시스템

#### Scenario (시나리오)

```kotlin
@Entity(tableName = "scenarios")
data class Scenario(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val genre: Genre,                     // ROMANCE, COMEDY, ...
    val thumbnailPath: String? = null,
    val isTemplate: Boolean = false,      // 템플릿 여부
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val duration: Float = 0f,             // 총 재생 시간 (초)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

#### ScenarioTimeline (타임라인)

```kotlin
@Entity(
    tableName = "scenario_timelines",
    foreignKeys = [
        ForeignKey(
            entity = Scenario::class,
            parentColumns = ["id"],
            childColumns = ["scenarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ScenarioTimeline(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val scenarioId: String,
    val moduleItemId: String,             // ModuleItem 참조
    val startTime: Float,                 // 시작 시간 (초)
    val duration: Float,                  // 지속 시간 (초)
    val layerIndex: Int = 0,              // 레이어 (동시 실행용)
    val parametersJson: String = "{}",    // 커스터마이징 파라미터
    val sortOrder: Int = 0
)
```

#### TimelineParameters (파라미터 예시)

```kotlin
// 타임라인에서 모듈 인스턴스별로 조정 가능한 값
data class TimelineParameters(
    // Dialogue용
    val customText: String? = null,       // 대사 텍스트 수정
    val textSpeed: Float = 1.0f,          // 텍스트 속도
    
    // Action용
    val speedMultiplier: Float = 1.0f,    // 동작 속도
    val customEndPosition: Position? = null,
    
    // 공통
    val volume: Float = 1.0f,
    val opacity: Float = 1.0f
)
```

---

### 3. 사용자 데이터

#### UserProfile (DataStore)

```kotlin
data class UserProfile(
    val userId: String,                   // Firebase Anonymous UID
    val tokenBalance: Int = 0,
    val isPremiumSubscriber: Boolean = false,
    val subscriptionExpireAt: Long? = null,
    val lastDailyRewardAt: Long = 0,
    val tutorialCompleted: Boolean = false,
    val totalScenariosCreated: Int = 0,
    val totalPlayTime: Long = 0           // 밀리초
)
```

#### UnlockedModule (언락된 프리미엄 모듈)

```kotlin
@Entity(tableName = "unlocked_modules")
data class UnlockedModule(
    @PrimaryKey val moduleItemId: String,
    val unlockedAt: Long = System.currentTimeMillis(),
    val unlockMethod: UnlockMethod        // TOKEN, SUBSCRIPTION, EVENT
)

enum class UnlockMethod {
    TOKEN,          // 토큰으로 구매
    SUBSCRIPTION,   // 구독으로 언락
    EVENT,          // 이벤트 보상
    DEFAULT         // 기본 제공
}
```

#### Achievement (뱃지)

```kotlin
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val rewardTokens: Int,
    val unlockedAt: Long? = null,         // null이면 미획득
    val progress: Int = 0,                // 진행도
    val target: Int = 1                   // 목표치
)

// 예시 뱃지들
// - "first_scenario": 첫 시나리오 제작 (20토큰)
// - "play_10_times": 10번 재생 (30토큰)
// - "daily_7days": 7일 연속 접속 (50토큰)
```

---

## UI/UX 구조

### 화면 구성

```
📱 앱 구조
├─ 🏠 홈 (Home)
│  ├─ 내 시나리오 목록
│  ├─ 템플릿 갤러리
│  ├─ 토큰 잔액 (상단)
│  └─ 배너 광고 (하단)
│
├─ ✏️ 에디터 (Editor)
│  ├─ 타임라인 뷰 (상단)
│  ├─ 스테이지 미리보기 (중앙)
│  ├─ 모듈 팔레트 (하단 탭)
│  │  ├─ 대사
│  │  ├─ 동작
│  │  ├─ 장면
│  │  ├─ 배경
│  │  └─ 효과
│  └─ 속성 패널 (선택된 모듈)
│
├─ ▶️ 재생 (Player)
│  └─ StageView (전체화면)
│
├─ 🏪 상점 (Shop)
│  ├─ 프리미엄 모듈 팩
│  ├─ 구독 안내
│  └─ 토큰 획득 방법
│
└─ ⚙️ 설정 (Settings)
   ├─ 내 정보
   ├─ 뱃지 컬렉션
   ├─ 백업/복원
   └─ 구독 관리
```

### 에디터 UI 상세

#### 타임라인 뷰

```
┌─────────────────────────────────────────┐
│ [저장] [미리보기] [설정]    토큰: 150 💎 │
├─────────────────────────────────────────┤
│ 00:00    00:05    00:10    00:15  00:20 │
│ ┌──────┐                                 │
│ │ 대사1 │   ┌────┐  ┌──────┐            │
│ └──────┘   │동작1│  │ 대사2 │            │
│            └────┘  └──────┘             │
│ [+] 모듈 추가                            │
└─────────────────────────────────────────┘
```

#### 모듈 팔레트

```
┌─────────────────────────────────────────┐
│ [대사] [동작] [장면] [배경] [효과]       │
├─────────────────────────────────────────┤
│ 🔍 검색...                               │
├─────────────────────────────────────────┤
│ ┌───────┐ ┌───────┐ ┌───────┐          │
│ │ 💬    │ │ 💬    │ │ 💬 🔒 │          │
│ │"안녕" │ │"반가워"│ │"사랑해"│          │
│ │  무료  │ │  무료  │ │ 50토큰│          │
│ └───────┘ └───────┘ └───────┘          │
└─────────────────────────────────────────┘
```

### 사용자 플로우

#### 첫 사용자 (Cold Start)

```
1. 앱 실행
   ↓
2. 환영 화면
   "연극을 만들어보세요!"
   [튜토리얼 시작] [건너뛰기]
   ↓
3. 튜토리얼 (3단계, 30초)
   ① 템플릿 선택하기
   ② 대사 수정해보기
   ③ 재생해보기
   → 완료 시 100토큰 지급
   ↓
4. 홈 화면 진입
   - 10개 템플릿 표시
   - "당신만의 시나리오 만들기" CTA
```

#### 시나리오 제작 플로우

```
1. 홈 → [+ 새로 만들기]
   ↓
2. 시작 방법 선택
   ├─ 빈 캔버스 (고급)
   └─ 템플릿에서 시작 (추천)
   ↓
3. 템플릿 선택 (10개)
   - 로맨스: 첫 만남 ⭐️
   - 로맨스: 이별
   - 코미디: 오해
   - ...
   ↓
4. 에디터 진입
   - 타임라인에 기본 모듈들이 배치됨
   - 대사 클릭 → 텍스트 수정
   - 모듈 추가/삭제
   - 타이밍 조절
   ↓
5. [미리보기] 버튼
   → 전체화면 재생
   → [계속 편집] [저장]
   ↓
6. 저장
   - 제목 입력
   - 썸네일 자동 생성
   - 첫 3개 완성 시 20토큰 보상
```

#### 프리미엄 모듈 언락 플로우

```
1. 에디터에서 🔒 모듈 클릭
   ↓
2. 프리뷰 팝업
   "고백 대사 팩"
   - 5개 대사 포함
   - 비용: 50토큰
   - 현재 토큰: 30
   [광고 보고 토큰 받기] [취소]
   ↓
3. [광고 보기] 클릭
   → 리워드 광고 재생 (30초)
   → +10토큰
   → "20토큰 더 필요합니다"
   ↓
4. 광고 4회 더 시청
   → 50토큰 도달
   → 자동 언락
   → "고백 대사 팩을 획득했습니다!"
```

---

## 콘텐츠 전략

### 초기 제공 콘텐츠 (Phase 1)

#### 모듈 타입 (5개)

```
1. Dialogue (대사)
   - 카테고리: 인사, 일상, 갈등, 화해, 고백
   - 총 50개 대사

2. Action (동작)
   - 카테고리: 이동, 감정, 제스처, 상호작용
   - 총 30개 동작

3. Scene (장면)
   - 카테고리: 만남, 갈등, 클라이맥스, 결말
   - 총 15개 장면

4. Background (배경)
   - 카테고리: 실내, 실외, 자연, 도시
   - 총 10개 배경

5. Effect (효과)
   - 카테고리: 전환, 강조, 분위기, 사운드
   - 총 20개 효과
```

#### 템플릿 시나리오 (10개)

```
로맨스 (3개):
├─ "우연한 만남" (2분, 쉬움)
│  └─ 공원에서 마주친 두 사람의 첫 대화
├─ "고백" (3분, 보통)
│  └─ 용기를 내어 마음을 전하는 순간
└─ "이별 후 재회" (4분, 어려움)
   └─ 헤어진 연인이 다시 만나는 이야기

코미디 (3개):
├─ "오해" (2분, 쉬움)
├─ "사고 현장" (3분, 보통)
└─ "엇갈린 마음" (3분, 보통)

드라마 (2개):
├─ "화해" (3분, 보통)
└─ "결단" (4분, 어려움)

액션 (2개):
├─ "추격" (2분, 보통)
└─ "대결" (3분, 어려움)
```

### 프리미엄 모듈 전략

#### 무료 vs 프리미엄 비율

```
무료: 70%
프리미엄: 30%

각 카테고리별:
├─ 기본 모듈 5-8개 (무료)
└─ 고급 모듈 2-3개 (프리미엄)
```

#### 프리미엄 모듈 선정 기준

```
✅ 프리미엄으로 만들 것:
├─ 특수한 감정 표현 (질투, 배신 등)
├─ 복잡한 동작 시퀀스
├─ 특수 효과 (슬로모션, 극적 연출)
└─ 테마 팩 (크리스마스, 밸런타인 등)

❌ 무료로 제공할 것:
├─ 기본 대사 (인사, 일상)
├─ 기본 동작 (걷기, 손흔들기)
└─ 기본 장면 구성
```

### 업데이트 계획

#### 주간 업데이트 (Phase 3 이후)

```
매주 월요일 오전 10시:
├─ 새 모듈 5-10개
│  ├─ 무료 3-5개
│  └─ 프리미엄 2-5개
├─ 새 템플릿 1개
└─ 이벤트 콘텐츠 (월 1회)

소요 시간: 1-2시간
방법: Remote Config로 메타데이터만 푸시
```

#### 계절/이벤트 콘텐츠

```
1월: 새해, 설날
2월: 밸런타인데이
3월: 화이트데이, 봄
4월: 벚꽃, 입학
5월: 어린이날, 어버이날
6월: 여름 시작
7월-8월: 여름 휴가
9월: 가을, 추석
10월: 할로윈
11월: 가을 감성
12월: 크리스마스, 연말
```

---

## 성장 전략

### 바이럴 요소

#### 1. 동영상 공유 (서버 없이)

```
시나리오 재생 → [공유] 버튼
├─ 화면 녹화 (30초-2분)
├─ 워터마크 자동 삽입
│  └─ "CuteStage로 만들었어요 🎭"
├─ MP4 저장
└─ SNS 공유
   ├─ 인스타그램 스토리
   ├─ 틱톡
   ├─ 유튜브 쇼츠
   └─ 카카오톡
```

#### 2. 챌린지 (앱 내)

```
"이번 주 챌린지"
- 주제: "첫눈에 반한 순간"
- 조건: 2분 이내, 로맨스 장르
- 보상: 100토큰

참여 방법:
└─ 시나리오 제작 시 #챌린지 태그
└─ (나중에) 커뮤니티 투표
```

#### 3. 추천 시스템

```
"친구 초대"
├─ 초대 코드 생성
├─ 친구 가입 시 양쪽 50토큰
└─ 친구가 구독하면 추가 200토큰
```

### 리텐션 전략

#### 1. 일일 미션

```
로그인 시 랜덤 미션 1개:
├─ "오늘의 템플릿 재생하기" (+5토큰)
├─ "대사 5개 수정하기" (+10토큰)
├─ "새 시나리오 만들기" (+20토큰)
└─ "광고 3번 시청하기" (+30토큰)
```

#### 2. 출석 보상

```
연속 출석:
Day 1: 5토큰
Day 2: 5토큰
Day 3: 10토큰
Day 4: 10토큰
Day 5: 15토큰
Day 6: 15토큰
Day 7: 50토큰 + 프리미엄 모듈 1개

총 7일: 110토큰 + 보너스
```

#### 3. 뱃지 시스템

```
제작 관련:
├─ "첫 시나리오" (20토큰)
├─ "10개 제작" (50토큰)
├─ "50개 제작" (200토큰)
└─ "마스터 크리에이터" (500토큰)

재생 관련:
├─ "관람객" - 10회 재생 (30토큰)
├─ "열혈 팬" - 50회 재생 (100토큰)
└─ "시나리오 마니아" - 100회 재생 (300토큰)

수집 관련:
├─ "모듈 컬렉터" - 50개 모듈 사용 (50토큰)
└─ "전체 수집" - 모든 카테고리 사용 (200토큰)

특수:
├─ "얼리어답터" - 베타 참여 (500토큰)
└─ "VIP" - 구독 6개월 유지 (1000토큰)
```

### 마케팅 전략

#### ASO (앱스토어 최적화)

```
제목: "제롬 연극부 - 나만의 시나리오 만들기"

키워드:
- 연극, 시나리오, 스토리 메이커
- 캐릭터, 애니메이션, 만들기
- 창작, 크리에이터, 콘텐츠

스크린샷 순서:
1. 귀여운 캐릭터 연극 장면
2. 쉬운 에디터 인터페이스
3. 다양한 템플릿
4. "3분만에 내 이야기 만들기"
5. 커뮤니티 인기 시나리오 (추후)
```

#### 타겟 유저

```
Primary:
- 10대-20대 여성
- 창작 활동 좋아하는 사람
- 웹툰/웹소설 독자

Secondary:
- 커플 (함께 만들기)
- 교육 목적 (학교 연극부)
- 콘텐츠 크리에이터 (숏폼 소재)
```

---

## 제외 사항 (명확히)

### ❌ 구현하지 않을 것

#### 1. 마켓플레이스

```
이유:
- 서버 운영 비용
- 콘텐츠 검수 인력 필요
- 결제 시스템 복잡도
- 법적 책임 (저작권)

대안:
- 개발자가 직접 제작한 콘텐츠만
- Remote Config로 업데이트
```

#### 2. 소셜 기능

```
이유:
- 커뮤니티 관리 부담
- 악성 사용자 대응
- 댓글/신고 시스템 필요

대안:
- 동영상 내보내기 + SNS 공유
- 외부 플랫폼 활용
```

#### 3. 사용자 제작 모듈 공유

```
이유:
- 품질 관리 불가
- 부적절한 콘텐츠 위험
- 저작권 분쟁 가능성

대안:
- 사용자는 로컬에서만 커스터마이징
- 개발자가 우수 아이디어 수집 후 공식 모듈화
```

#### 4. AI 시나리오 생성

```
이유:
- API 비용 부담
- 품질 예측 불가
- 사용자당 과금 저항

대안:
- 풍부한 템플릿 제공
- 모듈 조합의 무한한 가능성
- (추후) AI 대사 다듬기만 유료 제공
```

#### 5. 복잡한 결제

```
이유:
- 토큰 개별 판매 → 관리 복잡
- 환불 처리 이슈

대안:
- 광고로 토큰 무료 획득
- 구독 1개 플랜만 (2,900원/월)
```

---

## 핵심 원칙 (항상 기억)

### 1. 로컬 우선 (Local-First)

```
✅ 모든 기능이 오프라인에서도 동작
✅ 서버는 백업 용도로만
✅ 빠른 반응 속도
```

### 2. 점진적 공개 (Progressive Disclosure)

```
✅ 처음엔 간단하게 (템플릿 → 재생)
✅ 익숙해지면 고급 기능 노출
✅ 파워 유저는 모든 기능 활용
```

### 3. 개발자 통제 (Developer-Controlled)

```
✅ 콘텐츠는 개발자가 제작
✅ 사용자는 소비와 조합만
✅ 품질 보장
```

### 4. 지속 가능한 수익 (Sustainable Revenue)

```
✅ 광고 (주 수익원)
✅ 구독 (충성 고객)
✅ 서버 비용 최소화
```

### 5. 1인 개발 가능 (Solo-Dev Friendly)

```
✅ 서버리스 아키텍처
✅ 자동화된 배포
✅ 주 1-2시간 콘텐츠 제작
✅ 커뮤니티 관리 불필요
```

---

## 성공 지표 (KPI)

### Phase 1 목표 (런치 후 1개월)

```
- 다운로드: 1,000명
- DAU: 100명 (10%)
- 시나리오 제작: 평균 2개/유저
- 재생 완료율: 80%
- 리텐션 D7: 20%
```

### Phase 2 목표 (3개월)

```
- 다운로드: 10,000명
- DAU: 1,500명 (15%)
- 광고 수익: 월 $300
- 토큰 사용률: 유저당 평균 50토큰/주
- 프리미엄 언락률: 30%
```

### Phase 3 목표 (6개월)

```
- 다��로드: 50,000명
- DAU: 10,000명 (20%)
- 광고 수익: 월 $1,500
- 구독자: 100명 (월 290,000원)
- 리텐션 D30: 15%
- 바이럴: 주 100회 SNS 공유
```

---

## 다음 단계 (즉시 시작할 것)

### 1. 데이터 모델 구현

```kotlin
// 1주일
- Room 엔티티 작성
- DAO 인터페이스
- Repository 패턴
- 샘플 데이터 JSON
```

### 2. 모듈 시스템 기초

```kotlin
// 2주일
- 모듈 타입별 Content 클래스
- 모듈 로더 (JSON → Entity)
- 기본 모듈 50개 작성
```

### 3. 에디터 UI

```kotlin
// 3주일
- 타임라인 Composable
- 드래그 앤 드롭
- 모듈 팔레트
- 미리보기 통합
```

### 4. 템플릿 제작

```kotlin
// 1주일
- 10개 시나리오 시나리오 작성
- JSON 직렬화
- 썸네일 제작
```

### 5. 광고 통합

```kotlin
// 3일
- AdMob 계정
- 배너 광고 통합
- 테스트
```

### 총 개발 기간: 약 8주 (2개월)

---

## 참고 사항

### 유사 앱 분석

```
Gacha Life:
- 장점: 캐릭터 커스터마이징, 장면 제작
- 단점: UI 복잡, 초보자 진입장벽
- 차별점: 우리는 템플릿 중심, 더 쉬움

Episode - Choose Your Story:
- 장점: 스토리 텔링, 선택지
- 단점: 유료 콘텐츠 많음, 읽기만 가능
- 차별점: 우리는 직접 만들기, 시각적

Plotagon:
- 장점: 3D 애니메이션 생성
- 단점: 비싸고 복잡함
- 차별점: 우리는 2D 귀여운 스타일, 무료
```

### 기술 레퍼런스

```
타임라인 UI:
- https://github.com/android/compose-samples
- Video Editor 앱들 참고

드래그 앤 드롭:
- Accompanist DragAndDrop (실험적)
- Custom Gesture 구현

데이터 구조:
- JSON Schema로 모듈 정의
- Room Relations로 쿼리 최적화
```

---

## 마무리

### 핵심 가치 제안

```
"누구나 3분 안에
 자신만의 연극을 만들 수 있다"
```

### 비전

```
"사용자가 소비자가 아닌 크리에이터가 되는 플랫폼"
```

### 차별화 포인트

```
✅ 초보자도 쉽게 시작 (템플릿)
✅ 무한한 조합 (모듈식)
✅ 부담 없는 무료 (광고만)
✅ 귀여운 캐릭터 (감성)
✅ 빠른 제작 (3분)
```

---

**이 문서는 개발 전반에 걸쳐 항상 참고할 북극성(North Star)입니다.**

새로운 기능을 추가할 때마다 자문하세요:

1. 이게 1인 개발로 가능한가?
2. 사용자가 정말 원하는 기능인가?
3. 수익화와 연결되는가?
4. 핵심 가치에 부합하는가?

**Happy Coding! 🎭✨**
