package isel.yawa

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import isel.yawa.connect.GetForecastRequest
import isel.yawa.connect.RequestManager
import kotlinx.android.synthetic.main.activity_weather.*

class WeatherActivity : AppCompatActivity() {
    var image_url = "http://openweathermap.org/img/w/"
    var extension = ".png"

    private lateinit var requestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestManager = RequestManager(applicationContext)

        setContentView(R.layout.activity_weather)
    }

    override fun onStart() {
        super.onStart()

        val url = intent.extras.getString("url")
        getCityWeather(url)
    }

    private fun getCityWeather(url: String){
        requestManager.put(
                GetForecastRequest(
                        url,
                        { city ->
                            cityText.text=city.name
                            val weather = city.weather.elementAt(0)
                            weatherText.text="Weather: "+weather.main
                            weatherDescText.text="Description: "+weather.description
                            getImageview(weather.icon)
                        },
                        { error -> throw error }
                )
        )
    }
    private fun  getImageview(icon: String?) {
        val url = image_url+icon+extension
        val imgRequest = ImageRequest(url,
                Response.Listener<android.graphics.Bitmap> { response -> iconView.setImageBitmap(response) }, 0, 0, iconView.scaleType, Bitmap.Config.ARGB_8888, Response.ErrorListener { error ->
            iconView.setBackgroundColor(Color.parseColor("#ff0000"))
            error.printStackTrace()
        })

        requestManager.put(imgRequest)
    }
}
