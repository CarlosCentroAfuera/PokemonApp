package com.example.pokemonapp

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemonapp.databinding.ActivitySeleccionBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface PokemonFavoritoSelected {
    fun pokemonFavoritoSelected(pokemon: Pokemon)
}

class MainActivity : AppCompatActivity(), PokemonFavoritoSelected {

    private lateinit var binding: ActivitySeleccionBinding
    private lateinit var listaPokemon: ListaPokemon
    private var userToken = ""

    private val tagListaPokemon = "TAG_LISTA_POKEMON"
    private val tagUserToken = "TAG_USER_TOKEN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ejemplo de Enum
        val usuario = Usuario("test1", "pass1")
        println(usuario.tipoUsuario)
        usuario.subirNivel()
        println(usuario.tipoUsuario)
        usuario.subirNivel()
        println(usuario.tipoUsuario)
        usuario.subirNivel()
        println(usuario.tipoUsuario)
        when (usuario.tipoUsuario){
            TipoUsuario.PLATINO -> {

            }
            TipoUsuario.ORO -> {

            }
            TipoUsuario.PLATA -> {

            }
        }



        binding.rvPokemon.layoutManager = LinearLayoutManager(this)
        binding.rvPokemon.adapter = AdapterPokemon(this)

        readFromPreferences()

        if (userToken.isEmpty()) {
            loguearUsuario()
        }

        actualizarAdapter(listaPokemon)

        initBotonDescarga()
    }

    private fun initBotonDescarga() {
        binding.bDescarga.contentDescription = if (listaPokemon.listaPokemon.isNullOrEmpty()) {
            getString(R.string.descargar_pokemons)
        } else {
            getString(R.string.recargar_pokemons)
        }

        if (listaPokemon.listaPokemon.isNullOrEmpty()) {
            binding.bDescarga.setImageResource(R.mipmap.ic_descarga)
        } else {
            binding.bDescarga.setImageResource(R.mipmap.ic_recarga)
        }


        binding.bDescarga.setOnClickListener {
            loadingVisible(true)
            lifecycleScope.launch(Dispatchers.IO) {
                listaPokemon = ObtenerPokemonRequest.get()
                withContext(Dispatchers.Main) {
                    actualizarAdapter(listaPokemon)
                    initBotonDescarga()
                    loadingVisible(false)
                }
                writeInPreferences()
            }
        }
    }

    private fun loadingVisible(visible : Boolean) {
        binding.pbLoading.visibility = if (visible) View.VISIBLE else View.GONE
        binding.bDescarga.visibility = if (!visible) View.VISIBLE else View.GONE

    }

    private fun actualizarAdapter(listaPokemon : ListaPokemon){
        (binding.rvPokemon.adapter as AdapterPokemon).actualizarLista(listaPokemon)
    }

    private fun writeInPreferences() {
         getPreferences(Context.MODE_PRIVATE).edit().apply {
            putString(tagListaPokemon, this@MainActivity.listaPokemon.toJson())
            apply()
        }
    }

    private fun readFromPreferences() {
        val pokemonsText = getPreferences(Context.MODE_PRIVATE).getString(tagListaPokemon, "")
        listaPokemon = if (pokemonsText.isNullOrBlank()){
            ListaPokemon()
        } else {
            ListaPokemon.fromJson(pokemonsText)
        }
    }

    private fun loguearUsuario(){
        lifecycleScope.launch(Dispatchers.Main) {
            userToken = LoguearUsuarioRequest.get()
            println(userToken)
        }
    }

    override fun pokemonFavoritoSelected(pokemon: Pokemon) {
        lifecycleScope.launch(Dispatchers.Main) {
            val success = PokemonFavoritoRequest.get(pokemon.id, userToken)
            if (success){
                Snackbar.make(binding.root, "El Pokemon favorito es: ${pokemon.name}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

}