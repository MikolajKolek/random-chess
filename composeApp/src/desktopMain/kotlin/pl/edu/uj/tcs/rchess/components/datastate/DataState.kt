package pl.edu.uj.tcs.rchess.components.datastate

sealed class DataState<T>() {
    class Success<T>(val data: T) : DataState<T>()
    class Error<T>(val error: Throwable) : DataState<T>()
    class Loading<T>() : DataState<T>()
}
