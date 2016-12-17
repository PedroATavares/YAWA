package isel.yawa.connect

import android.content.Context
import android.net.ConnectivityManager
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import isel.yawa.R

/**
 * Wrapper object around Volley's RequestQueue.
 * It's purpose is to funnel all requests trough here so the rate at which they are made can be limited
 * and their responses cached.
 */
object RequestManager {

    private lateinit var requestQueue: RequestQueue
    lateinit var imgLoader : ImageLoader

    fun setup(applicationContext: Context) {
        requestQueue = Volley.newRequestQueue(applicationContext)

        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024)
        val maxCacheSize = (maxMemory/ 8).toInt()
        imgLoader = ImageLoader(requestQueue, LruImageCache(maxCacheSize))
    }

    fun <T> put(request: Request<T>) = requestQueue.add(request)
}

fun deviceHasConnection(ctx: Context) : Boolean {
    val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val ni = cm.activeNetworkInfo

    return ni != null && ni.isConnectedOrConnecting
}

// i love kotlin
private fun  Context.buildQueryString(city: String, endPoint: String): String {
    val api_base = resources.getString(R.string.api_base_uri)
    val api_key = resources.getString(R.string.api_key)
    val api_lang = resources.getString(R.string.api_lang)

    return "$api_base$endPoint?$api_key&q=$city&$api_lang&units=metric"
}

fun Context.buildWeatherQueryString(city : String) =
        buildQueryString(city, resources.getString(R.string.api_weather_endpoint))

fun Context.buildForecastQueryString(city : String) =
        buildQueryString(city, resources.getString(R.string.api_forecast_endpoint))

fun Context.buildDailyForecastQueryString(city : String) =
        buildQueryString(city, resources.getString(R.string.api_daily_forecast_endpoint))+"&cnt=5"