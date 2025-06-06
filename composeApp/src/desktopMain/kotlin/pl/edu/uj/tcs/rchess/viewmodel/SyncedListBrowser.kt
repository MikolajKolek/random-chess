package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.*

interface SyncedListBrowser<T> {
    val current: T
    val index: Int
    val firstSelected: Boolean
    val lastSelected: Boolean

    fun select(value: Int)
    fun selectDelta(delta: Int)
    fun selectPrev()
    fun selectNext()
    fun selectFirst()
    fun selectLast()
}

/**
 * For a non-empty list returns a [SyncedListBrowser] pointing by default to the last element.
 * The index is kept in the correct range.
 * If the list length increases and the last element was selected,
 * the index will update to still point to the last element.
 */
@Composable
fun <T> rememberListBrowser(listState: State<List<T>>): SyncedListBrowser<T> {
    var index by remember { mutableStateOf(listState.value.lastIndex) }
    var prevLastIndex by remember { mutableStateOf(listState.value.lastIndex) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.value }
            .collect { list ->
                require(list.isNotEmpty()) { "The list must not be empty" }
                index = index.coerceIn(0, list.lastIndex)
                if (index == prevLastIndex && list.lastIndex > prevLastIndex) {
                    // The list size just increased, and previously the last element was selected
                    index = list.lastIndex
                }
                prevLastIndex = list.lastIndex
            }
    }

    return object : SyncedListBrowser<T> {
        override val current
            get() = listState.value[index.coerceIn(0, listState.value.lastIndex)]

        override val index
            get() = index

        override val firstSelected
            get() = index == 0

        override val lastSelected
            get() = index == listState.value.lastIndex

        override fun select(value: Int) {
            index = value.coerceIn(0, listState.value.lastIndex)
        }

        override fun selectDelta(delta: Int) = select(index + delta)

        override fun selectPrev() = selectDelta(-1)

        override fun selectNext() = selectDelta(+1)

        override fun selectFirst() = select(0)

        override fun selectLast() = select(listState.value.lastIndex)
    }
}
