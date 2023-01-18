package apps.cooper.pokedex.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PokemonViewModel : ViewModel() {
    private val _items : MutableLiveData<List<Result>> = MutableLiveData(listOf())
    private val _pokemonInfo : MutableLiveData<List<PokemonDetails>> = MutableLiveData(listOf())
//    private val _url : MutableLiveData<String> = MutableLiveData("")
    val items : LiveData<List<Result>> = _items
    val pokemonInfo : LiveData<List<PokemonDetails>> = _pokemonInfo

    fun updatePokemonDetails(abilities: List<Ability>,
                             base_experience: Int,
                             forms: List<Form>,
                             game_indices: List<GameIndice>,
                             height: Int,
                             held_items: List<Any>,
                             id: Int,
                             is_default: Boolean,
                             location_area_encounters: String,
                             moves: List<Move>,
                             name: String,
                             order: Int,
                             past_types: List<Any>,
                             species: Species,
                             sprites: Sprites,
                             stats: List<Stat>,
                             types: List<Type>,
                             weight: Int){
        val pokeDetails = PokemonDetails(abilities, base_experience, forms, game_indices, height, held_items, id, is_default, location_area_encounters, moves, name, order, past_types, species, sprites, stats, types, weight)
        _pokemonInfo.value = listOf(pokeDetails)
    }
    fun addPokemonDetails(name:String,url:String){
        val pokemon = Result(name = name,url=url)
        _items.value =_items.value?.plus(pokemon)?: listOf(pokemon)
    }
}