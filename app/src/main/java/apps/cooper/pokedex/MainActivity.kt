package apps.cooper.pokedex

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.TextField
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import apps.cooper.pokedex.ui.theme.PokeDEXTheme
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import apps.cooper.pokedex.models.*
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import java.lang.Math.floor
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import kotlin.Comparator

private lateinit var sortedList: List<Result>

// Need to work on abilities screen
private val interFontFamily = FontFamily(
    Font(R.font.inter, FontWeight.Light)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pokemonViewModel: PokemonViewModel by viewModels()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                if (pokemonViewModel.items.value?.isEmpty() == true) {
                    Log.i("IF CONDITION", "FETCHING...")
                    fetchPokemonNames(pokemonViewModel)

                } else {
                    Log.i("LOAD FROM CACHE: ", pokemonViewModel.items.value.toString())
                }
            }
        }

        setContent {
            PokeDEXTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.i("POKEMON: ", pokemonViewModel.items.value.toString())
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "splash") {
                        composable(route = "splash") {
                            SplashActivity(pokemonViewModel, navController)
                        }
                        composable(route = "info/{id}") { navBackStack ->
                            val pokemonID = navBackStack.arguments?.getString("id")
                            PokemonInformationScreen(
                                modifier = Modifier.fillMaxSize(),
                                pokemonViewModel,
                                pokemonID!!,
                                navController
                            )
                        }
                        composable(route = "greet") {
                            PokemonGreeting(pokemonViewModel = pokemonViewModel, navController)
                        }
                    }

                }
            }
        }
    }

}


private fun fetchPokemonNames(pokemonViewModel: PokemonViewModel) {
    val rBuilder = retrofitBuilder("https://pokeapi.co/api/v2/")
    val rData = rBuilder.getPokemons()
    rData.enqueue(object : Callback<PokemonInfo> {

        override fun onResponse(call: Call<PokemonInfo>, response: Response<PokemonInfo>) {
            val data = response.body()!!
            println("RESULTS")
            Log.i("DATA: ", data.toString())
            for (pokemon in data.results) {
                pokemonViewModel.addPokemonDetails(pokemon.name, pokemon.url)
            }
            Log.i("VIEWMODEL: ", pokemonViewModel.items.value.toString())
            Log.i("VIEWMODEL SIZE: ", pokemonViewModel.items.value?.size.toString())
        }

        override fun onFailure(call: Call<PokemonInfo>, t: Throwable) {
            Log.i("ERR in fetchPoke: ", t.message.toString())
        }
    })
}

private fun retrofitBuilder(url: String): Pokemon {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(url)
        .build()
        .create(Pokemon::class.java)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoScreen() {
    var state by remember { mutableStateOf(1) }
    val titles = listOf("About", "Stats", "Abilities", "Evolution", "Location")
    val progress by remember { mutableStateOf(0.6f) }



    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        //This code part contains Pokemon Image Name and Attribute
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Image may not load in JIO net should change private dns to one.one.one.one in android phones
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/25.png")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(300.dp),
                alignment = Alignment.Center,
            )
            Text("Pikachu", fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold)

            Row() {
                AssistChip(
                    onClick = { /* Do something! */ },
                    label = { Text("Lightning") },
                    Modifier
                        .padding(2.dp),
                    shape = RoundedCornerShape(50),
                )
            }
            //End of Pokemon Image Name and Attribute

            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = state,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state])
                                .height(4.dp) // clip modifier not working
                                .padding(horizontal = 12.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    },
                    divider = {}) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }


                if (state == 0) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(12.dp),
                        text = "It keeps its tail raised to monitor its surroundings. If you yank its tail, it will try to bite you.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold,
                    )
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(12.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column() {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth(0.5f)
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_weight),
                                        contentDescription = "Weight",
                                        Modifier.size(24.dp)
                                    )
                                    Text(
                                        "6.9 kg (15.2 lbs)",
                                        fontFamily = interFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                    )
                                }
                                Text(
                                    "Weight",
                                    fontFamily = interFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth(
                                        0.5f
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Divider(
                                color = Color.LightGray,
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .fillMaxHeight()
                                    .width(1.dp),


                                )

                            Column() {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_height),
                                        contentDescription = "Height",
                                        Modifier.size(24.dp)
                                    )
                                    Text(
                                        "0.7 m (2' 04'')",
                                        fontFamily = interFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                                Text(
                                    "Height",
                                    fontFamily = interFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth(
                                        1f
                                    ),
                                    textAlign = TextAlign.Center
                                )


                            }

                        }
                    }
                }
                if (state == 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(9.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
//                        The linear progress indicator will be worked upon since it doesn't have documentation on how to have rounded corners in progress indicators
//                        LinearProgressIndicator(
//                            modifier = Modifier
//                                .padding(12.dp)
//                                .height(10.dp)
//                                .clip(
//                                    RoundedCornerShape(12.dp)
//                                ),
//                            progress = animatedProgress,
//                        )
//                      The below examples describes the use of custom linear indicator
                        val dict = mapOf("attack" to "Atk", "defense" to "Def",
                            "special-attack" to "SAtk", "special-defense" to "SDef" ,"defense" to "Def" ,"speed" to "Spd","hp" to "HP"
                        )
                        for (i in 1..6) {
//                            val floatValue =
//                                100/10
//                            Column(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.SpaceEvenly
//                            ) {
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .horizontalScroll(
//                                            rememberScrollState()
//                                        ),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceAround,
//                                ) {
//                                    Text(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(5.dp)
//                                            .align(Alignment.CenterVertically),
//                                        text =
//                                        dict[pokemonViewModel.pokemonInfo.value!![0].stats[i].stat.name]!!
//                                        ,
//                                        fontFamily = interFontFamily,
//                                        textAlign = TextAlign.Start,
//                                        fontWeight = FontWeight.SemiBold,
//                                        fontSize = 12.sp,
//                                        overflow = TextOverflow.Ellipsis
//                                    )
//                                    Text(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(7.dp)
//                                            .align(Alignment.CenterVertically),
//                                        text = pokemonViewModel.pokemonInfo.value!![0].stats[i].base_stat.toString(),
//                                        fontFamily = interFontFamily,
//                                        fontWeight = FontWeight.Bold,
//                                        textAlign = TextAlign.Start,
//                                        overflow = TextOverflow.Ellipsis
//                                    )
//                                    Box(
//                                        modifier = Modifier
//
//                                            .align(Alignment.CenterVertically)
//                                            .clip(RoundedCornerShape(15.dp))
//                                            .height(10.dp)
//                                            .background(ProgressIndicatorDefaults.linearTrackColor)
//                                            .width(240.dp)
//                                    ) {
//                                        Log.i("FLOAT VALUE", floatValue.toString())
//                                        Box(
//                                            modifier = Modifier
//                                                .clip(RoundedCornerShape(15.dp))
//                                                .height(10.dp)
//                                                .background(
//                                                    ProgressIndicatorDefaults.linearColor
//                                                )
//                                                .width(240.dp * (floatValue / 10))
//                                        )
//                                    }
//                                }
//                            }

                        }
                    }
                }
                if (state == 2) {
                    CardContentAbilities(name = "", pokeURL = "")
                }

            }
        }
    }
}

@Composable
fun CardContentAbilities(name: String, pokeURL: String) {

}

private fun fetchPokemonSpeciesInformation(
    pokemonViewModel: PokemonViewModel,
    no: String,
    navController: NavController,
) {
    val rBuilderPokemonSpecies = retrofitBuilder("https://pokeapi.co/api/v2/pokemon-species/")
    val rPokeSpeciesData = rBuilderPokemonSpecies.getPokemonSpeciesInfo(no)
    rPokeSpeciesData.enqueue(object : Callback<PokemonSpeciesInfo> {
        override fun onResponse(
            call: Call<PokemonSpeciesInfo>,
            response: Response<PokemonSpeciesInfo>
        ) {
            Log.i("POKE SPECIES RESPONSE: ", response.toString())
            println("RESULTS")
            if (response.code() != 404) {
                val pokeSpeciesData = response.body()!!
                pokemonViewModel.updatePokemonSpeciesDetails(
                    pokeSpeciesData.base_happiness,
                    pokeSpeciesData.capture_rate,
                    pokeSpeciesData.color,
                    pokeSpeciesData.egg_groups,
                    pokeSpeciesData.evolution_chain,
                    pokeSpeciesData.evolves_from_species,
                    pokeSpeciesData.flavor_text_entries,
                    pokeSpeciesData.form_descriptions,
                    pokeSpeciesData.forms_switchable,
                    pokeSpeciesData.gender_rate,
                    pokeSpeciesData.genera,
                    pokeSpeciesData.generation,
                    pokeSpeciesData.growth_rate,
                    pokeSpeciesData.habitat,
                    pokeSpeciesData.has_gender_differences,
                    pokeSpeciesData.hatch_counter,
                    pokeSpeciesData.id,
                    pokeSpeciesData.is_baby,
                    pokeSpeciesData.is_legendary,
                    pokeSpeciesData.is_mythical,
                    pokeSpeciesData.name,
                    pokeSpeciesData.names,
                    pokeSpeciesData.order,
                    pokeSpeciesData.pal_park_encounters,
                    pokeSpeciesData.pokedex_numbers,
                    pokeSpeciesData.shape,
                    pokeSpeciesData.varieties
                )
            } else {
                pokemonViewModel.updatePokemonSpeciesDetails(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            }

            Log.i(
                "POKE SPECIES INFO: ",
                pokemonViewModel.pokemonSpeciesInfo.value!![0].names.toString()
            )
            navController.navigate("info/$no")
        }

        override fun onFailure(call: Call<PokemonSpeciesInfo>, t: Throwable) {
            Log.i("ERR in PokeSpecies: ", t.message.toString())
        }
    })
}

private fun fetchPokemonInformation(
    pokemonViewModel: PokemonViewModel,
    no: String,
    navController: NavController,
) {
    val rBuilder = retrofitBuilder("https://pokeapi.co/api/v2/pokemon/")
    val rData = rBuilder.getPokemonInfo(no)
    rData.enqueue(object : Callback<PokemonDetails> {
        override fun onResponse(call: Call<PokemonDetails>, response: Response<PokemonDetails>) {
            val data = response.body()!!
            println("RESULTS")
            Log.i("INFO DATA: ", data.name)
            Log.i("POKEMON NAME: ", data.name)

            pokemonViewModel.updatePokemonDetails(
                data.abilities,
                data.base_experience,
                data.forms,
                data.game_indices,
                data.height,
                data.held_items,
                data.id,
                data.is_default,
                data.location_area_encounters,
                data.moves,
                data.name,
                data.order,
                data.past_types,
                data.species,
                data.sprites,
                data.stats,
                data.types,
                data.weight
            )
            Log.i(
                "POKE BASE EXP INFO: ",
                pokemonViewModel.pokemonInfo.value?.get(0)?.base_experience.toString()
            )
            fetchPokemonSpeciesInformation(pokemonViewModel, no, navController)
        }

        override fun onFailure(call: Call<PokemonDetails>, t: Throwable) {
            Log.i("ERR in PokeInfo: ", t.message.toString())
        }
    })

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonInformationScreen(
    modifier: Modifier,
    pokemonViewModel: PokemonViewModel,
    pokemonID: String,
    navController: NavController
) {
    var state by remember { mutableStateOf(0) }
    val titles = listOf("About", "Stats", "Abilities", "Evolution", "Location")
    val progress by remember { mutableStateOf(0.6f) }


    Log.i("Pokemon ID: ", pokemonID)
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        //This code part contains Pokemon Image Name and Attribute
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //https://gdurl.com/OQTe
            //https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/25.png
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://raw.githubusercontent.com/Vijay0710/poke-images/main/sprites/pokemon/other/home/$pokemonID.png")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(300.dp),
                alignment = Alignment.Center,
            )
            Text(
                pokemonViewModel.pokemonInfo.value!![0].name,
                fontFamily = interFontFamily,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                for (pokemon in pokemonViewModel.pokemonInfo.value!![0].types) {
                    AssistChip(
                        onClick = { /* Do something! */ },
                        label = { Text(pokemon.type.name.capitalize()) },
                        Modifier
                            .padding(2.dp),
                        shape = RoundedCornerShape(50),
                    )
                }
            }

            //End of Pokemon Image Name and Attribute

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ScrollableTabRow(
                    selectedTabIndex = state,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state])
                                .height(4.dp) // clip modifier not working
                                .padding(horizontal = 12.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    },
                    divider = {}) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                if (state == 0) {
                    for (i in pokemonViewModel.pokemonSpeciesInfo.value!![0].flavor_text_entries) {
                        val string = URLDecoder.decode(i.flavor_text, "utf-8")
                        Log.i("ENCSTR", string)
                        if (i.language.name == "en") {
                            Log.i("TEXT", i.flavor_text.toString())
                            Text(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),

                                text = i.flavor_text,
                                overflow = TextOverflow.Clip,

                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold,
                            )
                            break
                        }
                    }

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(12.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column() {
                                val weight =
                                    pokemonViewModel.pokemonInfo.value!![0].weight.toFloat() / 10
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth(0.5f)
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_weight),
                                        contentDescription = "Weight",
                                        Modifier.size(24.dp)
                                    )
                                    Text(
                                        "$weight kg (${floor(weight * 2.205)} lbs)",
                                        fontFamily = interFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                    )
                                }
                                Text(
                                    "Weight",
                                    fontFamily = interFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth(
                                        0.5f
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Divider(
                                color = Color.LightGray,
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .fillMaxHeight()
                                    .width(1.dp),


                                )

                            Column() {
                                val height =
                                    pokemonViewModel.pokemonInfo.value!![0].height.toFloat() / 10
                                val calc = 100 * height / 2.54
                                val feet = kotlin.math.floor(calc / 12)
                                val inches = floor(calc - (12 * feet))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_height),
                                        contentDescription = "Height",
                                        Modifier.size(24.dp)
                                    )
                                    Text(
                                        "$height m (${feet}' ${inches}'')",
                                        fontFamily = interFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                                Text(
                                    "Height",
                                    fontFamily = interFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth(
                                        1f
                                    ),
                                    textAlign = TextAlign.Center
                                )


                            }

                        }
                    }
                }
                if (state == 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
//                        The linear progress indicator will be worked upon since it doesn't have documentation on how to have rounded corners in progress indicators
//                        LinearProgressIndicator(
//                            modifier = Modifier
//                                .padding(12.dp)
//                                .height(10.dp)
//                                .clip(
//                                    RoundedCornerShape(12.dp)
//                                ),
//                            progress = animatedProgress,
//                        )
//                      The below examples describes the use of custom linear indicator
                        val dict = mapOf("attack" to "Atk", "defense" to "Def",
                        "special-attack" to "SAtk", "special-defense" to "SDef" ,"defense" to "Def" ,"speed" to "Spd","hp" to "HP"
                            )
                        for (i in 0..5) {
                            val floatValue =
                                pokemonViewModel.pokemonInfo.value!![0].stats[i].base_stat.toFloat() / 10
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(
                                            rememberScrollState()
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround,
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp)
                                            .align(Alignment.CenterVertically),
                                        text =
                                            dict[pokemonViewModel.pokemonInfo.value!![0].stats[i].stat.name]!!
                                        ,
                                        fontFamily = interFontFamily,
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(7.dp)
                                            .align(Alignment.CenterVertically),
                                        text = pokemonViewModel.pokemonInfo.value!![0].stats[i].base_stat.toString(),
                                        fontFamily = interFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Start,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Box(
                                        modifier = Modifier

                                            .align(Alignment.CenterVertically)
                                            .clip(RoundedCornerShape(15.dp))
                                            .height(10.dp)
                                            .background(ProgressIndicatorDefaults.linearTrackColor)
                                            .width(240.dp)
                                    ) {
                                        Log.i("FLOAT VALUE", floatValue.toString())
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(15.dp))
                                                .height(10.dp)
                                                .background(
                                                    ProgressIndicatorDefaults.linearColor
                                                )
                                                .width(240.dp * (floatValue / 10))
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
                if (state == 2) {
                    CardContentAbilities(name = "", pokeURL = "")
                }

            }
        }
    }

}

@Composable
private fun CardContent(
    name: String,
    pokeURL: String,
    pokemonViewModel: PokemonViewModel,
    navController: NavController
) {

    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .height(85.dp)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, fontFamily = interFontFamily, fontWeight = FontWeight.ExtraBold)
            Row() {
                IconButton(
                    onClick = {
                        Log.i("POKEMON URI: ", pokeURL)

                        val lstValues: List<String> = pokeURL.split("/").map { it -> it.trim() }
                        Log.i("LST VALUES", lstValues.toString())
                        Log.i("URL LIST POKE NO", lstValues[6].trim().toString())
                        fetchPokemonInformation(
                            pokemonViewModel,
                            lstValues[6].trim(),
                            navController
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Click here"
                    )
                }

            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonGreeting(pokemonViewModel: PokemonViewModel, navController: NavController) {
    var inputText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var expandedSort by remember { mutableStateOf(false) }
    Column {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp))
                .align(Alignment.CenterHorizontally),
            singleLine = true,

            trailingIcon = {
                Row(Modifier.padding(6.dp)) {
                    var expanded by remember { mutableStateOf(false) }


                    Box(
                        modifier = Modifier
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    painterResource(id = R.drawable.ic_filter_list),
                                    contentDescription = "Filter"
                                )
                            }
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Name") },
                                onClick = {
                                    sortedList = pokemonViewModel.items.value!!.sortedBy {
                                        it.name
                                    }
                                    expandedSort = !expandedSort
                                    Log.i("SORTED LIST: ", sortedList.toString())
                                },
                                leadingIcon = {
                                    Icon(
                                        painterResource(id = R.drawable.pikachu_icon),
                                        contentDescription = null
                                    )
                                })
                        }
                    }
                }
            },
        )
        Log.i("IN DROPDOWN: ", pokemonViewModel.items.value!!.toString())


        if (!expandedSort) {
            LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                items(items = pokemonViewModel.items.value!!.filter {
                    it.name.contains(inputText.text, ignoreCase = true)
                }.sortedBy { it.name }, key = { it.url }) { pokemon ->

                    CardContent(
                        name = pokemon.name,
                        pokeURL = pokemon.url,
                        pokemonViewModel,
                        navController
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                items(items = pokemonViewModel.items.value!!.filter {
                    it.name.contains(inputText.text, ignoreCase = true)
                }.sortedByDescending { it.name }, key = { it.url }) { pokemon ->

                    CardContent(
                        name = pokemon.name,
                        pokeURL = pokemon.url,
                        pokemonViewModel,
                        navController
                    )
                }
            }
        }


    }


}

@Composable()
fun SplashActivity(pokemonViewModel: PokemonViewModel, navController: NavController) {
//    var visible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Pokemon Splash",
            contentScale = ContentScale.Fit,
        )

        Button(modifier = Modifier.padding(8.dp), onClick = { navController.navigate("greet") }) {
            Text("Check out")
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    PokeDEXTheme {
        InfoScreen()
    }
}