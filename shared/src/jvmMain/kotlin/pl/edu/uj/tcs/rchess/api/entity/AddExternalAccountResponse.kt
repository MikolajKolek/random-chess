package pl.edu.uj.tcs.rchess.api.entity

import kotlinx.coroutines.Deferred

data class AddExternalAccountResponse (
    val oauthURL: String,
    val completionCallback: Deferred<Unit>
)