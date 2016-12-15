package isel.yawa.model.content

import android.content.ContentValues
import isel.yawa.model.AmbientInfo
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
 * Constructs a WeatherInfo object from a JSONObject
 */
fun WeatherInfo.fromJsonObject(json: JSONObject) : WeatherInfo = with(json){
    val sys = getJSONObject("sys")
    val weatherFirst = getJSONArray("weather").getJSONObject(0)
    val  _main = getJSONObject("main")

    this@fromJsonObject.apply {
            date = getString("dt").toLong() // see warnings about lossy coercions in json.getLong() documentation
            city = getString("name")
            country = sys.getString("country")
            main = weatherFirst.getString("main")
            description = weatherFirst.getString("description")
            ambientInfo = AmbientInfo().fromJsonObject(_main)
            icon_url = weatherFirst.getString("icon")
    }
}

/*
 * See link below for data format(look for field named main)
 * http://api.openweathermap.org/data/2.5/weather/?appid=64ebb6f1a2a7abf9c9a91fedf34426dd&q=Lisboa&lang=pt&units=metric
 *
 * Constructs an AmbientInfo object from a JSONObject
 */
fun AmbientInfo.fromJsonObject(main: JSONObject) : AmbientInfo = with(main){
    this@fromJsonObject.apply {
        temp = getDouble("temp")
        tempMin = getDouble("temp_min")
        tempMax = getDouble("temp_max")
        pressure = getInt("pressure")
        humidity = getInt("humidity")
    }
}