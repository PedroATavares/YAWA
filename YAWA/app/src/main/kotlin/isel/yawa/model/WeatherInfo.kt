package isel.yawa.model

import android.os.Parcel
import android.os.Parcelable

data class WeatherInfo(
        var date: Long?,
        var city: String?,
        var country: String?,
        var main: String?,
        var description: String?,
        var ambientInfo : AmbientInfo?,
        var icon_url: String?
) : Parcelable{

    constructor() : this(null, null, null, null, null, null, null)

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<WeatherInfo> {

            override fun newArray(size: Int): Array<WeatherInfo?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel) = with(source){
                WeatherInfo(
                        date = readLong(),
                        city = readString(),
                        country = readString(),
                        main = readString(),
                        description = readString(),
                        ambientInfo = readParcelable(AmbientInfo::class.java.classLoader),
                        icon_url = readString()
                )
            }
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeLong(date!!)
            writeString(city)
            writeString(country)
            writeString(main)
            writeString(description)
            writeParcelable(ambientInfo, flags)
            writeString(icon_url)
        }
    }

    override fun describeContents() = 0
}