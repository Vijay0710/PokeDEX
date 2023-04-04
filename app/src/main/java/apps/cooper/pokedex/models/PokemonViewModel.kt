package apps.cooper.pokedex.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PokemonViewModel : ViewModel() {
    private val _items: MutableLiveData<List<Result>> = MutableLiveData(listOf())
    private val _pokemonInfo: MutableLiveData<List<PokemonDetails>> = MutableLiveData(listOf())
    private val _pokemonSpeciesInfo: MutableLiveData<List<PokemonSpeciesInfo>> =
        MutableLiveData(listOf())

    //    private val _url : MutableLiveData<String> = MutableLiveData("")
    var items: LiveData<List<Result>> = _items
    var pokemonInfo: LiveData<List<PokemonDetails>> = _pokemonInfo
    var pokemonSpeciesInfo: LiveData<List<PokemonSpeciesInfo>> = _pokemonSpeciesInfo
    val currentCardClickState: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    fun updatePokemonDetails(
        abilities: List<Ability>,
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
        weight: Int
    ) {
        val pokeDetails = PokemonDetails(
            abilities,
            base_experience,
            forms,
            game_indices,
            height,
            held_items,
            id,
            is_default,
            location_area_encounters,
            moves,
            name,
            order,
            past_types,
            species,
            sprites,
            stats,
            types,
            weight
        )
        _pokemonInfo.value = listOf(pokeDetails)
    }

    fun addPokemonDetails(name: String, url: String) {
        val pokemon = Result(name = name, url = url)
        _items.value = _items.value?.plus(pokemon) ?: listOf(pokemon)
    }

    fun updatePokemonSpeciesDetails(
        base_happiness: Int?,
        capture_rate: Int?,
        color: Color?,
        egg_groups: List<EggGroup>?,
        evolution_chain: EvolutionChain?,
        evolves_from_species: EvolvesFromSpecies?,
        flavor_text_entries: List<FlavorTextEntry>?,
        form_descriptions: List<Any>?,
        forms_switchable: Boolean?,
        gender_rate: Int?,
        genera: List<Genera>?,
        generation: Generation?,
        growth_rate: GrowthRate?,
        habitat: Habitat?,
        has_gender_differences: Boolean?,
        hatch_counter: Int?,
        id: Int?,
        is_baby: Boolean?,
        is_legendary: Boolean?,
        is_mythical: Boolean?,
        name: String?,
        names: List<Name>?,
        order: Int?,
        pal_park_encounters: List<PalParkEncounter>?,
        pokedex_numbers: List<PokedexNumber>?,
        shape: Shape?,
        varieties: List<Variety>?
    ) {
//        if(evolves_from_species == null){
//            val evolves_from_species = EvolvesFromSpecies("","")
//        }

        val pokeSpeciesDetails = PokemonSpeciesInfo(
            base_happiness,
            capture_rate,
            color,
            egg_groups,
            evolution_chain,
            evolves_from_species ,
            flavor_text_entries,
            form_descriptions,
            forms_switchable,
            gender_rate,
            genera,
            generation,
            growth_rate,
            habitat,
            has_gender_differences,
            hatch_counter,
            id,
            is_baby,
            is_legendary,
            is_mythical,
            name,
            names,
            order,
            pal_park_encounters,
            pokedex_numbers,
            shape,
            varieties
        )
        _pokemonSpeciesInfo.value = listOf(pokeSpeciesDetails)
    }
}