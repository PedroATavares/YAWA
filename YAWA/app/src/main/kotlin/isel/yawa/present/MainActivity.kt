package isel.yawa.present

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import isel.yawa.R
import isel.yawa.connect.buildDailyForecastQueryString
import isel.yawa.connect.buildForecastQueryString
import isel.yawa.connect.buildWeatherQueryString
import isel.yawa.connect.deviceHasConnection
import isel.yawa.model.services.CITIES_EXTRA_KEY
import isel.yawa.model.services.CurrentWeatherService
import isel.yawa.model.services.ForecastFetchingService
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        creditsButton.setOnClickListener{
            startActivity(Intent(this, CreditsActivity::class.java));
        }

        settings_button.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java));
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
            getForecastForCity(urlEncodedCity)
        else if(today_button.isChecked)
            getCurrentWeatherForCity(urlEncodedCity)
    }

    private fun getCurrentWeatherForCity(city : String) {
        startActivity(Intent(this, WeatherActivity::class.java).apply {
            val qString : String = buildWeatherQueryString(city)
            putExtra("url", qString)
        })
    }

    private fun getForecastForCity(city : String) {
        startActivity(Intent(this, ForecastActivity::class.java).apply {
            val qString : String = buildDailyForecastQueryString(city)
            putExtra("url", qString)
        })
    }
}
