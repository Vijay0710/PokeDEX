package apps.cooper.pokedex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.TextField
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
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
import apps.cooper.pokedex.ui.theme.PokeDEXTheme
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight


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


private fun fetchPokemonInformation(pokemonViewModel: PokemonViewModel,no:String,navController: NavController){
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

//            for(pokemon in data.results){
////                pokemonViewModel.addPokemonDetails(pokemon.name,pokemon.url)
//            }
//            Log.i("VIEWMODEL: ",pokemonViewModel.items.value.toString())
//            Log.i("VIEWMODEL SIZE: ",pokemonViewModel.items.value?.size.toString())
        }

        override fun onFailure(call: Call<PokemonDetails>, t: Throwable) {
            Log.i("ERR in PokeInfo: ",t.message.toString())
        }
    })
}
@Composable
fun PokemonInformationScreen(modifier: Modifier,pokemonViewModel:PokemonViewModel,navController: NavController){
    Column( modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(12.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color.LightGray)
        .padding(12.dp),
        ){
        Column(modifier = Modifier.fillMaxWidth()){
            Image(
                painter = rememberAsyncImagePainter("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/132.png"),
                contentDescription = "Pokemon Splash",
                modifier = Modifier
                    .size(128.dp)
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
    val interFontFamily = FontFamily(
        Font(R.font.inter, FontWeight.Light)
    )
    ElevatedCard(Modifier.fillMaxWidth().height(85.dp).padding(12.dp)) {
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

//@Composable
//private fun CardContent(name: String,pokeURL:String,pokemonViewModel: PokemonViewModel,navController: NavController){
////    println("Hello")
//
//
//    Row(modifier = Modifier
//        .padding(12.dp)
//        .clip(RoundedCornerShape(14.dp))
//        .fillMaxWidth()
//        .background(colorResource(id = R.color.dark))
//        .padding(12.dp, vertical = 8.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text("Hello $name",color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
//        Row() {
//            IconButton(
//                onClick = { Log.i("POKEMON URI: ",pokeURL)
//                    val lstValues: List<String> = pokeURL.split("/").map { it -> it.trim() }
//                    Log.i("LST VALUES",lstValues.toString())
//                    Log.i("URL LIST POKE NO",lstValues[6].trim().toString())
//                    fetchPokemonInformation(pokemonViewModel,lstValues[6].trim(),navController)
//                }
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.ArrowForward,
//                    tint = Color.White,
//                    contentDescription = "Click here"
//                )
//            }
//
//        }
//
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonGreeting(pokemonViewModel: PokemonViewModel,navController: NavController) {
    var inputText by rememberSaveable(stateSaver = TextFieldValue.Saver){
        mutableStateOf(TextFieldValue())
    }
    Column(){
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = {Text("Search")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp))
                .align(Alignment.CenterHorizontally),
            singleLine = true,
            trailingIcon = {Icon(Icons.Filled.Search,contentDescription = "Search")}
        )

        LazyColumn(modifier = Modifier.padding(vertical = 8.dp)){
            items(items = pokemonViewModel.items.value!!.filter {
                it.name.contains(inputText.text,ignoreCase = true)
            },key = {it.url}){ pokemon ->

                CardContent(name = pokemon.name,pokeURL = pokemon.url,pokemonViewModel,navController)
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
//        PokemonInformationScreen(modifier = Modifier.fillMaxSize())
//        CardContent(name = "demo", pokeURL = "google.com", pokemonViewModel = , navController = NavController(this@MainActivity))
//        DemoCardContent()
    }
}