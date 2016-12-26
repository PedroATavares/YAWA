package isel.yawa.present

import android.content.AsyncQueryHandler
import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.android.volley.toolbox.NetworkImageView
import isel.yawa.Application.Companion.CITY_KEY
import isel.yawa.R
import isel.yawa.connect.MappingRequest
import isel.yawa.connect.RequestManager
import isel.yawa.connect.buildDailyForecastQueryString
import isel.yawa.model.WeatherForecast
import isel.yawa.model.WeatherInfo
import isel.yawa.model.content.WeatherProvider
import isel.yawa.model.content.fromJsonObject
import isel.yawa.model.content.toWeatherForecast
import kotlinx.android.synthetic.main.activity_forecast.*

class ForecastActivity : AppCompatActivity() {

    companion object {
        const val FORECAST_KEY: String = "forecast"
        const val FORECAST_LENGTH = 5

        private lateinit var forecastSlots : Array<Pair<TextView, NetworkImageView>>
        private lateinit var queryHandler: AsyncQueryHandler
        private lateinit var target: WeatherForecast
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        forecastSlots = arrayOf(
                Pair(todaydesc, todayicon),
                Pair(tomorrowdesc, tomorrowicon),
                Pair(twodaysdesc, twodaysicon),
                Pair(threedaysdesc, threedaysicon),
                Pair(fourdaysdesc, fourdaysicon)
        )

        setupAsyncQueryHandler()

        if(savedInstanceState != null && savedInstanceState.containsKey(FORECAST_KEY)){
            restoreViewState(savedInstanceState.getParcelable(FORECAST_KEY))
            return
        }

        if(intent.extras.containsKey(CITY_KEY))
            queryContentProvider(intent.extras.getString(CITY_KEY))
    }

    private fun setupAsyncQueryHandler() {
        queryHandler = object : AsyncQueryHandler(contentResolver) {
            override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor) {
                if (!cursor.moveToNext() && cursor.count < FORECAST_LENGTH) {
                    val city = cookie as String
                    fetchCityForecast(city)
                    return
                }

                val forecast = cursor.toWeatherForecast(FORECAST_LENGTH)
                restoreViewState(forecast)
            }
        }
    }

    private fun queryContentProvider(city: String) {
        with(WeatherProvider) {
            queryHandler.startQuery(0, city,
                    FORECAST_CONTENT_URI, null,
                    "$COLUMN_CITY = ?",
                    arrayOf(city),
                    "$COLUMN_DATE asc")
        }
    }

    private fun fetchCityForecast(city: String){
        RequestManager.put(
                MappingRequest(
                        buildDailyForecastQueryString(city),
                        { WeatherForecast().fromJsonObject(it) },
                        { restoreViewState(it) },
                        { error ->
                            Log.e("ERROR", error.message)
                            throw error
                        }
                )
        )
    }

    private fun restoreViewState(forecast: WeatherForecast) {
        target = forecast

        cityTextForecast.text = forecast.cityName

        forecastSlots.forEachIndexed { i, pair ->
            val wInfo = forecast[i]!!

            pair.first.text = wInfo.description
            fetchAndShowIcon(wInfo.icon_url, pair.second)

            pair.first.setOnClickListener { onItemClick(wInfo) }
            pair.second.setOnClickListener { onItemClick(wInfo) }
        }
    }

    private fun onItemClick(info: WeatherInfo) {
        startActivity(Intent(this, WeatherActivity::class.java).apply {
            putExtra(WeatherActivity.WEATHER_KEY, info)
        })
    }

    private fun fetchAndShowIcon(iconStr: String?, icon: NetworkImageView) {
        val url = "${getString(R.string.api_image_endpoint)}$iconStr.png"
        RequestManager.fetchImageAndDisplay(url, icon)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(FORECAST_KEY, target)
    }
}
