package apps.cooper.pokedex.models

data class PokemonInfo(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<Result>
)