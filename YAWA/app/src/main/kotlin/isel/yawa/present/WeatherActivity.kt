package isel.yawa.present

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import isel.yawa.R
import isel.yawa.connect.DtoGetRequest
import isel.yawa.connect.RequestManager
import isel.yawa.model.CityForecast
import isel.yawa.model.CityWeather
import isel.yawa.model.content.WeatherProvider
import kotlinx.android.synthetic.main.activity_weather.*
import java.util.*

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_weather)

        val weather = intent.extras.getParcelable<CityWeather.Weather>("weather")
        val meter= intent.extras.getParcelable<CityForecast.Meteorology>("meter")

        if(weather !=null && meter!= null)
        {
            populateFromWeather(weather,meter,intent.extras.getString("city"))

        }else
            if(savedInstanceState == null){
                val url = intent.extras.getString("url")

                with(WeatherProvider) {
                    val res = contentResolver.query(WEATHER_CONTENT_URI, null,
                            "${WeatherProvider.COLUMN_CITY} = ?",
                            arrayOf(url.substring(url.lastIndexOf('=') + 1, url.length))
                            , "$COLUMN_DATE desc")

                    if (res.moveToNext()) {
                        populateFromWeather(
                                CityWeather.Weather(
                                        res.getString(COLUMN_MAIN_IDX),
                                        res.getString(COLUMN_DESCRIPTION_IDX),
                                        res.getString(COLUMN_ICON_URL_IDX)),
                                CityForecast.Meteorology(
                                        res.getLong(COLUMN_TEMP_IDX),
                                        res.getLong(COLUMN_TEMP_MIN_IDX),
                                        res.getLong(COLUMN_TEMP_MAX_IDX)
                                        )
                                , res.getString(COLUMN_CITY_IDX))
                    }else getCurrentWeather(url)
                }
            }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        with(savedInstanceState){
            populateViews(
                    getString("cityText"),
                    getString("weatherText"),
                    getString("weatherDescText"),
                    getString("medTemp")
            )

            val icon : Bitmap = getParcelable("icon")
            iconView.setImageBitmap(icon)
        }
    }

    private fun getCurrentWeather(url: String){
        RequestManager.put(
                DtoGetRequest(
                        url,
                        CityWeather::class.java,
                        { city ->

                            val weather = city.weather.elementAt(0)

                            populateViews(
                                    city.name,
                                    weather.main,
                                    weather.description,
                                    city.main.temp.toString()
                            )

                            fetchAndShowIcon(weather.icon)
                        },
                        { error ->
                            Toast.makeText(this, R.string.get_current_weather_fail_message, Toast.LENGTH_SHORT).show()
                            throw error
                        }
                )
        )
    }

    private fun populateFromWeather(weather: CityWeather.Weather, meter: CityForecast.Meteorology, city:String){
        populateViews(city,
                weather.main,
                weather.description,
                meter.day.toString()
                )

        fetchAndShowIcon(weather.icon)
    }

    private fun populateViews(cityName: String, _weatherText : String, _weatherDescText : String, _medTemp: String) {
        cityText.text = cityName
        weatherText.text = _weatherText
        weatherDescText.text = _weatherDescText
        medTemp.text= "${resources.getString(R.string.weather_detail_temp)} $_medTemp ${resources.getString(R.string.weather_detail_unit)}"

    }

    private fun fetchAndShowIcon(icon: String?) {
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

    private fun  buildIconQueryUrl(icon: String?): String? {
        return "${resources.getString(R.string.api_image_endpoint)}$icon.png"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        with(outState){
            putString("cityText", cityText.text.toString())
            putString("weatherText", weatherText.text.toString())
            putString("weatherDescText", weatherDescText.text.toString())
            putString("medTemp", medTemp.text.toString())

            val icon = (iconView.drawable as BitmapDrawable).bitmap
            putParcelable("icon", icon) // TODO: this is expensive, find another solution
        }
    }
}
