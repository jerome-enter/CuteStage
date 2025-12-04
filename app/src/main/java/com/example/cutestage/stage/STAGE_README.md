# StageView - 연극 무대 시스템 with 동물의 숲 음성

## 개요

2D 이미지 기반의 캐릭터 애니메이션과 말풍선을 조합한 연극 무대 컴포저블입니다.
**동물의 숲 스타일 음성 효과**가 포함되어 있습니다!

## 빠른 시작

### 1️⃣ 리소스 준비

`res/drawable/` 폴더에 다음 이미지들을 준비하세요:

```
res/drawable/
├── stage_floor.png         # 무대 배경
├── stage_ch_m_1.png       # 남자 캐릭터 (상철)
└── stage_ch_f_1.png       # 여자 캐릭터 (옥순)
```

### 2️⃣ 사용 방법

Activity나 다른 Composable에 `StageView`를 추가하기만 하면 됩니다:

```kotlin
@Composable
fun YourScreen() {
    StageView(
        modifier = Modifier.fillMaxSize()
    )
}
```

### 3️⃣ 테스트

1. 앱 실행
2. `StageView`가 있는 화면으로 이동
3. **무대를 클릭**하면 자동으로 테스트 시나리오 실행
4. 14개 씬의 애니메이션과 대사가 자동 재생
5. **동물의 숲 스타일 음성도 함께 재생됩니다!**

---

## 음성 엔진 시스템

### 두 가지 엔진 지원

**1. AudioTrack (기본값)** ✨ 추천

- 사인파 기반 부드러운 소리
- 페이드 인/아웃 적용
- 동물의 숲과 가장 유사한 귀여운 느낌

**2. ToneGenerator**  레트로

- 단순하고 명확한 비프음
- 가볍고 빠른 처리
- 팩맨/갤러그 같은 레트로 게임 느낌

### 엔진 전환 방법

#### 방법 1: 테스트 시나리오에서 설정 (간단)

```kotlin
// AudioTrack 사용 (기본값)
StageTestScenario.voiceEngineType = VoiceSoundType.AUDIO_TRACK

// ToneGenerator 사용
StageTestScenario.voiceEngineType = VoiceSoundType.TONE_GENERATOR
```

#### 방법 2: 전역 설정

```kotlin
// 어디서든 변경 가능
VoiceSoundManagerFactory.currentEngineType = VoiceSoundType.AUDIO_TRACK
VoiceSoundManagerFactory.currentEngineType = VoiceSoundType.TONE_GENERATOR
```

### 실시간 비교

```kotlin
// 앱 실행 중 전환해서 비교해보세요!
Button(onClick = { 
    StageTestScenario.voiceEngineType = VoiceSoundType.AUDIO_TRACK 
}) {
    Text("AudioTrack")
}

Button(onClick = { 
    StageTestScenario.voiceEngineType = VoiceSoundType.TONE_GENERATOR 
}) {
    Text("ToneGenerator")
}
```

---

## 캐릭터 음성 설정

### 상철 (남자) 음성

```kotlin
CharacterVoice(
    pitch = 0.8f,      // 낮은 목소리
    speed = 90,         // 느리게 말함
    duration = 55,      // 소리 길이
    volume = 0.6f       // 볼륨
)
```

### 옥순 (여자) 음성

```kotlin
CharacterVoice(
    pitch = 1.3f,      // 높은 목소리
    speed = 65,         // 빠르게 말함
    duration = 48,
    volume = 0.5f
)
```

### 프리셋 사용

```kotlin
// 미리 정의된 음성 프리셋
val voice = CharacterVoice.MALE_DEEP      // 남자 - 깊은 목소리
val voice = CharacterVoice.MALE_NORMAL    // 남자 - 보통
val voice = CharacterVoice.FEMALE_NORMAL  // 여자 - 보통
val voice = CharacterVoice.FEMALE_HIGH    // 여자 - 높은 목소리
val voice = CharacterVoice.CHILD          // 어린이
val voice = CharacterVoice.SILENT         // 음성 없음
```

---

## 파일 구조

```
com/kakaoent/presentation/schedule/compose/
├── StageView.kt                  # 메인 무대 컴포저블 + 데이터 모델
├── VoiceSoundEngine.kt          # 🎵 음성 엔진 (AudioTrack + ToneGenerator)
├── StageTestScenario.kt         # 테스트 시나리오 (음성 포함)
├── TheaterScriptBuilder.kt      # DSL 빌더
├── SampleTheaterScript.kt       # 추가 샘플 시나리오
└── STAGE_README.md              # 이 파일
```

---

## 커스터마이징

### 리소스 이름 변경

`StageTestScenario.kt` 파일을 열어서 리소스 이름을 실제 이미지 파일명으로 변경:

```kotlin
// Before
backgroundRes = R.drawable.stage_floor,

// After
backgroundRes = R.drawable.your_background_image,
```

### 새로운 캐릭터 음성 만들기

```kotlin
val customVoice = CharacterVoice(
    pitch = 1.5f,   // 0.5 ~ 2.0
    speed = 70,      // 글자당 ms (작을수록 빠름)
    duration = 50,   // 소리 길이 (ms)
    volume = 0.6f    // 0.0 ~ 1.0
)
```

### 새로운 시나리오 만들기

```kotlin
val myScript = theaterScript {
    scene(
        backgroundRes = R.drawable.my_background,
        durationMillis = 2000L
    ) {
        character(
            id = "hero",
            imageRes = R.drawable.my_character,
            name = "주인공",
            x = 50.dp,
            y = 150.dp,
            voice = CharacterVoice.MALE_NORMAL  // 음성 설정!
        )
        
        dialogue(
            text = "안녕하세요!",
            x = 120.dp,
            y = 120.dp,
            speakerName = "주인공",
            voice = CharacterVoice.MALE_NORMAL  // 음성 설정!
        )
    }
}

StageView(script = myScript)
```

---

## 주요 기능

### 애니메이션

- ✅ 위치 이동 (부드러운 이동)
- ✅ 페이드인/아웃 (투명도)
- ✅ 크기 변화 (강조 효과)
- ✅ 회전
- ✅ 좌우 반전 (캐릭터 방향)

### 말풍선

- ✅ 타자기 효과 (텍스트 한 글자씩 표시)
- ✅ **동물의 숲 스타일 음성 재생** 🎵
- ✅ 지연 표시 (delayMillis)
- ✅ 화자 이름 표시
- ✅ 부드러운 등장/퇴장

### 음성

- ✅ **캐릭터별 음높이(pitch) 설정**
- ✅ **말하는 속도 제어**
- ✅ **두 가지 엔진 선택 가능**
- ✅ 실시간 전환 가능
- ✅ 추가 라이브러리 필요 없음 (Android SDK 기본)

---

## 문제 해결

### 이미지가 안 보여요

1. 리소스 파일이 `res/drawable/` 폴더에 있는지 확인
2. 파일 이름이 `StageTestScenario.kt`의 리소스 이름과 일치하는지 확인
3. 프로젝트를 Clean & Rebuild

### 음성이 안 들려요

1. 기기 볼륨 확인
2. 음소거 모드가 아닌지 확인
3. `CharacterVoice.enabled = true`인지 확인
4. 다른 엔진 타입으로 전환해보기:
   ```kotlin
   StageTestScenario.voiceEngineType = VoiceSoundType.TONE_GENERATOR
   ```

### AudioTrack vs ToneGenerator 중 어떤 걸 써야 하나요?

- **AudioTrack (추천)**: 부드럽고 귀여운 소리, 동물의 숲과 유사
- **ToneGenerator**: 레트로 게임 느낌, 더 가볍고 빠름

둘 다 테스트해보고 마음에 드는 걸 선택하세요!

---

## 성능

### 최적화 적용

- `remember`로 계산 캐싱
- `derivedStateOf`로 불필요한 recomposition 방지
- `@Immutable` 어노테이션으로 안정성 확보
- Lambda 기반 Modifier 사용
- 음성 재생은 백그라운드 스레드에서 처리

### 음성 성능

- **매우 가벼움**: 각 비프음은 50ms 이하
- **메모리 효율적**: 실시간 생성, 캐싱 불필요
- **CPU 부담 없음**: 단순한 사인파 생성

---

## 향후 개선 가능 사항

1. **스프라이트 시트 지원** - 걷기 애니메이션 등
2. **감정에 따른 음성 변화** - 기쁨/슬픔/화남
3. **인터렉션** - 사용자 클릭으로 다음 씬 진행
4. **조건부 분기** - 선택지에 따른 스토리 분기
5. **카메라 효과** - 줌인/줌아웃, 화면 흔들림
6. **파티클 효과** - 불, 연기, 마법 등

---

**🎮 이제 바로 테스트 가능합니다!**

추가 라이브러리 없이 Android SDK만으로 동물의 숲 스타일 음성이 재생됩니다!
