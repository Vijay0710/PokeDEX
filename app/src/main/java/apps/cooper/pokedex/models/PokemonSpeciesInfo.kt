package apps.cooper.pokedex.models

data class PokemonSpeciesInfo(
    val base_happiness: Int,
    val capture_rate: Int,
    val color: Color,
    val egg_groups: List<EggGroup>,
    val evolution_chain: EvolutionChain,
    val evolves_from_species: EvolvesFromSpecies,
    val flavor_text_entries: List<FlavorTextEntry>,
    val form_descriptions: List<Any>,
    val forms_switchable: Boolean,
    val gender_rate: Int,
    val genera: List<Genera>,
    val generation: Generation,
    val growth_rate: GrowthRate,
    val habitat: Habitat,
    val has_gender_differences: Boolean,
    val hatch_counter: Int,
    val id: Int,
    val is_baby: Boolean,
    val is_legendary: Boolean,
    val is_mythical: Boolean,
    val name: String,
    val names: List<Name>,
    val order: Int,
    val pal_park_encounters: List<PalParkEncounter>,
    val pokedex_numbers: List<PokedexNumber>,
    val shape: Shape,
    val varieties: List<Variety>
) {
    companion object {

        operator fun invoke(
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
        ) = PokemonSpeciesInfo(
            base_happiness = base_happiness ?: 0,
            capture_rate = capture_rate ?: 0,
            color = color ?: Color("", ""),
            egg_groups = egg_groups ?: listOf(EggGroup("", "")),
            evolution_chain = evolution_chain ?: EvolutionChain(""),
            evolves_from_species = evolves_from_species ?: EvolvesFromSpecies("", ""),
            flavor_text_entries = flavor_text_entries ?: listOf(
                FlavorTextEntry(
                    "",
                    Language("", ""),
                    VersionX("", "")
                )
            ),
            form_descriptions = form_descriptions ?: listOf(),
            forms_switchable = forms_switchable ?: false,
            gender_rate = gender_rate ?: 0,
            genera = genera ?: listOf(Genera("", Language("", ""))),
            generation = generation ?: Generation("", ""),
            growth_rate = growth_rate ?: GrowthRate("", ""),
            habitat = habitat ?: Habitat("", ""),
            has_gender_differences = has_gender_differences ?: false,
            hatch_counter = hatch_counter ?: 0,
            id = id ?: 0,
            is_baby = is_baby ?: false,
            is_legendary = is_legendary ?: false,
            is_mythical = is_mythical ?: false,
            name = name ?: "",
            names = names ?: listOf(Name(Language("", ""), "")),
            order = order ?: 0,
            pal_park_encounters = pal_park_encounters ?: listOf(
                PalParkEncounter(
                    Area("", ""),
                    0,
                    0
                )
            ),
            pokedex_numbers = pokedex_numbers ?: listOf(PokedexNumber(0, Pokedex("", ""))),
            shape = shape ?: Shape("", ""),
            varieties = varieties ?: listOf(Variety(false, PokemonX("", "")))


        )
    }
}