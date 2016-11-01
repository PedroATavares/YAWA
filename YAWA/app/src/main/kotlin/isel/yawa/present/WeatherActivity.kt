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
import isel.yawa.connect.GetWeatherRequest
import isel.yawa.connect.RequestManager
import isel.yawa.model.CityWheather
import kotlinx.android.synthetic.main.activity_weather.*

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_weather)

        val weather = intent.extras.getParcelable<CityWheather.Weather>("weather")
        val meter= intent.extras.getParcelable<CityWheather.Meteorology>("meter")

        if(weather !=null && meter!= null)
        {
            populateFromWeather(weather,meter)
        }else
            if(savedInstanceState == null){ // shouldnt have to do this check here
                val url = intent.extras.getString("url")
                getCurrentWeather(url)
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
                GetWeatherRequest(
                        url,
                        { city ->

                            val weather = city.weather.elementAt(0)

                            populateViews(
                                    city.name,
                                    weather.main,
                                    weather.description,
                                    "Temperature: " + city.main.temp.toString()+ " ºC"
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

    private fun populateFromWeather(weather:CityWheather.Weather, meter: CityWheather.Meteorology){
        populateViews(intent.extras.getString("city"),
                weather.main,
                weather.description,
                "Temperature: " +meter.temp.toString() + " ºC"
                )

        fetchAndShowIcon(weather.icon)
    }

    private fun populateViews(cityName: String, _weatherText : String, _weatherDescText : String, _medTemp: String) {
        cityText.text = cityName
        weatherText.text = _weatherText
        weatherDescText.text = _weatherDescText
        medTemp.text= _medTemp

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

            var icon = (iconView.drawable as BitmapDrawable).bitmap
            putParcelable("icon", icon) // TODO: this is expensive, find another solution
        }
    }
}
