package com.example.cutestage.stage

/**
 * StageView에서 발생하는 이벤트
 */
sealed class StageEvent {
    // 재생 제어
    object Play : StageEvent()
    object Stop : StageEvent()
    data class ChangePlaybackSpeed(val speed: Float) : StageEvent()

    // 시나리오 제어
    data class LoadScenario(
        val scenario: StageTestScenario.ScenarioType,
        val script: TheaterScript,
        val shouldPlay: Boolean
    ) : StageEvent()

    // 캐릭터 상호작용
    data class CharacterClick(val character: CharacterState) : StageEvent()
    object DismissInteraction : StageEvent()

    // 선택지
    data class SelectChoice(val nextSceneIndex: Int) : StageEvent()

    // AI 생성
    object ShowAIDialog : StageEvent()
    object HideAIDialog : StageEvent()
    data class UpdateAIInput(val input: String) : StageEvent()
    object GenerateAIScenario : StageEvent()

    // 디버그
    object ToggleDebugPoints : StageEvent()

    // 타임라인 (내부 이벤트)
    object AdvanceScene : StageEvent()
    object ScriptEnded : StageEvent()
    data class DetectChoice(val choices: List<Choice>) : StageEvent()
}
