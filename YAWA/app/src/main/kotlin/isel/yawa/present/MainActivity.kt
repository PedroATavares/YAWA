package isel.yawa.present

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import isel.yawa.Application
import isel.yawa.R
import isel.yawa.model.services.NotificationEmitter
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.content.ContextCompat
import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.support.v4.app.ActivityCompat


class MainActivity : AppCompatActivity() {

    val LOCATION_REQUEST_CODE =1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        creditsButton.setOnClickListener{
            startActivity(Intent(this, CreditsActivity::class.java));
        }

        settings_button.setOnClickListener{

            startActivity(Intent(this, PreferencesActivity::class.java))

        }

        local.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    ActivityCompat.requestPermissions(this,
                            arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_REQUEST_CODE)

            }else {
                val locManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                var lat:Double;
                var long:Double;
                if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    val loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(loc!=null) {
                        lat = loc.latitude
                        long = loc.longitude

                        startWeatherActivity(lat, long)
                    }else{
                        makeToast( resources.getString(R.string.no_last_pos))
                    }
                }else{
                    if(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                        val loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if(loc!=null) {
                            lat = loc.latitude
                            long = loc.longitude

                            startWeatherActivity(lat, long)
                        }else{
                            makeToast( resources.getString(R.string.no_last_pos))
                        }
                    }else{
                        makeToast( resources.getString(R.string.pos_not_found))
                    }
                }
            }
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

        if(editText.text.length==0){
            makeToast(resources.getString(R.string.provide_text))
        }

        if (radioButton_day5.isChecked) {
            showForecastForCity(city)
            return
        }

        if(today_button.isChecked)
            showCurrentWeatherForCity(city)
    }

    private fun showForecastForCity(city : String) {
        beginActivityWithExtra(ForecastActivity::class.java, city)
    }

    private fun showCurrentWeatherForCity(city : String) {
        beginActivityWithExtra(WeatherActivity::class.java, city)
    }

    private fun beginActivityWithExtra(clazz : Class<out Activity>, city: String){
        startActivity(Intent(this, clazz).apply {
            putExtra(Application.CITY_KEY, city)
        })
    }
    private fun makeToast(message:String){
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, message, duration)
        toast.show()
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val locManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                    val prov = locManager.getProviders(true)

                    if (prov.size != 0) {
                        val loc = locManager.getLastKnownLocation(prov[0])

                        val lat = loc.latitude
                        val long = loc.longitude

                        startWeatherActivity(lat, long)
                    } else {
                        makeToast(resources.getString(R.string.pos_not_found))
                    }


                } else{
                    makeToast(resources.getString(R.string.permission_not_granted))
                }
                return
            }
        }
    }

    private fun startWeatherActivity(lat: Double, long: Double) {
        startActivity(Intent(this, WeatherActivity::class.java).apply {
            putExtra("latitude", lat)
            putExtra("longitude", long)
        })
    }
}
