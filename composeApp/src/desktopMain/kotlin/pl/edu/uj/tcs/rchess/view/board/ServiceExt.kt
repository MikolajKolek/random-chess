package pl.edu.uj.tcs.rchess.view.board

import pl.edu.uj.tcs.rchess.api.entity.Service
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.logo_lichess
import rchess.composeapp.generated.resources.logo_rchess

val Service.icon
    get() = this.let { service ->
        Res.drawable.run {
            when (service) {
                Service.RANDOM_CHESS -> logo_rchess
                Service.LICHESS -> logo_lichess
                Service.UNKNOWN -> null
            }
        }
    }
