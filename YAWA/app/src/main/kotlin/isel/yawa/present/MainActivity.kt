package isel.yawa.present

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import isel.yawa.R
import isel.yawa.connect.RequestManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RequestManager.setup(applicationContext) // ideally this would be in Application class

        creditsButton.setOnClickListener{
            startActivity(Intent(this, CreditsActivity::class.java));
        }

        editText.setOnEditorActionListener({ tv, actionId, kev ->
            var enterPressed = actionId == EditorInfo.IME_ACTION_DONE

            if (enterPressed && radioButton_day5.isChecked)
                getCurrentForecastForCity(tv.text.toString())
            else
                if (enterPressed && today_button.isChecked)
                    getCurrentWeatherForCity(tv.text.toString())

            return@setOnEditorActionListener enterPressed // kotlin is weird, man
        })

        go_button.setOnClickListener {
            if (radioButton_day5.isChecked)
                getCurrentForecastForCity(editText.text.toString())
            else
                if (today_button.isChecked)
                    getCurrentWeatherForCity(editText.text.toString())
        }
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
        var api_base = resources.getString(R.string.api_base_uri)
        var api_key = resources.getString(R.string.api_key)

        return "${api_base}${endPoint}?${api_key}&q=${city}"
    }

}
