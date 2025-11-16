package com.example.groceyapp.ui.viewmodel.support

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Small utility that keeps a map of collection ids -> item counts.
 * Shopping lists, pantries, or any other \"collection of items\" can share
 * this tracker instead of duplicating the same counting boilerplate.
 */
class CollectionCountTracker(
    private val scope: CoroutineScope
) {
    private val _counts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val counts: StateFlow<Map<Int, Int>> = _counts.asStateFlow()

    /**
     * Warm up counts for the provided ids. The [fetchCount] lambda is only
     * responsible for returning the latest count for a given id.
     */
    fun warmUp(ids: List<Int>, fetchCount: suspend (Int) -> Int) {
        scope.launch {
            val previous = _counts.value.toMutableMap()
            val updated = mutableMapOf<Int, Int>()
            ids.forEach { id ->
                val count = runCatching { fetchCount(id) }.getOrElse { previous[id] ?: 0 }
                updated[id] = count
            }
            _counts.value = updated
        }
    }

    /**
     * Refresh the count for a single id after a mutation (add/remove/etc).
     */
    fun refresh(id: Int, fetchCount: suspend () -> Int) {
        scope.launch {
            val previous = _counts.value[id] ?: 0
            val count = runCatching { fetchCount() }.getOrElse { previous }
            _counts.value = _counts.value.toMutableMap().apply { this[id] = count }
        }
    }

    /**
     * Manually set a count when it is already known.
     */
    fun set(id: Int, count: Int) {
        _counts.value = _counts.value.toMutableMap().apply { this[id] = count }
    }
}
