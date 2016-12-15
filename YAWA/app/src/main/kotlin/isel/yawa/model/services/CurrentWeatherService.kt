package isel.yawa.model.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import isel.yawa.connect.RequestManager
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.JsonObjectRequest
import isel.yawa.connect.buildWeatherQueryString
import isel.yawa.model.WeatherInfo
import isel.yawa.model.content.WeatherProvider
import isel.yawa.model.content.fromJsonObject
import isel.yawa.model.content.toContentValues
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Fetches current weather info from API and stores said info in a content provider
 */
class CurrentWeatherService : IntentService("fetch-thread"){

    companion object{
        const val CITY_EXTRA_KEY = "city"
        const val MAX_WAIT_TIMEOUT_S : Long = 15
    }

    /**
     * This method is not running on this service's main thread so any CPU intensive work is safe to do.
     * For that reason there is no need to use Volley's async API and instead the sync API was chosen
     */
    override fun onHandleIntent(workIntent: Intent?) {
        if(workIntent == null)
            return

        val city = workIntent.getStringExtra(CITY_EXTRA_KEY)
        val url = buildWeatherQueryString(city)

        val future = RequestFuture.newFuture<JSONObject>()
        val request = JsonObjectRequest(url, null, future, future)

        RequestManager.put(request)
        try {
            val response = future.get(MAX_WAIT_TIMEOUT_S, TimeUnit.SECONDS)

            val wInfo = WeatherInfo().fromJsonObject(response)
            
            storeInContentProvider(wInfo)
        } catch (t: Throwable) {
            Log.e("SRV", "Failure upon trying to fetch current weather info for $city", t)
            throw t;
        }
    }

    private fun storeInContentProvider(weatherInfo : WeatherInfo){
        val mappedWeatherInfo = weatherInfo.toContentValues()

        contentResolver.insert(WeatherProvider.WEATHER_CONTENT_URI, mappedWeatherInfo)
    }
}
