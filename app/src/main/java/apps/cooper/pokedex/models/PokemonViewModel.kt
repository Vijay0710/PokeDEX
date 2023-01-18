package apps.cooper.pokedex.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PokemonViewModel : ViewModel() {
    private val _items : MutableLiveData<List<Result>> = MutableLiveData(listOf())
    val items : LiveData<List<Result>> = _items

    fun addPokemonDetails(name:String,url:String){
        val pokemon = Result(name = name,url=url)
        _items.value =_items.value?.plus(pokemon)?: listOf(pokemon)
    }
}