package isel.yawa.model

import android.os.Parcel
import android.os.Parcelable

data class AmbientInfo(
        var temp: Double?,
        var tempMin: Double?,
        var tempMax: Double?,
        var pressure: Int?,
        var humidity: Int?
) : Parcelable{

    constructor() : this(null, null, null, null, null)

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<AmbientInfo> {

            override fun newArray(size: Int): Array<AmbientInfo?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel) = with(source){
                AmbientInfo(
                        temp = readDouble(),
                        tempMin = readDouble(),
                        tempMax = readDouble(),
                        pressure = readInt(),
                        humidity = readInt()
                )
            }
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeDouble(temp!!)
            writeDouble(tempMin!!)
            writeDouble(tempMax!!)
            writeInt(pressure!!)
            writeInt(humidity!!)
        }
    }

    override fun describeContents(): Int = 0

}