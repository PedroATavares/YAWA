package isel.yawa.connect

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Wrapper class around Volley's RequestQueue.
 * It's purpose is to funnel all requests trough here so the rate at which they are made can be limited
 * and their responses cached.
 * It also guarantees the use of only one Volley Queue at runtime trough the use of the singleton pattern.
 */
object RequestManager {

    private var requestQueue: RequestQueue? = null

    fun setup(applicationContext: Context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }

        requestQueue!!.start()
    }

    /**
     * Deposit a request in the Queue
     */
    fun <T> put(request: Request<T>) = requestQueue?.add(request) ?: throw IllegalStateException("Un-initialized singleton")

}