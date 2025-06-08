package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.mutableStateListOf

class DismissibleErrorsState {
    private var _errors = mutableStateListOf<DismissibleError>()

    val topError: DismissibleError?
        get() = _errors.lastOrNull()

    fun dismissTopError() {
        _errors.removeLastOrNull()
    }

    fun submitError(error: DismissibleError) {
        _errors.addLast(error)
    }

    fun submitError(header: String, exception: Exception) {
        submitError(DismissibleError(header, exception))
    }

    data class DismissibleError(
        val header: String,
        val exception: Exception,
    )
}