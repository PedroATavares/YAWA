package isel.yawa.present

import android.content.AsyncQueryHandler
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import isel.yawa.R
import isel.yawa.connect.MappingRequest
import isel.yawa.connect.RequestManager
import isel.yawa.connect.buildWeatherQueryString
import isel.yawa.model.WeatherInfo
import isel.yawa.model.content.WeatherProvider
import isel.yawa.model.content.fromCursor
import isel.yawa.model.content.fromJsonObject
import kotlinx.android.synthetic.main.activity_weather.*
import java.util.*

class WeatherActivity : AppCompatActivity() {

    companion object {
        const val CITY_KEY: String = "city"
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

            target = weather
            populateViewsFromWeather(weather)

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

                target = WeatherInfo().fromCursor(cursor)
                populateViewsFromWeather(target)
            }
        }
    }

    private fun fetchCurrentWeather(city: String){
        RequestManager.put(
                MappingRequest(
                        buildWeatherQueryString(city),
                        { WeatherInfo().fromJsonObject(it) },
                        { target = it; populateViewsFromWeather(it) },
                        { error ->
                            Log.e("ERROR", error.message)
                            throw error
                        }
                )
        )
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

            fetchAndShowIcon(icon_url) // TODO: fix running on UI thread
        }
    }


    private fun fetchAndShowIcon(icon: String?) {
        fun  buildIconQueryUrl(icon: String?): String? {
            return "${resources.getString(R.string.api_image_endpoint)}$icon.png"
        }

        RequestManager.put(ImageRequest(
                buildIconQueryUrl(icon),
                Response.Listener<Bitmap> { bitmap -> iconView.setImageBitmap(bitmap) },
                0,
                0,
                iconView.scaleType,
                Bitmap.Config.ARGB_8888,
                Response.ErrorListener { error ->
                    iconView.setBackgroundColor(Color.parseColor("#ff0000"))
                    Toast.makeText(this, R.string.icon_fetch_failed_message, Toast.LENGTH_SHORT).show()
                })
        )
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
