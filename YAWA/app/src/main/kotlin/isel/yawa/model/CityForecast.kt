package isel.yawa.model

import android.os.Parcel
import android.os.Parcelable
import java.util.Collections


data class CityForecast(var city:City, val list: kotlin.collections.List<List>) : Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<CityForecast> {
            override fun createFromParcel(source: Parcel) = CityForecast(source)
            override fun newArray(size: Int): Array<CityForecast?> = arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.apply {
            writeParcelable(city, flags)
            writeList(list)
        }
    }

    constructor(source: Parcel) : this() {
        city = source.readParcelable(ClassLoader.getSystemClassLoader())
        source.readList(list, ClassLoader.getSystemClassLoader())

    }

    @Suppress("unused")
    constructor() : this(City(), Collections.emptyList())


    data class List(val weather: kotlin.collections.List<CityWeather.Weather>,
                    var main: CityWeather.Meteorology) : Parcelable {

        companion object {
            @JvmField @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<List> {
                override fun createFromParcel(source: Parcel) = List(source)
                override fun newArray(size: Int): Array<List?> = arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int = 0

        override fun writeToParcel(out: Parcel, flags: Int) {
            out.apply {
                writeList(weather)
                writeParcelable(main,flags)
            }
        }

        constructor(source: Parcel) : this() {
            source.readList(weather,ClassLoader.getSystemClassLoader())
            main = source.readParcelable<CityWeather.Meteorology>(ClassLoader.getSystemClassLoader())

        }

        @Suppress("unused")
        constructor() : this(Collections.emptyList(), CityWeather.Meteorology())
    }

    data class City(val name: String) : Parcelable {

        companion object {
            @JvmField @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<City> {
                override fun createFromParcel(source: Parcel) = City(source)
                override fun newArray(size: Int): Array<City?> = arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int = 0

        override fun writeToParcel(out: Parcel, flags: Int) {
            out.apply {
                writeString(name)
            }
        }

        constructor(source: Parcel) : this(
                name = source.readString()
        )


        constructor() : this("")
    }
}