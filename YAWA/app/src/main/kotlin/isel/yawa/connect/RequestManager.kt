package isel.yawa.connect

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Wrapper class around Volley's RequestQueue.
 * It's purpose is to limit request rate and cache responses
 */
class RequestManager {

    companion object Queue {
        lateinit var requestQueue : RequestQueue
    }

    constructor(applicationContext : Context) {
        requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.start()
    }

    fun <T> put(request : Request<T>) = requestQueue.add(request)

}