package isel.yawa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        creditsButton.setOnClickListener{
            startActivity(Intent(this, CreditsActivity::class.java));
        }

        editText.setOnEditorActionListener({ tv, actionId, kev ->
            var enterPressed = actionId == EditorInfo.IME_ACTION_DONE

            if (enterPressed)
                getCurrentWeatherForCity(tv.text.toString())

            return@setOnEditorActionListener enterPressed // kotlin is weird, man
        })
    }

    private fun getCurrentWeatherForCity(city : String) {
        val myIntent = Intent(this, WeatherActivity::class.java)

        var qString : String = buildQueryString(city)
        myIntent.putExtra("url", qString)

        startActivity(myIntent)
    }

    private fun  buildQueryString(city: String): String {
        var api_base = resources.getString(R.string.api_base_uri)
        var api_weather = resources.getString(R.string.api_weather_endpoint)
        var api_key = resources.getString(R.string.api_key)

        return "${api_base}${api_weather}?${api_key}&q=${city}"
    }

}
