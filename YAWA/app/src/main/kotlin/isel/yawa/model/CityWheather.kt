package isel.yawa.model;

import android.os.Parcel
import android.os.Parcelable
import java.util.*


data class CityWheather(val name:String, val weather: Collection<Weather>, val main : Meteorology){
    constructor() : this("", Collections.emptyList(), Meteorology())

    data class Weather(val main: String,
                       val description: String,
                       val icon : String) : Parcelable{


        companion object {
            /** Factory of MovieCollectionqç instances */
            @JvmField @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<Weather> {
                override fun createFromParcel(source: Parcel) = Weather(source)
                override fun newArray(size: Int): Array<Weather?> = arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int =0

       override fun writeToParcel(out: Parcel, flags: Int) {
           out.apply {
               writeString(main)
               writeString(description)
               writeString(icon)
           }
        }


        constructor() : this("", "","")

        constructor(source :Parcel ): this(
                main = source.readString(),
                description = source.readString(),
                icon = source.readString()
        )

    }

    data class Meteorology(val temp: Long,
                       val temp_min: Long,
                       val temp_max : Long,
                       val humidity : Long,
                       val temp_kf : Long): Parcelable{


        companion object {
            /** Factory of MovieCollectionqç instances */
            @JvmField @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<Meteorology> {
                override fun createFromParcel(source: Parcel) = Meteorology(source)
                override fun newArray(size: Int): Array<Meteorology?> = arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int =0

        override fun writeToParcel(out: Parcel, flags: Int) {
            out.apply {
                writeLong(temp)
                writeLong(temp_min)
                writeLong(temp_max)
                writeLong(humidity)
                writeLong(temp_kf)
            }
        }

        constructor(source :Parcel ): this(
                temp = source.readLong(),
                temp_min = source.readLong(),
                temp_max = source.readLong(),
                humidity = source.readLong(),
                temp_kf = source.readLong()
        )

        constructor() : this(0,0,0,0,0)

    }

}

