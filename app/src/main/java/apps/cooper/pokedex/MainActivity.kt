package apps.cooper.pokedex

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import apps.cooper.pokedex.models.Pokemon
import apps.cooper.pokedex.models.PokemonDetails
import apps.cooper.pokedex.models.PokemonInfo
import apps.cooper.pokedex.models.PokemonViewModel
import apps.cooper.pokedex.models.Result
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import java.util.*
import kotlin.Comparator

private lateinit var sortedList: List<Result>
//private lateinit var pokemonViewModel: PokemonViewModel
private val interFontFamily = FontFamily(
    Font(R.font.inter, FontWeight.Light)
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pokemonViewModel : PokemonViewModel by viewModels()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                if(pokemonViewModel.items.value?.isEmpty() == true){
                    Log.i("IF CONDITION","FETCHING...")
                    fetchPokemonNames(pokemonViewModel)

                }
                else{
                    Log.i("LOAD FROM CACHE: ",pokemonViewModel.items.value.toString())
                }
            }
        }

        setContent {
            PokeDEXTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.i("POKEMON: ",pokemonViewModel.items.value.toString())
//                    PokemonGreeting(pokemonViewModel)
//                      PokemonInformationScreen(no = "1")
//                    fetchPokemonInformation(pokemonViewModel,"1")
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "splash") {
                        composable(route="splash"){
                            SplashActivity(pokemonViewModel,navController)
                        }
                        composable(route = "info"){
                            PokemonInformationScreen(modifier = Modifier.fillMaxSize(),pokemonViewModel,navController)
                        }
                        composable(route = "greet") {
                            PokemonGreeting(pokemonViewModel = pokemonViewModel,navController)
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
            Log.i("DATA: ",data.toString())
            for(pokemon in data.results){
                pokemonViewModel.addPokemonDetails(pokemon.name,pokemon.url)
            }
            Log.i("VIEWMODEL: ",pokemonViewModel.items.value.toString())
            Log.i("VIEWMODEL SIZE: ",pokemonViewModel.items.value?.size.toString())
        }

        override fun onFailure(call: Call<PokemonInfo>, t: Throwable) {
            Log.i("ERR in fetchPoke: ",t.message.toString())
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

private val nameComparator = Comparator<Result>{left, right ->
    right.name.compareTo(left.name)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoScreen(){
//    val pokemonViewModel:PokemonViewModel by viewModel()
    val height = 0
    var state by remember { mutableStateOf(0) }
    val titles = listOf("About", "Stats", "Moves","Evolution","Location")
    //The below text was taken from flavor_text_entries from pokemon-species/25 endpoint
    val body = listOf("It keeps its tail raised to monitor its surroundings. If you yank its tail, it will try to bite you.")
    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(12.dp),
        ){
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ){

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/25.png")
                    .decoderFactory(SvgDecoder.Factory())
                    .build()
                ,
                contentDescription =null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(200.dp),
                alignment = Alignment.Center,
            )
            Text("Pikachu", fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold)

            Row(){
                AssistChip(
                    onClick = { /* Do something! */ },
                    label = { Text("Lightning") },
                    Modifier
                        .padding(2.dp),
//                    leadingIcon = {
//                        Icon(
//                            painterResource(id = R.drawable.thunder),
//                            contentDescription = "Localized description",
//                            Modifier.width(18.dp)
//
//                        )
//                    },
                    shape = RoundedCornerShape(50),
                )
            }

//            Image(
//                painter = rememberAsyncImagePainter("https://unpkg.com/pokeapi-sprites@2.0.4/sprites/pokemon/other/dream-world/1.svg"),
//                contentDescription = "Pokemon Splash",
//                modifier = Modifier
//                    .padding(12.dp)
//                    .clip(RoundedCornerShape(10.dp))
//                    .size(250.dp),
//                alignment = Alignment.Center,
//
//            )
            Column(){
                ScrollableTabRow(selectedTabIndex = state, edgePadding = 0.dp) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(12.dp),
                    text = "${body[0]}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold,
                )
            }
//            Text("Order is 32",fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(2.dp))
//            Text("Hello",fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(2.dp))
//            Text("Hello",fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(2.dp))
        }
    }
}


private fun fetchPokemonInformation(
    pokemonViewModel: PokemonViewModel,
    no: String,
    navController: NavController,
){
    val rBuilder = retrofitBuilder("https://pokeapi.co/api/v2/pokemon/")
    val rData = rBuilder.getPokemonInfo(no)
    rData.enqueue(object : Callback<PokemonDetails> {
        override fun onResponse(call: Call<PokemonDetails>, response: Response<PokemonDetails>) {
            val data = response.body()!!
            println("RESULTS")
            Log.i("DATA: ",data.toString())
            Log.i("EACH POKEMON NAME: ",data.name)
            pokemonViewModel.updatePokemonDetails(data.abilities,data.base_experience,data.forms,data.game_indices,
            data.height,data.held_items,data.id,data.is_default,data.location_area_encounters,data.moves,data.name,
            data.order,data.past_types,data.species,data.sprites,data.stats,data.types,data.weight)
            Log.i("POKE BASE EXP INFO: ", pokemonViewModel.pokemonInfo.value?.get(0)?.base_experience.toString())
            navController.navigate("info")

        }

        override fun onFailure(call: Call<PokemonDetails>, t: Throwable) {
            Log.i("ERR in PokeInfo: ",t.message.toString())
        }
    })
}
@Composable
fun PokemonInformationScreen(modifier: Modifier,pokemonViewModel:PokemonViewModel,navController: NavController){
    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .height(500.dp)
        .padding(12.dp)
    ){
        Column(){
            Image(
                painter = rememberAsyncImagePainter(pokemonViewModel.pokemonInfo.value!![0].sprites.other.dream_world),
                contentDescription = "Pokemon Splash",
                modifier = Modifier
                    .size(256.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit,
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
            ){
            Column(
            ){
                Text("Name : ",Modifier.padding(bottom = 4.dp))
                Text("BE : ",Modifier.padding(bottom = 4.dp))
                Text("Order : ",Modifier.padding(bottom = 4.dp))
                Text("Weight : ",Modifier.padding(bottom = 4.dp))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(pokemonViewModel.pokemonInfo.value!![0].name,Modifier.padding(bottom = 4.dp))
                Text(pokemonViewModel.pokemonInfo.value!![0].base_experience.toString(),Modifier.padding(bottom = 4.dp))
                Text(pokemonViewModel.pokemonInfo.value!![0].order.toString(),Modifier.padding(bottom = 4.dp))
                Text(pokemonViewModel.pokemonInfo.value!![0].weight.toString(),Modifier.padding(bottom = 4.dp))

            }
        }


    }

}

@Composable
private fun CardContent(name: String,pokeURL:String,pokemonViewModel: PokemonViewModel,navController: NavController){

    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .height(85.dp)
            .padding(12.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, fontFamily = interFontFamily, fontWeight = FontWeight.ExtraBold)
            Row() {
                IconButton(
                    onClick = {
                        Log.i("POKEMON URI: ",pokeURL)

                        val lstValues: List<String> = pokeURL.split("/").map { it -> it.trim() }
                        Log.i("LST VALUES",lstValues.toString())
                        Log.i("URL LIST POKE NO",lstValues[6].trim().toString())
                        fetchPokemonInformation(pokemonViewModel,lstValues[6].trim(),navController)
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
fun PokemonGreeting(pokemonViewModel: PokemonViewModel,navController: NavController) {
    var inputText by rememberSaveable(stateSaver = TextFieldValue.Saver){
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

            trailingIcon = { Row(Modifier.padding(6.dp)) {
                var expanded by remember { mutableStateOf(false) }


                Box(modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        IconButton(onClick = { expanded = true }) {
                            Icon(painterResource(id = R.drawable.ic_filter_list), contentDescription = "Filter")
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
            } },
        )
        Log.i("IN DROPDOWN: ", pokemonViewModel.items.value!!.toString())
//        FilterMenu()

//        IconButton(onClick = { expandedSort = !expandedSort}) {
//            Icon(painterResource(id = R.drawable.ic_filter_list), contentDescription = null)
//        }



        if(expandedSort) {
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
        }

        else {
            LazyColumn(modifier = Modifier.padding(vertical = 8.dp)){
                items(items =  pokemonViewModel.items.value!!.filter {
                    it.name.contains(inputText.text,ignoreCase = true)
                }.sortedByDescending { it.name },key = {it.url}){ pokemon ->

                    CardContent(name = pokemon.name,pokeURL = pokemon.url,pokemonViewModel,navController)
                }
            }
        }


    }



}

@Composable()
fun SplashActivity(pokemonViewModel: PokemonViewModel,navController:NavController){
//    var visible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Pokemon Splash",
            contentScale = ContentScale.Fit,
        )

        Button(modifier = Modifier.padding(8.dp),onClick = { navController.navigate("greet") }) {
            Text("Check out")
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    PokeDEXTheme {
//        SearchContent(Modifier)
//        PokemonGreeting("Android")
//        SplashActivity()
//        FilterMenu()
        InfoScreen()
//        PokemonInformationScreen(modifier = Modifier.fillMaxSize())
//        CardContent(name = "demo", pokeURL = "google.com", pokemonViewModel = , navController = NavController(this@MainActivity))
//        DemoCardContent()
    }
}