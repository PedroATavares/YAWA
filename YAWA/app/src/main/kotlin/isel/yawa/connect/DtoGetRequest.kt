package isel.yawa.connect

import com.android.volley.Cache
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Generic implementation of a custom HTTP GET request.
 *
 * @param DTO The concrete type of DTO contained in the payload of the API response
 * @property
 */
class DtoGetRequest<DTO>(
        url: String,
        private val dtoType: Class<DTO>,
        success: (DTO) -> Unit,
        error: (VolleyError) -> Unit)

: JsonRequest<DTO>(Method.GET, url, "", success, error) {

    private val ONE_HOUR = TimeUnit.HOURS.toMillis(1)

    companion object {
        val mapper: ObjectMapper = ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<DTO> {
        try {
            val dto = mapper.readValue(response.data, dtoType)
            // TODO: handle error responses better {message: "some msg", code:404}
            // this will make requests expire in 1hr
            return Response.success(dto, buildExpirableEntry(response))
        } catch (e: IOException) {
            e.printStackTrace()
            return Response.error(VolleyError())
        }
    }

    /**
     * By default Volley's cache entries use the server's HTTP response headers Cache-Control, Expires and maxAge values as expiration date values
     * This method creates a new cache entry which will override the values in those headers to ones best fit to the application's caching policy
     */
    private fun  buildExpirableEntry(response: NetworkResponse): Cache.Entry {
        val entry = Cache.Entry()
        with(entry){
            responseHeaders = response.headers
            data = response.data

            val now = System.currentTimeMillis()
            ttl = now + ONE_HOUR
            softTtl = ttl
        }

        return entry
    }
}