# 🎭 StageView 스프라이트 애니메이션 시스템

## 📋 개요

28개의 스프라이트 리소스를 활용하여 **생동감 있는 2D 캐릭터 애니메이션**을 구현한 시스템입니다.

---

## 🎬 주요 기능

### ✨ 지원하는 애니메이션

| 애니메이션 타입         | 설명       | 프레임 | 용도       | 효과음       |
|------------------|----------|-----|----------|-----------|
| **IDLE**         | 기본 서있기   | 2   | 대기 상태    | -         |
| **IDLE_ANNOYED** | 짜증나서 서있기 | 2   | 불만 표현    | -         |
| **SPEAK_NORMAL** | 평범하게 말하기 | 2   | 일반 대화    | 음성        |
| **SPEAK_ANGRY**  | 화나서 말하기  | 2   | 화남/분노 표현 | 음성        |
| **LISTENING**    | 상대 말 듣기  | 2   | 경청 자세    | -         |
| **WALKING**      | 걷기       | 2   | 이동 시     | 🔊 발걸음 소리 |
| **ANNOYED**      | 짜증       | 2   | 불쾌함 표현   | -         |

### 🎯 2프레임 애니메이션

- 각 동작마다 **2개의 프레임**이 자동으로 교차 재생
- 기본 프레임 간격: **500ms** (조절 가능)
- 부드러운 움직임과 생동감 표현

### 🔊 자동 효과음 시스템

- **WALKING 애니메이션**: 프레임 전환마다 발걸음 소리 자동 재생
    - 낮은 음 (pitch 0.3f)
    - 짧은 지속시간 (50ms)
    - 작은 볼륨 (0.3f)
- **SPEAK 애니메이션**: 대사 타이핑과 동기화된 음성
    - 동물의 숲 스타일 비프음
    - 캐릭터별 다른 pitch/속도

---

## 🚀 사용 방법

### 기본 사용

```kotlin
// 스프라이트 애니메이션과 함께 캐릭터 생성
character(
    id = "hero",
    imageRes = R.drawable.stage_ch_m_1,  // 정적 이미지 (필수)
    name = "상철",
    x = 100.dp,
    y = 150.dp,
    size = 100.dp,
    spriteAnimation = CharacterAnimationState(
        gender = CharacterGender.MALE,
        currentAnimation = CharacterAnimationType.SPEAK_NORMAL,
        isAnimating = true,  // 애니메이션 활성화
        frameDuration = 500L // 프레임 간격 (ms)
    )
)
```

### 애니메이션 없이 사용 (정적 이미지)

```kotlin
character(
    id = "hero",
    imageRes = R.drawable.stage_ch_m_1,
    name = "상철",
    // spriteAnimation = null  (기본값)
)
```

---

## 🎨 시나리오 예시

### 1. 기본 시나리오 - 만남 (정적 이미지)

```kotlin
StageTestScenario.currentScenario = StageTestScenario.ScenarioType.BASIC
val script = StageTestScenario.createTestScript()
```

### 2. 부부싸움 시나리오 (스프라이트 애니메이션)

```kotlin
StageTestScenario.currentScenario = StageTestScenario.ScenarioType.COUPLE_FIGHT
val script = StageTestScenario.createTestScript()
```

### 3. 옥순의 혼잣말 (모든 애니메이션 활용) ✨

```kotlin
StageTestScenario.currentScenario = StageTestScenario.ScenarioType.OKSUN_MONOLOGUE  // 기본값
val script = StageTestScenario.createTestScript()
```

---

## 📁 리소스 구조

### 배경

- `stage_floor.png` - 무대 바닥

### 남자 캐릭터 (상철)

```
stage_male_1_idle_1.png
stage_male_1_idle_2.png
stage_male_1_speak_normal_1.png
stage_male_1_speak_normal_2.png
stage_male_1_speak_angry_1.png
stage_male_1_speak_angry_2.png
stage_male_1_listening_1.png
stage_male_1_listening_2.png
stage_male_1_waking_1.png  ⚠️ (walking 오타)
stage_male_1_waking_2.png
stage_male_1_annoyed_1.png
stage_male_1_annoyed_2.png
stage_male_1_idle_annoyed_1.png
stage_male_1_idle_annoyed_2.png
```

### 여자 캐릭터 (옥순)

```
stage_female_1_idle_1.png
stage_female_1_idle_2.png
stage_female_1_speak_normal_1.png
stage_female_1_speak_normal_2.png
stage_female_1_speak_angry_1.png
stage_female_1_speak_angry_2.png
stage_female_1_listening_1.png
stage_female_1_listening_2.png
stage_female_1_walking_1.png
stage_female_1_walking_2.png
stage_female_1_annoyed_1.png
stage_female_1_annoyed_2.png
stage_female_1_idle_annoyed_1.png
stage_female_1_idle_annoyed_2.png
```

---

## 🎭 시나리오 상세

### 1️⃣ 부부싸움 시나리오

**줄거리**: 저녁 메뉴를 두고 다투다가 화해하는 상철과 옥순

1. **평화로운 시작** (idle)
    - 상철: "여보, 오늘 저녁 뭐 먹을까?"
    - 옥순: "글쎄... 라면?"

2. **불만 시작** (annoyed)
    - 상철: "어제도 라면이었잖아..."
    - 옥순: "그럼 당신이 해요!"

3. **본격 싸움** (speak_angry)
    - 상철: "내가 언제 못한다고 했어!"
    - 옥순: "그럼 지금 당장 해봐요!"

4. **상철 후퇴** (walking)
    - 상철: "앗... 잠깐..."
    - 옥순 다가옴 (walking)

5. **항복** (speak_normal)
    - 상철: "라면... 좋지... 맛있어..."
    - 옥순: "그럼 라면 끓여줄게~"

6. **화해** (idle)
    - 함께: "역시 우리는 라면이 최고야!"

---

### 2️⃣ 옥순의 혼잣말 시나리오 ✨ NEW!

**줄거리**: 어제 슈퍼마켓에서 겪은 황당한 경험을 혼자 중얼거리는 옥순

**특징**: 모든 7가지 애니메이션 타입 활용!

1. **이야기 시작** (idle → speak_normal)
    - "아휴... 어제 슈퍼마켓 갔다가..."

2. **회상하며 이동** (walking)
    - "계산대에 줄 서있는데..."
    - 무대를 이리저리 돌아다니며 회상

3. **짜증나는 순간** (annoyed)
    - "30분을 기다렸어..."

4. **황당했던 순간** (speak_normal)
    - "점원이 '잠시만요~' 하더니..."

5. **화났던 순간** (speak_angry)
    - "휴게실 가버렸어!!"

6. **계속 이동** (walking)
    - 발걸음 소리와 함께 무대를 돌아다님

7. **들어야만 했던 순간** (listening)
    - "\"신입이라 잘 몰라요~\" 라고..."

8. **짜증나서 서있기** (idle_annoyed)
    - "그래서 40분 기다렸어..."

9. **완전 분노** (speak_angry)
    - "\"죄송합니다\"만 10번 들었어!"

10. **반전** (speak_normal)
    - "쿠폰 5장 받았지 뭐야~"

11. **또 짜증** (annoyed)
    - "...아 또 줄 서야 하나..."

**사용된 애니메이션 (7/7)**:
✅ IDLE, ✅ SPEAK_NORMAL, ✅ WALKING, ✅ ANNOYED, ✅ SPEAK_ANGRY, ✅ IDLE_ANNOYED, ✅ LISTENING

### 애니메이션 활용 포인트

- ✅ **대사할 때**: `SPEAK_NORMAL` 또는 `SPEAK_ANGRY` + 음성
- ✅ **듣고 있을 때**: `LISTENING`
- ✅ **짜증날 때**: `ANNOYED` 또는 `IDLE_ANNOYED`
- ✅ **이동할 때**: `WALKING` + 🔊 발걸음 소리
- ✅ **기본 자세**: `IDLE`

---

## ⚙️ 설정 변경

### 시나리오 전환

```kotlin
// 옥순의 혼잣말 (기본값)
StageTestScenario.currentScenario = StageTestScenario.ScenarioType.OKSUN_MONOLOGUE // 부부싸움
StageTestScenario.currentScenario = StageTestScenario.ScenarioType.COUPLE_FIGHT // 기본 만남 (정적 이미지)
StageTestScenario.currentScenario = StageTestScenario.ScenarioType.BASIC
```

### 음성 엔진 전환

```kotlin
// AudioTrack (부드러운 소리, 기본값)
StageTestScenario.voiceEngineType = VoiceSoundType.AUDIO_TRACK

// ToneGenerator (레트로 비프음)
StageTestScenario.voiceEngineType = VoiceSoundType.TONE_GENERATOR
```

### 프레임 속도 조절

```kotlin
spriteAnimation = CharacterAnimationState(
    gender = CharacterGender.MALE,
    currentAnimation = CharacterAnimationType.IDLE,
    frameDuration = 300L  // 빠르게 (기본 500ms)
)
```

---

## 🔧 확장 방법

### 새로운 캐릭터 추가

1. **리소스 준비**
   ```
   stage_{gender}_{number}_{action}_{frame}.png
   예: stage_male_2_idle_1.png
   ```

2. **CharacterGender enum 확장**
   ```kotlin
   enum class CharacterGender {
       MALE,
       FEMALE,
       CHILD  // 추가
   }
   ```

3. **CharacterAnimationResources 매핑 추가**

### 새로운 애니메이션 타입 추가

1. **CharacterAnimationType enum 확장**
   ```kotlin
   enum class CharacterAnimationType {
       IDLE,
       SPEAK_NORMAL,
       JUMPING,  // 추가!
       RUNNING   // 추가!
   }
   ```

2. **리소스 매핑 추가**

---

## 📊 성능 최적화

### 적용된 최적화

- ✅ **LaunchedEffect** 사용으로 프레임 전환 자동화
- ✅ **remember** 사용으로 불필요한 재생성 방지
- ✅ **sceneIndex 키** 사용으로 씬 전환 시 정확히 리셋
- ✅ **derivedStateOf** 사용으로 recomposition 최소화
- ✅ **DisposableEffect** 사용으로 사운드 매니저 자동 정리
- ✅ **프레임 1에서만 발걸음 소리** 재생으로 중복 방지

### 메모리 사용량

- 각 캐릭터당 약 **11-15KB** (압축된 PNG)
- 2프레임 애니메이션이므로 부담 없음
- 필요할 때만 로드

---

## 🎮 테스트 방법

1. 앱 실행
2. `StageView` 포함된 화면으로 이동
3. **"클릭하여 시작"** 버튼 클릭
4. 애니메이션 시나리오 자동 재생!

### 시나리오 전환 테스트

```kotlin
// 개발자 옵션 또는 설정 화면에서
Button(onClick = {
    StageTestScenario.useAnimatedScenario = 
        !StageTestScenario.useAnimatedScenario
}) {
    Text("시나리오 전환")
}
```

---

## 📝 파일 구조

```
com.kakaoent.presentation.schedule.compose/
├── StageView.kt                    # 메인 무대 컴포저블 + 발걸음 소리
├── CharacterAnimation.kt           # 애니메이션 타입 & 리소스 매니저
├── VoiceSoundEngine.kt            # 음성 엔진 (AudioTrack/ToneGenerator)
├── TheaterScriptBuilder.kt        # DSL 빌더
├── StageTestScenario.kt           # 시나리오 선택 & 기본 시나리오
├── StageAnimatedScenario.kt       # 부부싸움 시나리오
├── StageOksunMonologue.kt         # 옥순의 혼잣말 시나리오 ✨ NEW!
└── STAGE_ANIMATION_README.md      # 이 문서
```

---

## 🐛 알려진 이슈

### 리소스 오타

- **남자 walking**: `stage_male_1_waking_*.png` (walking이어야 함)
    - 현재 코드에서는 이를 고려하여 처리 중

---

## 💡 추가 개선 아이디어

### 단기 개선

- [ ] **감정 표현 강화**: 기쁨, 슬픔 등 추가
- [ ] **3프레임 이상 지원**: 더 부드러운 애니메이션
- [x] ~~**발걸음 소리 추가**~~ ✅ 완료!
- [ ] **추가 효과음**: 화난 소리, 웃음 소리 등

### 중기 개선

- [ ] **방향 전환**: 좌/우/상/하 각각 다른 스프라이트
- [ ] **카메라 효과**: 줌인/줌아웃
- [ ] **파티클 효과**: 땀방울, 하트 등

### 장기 개선

- [ ] **AI 대화 연동**: ChatGPT로 동적 대사 생성
- [ ] **멀티플레이어**: 실시간 협업 연극
- [ ] **녹화 기능**: 시나리오 녹화 및 공유

---

**완성! 이제 생동감 넘치는 무대를 즐겨보세요!** 🎉
