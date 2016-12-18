package isel.yawa.present

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import isel.yawa.R
import isel.yawa.connect.DtoGetRequest
import isel.yawa.connect.RequestManager
import isel.yawa.model.CityForecast
import isel.yawa.model.CityWeather
import isel.yawa.model.content.WeatherProvider
import kotlinx.android.synthetic.main.activity_forecast.*
import java.util.*

class ForecastActivity : AppCompatActivity() {

    private lateinit  var forecastSlots : Array<Pair<TextView, ImageView>>
    private var cityForecast: CityForecast =CityForecast()


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
        val url = intent.extras.getString("url")

        with(WeatherProvider) {
            val listList= ArrayList<CityForecast.List>()
            val res = contentResolver.query(
                    FORECAST_CONTENT_URI, null,
                    "${WeatherProvider.COLUMN_CITY} = ? and $COLUMN_DATE > ?",
                    arrayOf(url.substring(url.lastIndexOf('=') + 1, url.length), makeDate())
                    , "$COLUMN_DATE asc")

            if (res.moveToNext() && res.count >= 5) {
                makeCityForecastFromCP(listList, res)
            }else
                if (savedInstanceState == null) {
                    getCityForecast(url)
                }
        }
    }

    private fun makeCityForecastFromCP(listList: ArrayList<CityForecast.List>, res: Cursor) {
        with(WeatherProvider) {
            var i = 0
            while (i < 5) {
                if (i++ != 0)
                    res.moveToNext()

                val weatherList = ArrayList<CityWeather.Weather>()
                weatherList.add(CityWeather.Weather(
                        res.getString(COLUMN_MAIN_IDX),
                        res.getString(COLUMN_DESCRIPTION_IDX),
                        res.getString(COLUMN_ICON_URL_IDX)
                ))
                listList.add(
                        CityForecast.List(
                                weatherList,
                                CityForecast.Meteorology(
                                        res.getLong(COLUMN_TEMP_IDX),
                                        res.getLong(COLUMN_TEMP_MIN_IDX),
                                        res.getLong(COLUMN_TEMP_MAX_IDX)
                                ),
                                res.getLong(COLUMN_PRESSURE_IDX),
                                res.getLong(COLUMN_HUMIDITY_IDX)
                        )
                )
            }
            setListeners(
                    CityForecast(
                            CityForecast.City(res.getString(COLUMN_CITY_IDX)),
                            listList
                    )
            )
        }
    }

    private fun makeDate(): String {
        val cal = Calendar.getInstance()
        var time = cal.timeInMillis
        time -=(cal.get(Calendar.HOUR_OF_DAY)*3600*1000)
        time -=(cal.get(Calendar.MINUTE)*60*1000)
        time -=(cal.get(Calendar.SECOND)*1000)
        time /= 1000
        return  time.toString()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        with(savedInstanceState){
            cityTextForecast.text = getString("cityTextForecast")
            cityForecast=getParcelable<CityForecast>("city")
            
            setListeners(cityForecast)

            forecastSlots.forEachIndexed { i, pair ->
                pair.first.text = getString("textView$i")

                pair.second.setImageBitmap(getParcelable("icon$i"))
            }
        }
    }

    private fun getCityForecast(url: String){
        RequestManager.put(
                DtoGetRequest(
                        url,
                        CityForecast::class.java,
                        { city ->

                            cityForecast=city

                            setListeners(city)
                        },
                        { error ->
                            Toast.makeText(this, R.string.get_forecast_fail_message, Toast.LENGTH_SHORT).show()
                            throw error
                        }
                )
        )
    }

    private fun setListeners(city: CityForecast) {
        cityTextForecast.text = city.city.name

        forecastSlots.forEachIndexed { i, pair ->
            val weather = city.list.elementAt(i).weather.elementAt(0)
            val meter = city.list.elementAt(i).temp

            pair.first.text = "${getString(R.string.weather_forecast_description)} ${weather.description}"

            pair.first.setOnClickListener { onItemClick(city, meter, weather) }
            pair.second.setOnClickListener { onItemClick(city, meter, weather) }

            fetchAndShowIcon(weather.icon, pair.second)
        }
    }

    private fun onItemClick(city: CityForecast, meter: CityForecast.Meteorology, weather: CityWeather.Weather) {
        val myIntent = Intent(this, WeatherActivity::class.java)

        myIntent.putExtra("weather", weather)
        myIntent.putExtra("city", city.city.name)
        myIntent.putExtra("meter", meter)

        startActivity(myIntent)
    }

    private fun fetchAndShowIcon(iconStr: String?, icon: ImageView) {
        val url = "${getString(R.string.api_image_endpoint)}$iconStr.png"

        RequestManager.put(ImageRequest(
                url,
                Response.Listener<Bitmap> { response -> icon.setImageBitmap(response) },
                0,
                0,
                icon.scaleType,
                Bitmap.Config.ARGB_8888,
                Response.ErrorListener { error ->
                    icon.setBackgroundColor(Color.parseColor("#ff0000"))
                    Toast.makeText(this, R.string.icon_fetch_failed_message, Toast.LENGTH_SHORT).show()
                })
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        with(outState){
            putString("cityTextForecast", cityTextForecast.text.toString())
            putParcelable("city", cityForecast)
            forecastSlots.forEachIndexed { i, pair ->
                putString("textView$i", pair.first.text.toString())

                // TODO: change way to pass icons, very poor efficiency
                // how to store icon efficiently?
                val icon = (pair.second.drawable as BitmapDrawable).bitmap
                putParcelable("icon$i", icon)
            }
        }
    }
}
