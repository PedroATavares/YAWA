package isel.yawa.connect

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import isel.yawa.model.CityForecast
import isel.yawa.model.CityWheather

import java.io.IOException

class GetWeatherRequest(url: String, success: (CityWheather) -> Unit, error: (VolleyError) -> Unit) : JsonRequest<CityWheather>(Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<CityWheather> {

        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        try {
            val city = mapper.readValue(String(response.data), CityWheather::class.java)

            return Response.success(city, null)
        } catch (e: IOException) {
            e.printStackTrace()
            return Response.error(VolleyError())
        }
    }
}

