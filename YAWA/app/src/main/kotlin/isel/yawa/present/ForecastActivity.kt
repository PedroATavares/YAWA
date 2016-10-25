package isel.yawa.present

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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

    private lateinit var requestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        forecastDescs= arrayOf(todaydesc,tomorrowdesc,twodaysdesc,threedaysdesc,fourdaysdesc)
        forecastIcons= arrayOf(todayicon,tomorrowicon,twodaysicon,threedaysicon,fourdaysicon)
       /* forecastDescs.set(2,twodaysdesc)
        forecastDescs.set(3,threedaysdesc)
        forecastDescs.set(4,fourdaysdesc)

        forecastIcons.set(0,todayicon)
        forecastIcons.set(1,tomorrowicon)
        forecastIcons.set(2,twodaysicon)
        forecastIcons.set(3,threedaysicon)
        forecastIcons.set(4,fourdaysicon)
*/
        requestManager = RequestManager(applicationContext)


    }

    override fun onStart() {
        super.onStart()
        val url = intent.extras.getString("url")
        getCityForecast(url)
    }

    private fun getCityForecast(url: String){

        requestManager.put(
                GetForecastRequest(
                        url,
                        { city ->
                            var i=0
                            var k=0
                            while(k<5){
                                val weather = city.list.elementAt(i).weather.elementAt(0)
                                forecastDescs.get(k).text="Description: "+weather.description
                                getImageview(weather.icon,forecastIcons[k])
                                i+=8
                                k++
                            }

                        },
                        { error -> throw error }
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

        requestManager.put(imgRequest)
    }
}
