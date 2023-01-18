package apps.cooper.pokedex.models

import retrofit2.Call
import retrofit2.http.GET

interface Pokemon {

    val pokemonViewModel:PokemonViewModel
    @GET("pokemon?limit=10000&offset=0")
    fun getPokemons(): Call<PokemonInfo>

    @GET(" https://pokeapi.co/api/v2/pokemon/")
    fun getPokemonInfo():Call<PokemonInfo>

}