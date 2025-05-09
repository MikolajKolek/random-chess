package pl.edu.uj.tcs.rchess.server

//TODO: czy powinno to też zawierać informację o tym, czy jest to konto twoje, czy innego użytkownika?
// brzmi to głupio, ale ma sens: nie chcemy przekazywać klientowi zawsze user_id (for security reasons)
// ale może on chcieć łatwiej wiedzieć, czy konto jest jego czy nie
data class ServiceAccount(
    val service: Service,
    val userIdInService: String,
    val displayName: String,
    val isBot: Boolean
) {

}