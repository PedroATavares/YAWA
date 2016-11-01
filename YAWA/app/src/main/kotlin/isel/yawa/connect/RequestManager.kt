package isel.yawa.connect

import android.content.Context
import android.net.ConnectivityManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Wrapper object around Volley's RequestQueue.
 * It's purpose is to funnel all requests trough here so the rate at which they are made can be limited
 * and their responses cached.
 */
object RequestManager {

    private lateinit var requestQueue: RequestQueue

    fun setup(applicationContext: Context) {
        requestQueue = Volley.newRequestQueue(applicationContext)
    }

    /**
     * Deposit a request in the Queue
     */
    fun <T> put(request: Request<T>) = requestQueue.add(request)
}

fun deviceHasConnection(ctx: Context) : Boolean {
    val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val ni = cm.activeNetworkInfo

    return ni != null && ni.isConnectedOrConnecting
}