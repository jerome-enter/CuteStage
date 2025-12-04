# "폭삭 속았수다 - 제1막 조기 밥상" 제작 완료 보고서 (최종판)

## 📋 작업 완료 사항

### ✅ 1. 제작 가이드 문서 작성
**파일**: `FOOLISH_TRICK_PRODUCTION_RULE.md`

- 등장인물 분석 및 리소스 매핑
- 음성 설정 상세 가이드
- 무대 구성 및 배치 계획
- **새로운 리소스 명명 규칙**: `인물_자세_감정_소지품.png`
- **애니메이션 시스템**: 숫자 붙은 리소스 (1->2->1 반복) ⭐

### ✅ 2. 시나리오 구현 (최종판 - 애니메이션 적용)
**파일**: `StageFoolishTrick.kt`

#### 리소스 명명 규칙 & 애니메이션 시스템
```
인물_자세_감정_소지품숫자.png
```

**애니메이션 원리**:

- 숫자가 붙은 리소스 (예: `mother_walk1.png`, `mother_walk2.png`)
- 1 → 2 → 1 순으로 반복하여 프레임 애니메이션 효과
- 각 씬마다 교대로 표시하여 움직임 표현

**구성 요소**:
- **인물**: mother, father, daughter, grandma 등
- **자세**: sit, stand, walk, throw 등
- **감정**: angry, sad, suprise 등 (선택사항)
- **소지품**: fish, basket 등 (선택사항)
- **숫자**: 1, 2 (애니메이션용)

#### 등장인물 (4명)

1. **엄마 (광례)** (애순의 엄마)
    - **애니메이션 리소스** (1→2→1 반복):
        - `mother_walk1.png` ↔ `mother_walk2.png` - 걷기 애니메이션
        - `mother_throw1.png` ↔ `mother_throw2.png` - 조기 던지기 애니메이션
        - `mother_stand_angry1.png` ↔ `mother_stand_angry2.png` - 분노 애니메이션
    - 음성: pitch 1.2f, speed 80, volume 0.7f (큰 목소리)

2. **작은아버지** (애순의 삼촌)
    - **애니메이션 리소스**:
        - `father_stand_angry1.png` ↔ `father_stand_angry2.png` - 분노 애니메이션
        - `father_sit_suprise1.png` ↔ `father_sit_suprise2.png` - 놀람 애니메이션
    - **정적 리소스**: `father_sit.png`
    - 음성: pitch 0.7f, speed 95, volume 0.6f (낮은 목소리)

3. **할머니** (애순의 친할머니)
    - **정적 리소스**: `grandma_sit.png`, `grandma_sit_sad.png`, `grandma_sit_suprise.png`
    - 음성: pitch 1.1f, speed 110, volume 0.4f (약한 목소리)

4. **애순** (딸)
    - **애니메이션 리소스**:
        - `daughter_walk1.png` ↔ `daughter_walk2.png` - 걷기 애니메이션
    - **정적 리소스**: `daughter_sit.png`, `daughter_sit_suprise.png`
    - 음성: pitch 1.6f, speed 75, volume 0.45f (어린 목소리)

#### 배경

- **house.png**: 제주 초가집 (씬 1-18)
- **stage_floor.png**: 일반 무대 (씬 19-36)

#### 씬 구성 (총 36개 씬) ⭐ 애니메이션 적용!
```
씬 1:        평화로운 초가집
씬 2-2-2:    엄마 등장! 조기 던지기 애니메이션 (throw1 → throw2)
씬 3-3-2:    모두 놀람 (애니메이션 1 → 2)
씬 4-5:      엄마의 반문 (애니메이션 1 → 2)
씬 6-10:     엄마의 연속 공격 (애니메이션 반복)
             "조구 애끼요?" → "조구 애껴 떼돈 버요?"
             "엄니, 오씨 딸이요" → "구천에서 피눈물 내요"
씬 11-13:    작은아빠 반격 & 엄마 절규
씬 14-15:    엄마의 역공 (애니메이션 반복)
씬 16-16-2:  엄마가 딸에게로 (walk1 → walk2 걷기 애니메이션)
씬 17:       "설거지는 우라질"
씬 18-18-2:  퇴장 (walk1 → walk2 애니메이션)

--- 장면 이동 (stage_floor 배경) ---

씬 19-19-2:  배경 전환 - 걷기 애니메이션 (walk1 → walk2)
씬 20:       딸 "조구 못 읃어먹길 잘했네"
씬 21-22:    엄마 "넌 내가 좋으냐?"
씬 23:       딸 "엄마니까. 엄마니까 좋지" ❤️
씬 24-31:    저녁 메뉴 우스꽝스러운 대화
             (stand_angry1 ↔ 2 애니메이션)
씬 32-32-2:  허탈한 웃음 (walk1 → walk2 애니메이션)
씬 33-33-2:  엔딩 - 함께 걷기 (walk1 → walk2 애니메이션)
씬 34:       암전 (막)
```

#### 러닝타임

- **총 시간**: 약 110~120초 (약 1분 50초~2분)
- **평균 씬 길이**: 3.1초
- **본편 (house)**: 약 58초 (씬 1-18)
- **에필로그 (stage_floor)**: 약 60초 (씬 19-36)

### ✅ 3. 시스템 통합
**파일**: `StageTestScenario.kt` 수정

- `ScenarioType.FOOLISH_TRICK` 추가
- 기본 시나리오로 설정

## 🎭 핵심 장면 하이라이트

### 1. 조기 던지기 애니메이션 (씬 2~2-2) 🎬
```kotlin
// 씬 2: 애니메이션 프레임 1
character(imageRes = R.drawable.mother_throw1)
dialogue(text = "옛다! 조구새끼!")

// 씬 2-2: 애니메이션 프레임 2
character(imageRes = R.drawable.mother_throw2)
dialogue(text = "(조기 집어던짐)")
```

**효과**: 던지는 동작이 2프레임 애니메이션으로 표현!

### 2. 엄마의 연속 공격 (씬 6-10) - 애니메이션 반복
```kotlin
 씬 6:  mother_stand_angry1  "조구 애껴 떼돈 버요?"
 씬 7:  mother_stand_angry2  "엄니, 오씨 딸이요!"
 씬 8:  mother_stand_angry1  "그 애비는 안 닮았겄소?"
 씬 9:  mother_stand_angry2  "그러지 마소, 그러지 마"
 씬 10: mother_stand_angry1  "구천에서 피눈물 내요"
```

**효과**: 분노하며 움직이는 모습이 자연스럽게!

### 3. 걷기 애니메이션 (씬 16~16-2, 18~18-2, 19~19-2, 32~33) 🚶‍♀️
```kotlin
// 엄마가 딸에게 다가가는 씬
씬 16:   mother_walk1  "내 딸. 내가 찾아가요"
씬 16-2: mother_walk2  "(애순에게 다가간다)"

// 퇴장 씬
씬 18:   mother_walk1 + daughter_walk1
씬 18-2: mother_walk2 + daughter_walk2

// 장면 이동
씬 19:   walk1  (장면 이동 시작)
씬 19-2: walk2  (계속 걷기)

// 엔딩
씬 33:   walk1  "손을 꼭 잡고..."
씬 33-2: walk2  (페이드아웃)
```

**효과**: 1→2→1 반복으로 자연스러운 걷기 표현!

### 4. 가장 감동적인 순간 (씬 23)
```kotlin
엄마: "넌 내가 좋으냐? 내가 뭐가 좋아?"
딸:   "엄마니까. 엄마니까 좋지. 말이라고 물어?" ❤️
```

### 5. 코미디 폭발 (씬 30-32)
```kotlin
딸:  "우리 집에 계란이랑 파... 있긴 해?"
엄마: "...아마도...?" 💀
     (walk1 → walk2 애니메이션으로 허탈한 웃음)
둘:  "하하하하! 라면 물이나 많이 넣어불자!" 😂
```

## 🎯 제작 기준 달성 체크

- [x] ✅ 간결하고 집중된 스토리라인
- [x] ✅ 핵심 대사만 포함
- [x] ✅ **애니메이션 시스템 적용** (1→2→1 반복)
- [x] ✅ 감동과 웃음의 조화
- [x] ✅ 제주 방언 살아있음
- [x] ✅ 모성애 주제 명확히 전달
- [x] ✅ 조기 논리 완벽 (throw1→throw2로 던지기)
- [x] ✅ 캐릭터 겹침 방지
- [x] ✅ **새로운 리소스 규칙 준수** (숫자 포함)

## 📝 기술적 구현 사항

### 리소스 활용 (애니메이션 적용)

**애니메이션 리소스 (1↔2 교대)**:

1. `mother_walk1.png` ↔ `mother_walk2.png`
2. `mother_throw1.png` ↔ `mother_throw2.png` ⭐
3. `mother_stand_angry1.png` ↔ `mother_stand_angry2.png`
4. `father_stand_angry1.png` ↔ `father_stand_angry2.png`
5. `father_sit_suprise1.png` ↔ `father_sit_suprise2.png`
6. `daughter_walk1.png` ↔ `daughter_walk2.png`

**정적 리소스**:

7. `father_sit.png`
8. `grandma_sit.png`
9. `grandma_sit_sad.png`
10. `grandma_sit_suprise.png`
11. `daughter_sit.png`
12. `daughter_sit_suprise.png`

**배경**:

13. `house.png`
14. `stage_floor.png`

### 애니메이션 시스템 ⭐⭐ 핵심!

**원리**:
```
씬 N:   리소스_1 표시 (예: mother_walk1.png)
        ↓
씬 N+1: 리소스_2 표시 (예: mother_walk2.png)
        ↓
씬 N+2: 리소스_1 표시 (다시 반복)
```

**효과**:

- 🎬 2프레임 애니메이션으로 움직임 표현
- 💃 자연스러운 걷기, 던지기, 분노 동작
- 🎭 정적 이미지에 생동감 부여

**사용 예시**:

```kotlin
// 걷기 애니메이션
scene { character(imageRes = R.drawable.mother_walk1) }  // 프레임 1
scene { character(imageRes = R.drawable.mother_walk2) }  // 프레임 2
scene { character(imageRes = R.drawable.mother_walk1) }  // 프레임 1 (반복)

// 던지기 애니메이션
scene { character(imageRes = R.drawable.mother_throw1) }  // 준비 자세
scene { character(imageRes = R.drawable.mother_throw2) }  // 던지는 동작

// 분노 애니메이션 (대사와 함께)
scene { 
    character(imageRes = R.drawable.mother_stand_angry1)
    dialogue("조구 애끼요?")
}
scene { 
    character(imageRes = R.drawable.mother_stand_angry2)
    dialogue("조구 애껴 떼돈 버요?")
}
```

### 감정 표현 기법

- **scale**: 0.95f ~ 1.3f (감정 강도)
- **flipX**: 시선 방향
- **alpha**: 0f ~ 1f (등장/퇴장)
- **position**: 거리감과 관계 표현
- **animationDuration**: 600~2000ms
- **애니메이션**: 1↔2 프레임 교대

## 🎬 스토리 구조

### Part 1: 갈등 (house 배경, 씬 1-18)
```
평화 → 엄마 등장 (애니메이션) → 조기 던지기 (throw1→2) → 언쟁 (angry1↔2) → 딸 데려감 (walk1→2)
```

### Part 2: 모성애 & 코미디 (stage_floor 배경, 씬 19-36)
```
장면 이동 (walk1→2) → 감동적 대화 → 저녁 고민 (애니메이션) → 허탈한 웃음 (walk1→2) → 엔딩
```

## 💡 개선 포인트

### 리소스 시스템 업데이트

| Before                          | After                                                           |
|---------------------------------|-----------------------------------------------------------------|
| ❌ `mother_walk.png` (정적)        | ✅ `mother_walk1.png` ↔ `mother_walk2.png` (애니메이션)               |
| ❌ `mother_throw_angry_fish.png` | ✅ `mother_throw1.png` ↔ `mother_throw2.png` (애니메이션)             |
| ❌ `mother_stand_angry.png` (정적) | ✅ `mother_stand_angry1.png` ↔ `mother_stand_angry2.png` (애니메이션) |

### 씬 구조

| Before     | After              |
|------------|--------------------|
| 34개 씬 (정적) | 36개 씬 (애니메이션 적용) ✨ |
| 단조로운 표현    | 2프레임 애니메이션 ✨       |
| 정적 이미지     | 생동감 있는 움직임 ✨       |

## 🚀 실행 방법

```bash
./gradlew installDebug
```

앱을 실행하면 자동으로 새로운 애니메이션 시나리오가 로드됩니다!

---

**제주 방언으로 펼쳐지는 1분 50초의 감동과 웃음!** 🌊  
**2프레임 애니메이션으로 살아 움직이는 연극!** 🎬  
"엄마니까. 엄마니까 좋지. 말이라고 물어?" 💕  
"...아마도...?" (계란이랑 파) 💀😂

