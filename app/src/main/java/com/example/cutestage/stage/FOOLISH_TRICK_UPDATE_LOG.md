# 🎨 리소스 명명 규칙 업데이트 로그

## 📅 업데이트 일자

2025년 12월 4일

## 🔄 변경 사항

### 새로운 리소스 명명 규칙

```
인물_자세_감정_소지품.png
```

#### 구성 요소

| 항목 | 설명 | 예시 | 필수 여부 |
|------|------|------|----------|
| **인물** | 캐릭터 이름 | mother, father, daughter, grandma | ✅ 필수 |
| **자세** | 동작/자세 | sit, stand, walk, throw | ✅ 필수 |
| **감정** | 감정 상태 | angry, sad, suprise, talk | ⭕ 선택 |
| **소지품** | 들고 있는 물건 | fish, basket, tool | ⭕ 선택 |

### 기존 규칙과 비교

| 기존 규칙 | 새 규칙 |
|-----------|---------|
| ❌ `배역_자세_감정.png` | ✅ `인물_자세_감정_소지품.png` |
| `mother_stand_angry.png` | `mother_stand_angry_fish.png` |
| `mother_throw_angry.png` | `mother_throw_angry_fish.png` |

## ✅ 적용된 파일

### 1. 문서 파일

- ✅ `FOOLISH_TRICK_PRODUCTION_RULE.md` - 제작 가이드 업데이트
- ✅ `FOOLISH_TRICK_SUMMARY.md` - 완료 보고서 업데이트
- ✅ `FOOLISH_TRICK_UPDATE_LOG.md` - 이 파일 (신규 작성)

### 2. 코드 파일

- ✅ `StageFoolishTrick.kt` - 리소스 참조 업데이트
    - `R.drawable.mother_stand_angry_fish` (7곳)
    - `R.drawable.mother_throw_angry_fish` (1곳)

### 3. 리소스 파일 (이미 준비됨)

- ✅ `mother_stand_angry_fish.png` - 광례가 조기를 들고 화남
- ✅ `mother_throw_angry_fish.png` - 광례가 조기를 던짐

## 🎯 변경 이유

### 1. 소지품 정보 추가

- 캐릭터가 들고 있는 **소품을 파일명에 명시**
- 예: `_fish` = 조기를 들고 있음
- 시각적 정보를 파일명만으로 파악 가능

### 2. 스토리텔링 강화

- "조기 밥상" 이야기의 핵심 소품인 **조기가 시각적으로 표현됨**
- 갈등의 중심이 되는 물건을 계속 보여줌
- 관객의 몰입도 향상

### 3. 확장성

- 향후 다른 소지품 추가 용이
- 예: `mother_walk_basket.png` (바구니를 들고 걷는 모습)
- 예: `daughter_sit_book.png` (책을 들고 앉은 모습)

## 📊 사용 통계

### "폭삭 속았수다" 시나리오에서의 사용

| 리소스 파일 | 사용 씬 수 | 주요 용도 |
|-------------|-----------|----------|
| `mother_stand_angry_fish.png` | 7개 씬 | 조기를 들고 갈등 중 |
| `mother_throw_angry_fish.png` | 1개 씬 | 조기를 던지는 클라이맥스 |

**총 8개 씬에서 조기 소지품 표현** (전체 24개 씬 중 33%)

## 🎬 연출 효과

### 시각적 연속성

```
씬 7:  조기를 던진다 (mother_throw_angry_fish.png)
     ↓
씬 8:  조기를 들고 절규 (mother_stand_angry_fish.png)
     ↓
씬 9:  조기를 들고 대치 (mother_stand_angry_fish.png)
     ↓
     ...
     ↓
씬 21: 조기를 들고 결별 (mother_stand_angry_fish.png)
```

### 스토리 효과

- 🐟 **조기 = 갈등의 상징**
- 📌 **시각적 일관성**: 광례가 계속 조기를 들고 있음
- 💥 **극적 효과**: 조기가 이야기의 중심임을 강조

## 💡 향후 적용 예시

### 추가 가능한 소지품 리소스

```
mother_walk_basket.png         - 바구니를 들고 걷기
mother_sit_tired_tool.png      - 피곤한 상태로 연장을 들고 앉음
daughter_stand_happy_flower.png - 행복하게 꽃을 들고 서있음
father_sit_angry_drink.png     - 화나서 술을 들고 앉음
grandma_stand_sad_cane.png     - 슬프게 지팡이를 짚고 서있음
```

### 명명 규칙 가이드라인

1. **인물명은 영어로** (mother, father, daughter, grandma)
2. **자세는 동사형** (sit, stand, walk, throw)
3. **감정은 형용사형** (angry, sad, happy, tired)
4. **소지품은 명사형** (fish, basket, tool, flower)
5. **언더스코어(_)로 구분**
6. **소문자 사용**

## ✨ 효과 검증

### 개선된 점

- ✅ 파일명만으로 **캐릭터 상태 완벽 파악**
- ✅ 스토리 **핵심 소품 시각화**
- ✅ 코드 **가독성 향상**
- ✅ 리소스 **관리 용이**

### Before (기존)

```kotlin
imageRes = R.drawable.mother_stand_angry
// 광례가 화났다는 것만 알 수 있음
```

### After (개선)

```kotlin
imageRes = R.drawable.mother_stand_angry_fish
// 광례가 조기를 들고 화났다는 것까지 명확히 알 수 있음 ⭐
```

## 🎉 결론

새로운 리소스 명명 규칙 `인물_자세_감정_소지품.png`을 통해:

1. **스토리텔링 강화** 🎭
2. **시각적 일관성** 🎨
3. **코드 가독성** 💻
4. **확장성 확보** 🚀

모두 달성했습니다!

---

**문의사항이나 제안사항이 있으시면 언제든 연락주세요!** 📧
