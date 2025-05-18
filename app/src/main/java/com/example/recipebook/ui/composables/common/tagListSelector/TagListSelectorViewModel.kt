@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.recipebook.ui.composables.common.tagListSelector

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.tag.TagRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TagListSelectorViewModel(
    private val tagRepository: TagRepository
) : ViewModel() {
    private val _tagListFilterState = MutableStateFlow(TagListSelectorFilterUiState())
    val tagListFilterState = _tagListFilterState.asStateFlow()

    var tagListUiState: StateFlow<TagListSelectorUiState> =
        _tagListFilterState
            .flatMapLatest { filter ->
                tagRepository.getTagFiltered(filter.filterNameOrNull)
            }
            .map { TagListSelectorUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TagListSelectorUiState()
            )

    var isTagListPopupOpen by mutableStateOf(false)
        private set

    fun openTagListPopup() {
        isTagListPopupOpen = true
    }

    fun closeTagListPopup() {
        isTagListPopupOpen = false
    }

    fun updateFilterName(newFilterName: String) {
        _tagListFilterState.value = _tagListFilterState.value.copy(
            filterName = newFilterName
        )
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}