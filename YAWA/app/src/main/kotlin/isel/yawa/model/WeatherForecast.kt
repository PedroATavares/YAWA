package isel.yawa.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Represents a list of WeatherInfo objects which in turn represent forecasts for a given day and city
 */
data class WeatherForecast(
    private val forecasts: MutableList<WeatherInfo> = mutableListOf<WeatherInfo>()
) : Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<WeatherForecast> {

            override fun newArray(size: Int): Array<WeatherForecast?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel) = with(source){
                val typedList = mutableListOf<WeatherInfo>()
                readTypedList(typedList, WeatherInfo.CREATOR)

                WeatherForecast(typedList)
            }
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeTypedList<WeatherInfo>(forecasts)
    }

    override fun describeContents() = 0

    fun addWeatherInfo(forecast : WeatherInfo) = forecasts.add(forecast)

    fun  getForecastsThat(predicate: (WeatherInfo) -> Boolean) = forecasts.filter(predicate)

    operator fun  get(i: Int): WeatherInfo? = forecasts[i]

    val cityName: String?
        get() = forecasts.firstOrNull()?.city

}