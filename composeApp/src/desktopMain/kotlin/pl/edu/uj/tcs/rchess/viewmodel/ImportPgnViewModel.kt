package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImportPgnViewModel(private val context: AppContext): ViewModel() {
    val pgnInput = mutableStateOf("")

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean
        get() = _isLoading.value

    val errors = DismissibleErrorsState()

    suspend fun submitAnd(
        onSuccess: () -> Unit,
    ) {
        if (_isLoading.value) return
        try {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                context.clientApi.addPgnGames(pgnInput.value)
            }
            onSuccess()
            pgnInput.value = ""
        } catch (error: Exception) {
            errors.submitError("Failed to import games", error)
        } finally {
            _isLoading.value = false
        }
    }
}
