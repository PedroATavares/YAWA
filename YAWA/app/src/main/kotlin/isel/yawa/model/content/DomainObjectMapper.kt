package isel.yawa.model.content

import android.content.ContentValues
import isel.yawa.model.AmbientInfo
import isel.yawa.model.WeatherForecast
import isel.yawa.model.WeatherInfo
import org.json.JSONObject

/**
 * Created to fullfill the need to transform domain objects into sets of data with different formats.
 * Basicly it holds the code with the logic necessary to adapt data in an object oriented
 * data model to other data modelling schemes.
 * Supports mapping to a table oriented data model trough the use of a ContentValues object
 * where the keys are the column names.
 */

fun WeatherInfo.toContentValues() : ContentValues {
    return ContentValues().apply{
        put(WeatherProvider.COLUMN_DATE, date)
        put(WeatherProvider.COLUMN_CITY, city)
        put(WeatherProvider.COLUMN_COUNTRY, country)

        with(ambientInfo!!){
            put(WeatherProvider.COLUMN_TEMP, temp)
            put(WeatherProvider.COLUMN_TEMP_MIN, tempMin)
            put(WeatherProvider.COLUMN_TEMP_MAX, tempMax)
            put(WeatherProvider.COLUMN_PRESSURE, pressure)
            put(WeatherProvider.COLUMN_HUMIDITY, humidity)
        }

        put(WeatherProvider.COLUMN_MAIN, main)
        put(WeatherProvider.COLUMN_DESCRIPTION, description)
        put(WeatherProvider.COLUMN_ICON_URL, icon_url)
    }
}

/*
 * See link below for data format
 * http://api.openweathermap.org/data/2.5/weather/?appid=64ebb6f1a2a7abf9c9a91fedf34426dd&q=Lisboa&lang=pt&units=metric
 *
 * Constructs a WeatherInfo object from a JSONObject.
 * Allows the user to specify how to create an AmbientInfo trough the use of a function
 * which receives, as it's first parameter, the JSONObject that was passed to this function
 */
fun WeatherInfo.fromJsonObject(json: JSONObject, ambInfoFactory: () -> AmbientInfo) : WeatherInfo = apply {
    with(json){
        val sys = optJSONObject("sys")
        val weatherFirst = getJSONArray("weather").getJSONObject(0)

        date = getString("dt").toLong() // see warnings about lossy coercions in json.getLong() documentation
        city = optString("name", null)
        country = sys?.getString("country")
        main = weatherFirst.getString("main")
        description = weatherFirst.getString("description")
        ambientInfo = ambInfoFactory()
        icon_url = weatherFirst.getString("icon")
    }
}

fun WeatherInfo.fromJsonObject(json: JSONObject) : WeatherInfo =
        fromJsonObject(json, {
            val main = json.getJSONObject("main")
            AmbientInfo().fromJsonObject(main)
        })


/*
 * See link below for data format(look for field named main)
 * http://api.openweathermap.org/data/2.5/weather/?appid=64ebb6f1a2a7abf9c9a91fedf34426dd&q=Lisboa&lang=pt&units=metric
 *
 * Constructs an AmbientInfo object from a JSONObject
 */
fun AmbientInfo.fromJsonObject(main: JSONObject) : AmbientInfo = apply {
    with(main){
        temp = getDouble("temp")
        tempMin = getDouble("temp_min")
        tempMax = getDouble("temp_max")
        pressure = getInt("pressure")
        humidity = getInt("humidity")
    }
}

/*
 * See link below for data format
 * http://api.openweathermap.org/data/2.5/forecast/daily?q=Lisboa&appid=64ebb6f1a2a7abf9c9a91fedf34426dd&cnt=5&lang=pt&units=metric
 *
 * Constructs a WeatherForecast object from a JSONObject
 */
fun WeatherForecast.fromJsonObject(forecast: JSONObject) : WeatherForecast{
    return apply {
        with(forecast){
            val _city = getJSONObject("city")

            val city = _city.getString("name")
            val country = _city.getString("country")

            val _forecasts = getJSONArray("list")
            var i = 0
            while(i < _forecasts.length()){
                val wInfo = constructWeatherInfo(_forecasts.get(i) as JSONObject)
                wInfo.city = city
                wInfo.country = country

                forecasts.add(wInfo)

                i++
            }
        }
    }
}

/**
 * See data format in link below (look for field named list)
 * Each item of that array is passed to this function
 * http://api.openweathermap.org/data/2.5/forecast/daily?q=Lisboa&appid=64ebb6f1a2a7abf9c9a91fedf34426dd&cnt=5&lang=pt&units=metric
 */
private fun constructWeatherInfo(listItem: JSONObject) : WeatherInfo{
    return WeatherInfo().fromJsonObject(listItem,
            { // AmbientInfo supplier function
                with(listItem.getJSONObject("temp")){
                    AmbientInfo(
                            getDouble("day"),
                            getDouble("min"),
                            getDouble("max"),
                            listItem.getInt("pressure"),
                            listItem.getInt("humidity")
                    )
                }
            }
    )
}

