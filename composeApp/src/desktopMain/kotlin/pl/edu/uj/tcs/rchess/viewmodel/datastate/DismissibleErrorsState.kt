package pl.edu.uj.tcs.rchess.viewmodel.datastate

import androidx.compose.runtime.mutableStateListOf

/**
 * A state holder for a list of errors that need to be shown to the user.
 *
 * Can be used with [pl.edu.uj.tcs.rchess.view.adapters.DismissibleErrorsAdapter]
 */
class DismissibleErrorsState {
    private var _errors = mutableStateListOf<DismissibleError>()

    /**
     * A getter for the most recent error.
     */
    val topError: DismissibleError?
        get() = _errors.lastOrNull()

    /**
     * Removes the most recent error from the list of errors.
     */
    fun dismissTopError() {
        _errors.removeLastOrNull()
    }

    /**
     * Adds an error to the list of errors.
     *
     * @param error A [DismissibleError] instance for the error.
     */
    fun submitError(error: DismissibleError) {
        _errors.addLast(error)
    }

    /**
     * Adds an error to the list of errors.
     *
     * @param header A header text for the error, which will be displayed in the UI.
     * @param exception The exception that caused the error.
     */
    fun submitError(header: String, exception: Exception) {
        submitError(DismissibleError(header, exception))
    }

    data class DismissibleError(
        /**
         * A header text for the error, which will be displayed in the UI.
         */
        val header: String,

        /**
         * The exception that caused the error.
         */
        val exception: Exception,
    )
}
