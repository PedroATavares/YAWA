package isel.yawa.model.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import isel.yawa.connect.RequestManager
import isel.yawa.connect.buildDailyForecastQueryString
import isel.yawa.model.WeatherForecast
import isel.yawa.model.WeatherInfo
import isel.yawa.model.content.WeatherProvider
import isel.yawa.model.content.fromJsonObject
import isel.yawa.model.content.toContentValues
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ForecastFetchingService : IntentService("forecast-fetcher") {

    companion object {
        const val TAG = "F-SRV"
    }

    override fun onHandleIntent(workIntent: Intent?) {
        if(workIntent == null)
            return

        val cities = workIntent.getStringArrayListExtra(CITIES_EXTRA_KEY)
        cities?.forEach {
            val uri = buildDailyForecastQueryString(it)

            val future = RequestFuture.newFuture<JSONObject>()
            val request = JsonObjectRequest(uri, null, future, future)

            RequestManager.put(request)
            try {
                val response = future.get(MAX_WAIT_TIMEOUT_S, TimeUnit.SECONDS)
                val forecast = WeatherForecast().fromJsonObject(response)
                storeInContentProvider(forecast)

            }catch (tm: TimeoutException){
                Log.e(TAG, "Timeout exceeded when trying to fetch forecast info for $it", tm)
            } catch (t: Throwable) {
                Log.e(TAG, "Failure upon trying to fetch forecast info for $it", t)
                throw t
            }
        }
    }

    private fun  storeInContentProvider(wf: WeatherForecast) {
        wf.forecasts.filter { // avoid duplicating info
            !alreadyExists(it)
        }.forEach {
            contentResolver.insert(WeatherProvider.FORECAST_CONTENT_URI, it.toContentValues())
        }
    }

    private fun  alreadyExists(it: WeatherInfo): Boolean {
        with(WeatherProvider){
            return contentResolver
                    .query(FORECAST_CONTENT_URI, arrayOf(COLUMN_CITY, COLUMN_DATE),
                    "$COLUMN_CITY = ? AND $COLUMN_DATE = ?", arrayOf(it.city, it.date.toString()), null)

                    .use({ it.count != 0})
        }
    }
}