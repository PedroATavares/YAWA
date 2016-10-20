package isel.pdm.yawa

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

import java.io.IOException

/**
 * Created by luism on 16/10/2016.
 */

class GetForecastRequest(url: String, success: (CityForecast) -> Unit, error: (VolleyError) -> Unit) : JsonRequest<CityForecast>(Request.Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<CityForecast> {

        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        try {
            val city = mapper.readValue(String(response.data), CityForecast::class.java)
            return Response.success(city, null)
        } catch (e: IOException) {
            e.printStackTrace()
            return Response.error(VolleyError())
        }
    }
}

