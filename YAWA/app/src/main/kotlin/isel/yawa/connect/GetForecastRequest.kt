package isel.yawa.connect

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import isel.yawa.model.CityForecast

import java.io.IOException

class GetForecastRequest(url: String, success: (CityForecast) -> Unit, error: (VolleyError) -> Unit) : JsonRequest<CityForecast>(Method.GET, url, "", success, error) {

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

