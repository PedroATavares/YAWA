package isel.pdm.demos.mymoviedb.models


/**
 * Class whose instances represent movie information obtained from the remote API.
 */
data class MovieDto(val adult: Boolean, val title: String, val id: Long) {
    private constructor() : this(false, "", 0)
}
