package com.example.cutestage.ui.creator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cutestage.data.module.*
import com.example.cutestage.data.scenario.ScenarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 시나리오 생성 화면의 ViewModel
 */
@HiltViewModel
class ScenarioCreatorViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val scenarioRepository: ScenarioRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI 상태
    var state by mutableStateOf(ScenarioCreatorState())
        private set

    // 모듈 타입 목록
    val moduleTypes = moduleRepository.getAllModuleTypes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 선택된 타입의 카테고리 목록
    private val _categories = MutableStateFlow<List<ModuleCategoryEntity>>(emptyList())
    val categories: StateFlow<List<ModuleCategoryEntity>> = _categories.asStateFlow()

    // 선택된 카테고리의 모듈 아이템 목록
    private val _moduleItems = MutableStateFlow<List<ModuleItemEntity>>(emptyList())
    val moduleItems: StateFlow<List<ModuleItemEntity>> = _moduleItems.asStateFlow()

    init {
        // scenarioId가 있으면 편집 모드로 로드
        val scenarioId = savedStateHandle.get<String>("scenarioId")
        if (scenarioId != null) {
            loadScenarioForEdit(scenarioId)
        }

        // 첫 번째 타입(대사) 자동 선택 (비동기로 로드되므로 collect 사용)
        viewModelScope.launch {
            moduleTypes.collect { types ->
                if (types.isNotEmpty() && state.selectedTypeId == null) {
                    selectModuleType(types.first().id)
                }
            }
        }
    }

    /**
     * 모듈 타입 선택
     */
    fun selectModuleType(typeId: String) {
        state = state.copy(selectedTypeId = typeId, selectedCategoryId = null)
        viewModelScope.launch {
            moduleRepository.getCategoriesByType(typeId)
                .collect { categories ->
                    _categories.value = categories
                    // 첫 번째 카테고리 자동 선택
                    if (categories.isNotEmpty()) {
                        selectCategory(categories.first().id)
                    }
                }
        }
    }

    /**
     * 카테고리 선택
     */
    fun selectCategory(categoryId: String) {
        state = state.copy(selectedCategoryId = categoryId)
        viewModelScope.launch {
            moduleRepository.getModuleItemsByCategory(categoryId)
                .collect { items ->
                    _moduleItems.value = items
                }
        }
    }

    /**
     * 모듈 아이템 선택 (타임라인에 추가)
     */
    fun selectModuleItem(item: ModuleItemEntity) {
        // 프리미엄 모듈이고 언락 안됐으면 언락 다이얼로그 표시
        viewModelScope.launch {
            val isUnlocked = moduleRepository.isModuleUnlocked(item.id)
            if (item.isPremium && !isUnlocked) {
                state = state.copy(
                    showUnlockDialog = true,
                    selectedModuleForUnlock = item
                )
            } else {
                // 타임라인에 추가
                addToTimeline(item)
            }
        }
    }

    /**
     * 타임라인에 모듈 추가
     */
    private fun addToTimeline(item: ModuleItemEntity) {
        val newItem = TimelineModuleItem(
            id = "${item.id}_${System.currentTimeMillis()}",
            moduleItem = item,
            order = state.timelineItems.size
        )
        state = state.copy(
            timelineItems = state.timelineItems + newItem
        )

        // 사용 횟수 증가
        viewModelScope.launch {
            moduleRepository.incrementUsageCount(item.id)
        }
    }

    /**
     * 타임라인에서 모듈 제거
     */
    fun removeFromTimeline(itemId: String) {
        state = state.copy(
            timelineItems = state.timelineItems.filter { it.id != itemId }
        )
    }

    /**
     * 타임라인 모듈 순서 변경
     */
    fun reorderTimeline(from: Int, to: Int) {
        val newList = state.timelineItems.toMutableList()
        val item = newList.removeAt(from)
        newList.add(to, item)
        state = state.copy(
            timelineItems = newList.mapIndexed { index, module ->
                module.copy(order = index)
            }
        )
    }

    /**
     * 언락 다이얼로그 닫기
     */
    fun dismissUnlockDialog() {
        state = state.copy(
            showUnlockDialog = false,
            selectedModuleForUnlock = null
        )
    }

    /**
     * 모듈 언락 (토큰 사용)
     */
    fun unlockModule(item: ModuleItemEntity) {
        viewModelScope.launch {
            // TODO: 토큰 차감 로직 추가 (Phase 5)
            moduleRepository.unlockModule(item.id, UnlockMethod.TOKEN)
            dismissUnlockDialog()
            // 언락 후 타임라인에 추가
            addToTimeline(item)
        }
    }

    /**
     * 저장 다이얼로그 표시
     */
    fun showSaveDialog() {
        state = state.copy(showSaveDialog = true)
    }

    /**
     * 저장 다이얼로그 닫기
     */
    fun dismissSaveDialog() {
        state = state.copy(showSaveDialog = false, saveDialogTitle = "", saveDialogDescription = "")
    }

    /**
     * 저장 다이얼로그 제목 업데이트
     */
    fun updateSaveDialogTitle(title: String) {
        state = state.copy(saveDialogTitle = title)
    }

    /**
     * 저장 다이얼로그 설명 업데이트
     */
    fun updateSaveDialogDescription(description: String) {
        state = state.copy(saveDialogDescription = description)
    }

    /**
     * 시나리오 저장 (생성 또는 업데이트)
     */
    fun saveScenario(onSuccess: (String) -> Unit) {
        val title = state.saveDialogTitle.trim()
        if (title.isEmpty()) {
            state = state.copy(errorMessage = "제목을 입력해주세요")
            return
        }

        if (state.timelineItems.isEmpty()) {
            state = state.copy(errorMessage = "모듈을 추가해주세요")
            return
        }

        state = state.copy(isSaving = true)
        viewModelScope.launch {
            try {
                val scenarioId = if (state.editingScenarioId != null) {
                    // 업데이트
                    scenarioRepository.updateScenario(
                        scenarioId = state.editingScenarioId!!,
                        title = title,
                        description = state.saveDialogDescription,
                        timelineItems = state.timelineItems
                    )
                    state.editingScenarioId!!
                } else {
                    // 새로 생성
                    scenarioRepository.createScenario(
                        title = title,
                        description = state.saveDialogDescription,
                        timelineItems = state.timelineItems
                    )
                }

                state = state.copy(
                    isSaving = false,
                    showSaveDialog = false,
                    showSaveSuccess = true
                )

                // 성공 콜백 호출
                onSuccess(scenarioId)
            } catch (e: Exception) {
                state = state.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "저장 중 오류가 발생했습니다"
                )
            }
        }
    }

    /**
     * 편집 모드로 시나리오 로드
     */
    fun loadScenarioForEdit(scenarioId: String) {
        viewModelScope.launch {
            try {
                val scenarioWithTimeline = scenarioRepository.getScenarioWithTimeline(scenarioId)
                if (scenarioWithTimeline != null) {
                    // 타임라인 아이템을 TimelineModuleItem으로 변환
                    val timelineItems =
                        scenarioWithTimeline.timelineItems.mapNotNull { timelineItem ->
                            val moduleItem =
                                moduleRepository.getModuleItemById(timelineItem.moduleItemId)
                            if (moduleItem != null) {
                                TimelineModuleItem(
                                    id = timelineItem.id,
                                    moduleItem = moduleItem,
                                    order = timelineItem.order
                                )
                            } else null
                        }

                    state = state.copy(
                        editingScenarioId = scenarioId,
                        timelineItems = timelineItems,
                        saveDialogTitle = scenarioWithTimeline.scenario.title,
                        saveDialogDescription = scenarioWithTimeline.scenario.description
                    )
                }
            } catch (e: Exception) {
                state = state.copy(errorMessage = "시나리오 로드 중 오류가 발생했습니다")
            }
        }
    }

    /**
     * 에러 메시지 초기화
     */
    fun clearError() {
        state = state.copy(errorMessage = null)
    }

    /**
     * 저장 성공 메시지 닫기
     */
    fun dismissSaveSuccess() {
        state = state.copy(showSaveSuccess = false)
    }
}

/**
 * 시나리오 생성 화면 상태
 */
data class ScenarioCreatorState(
    val selectedTypeId: String? = null,
    val selectedCategoryId: String? = null,
    val timelineItems: List<TimelineModuleItem> = emptyList(),
    val showUnlockDialog: Boolean = false,
    val selectedModuleForUnlock: ModuleItemEntity? = null,
    val showSaveDialog: Boolean = false,
    val saveDialogTitle: String = "",
    val saveDialogDescription: String = "",
    val isSaving: Boolean = false,
    val showSaveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val editingScenarioId: String? = null  // 편집 중인 시나리오 ID (null이면 새로 생성)
)

/**
 * 타임라인에 배치된 모듈 아이템
 */
data class TimelineModuleItem(
    val id: String,
    val moduleItem: ModuleItemEntity,
    val order: Int
)
