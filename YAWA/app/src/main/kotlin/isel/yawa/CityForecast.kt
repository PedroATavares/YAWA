package isel.pdm.yawa

import java.util.*

/**
 * Created by luism on 15/10/2016.
 */

data class CityForecast(val name:String, val weather: Collection<Weather>){
    constructor() : this("", Collections.emptyList())

    data class Weather(val main: String, val description: String, val icon : String){
        constructor() : this("", "","")
    }
}
