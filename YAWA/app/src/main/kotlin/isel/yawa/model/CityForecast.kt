package isel.yawa.model

import java.util.*


data class CityForecast(val city:City, val list: Collection<List>){
    constructor() : this(City(), Collections.emptyList())

    data class List( val weather: Collection<CityWheather.Weather> , val main: CityWheather.Meteorology){
        constructor() : this(Collections.emptyList(),CityWheather.Meteorology())
    }

    data class City (val name:String){
        constructor(): this("")
    }
}
