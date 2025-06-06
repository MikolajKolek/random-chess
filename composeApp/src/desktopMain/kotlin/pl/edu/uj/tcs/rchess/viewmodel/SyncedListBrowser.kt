package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.*
import pl.edu.uj.tcs.rchess.util.withPrev

interface SyncedListBrowser<T> {
    val current: T
    val index: Int
    val firstSelected: Boolean
    val lastSelected: Boolean

    fun select(value: Int)
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

    LaunchedEffect(listState) {
        snapshotFlow { listState.value }
            .withPrev()
            .collect { (prev, current) ->
                require(current.isNotEmpty()) { "The list must not be empty" }
                index = index.coerceIn(0, current.lastIndex)
                if (prev != null && current.lastIndex >= prev.lastIndex && index == prev.lastIndex) {
                    // The list size just increased, and previously the last element was selected
                    index = current.lastIndex
                }
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

        override fun selectPrev() = select(index - 1)

        override fun selectNext() = select(index + 1)

        override fun selectFirst() = select(0)

        override fun selectLast() = select(listState.value.lastIndex)
    }
}
