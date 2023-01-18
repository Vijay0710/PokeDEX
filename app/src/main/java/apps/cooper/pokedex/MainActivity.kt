package apps.cooper.pokedex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import apps.cooper.pokedex.models.Pokemon
import apps.cooper.pokedex.models.PokemonInfo
import apps.cooper.pokedex.models.PokemonViewModel
import apps.cooper.pokedex.ui.theme.PokeDEXTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pokemonViewModel : PokemonViewModel by viewModels()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                if(pokemonViewModel.items.value?.isEmpty() == true){
                    Log.i("IF CONDITION","FETCHING...")
                    fetchPokemonDetails(pokemonViewModel)
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

                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "splash") {
                        composable(route="splash"){
                            SplashActivity(pokemonViewModel,navController)
                        }
                        composable(route = "greet") {
                            PokemonGreeting(pokemonViewModel = pokemonViewModel)
                        }
                    }

                }
            }
        }
    }

    private fun fetchPokemonDetails(pokemonViewModel: PokemonViewModel) {
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
                Log.i("ERR: ",t.message.toString())
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
}
@Composable
private fun CardContent(name: String,pokeURL:String){
//    println("Hello")
    Row(modifier = Modifier
        .padding(12.dp)
        .clip(RoundedCornerShape(14.dp))
        .fillMaxWidth()
        .background(colorResource(id = R.color.dark))
        .padding(12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Hello $name",color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
        Row() {
            IconButton(
                onClick = { Log.i("POKEMON URI: ",pokeURL) }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    tint = Color.White,
                    contentDescription = "Click here"
                )
            }

        }

    }
}

@Composable
fun PokemonGreeting(pokemonViewModel: PokemonViewModel) {
    LazyColumn(modifier = Modifier.padding(vertical = 8.dp)){
        items(items = pokemonViewModel.items.value!!){ pokemon ->
            CardContent(name = pokemon.name,pokeURL = pokemon.url)
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
//        PokemonGreeting("Android")
//        SplashActivity()
    }
}