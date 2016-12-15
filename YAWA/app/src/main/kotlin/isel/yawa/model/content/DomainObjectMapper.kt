package isel.yawa.model.content

import android.content.ContentValues
import isel.yawa.model.CityWeather

/**
 * Created to fullfill the need to transform domain objects into sets of data that can fit
 * in a SQLite database table row.
 * Basicly it holds the code with the logic necessary to adapt data in an object oriented
 * data model to a table oriented data model trough the use of a ContentValues object
 */

fun CityWeather.toContentValues() : ContentValues {
    return ContentValues().apply{
        // TODO: add date to Domain obj
        put(WeatherProvider.COLUMN_DATE, System.currentTimeMillis() / 1000)

        val _wthr = weather.elementAt(0)
        put(WeatherProvider.COLUMN_CITY, name)
        put(WeatherProvider.COLUMN_TEMP, main.temp)
        put(WeatherProvider.COLUMN_TEMP_MIN, main.temp_min)
        put(WeatherProvider.COLUMN_TEMP_MAX, main.temp_max)
        put(WeatherProvider.COLUMN_TEMP_KF, main.temp_kf)
        put(WeatherProvider.COLUMN_HUMIDITY, main.humidity)
        put(WeatherProvider.COLUMN_MAIN, _wthr.main)
        put(WeatherProvider.COLUMN_DESCRIPTION, _wthr.description)
        put(WeatherProvider.COLUMN_ICON_URL, _wthr.icon)
    }
}