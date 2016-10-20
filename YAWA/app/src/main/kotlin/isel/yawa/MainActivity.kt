package isel.yawa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val apikey = "&appid=64ebb6f1a2a7abf9c9a91fedf34426dd"
    val apihttp = "http://api.openweathermap.org/data/2.5/weather?q="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        creditsButton.setOnClickListener{
            this.startActivity(Intent(this, CreditsActivity::class.java));
        }

        editText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val city =v.text.toString()
                val myIntent = Intent(this@MainActivity, WeatherActivity::class.java)
                myIntent.putExtra("url", apihttp+city+apikey)
                this@MainActivity.startActivity(myIntent)
                return@OnEditorActionListener true
            }
            false
        })
    }
}
