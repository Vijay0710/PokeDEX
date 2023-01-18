package apps.cooper.pokedex.models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Pokemon {

    val pokemonViewModel:PokemonViewModel
    @GET("pokemon?limit=10000&offset=0")
    fun getPokemons(): Call<PokemonInfo>

    @GET("{no}")
    fun getPokemonInfo(@Path("no") no : String):Call<PokemonDetails>

}
