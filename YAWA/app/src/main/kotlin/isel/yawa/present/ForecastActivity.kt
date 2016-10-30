package isel.yawa.present

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
import isel.yawa.connect.GetForecastRequest
import isel.yawa.connect.RequestManager
import kotlinx.android.synthetic.main.activity_forecast.*

class ForecastActivity : AppCompatActivity() {

    private lateinit  var forecastSlots : Array<Pair<TextView, ImageView>>

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

        if(savedInstanceState == null) {
            val url = intent.extras.getString("url")
            getCityForecast(url)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        with(savedInstanceState){
            cityTextForecast.text = getString("cityTextForecast")

            forecastSlots.forEachIndexed { i, pair ->
                pair.first.text = getString("textView$i")

                pair.second.setImageBitmap(getParcelable("icon$i"))
            }
        }
    }

    private fun getCityForecast(url: String){
        RequestManager.put(
                GetForecastRequest(
                        url,
                        { city ->
                            cityTextForecast.text = city.city.name

                            forecastSlots.forEachIndexed { i, pair ->
                                val weather = city.list.elementAt(i * 8).weather.elementAt(0)

                                pair.first.text = "${getString(R.string.forecast_description)} ${weather.description}"

                                fetchAndShowIcon(weather.icon, pair.second)
                            }

                        },
                        { error ->
                            Toast.makeText(this, R.string.get_forecast_fail_message, Toast.LENGTH_SHORT).show()
                            throw error
                        }
                )
        )
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

            forecastSlots.forEachIndexed { i, pair ->
                putString("textView$i", pair.first.text.toString())

                // TODO: change way to pass icons, very poor efficiency
                // how to store icon efficiently?
                var icon = (pair.second.drawable as BitmapDrawable).bitmap
                putParcelable("icon$i", icon)
            }
        }
    }
}
