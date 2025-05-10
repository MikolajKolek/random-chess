package pl.edu.uj.tcs.rchess.model.statemachine

interface Change<T> {
    fun applyTo(state: T): T
}
