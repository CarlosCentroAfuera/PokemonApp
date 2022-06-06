package com.example.pokemonapp

data class Usuario(var nombre: String, var pass: String) {
    val token = nombre + pass
    var pokemonFavoritoId : Int? = null

    var pokemonCapturados = mutableListOf<Int>()
}