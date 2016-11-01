package isel.yawa.model

import java.util.Collections


data class CityForecast(val city:City, val list: Collection<List>){
    @Suppress("unused")
    constructor() : this(City(), Collections.emptyList())

    data class List(val weather: Collection<CityWeather.Weather>, val main: CityWeather.Meteorology){
        @Suppress("unused")
        constructor() : this(Collections.emptyList(), CityWeather.Meteorology())
    }

    data class City (val name:String){
        constructor(): this("")
    }
}
