package isel.yawa.present

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import isel.yawa.R
import isel.yawa.connect.buildDailyForecastQueryString
import kotlinx.android.synthetic.main.activity_main.*

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
        if (radioButton_day5.isChecked) {
            val urlEncodedCity = Uri.encode(city)
            getForecastForCity(urlEncodedCity)

            return
        }

        if(today_button.isChecked)
            getCurrentWeatherForCity(city)
    }

    private fun beginActivityWithExtra(clazz : Class<out Activity>, city: String){
        startActivity(Intent(this, clazz).apply {
            putExtra("city", city)
        })
    }

    private fun getCurrentWeatherForCity(city : String) {
        beginActivityWithExtra(WeatherActivity::class.java, city)
    }

    private fun getForecastForCity(city : String) {
        startActivity(Intent(this, ForecastActivity::class.java).apply {
            val qString : String = buildDailyForecastQueryString(city)
            putExtra("url", qString)
        })
    }
}
