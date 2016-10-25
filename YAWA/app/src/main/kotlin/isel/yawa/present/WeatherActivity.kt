package isel.yawa.present

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import isel.yawa.R
import isel.yawa.connect.GetWeatherRequest
import isel.yawa.connect.RequestManager
import kotlinx.android.synthetic.main.activity_weather.*

class WeatherActivity : AppCompatActivity() {
    var image_url = "http://openweathermap.org/img/w/"
    var extension = ".png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_weather)
    }

    override fun onStart() {
        super.onStart()

        val url = intent.extras.getString("url")
        getCityWeather(url)
    }

    private fun getCityWeather(url: String){
        RequestManager.put(
                GetWeatherRequest(
                        url,
                        { city ->
                            cityText.text=city.name
                            val weather = city.weather.elementAt(0)
                            weatherText.text="Weather: "+weather.main
                            weatherDescText.text="Description: "+weather.description

                            fetchAndShowIcon(weather.icon)
                        },
                        { error -> throw error }
                )
        )
    }

    private fun fetchAndShowIcon(icon: String?) {
        val url = image_url+icon+extension

        val imgRequest = ImageRequest(url,
                Response.Listener<Bitmap> { bitmap -> iconView.setImageBitmap(bitmap) },
                0,
                0,
                iconView.scaleType,
                Bitmap.Config.ARGB_8888,
                Response.ErrorListener { error ->
                    iconView.setBackgroundColor(Color.parseColor("#ff0000"))
                    Toast.makeText(this, "Failed to fetch weather icon", Toast.LENGTH_SHORT).show()
                }
        )

        RequestManager.put(imgRequest)
    }
}
