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
                    var temp: Meteorology,
                    var pressure:Long,
                    var humidity:Long) : Parcelable {

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
                writeParcelable(temp,flags)
                writeLong(pressure)
                writeLong(humidity)
            }
        }

        constructor(source: Parcel) : this() {
            source.readList(weather,ClassLoader.getSystemClassLoader())
            temp = source.readParcelable<Meteorology>(ClassLoader.getSystemClassLoader())

        }

        @Suppress("unused")
        constructor() : this(Collections.emptyList(), Meteorology(),0,0)
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

    data class Meteorology(val day: Long,
                           val min: Long,
                           val max : Long): Parcelable{


        companion object {
            @JvmField @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<Meteorology> {
                override fun createFromParcel(source: Parcel) = Meteorology(source)
                override fun newArray(size: Int): Array<Meteorology?> = arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int =0

        override fun writeToParcel(out: Parcel, flags: Int) {
            out.apply {
                writeLong(day)
                writeLong(min)
                writeLong(max)
            }
        }

        constructor(source :Parcel ): this(
                day = source.readLong(),
                min = source.readLong(),
                max = source.readLong()
        )

        constructor() : this(0,0,0)

    }
}