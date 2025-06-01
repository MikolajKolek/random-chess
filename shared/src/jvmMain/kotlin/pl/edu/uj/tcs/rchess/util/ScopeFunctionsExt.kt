package pl.edu.uj.tcs.rchess.util

inline fun <T> T.runIf(condition: Boolean, block: T.() -> T) =
    if (condition) block() else this
