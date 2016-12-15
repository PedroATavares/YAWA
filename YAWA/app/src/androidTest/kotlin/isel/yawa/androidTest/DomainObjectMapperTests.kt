package isel.yawa.androidTest

import isel.yawa.model.AmbientInfo
import isel.yawa.model.WeatherInfo
import isel.yawa.model.content.WeatherProvider
import isel.yawa.model.content.fromJsonObject
import isel.yawa.model.content.toContentValues
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals

class DomainObjectMapperTests {
    companion object {
        const val DUMMY_DATE: Long = 1234
        const val DUMMY_CITY = "Barcelona"
        const val DUMMY_COUNTRY = "Korea"
        const val DUMMY_MAIN = "Sunny"
        const val DUMMY_DESCRIPTION = "abcdefghijklmnopqrstuvxyz"
        const val DUMMY_ICON_URL = "lol"

        const val DUMMY_TEMP = 2.0
        const val DUMMY_TEMP_MIN = 1.0
        const val DUMMY_TEMP_MAX = 3.0
        const val DUMMY_PRESSURE = 29
        const val DUMMY_HUMIDITY = 92

        val dummyAmbientInfo =
            AmbientInfo(DUMMY_TEMP, DUMMY_TEMP_MIN, DUMMY_TEMP_MAX, DUMMY_PRESSURE, DUMMY_HUMIDITY)

        val dummyWeatherInfo = WeatherInfo(
        DUMMY_DATE,
        DUMMY_CITY,
        DUMMY_COUNTRY,
        DUMMY_MAIN,
        DUMMY_DESCRIPTION,
        dummyAmbientInfo,
        DUMMY_ICON_URL
        )

        /**
         * See link below for data format
         * http://api.openweathermap.org/data/2.5/weather/?appid=64ebb6f1a2a7abf9c9a91fedf34426dd&q=Lisboa&lang=pt&units=metric
         */
        val json = JSONObject().apply {
            put("sys", JSONObject().apply { put("country", DUMMY_COUNTRY) })

            put("weather", JSONArray())
            accumulate("weather", JSONObject().apply {
                put("main", DUMMY_MAIN)
                put("description", DUMMY_DESCRIPTION)
                put("icon", DUMMY_ICON_URL)
            })

            put("main", JSONObject().apply {
                put("temp", DUMMY_TEMP)
                put("temp_min", DUMMY_TEMP_MIN)
                put("temp_max", DUMMY_TEMP_MAX)
                put("pressure", DUMMY_PRESSURE)
                put("humidity", DUMMY_HUMIDITY)
            })

            put("dt", DUMMY_DATE)
            put("name", DUMMY_CITY)
        }
    }

    @Test
    fun testWeatherInfoToContentValues(){
        val contentValues = dummyWeatherInfo.toContentValues()

        with(WeatherProvider){
            with(contentValues){
                assertEquals(DUMMY_DATE, getAsLong(COLUMN_DATE))
                assertEquals(DUMMY_CITY, getAsString(COLUMN_CITY))
                assertEquals(DUMMY_COUNTRY, getAsString(COLUMN_COUNTRY))
                assertEquals(DUMMY_MAIN, getAsString(COLUMN_MAIN))
                assertEquals(DUMMY_DESCRIPTION, getAsString(COLUMN_DESCRIPTION))
                assertEquals(DUMMY_ICON_URL, getAsString(COLUMN_ICON_URL))
                assertEquals(DUMMY_TEMP, getAsDouble(COLUMN_TEMP))
                assertEquals(DUMMY_TEMP_MIN, getAsDouble(COLUMN_TEMP_MIN))
                assertEquals(DUMMY_TEMP_MAX, getAsDouble(COLUMN_TEMP_MAX))
                assertEquals(DUMMY_PRESSURE, getAsInteger(COLUMN_PRESSURE))
                assertEquals(DUMMY_HUMIDITY, getAsInteger(COLUMN_HUMIDITY))
            }
        }
    }

    @Test
    fun testWeatherInfofromJSON(){
        val wi = WeatherInfo().fromJsonObject(json)

        with(dummyWeatherInfo){
            assertEquals(date, wi.date)
            assertEquals(city, wi.city)
            assertEquals(country, wi.country)
            assertEquals(main, wi.main)
            assertEquals(description, wi.description)
            assertEquals(icon_url, wi.icon_url)

            with(dummyAmbientInfo){
                val ai = ambientInfo!!

                assertEquals(temp, ai.temp)
                assertEquals(tempMin, ai.tempMin)
                assertEquals(tempMax, ai.tempMax)
                assertEquals(pressure, ai.pressure)
                assertEquals(humidity, ai.humidity)
            }

        }
    }

    @Test
    fun testAmbientInfofromJSON(){
        val ai = AmbientInfo().fromJsonObject(json.getJSONObject("main"))

        with(dummyAmbientInfo){
            assertEquals(temp, ai.temp)
            assertEquals(tempMin, ai.tempMin)
            assertEquals(tempMax, ai.tempMax)
            assertEquals(pressure, ai.pressure)
            assertEquals(humidity, ai.humidity)
        }
    }
}