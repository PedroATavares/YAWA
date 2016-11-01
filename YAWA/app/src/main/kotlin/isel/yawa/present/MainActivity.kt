package isel.yawa.present

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import isel.yawa.R
import isel.yawa.connect.deviceHasConnection
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        creditsButton.setOnClickListener{
            startActivity(Intent(this, CreditsActivity::class.java));
        }

        editText.setOnEditorActionListener({ tv, actionId, kev ->
            val enterPressed = actionId == EditorInfo.IME_ACTION_DONE

            if (enterPressed){
                val city = tv.text.toString()
                doWeatherQuery(city)
            }

            return@setOnEditorActionListener enterPressed // kotlin is weird, man
        })

        go_button.setOnClickListener {
            val city = editText.text.toString()
            doWeatherQuery(city)
        }
    }

    private fun  doWeatherQuery(city: String) {
        if(!deviceHasConnection(this)){
            Toast.makeText(this, R.string.no_connection_message, Toast.LENGTH_LONG).show()
            return
        }

        val urlEncodedCity = Uri.encode(city)

        if (radioButton_day5.isChecked)
            getCurrentForecastForCity(urlEncodedCity)
        else if(today_button.isChecked)
            getCurrentWeatherForCity(urlEncodedCity)
    }

    private fun getCurrentWeatherForCity(city : String) {
        val myIntent = Intent(this, WeatherActivity::class.java)

        var qString : String = buildQueryString(city,resources.getString(R.string.api_weather_endpoint))
        myIntent.putExtra("url", qString)

        startActivity(myIntent)
    }

    private fun getCurrentForecastForCity(city : String) {
        val myIntent = Intent(this, ForecastActivity::class.java)

        var qString : String = buildQueryString(city,resources.getString(R.string.api_forecast_endpoint));
        myIntent.putExtra("url", qString)

        startActivity(myIntent)
    }

    private fun  buildQueryString(city: String,endPoint: String): String {
        val api_base = resources.getString(R.string.api_base_uri)
        val api_key = resources.getString(R.string.api_key)
        val api_lang = resources.getString(R.string.api_lang)

        return "$api_base$endPoint?$api_key&q=$city&$api_lang&units=metric"
    }

}
