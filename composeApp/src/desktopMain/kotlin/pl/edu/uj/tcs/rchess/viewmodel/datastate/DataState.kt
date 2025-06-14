package pl.edu.uj.tcs.rchess.viewmodel.datastate

/**
 * A sealed interface representing the state of data loading operations.
 *
 * Used by [DataStateViewModel].
 */
sealed interface DataState<T> {
    class Success<T>(val data: T) : DataState<T>
    class Error<T>(val error: Throwable) : DataState<T>
    class Loading<T> : DataState<T>
}
