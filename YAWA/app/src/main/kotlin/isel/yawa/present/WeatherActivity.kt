package isel.yawa.present

import android.content.AsyncQueryHandler
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import isel.yawa.Application.Companion.CITY_KEY
import isel.yawa.R
import isel.yawa.connect.MappingRequest
import isel.yawa.connect.RequestManager
import isel.yawa.connect.buildWeatherQueryString
import isel.yawa.model.WeatherInfo
import isel.yawa.model.content.WeatherProvider
import isel.yawa.model.content.fromJsonObject
import isel.yawa.model.content.toWeatherInfo
import kotlinx.android.synthetic.main.activity_weather.*
import java.util.*

class WeatherActivity : AppCompatActivity() {

    companion object {
        const val WEATHER_KEY: String = "weather"

        private lateinit var queryHandler: AsyncQueryHandler
        private lateinit var target: WeatherInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_weather)
        setupAsyncQueryHandler()

        if(intent.extras.containsKey(WEATHER_KEY)){
            val weather = intent.extras.getParcelable<WeatherInfo>(WEATHER_KEY)
            restoreViewState(weather)
            return
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(WEATHER_KEY)){
            restoreViewState(savedInstanceState.getParcelable(WEATHER_KEY))
            return
        }

        val city = intent.extras.getString(CITY_KEY)
        with(WeatherProvider) {
            queryHandler.startQuery(0, city,
                    WEATHER_CONTENT_URI, null,
                    "$COLUMN_CITY = ?",
                    arrayOf(city),
                    "$COLUMN_DATE desc")
        }
    }

    private fun setupAsyncQueryHandler() {
        queryHandler = object : AsyncQueryHandler(contentResolver) {
            override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor) {
                if (!cursor.moveToNext()) {
                    fetchCurrentWeather(intent.extras.getString(CITY_KEY))
                    return
                }

                val wInfo = cursor.toWeatherInfo()
                restoreViewState(wInfo)
            }
        }
    }

    private fun fetchCurrentWeather(city: String){
        RequestManager.put(
                MappingRequest(
                        buildWeatherQueryString(city),
                        { WeatherInfo().fromJsonObject(it) },
                        { restoreViewState(it) },
                        { error ->
                            Log.e("ERROR", error.message)
                            throw error
                        }
                )
        )
    }

    private fun restoreViewState(info: WeatherInfo){
        target = info
        populateViewsFromWeather(info)
    }

    private fun populateViewsFromWeather(weather: WeatherInfo) {
        with(weather){
            cityName.text = city
            dateField.text = Date(date!! * 1000).toString()
            countryText.text = country

            weatherText.text = main
            weatherDescText.text = description

            fun addUnit(temp: Double?) = "$temp ${resources.getString(R.string.weather_detail_unit)}"

            with(ambientInfo!!){
                medTemp.text = addUnit(temp)
                minTemp.text = addUnit(tempMin)
                maxTemp.text = addUnit(tempMax)

                pressureTextView.text = String.format(resources.getString(R.string.weather_detail_pressure), pressure)
                humidityTextView.text = String.format(resources.getString(R.string.weather_detail_humidity), humidity)
            }

            fetchAndShowIcon(icon_url)
        }
    }

    private fun fetchAndShowIcon(icon: String?) {
        fun  buildIconQueryUrl(icon: String?): String? {
            return "${resources.getString(R.string.api_image_endpoint)}$icon.png"
        }

        RequestManager.fetchImageAndDisplay(buildIconQueryUrl(icon), iconView)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        target = savedInstanceState.getParcelable(WEATHER_KEY)
        populateViewsFromWeather(target)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(WEATHER_KEY, target)
    }
}
