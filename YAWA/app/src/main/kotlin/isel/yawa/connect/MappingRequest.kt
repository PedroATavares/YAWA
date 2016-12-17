package isel.yawa.connect

import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

/**
 * Class representing a JSON HTTP request.
 * Automaticly parses inbound responses and transforms them to a JSONObject which is then passed to the
 * mapper function provided by the user.
 * This allows for a larger degree of control over how to map JSON network responses to domain objects
 */
class MappingRequest<DTO>(
        url: String?,
        mapper: (JSONObject) -> DTO,
        onSuccess: (DTO) -> Unit,
        onError: (VolleyError) -> Unit

): JsonObjectRequest(
    url,
    null,
    { json : JSONObject ->
        val mapped = mapper(json)
        onSuccess(mapped)
    },
    onError)