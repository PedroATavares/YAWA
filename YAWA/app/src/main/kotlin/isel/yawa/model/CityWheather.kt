package isel.yawa.model;

import java.util.*


data class CityWheather(val name:String, val weather: Collection<Weather>){
    constructor() : this("", Collections.emptyList())

    data class Weather(val main: String, val description: String, val icon : String){
        constructor() : this("", "","")
    }
}
