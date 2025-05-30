package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.*

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
fun <T> rememberListBrowser(list: List<T>): SyncedListBrowser<T> {
    require(list.isNotEmpty()) { "The list must not be empty" }

    var prevLastIndex by remember { mutableStateOf(list.lastIndex) }
    var index by remember { mutableStateOf(list.lastIndex) }

    if (index < 0) index = 0
    // The list size just increased, and previously the last element was selected
    if (list.lastIndex > prevLastIndex && index == prevLastIndex) index = list.lastIndex
    if (index > list.lastIndex) index = list.lastIndex

    prevLastIndex = list.lastIndex

    return object : SyncedListBrowser<T> {
        override val current = list[index]

        override val index = index

        override val firstSelected = index == 0

        override val lastSelected = index == list.lastIndex

        override fun select(value: Int) {
            index = value.coerceIn(0, list.lastIndex)
        }

        override fun selectPrev() = select(index - 1)

        override fun selectNext() = select(index + 1)

        override fun selectFirst() = select(0)

        override fun selectLast() = select(list.lastIndex)
    }
}
