package pl.edu.uj.tcs.rchess

import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.server.Server

fun provideApi(): ClientApi = Server()