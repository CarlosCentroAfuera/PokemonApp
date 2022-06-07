package com.example.pokemonapp

import kotlin.random.Random

data class Usuario(var nombre: String, var pass: String) {
    val token = nombre + pass
    var pokemonFavoritoId : Int? = null

    var pokemonCapturados = mutableListOf<Int>()

    var tipoUsuario = TipoUsuario.PLATA

    fun subirNivel() {
        when(tipoUsuario){
            TipoUsuario.PLATA -> tipoUsuario = TipoUsuario.ORO
            TipoUsuario.ORO -> tipoUsuario = TipoUsuario.PLATINO
            TipoUsuario.PLATINO -> println("No es posible subir de nivel")
        }
    }
}

enum class TipoUsuario {
    PLATA,
    ORO,
    PLATINO
}