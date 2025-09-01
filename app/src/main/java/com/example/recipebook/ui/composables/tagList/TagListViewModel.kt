@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.recipebook.ui.composables.tagList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TagListViewModel(tagRepository: TagRepository) : ViewModel() {
    private val _filterState = MutableStateFlow(TagListFilterState())
    val filterState = _filterState.asStateFlow()

    val tagListUiState: StateFlow<TagListUiState> =
        _filterState
            .flatMapLatest { filter ->
                tagRepository.getTagFiltered(filter.filterNameOrNull)
            }
            .map { TagListUiState(tagList = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TagListUiState()
            )

    var isTagEditPopupOpen by mutableStateOf(false)
        private set

    fun openTagEditPopup() {
        isTagEditPopupOpen = true
    }

    fun closeTagEditPopup() {
        isTagEditPopupOpen = false
    }

    //endregion

    //region Filters

    fun updateFilterName(newFilterName: String) {
        _filterState.value = _filterState.value.copy(
            filterName = newFilterName
        )
    }

    //endregion

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class TagListFilterState(val filterName: String = "") {
    val filterNameOrNull = filterName.ifBlank { null }
}

data class TagListUiState(val tagList: List<Tag> = listOf())