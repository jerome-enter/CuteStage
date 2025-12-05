# Character Resources

인물_행동/감정 패턴으로 이루어진 PNG 파일 목록입니다.

## 📁 주요 파일 위치

### Kotlin 코드 파일 (앱에서 동적으로 접근)
- **경로**: `app/src/main/java/com/example/cutestage/CharacterResources.kt`
- **용도**: Android 앱 실행 중 drawable 리소스에 동적으로 접근

## 🎯 앱에서 동적으로 사용하는 방법

### 1. 기본 사용법

```kotlin
import com.example.cutestage.CharacterResources

// Activity 또는 Fragment에서
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 여성 캐릭터 리소스 이름 배열 (문자열)
        val femaleResources = CharacterResources.FEMALE_CHARACTER_RESOURCES

        // 남성 캐릭터 리소스 이름 배열 (문자열)
        val maleResources = CharacterResources.MALE_CHARACTER_RESOURCES

        // 모든 캐릭터 리소스 이름 배열 (문자열)
        val allResources = CharacterResources.ALL_CHARACTER_RESOURCES
    }
}
```

### 2. 문자열에서 리소스 ID로 변환하여 사용

```kotlin
// 문자열 리소스 이름에서 리소스 ID 가져오기
val resourceName = "stage_female_1_idle_1"
val resourceId = CharacterResources.getResourceId(context, resourceName)

// ImageView에 설정
if (resourceId != 0) {
    imageView.setImageResource(resourceId)
}

// 또는 헬퍼 함수 사용
val drawable = CharacterResources.getDrawable(context, resourceName)
imageView.setImageDrawable(drawable)
```

### 3. 배열에서 동적으로 로드

```kotlin
// 첫 번째 여성 캐릭터 로드
val firstFemale = CharacterResources.FEMALE_CHARACTER_RESOURCES[0]
val resourceId = CharacterResources.getResourceId(context, firstFemale)
imageView.setImageResource(resourceId)

// 또는 직접 drawable로
val drawable = CharacterResources.getDrawable(context, firstFemale)
imageView.setImageDrawable(drawable)
```

### 4. 랜덤 캐릭터 표시

```kotlin
// 랜덤 여성 캐릭터
val randomFemaleName = CharacterResources.FEMALE_CHARACTER_RESOURCES.random()
val drawable = CharacterResources.getDrawable(context, randomFemaleName)
imageView.setImageDrawable(drawable)

// 랜덤 모든 캐릭터
val randomName = CharacterResources.ALL_CHARACTER_RESOURCES.random()
val resourceId = CharacterResources.getResourceId(context, randomName)
imageView.setImageResource(resourceId)
```

### 5. 순차적으로 애니메이션 재생

```kotlin
// idle 애니메이션 프레임
val idleFrames = arrayOf(
    "stage_female_1_idle_1",
    "stage_female_1_idle_2"
)

var currentFrame = 0
val handler = Handler(Looper.getMainLooper())
val runnable = object : Runnable {
    override fun run() {
        val resourceId = CharacterResources.getResourceId(context, idleFrames[currentFrame])
        if (resourceId != 0) {
            imageView.setImageResource(resourceId)
        }
        currentFrame = (currentFrame + 1) % idleFrames.size
        handler.postDelayed(this, 500) // 0.5초마다 프레임 전환
    }
}
handler.post(runnable)
```

### 6. RecyclerView에서 사용

```kotlin
class CharacterAdapter(
    private val context: Context,
    private val resourceNames: Array<String>
) : RecyclerView.Adapter<CharacterAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.characterImage)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resourceName = resourceNames[position]
        val drawable = CharacterResources.getDrawable(context, resourceName)
        holder.imageView.setImageDrawable(drawable)
    }

    override fun getItemCount() = resourceNames.size
}

// 사용
val adapter = CharacterAdapter(this, CharacterResources.FEMALE_CHARACTER_RESOURCES)
recyclerView.adapter = adapter
```

### 7. 조건에 따라 문자열로 선택

```kotlin
fun getCharacterResourceName(gender: Gender, action: Action): String {
    return when (gender) {
        Gender.FEMALE -> when (action) {
            Action.IDLE -> "stage_female_1_idle_1"
            Action.WALKING -> "stage_female_1_walking_1"
            Action.SPEAKING -> "stage_female_1_speak_normal_1"
            Action.SINGING -> "stage_female_1_sing_nomal_1"
            Action.DANCING -> "stage_female_1_dancing_type_a_1"
            Action.CLAPPING -> "stage_female_1_clap_1"
            else -> "stage_female_1_idle_1"
        }
        Gender.MALE -> when (action) {
            Action.IDLE -> "stage_male_1_idle_1"
            Action.WALKING -> "stage_male_1_waking_1"
            Action.SPEAKING -> "stage_male_1_speak_normal_1"
            Action.SINGING -> "stage_male_1_sing_nomal_1"
            Action.DANCING -> "stage_male_1_dancing_type_a_1"
            Action.CLAPPING -> "stage_male_1_clap_1"
            else -> "stage_male_1_idle_1"
        }
    }
}

// 사용
val resourceName = getCharacterResourceName(Gender.FEMALE, Action.DANCING)
val drawable = CharacterResources.getDrawable(context, resourceName)
imageView.setImageDrawable(drawable)

enum class Gender { FEMALE, MALE }
enum class Action { IDLE, WALKING, SPEAKING, SINGING, DANCING, CLAPPING }
```

### 8. 리스트 반복으로 모든 캐릭터 로드

```kotlin
// 모든 여성 캐릭터를 순회하며 처리
CharacterResources.FEMALE_CHARACTER_RESOURCES.forEach { resourceName ->
    val drawable = CharacterResources.getDrawable(context, resourceName)
    // drawable 사용
    Log.d("Character", "Loaded: $resourceName")
}
```

### 9. 특정 패턴 필터링

```kotlin
// "singing"이 포함된 리소스만 필터링
val singingResources = CharacterResources.ALL_CHARACTER_RESOURCES.filter { 
    it.contains("sing") 
}

// 첫 번째 singing 리소스 로드
val firstSinging = singingResources.firstOrNull()
firstSinging?.let { resourceName ->
    val drawable = CharacterResources.getDrawable(context, resourceName)
    imageView.setImageDrawable(drawable)
}
```

### 10. 서버에서 받은 문자열로 동적 로드

```kotlin
// 서버에서 "stage_male_1_dancing_type_a_1" 같은 문자열을 받았다고 가정
fun loadCharacterFromServer(characterName: String) {
    // 리소스가 존재하는지 확인
    val resourceId = CharacterResources.getResourceId(context, characterName)

    if (resourceId != 0) {
        // 리소스가 존재하면 로드
        imageView.setImageResource(resourceId)
    } else {
        // 리소스가 없으면 기본 이미지 표시
        Log.e("Character", "Resource not found: $characterName")
        imageView.setImageResource(R.drawable.stage_female_1_idle_1)
    }
}
```

## 📊 리소스 통계

- **총 파일 수**: 56개
- **여성 캐릭터**: 28개
- **남성 캐릭터**: 28개

### 여성 캐릭터 (Female)
- Idle (기본 자세): 4개
- Walking (걷기): 2개
- Annoyed (짜증): 4개
- Listening (듣기): 2개
- Speaking (말하기): 4개
- Singing (노래하기): 6개
- Dancing (춤추기): 6개
- Clapping (박수): 2개

### 남성 캐릭터 (Male)
- Idle (기본 자세): 4개
- Waking (걷기): 2개
- Annoyed (짜증): 4개
- Listening (듣기): 2개
- Speaking (말하기): 4개
- Singing (노래하기): 6개
- Dancing (춤추기): 6개
- Clapping (박수): 2개

## 🖼️ 실제 PNG 파일 위치

모든 PNG 파일은 다음 위치에 있습니다:

```
app/src/main/res/drawable-xxxhdpi/
```

## 📝 참고 사항

- 리소스 이름은 `.png` 확장자를 제외한 **문자열**입니다
- `getResourceId()`로 문자열 → 리소스 ID 변환 (실패시 0 반환)
- `getDrawable()`로 문자열 → Drawable 객체 변환 (실패시 null 반환)
- 파일명 패턴: `stage_{gender}_{index}_{action}_{variant}`
- 애니메이션은 `_1`, `_2` 등의 변형으로 구분됩니다
- 문자열 배열이므로 필터링, 검색, 매핑 등 자유롭게 사용 가능

## ⚡ 성능 팁

```kotlin
// getIdentifier는 상대적으로 느리므로, 자주 사용한다면 캐싱 권장
class CharacterResourceCache(private val context: Context) {
    private val cache = mutableMapOf<String, Int>()

    fun getResourceId(resourceName: String): Int {
        return cache.getOrPut(resourceName) {
            context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        }
    }
}
```

## 📦 추가 파일 (참고용)

프로젝트 루트에 참고용 파일도 제공됩니다:
- `character_resources.json` - JSON 형식
- `character_resources.txt` - 텍스트 형식 (줄바꿈 구분)
