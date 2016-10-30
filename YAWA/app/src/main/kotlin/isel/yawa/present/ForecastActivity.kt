package isel.yawa.present

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import isel.yawa.R
import isel.yawa.connect.GetForecastRequest
import isel.yawa.connect.RequestManager
import kotlinx.android.synthetic.main.activity_forecast.*
import kotlinx.android.synthetic.main.activity_forecast.view.*
import kotlinx.android.synthetic.main.activity_weather.*

class ForecastActivity : AppCompatActivity() {

    var image_url = "http://openweathermap.org/img/w/"
    var extension = ".png"

    var forecastDescs : Array<TextView> = emptyArray()
    var forecastIcons :  Array<ImageView> = emptyArray()
    //var forecastDescs= arrayOf(todaydesc,tomorrowdesc,twodaysdesc,threedaysdesc,fourdaysdesc)
    //var forecastIcons= arrayOf(todayicon,tomorrowicon,twodaysicon,threedaysicon,fourdaysicon)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        forecastDescs= arrayOf(todaydesc,tomorrowdesc,twodaysdesc,threedaysdesc,fourdaysdesc)
        forecastIcons= arrayOf(todayicon,tomorrowicon,twodaysicon,threedaysicon,fourdaysicon)

    }

    override fun onStart() {
        super.onStart()
        val url = intent.extras.getString("url")
        getCityForecast(url)
    }

    private fun getCityForecast(url: String){
        RequestManager.put(
                GetForecastRequest(
                        url,
                        { city ->
                            var i=0
                            var k=0
                            cityTextForecast.text = city.city.name
                            while(k<5){
                                val weather = city.list.elementAt(i).weather.elementAt(0)
                                forecastDescs.get(k).text="Description: "+weather.description
                                getImageview(weather.icon,forecastIcons[k])
                                i+=8
                                k++
                            }

                        },
                        { error ->
                            Toast.makeText(this, R.string.get_forecast_fail_message, Toast.LENGTH_SHORT).show()
                            throw error
                        }
                )
        )
    }
    private fun  getImageview(iconStr: String?, icon: ImageView) {
        val url = image_url+ iconStr +extension
        val imgRequest = ImageRequest(url,
                Response.Listener<Bitmap> { response -> icon.setImageBitmap(response) }, 0, 0, icon.scaleType, Bitmap.Config.ARGB_8888, Response.ErrorListener { error ->
            icon.setBackgroundColor(Color.parseColor("#ff0000"))
            error.printStackTrace()
        })

        RequestManager.put(imgRequest)
    }
}
