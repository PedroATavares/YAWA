package isel.yawa.androidTest

import android.os.Parcel
import isel.yawa.model.AmbientInfo
import isel.yawa.model.WeatherInfo
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class WeatherInfoTests {

    companion object {

        const val DUMMY_DATE: Long = 1234
        const val DUMMY_CITY = "Barcelona"
        const val DUMMY_COUNTRY = "Korea"
        const val DUMMY_MAIN = "Sunny"
        const val DUMMY_DESCRIPTION = "abcdefghijklmnopqrstuvxyz"
        const val DUMMY_ICON_URL = "lol"

        const val DUMMY_TEMP = 2.0
        const val DUMMY_TEMP_MIN = 1.0
        const val DUMMY_TEMP_MAX = 3.0
        const val DUMMY_PRESSURE = 29
        const val DUMMY_HUMIDITY = 92

        val dummyAmbientInfo =
                AmbientInfo(DUMMY_TEMP, DUMMY_TEMP_MIN, DUMMY_TEMP_MAX, DUMMY_PRESSURE, DUMMY_HUMIDITY)

        var dummy: WeatherInfo? = null

    }

    @Before
    fun setUp(){
        dummy = WeatherInfo(
                DUMMY_DATE,
                DUMMY_CITY,
                DUMMY_COUNTRY,
                DUMMY_MAIN,
                DUMMY_DESCRIPTION,
                dummyAmbientInfo,
                DUMMY_ICON_URL
        )
    }

    @After
    fun tearDown(){
        dummy = null
    }

    @Test
    fun testCorrectInstantiation(){
        val DATE: Long = 5555
        val CITY = "Minsk"
        val COUNTRY = "Siberia"
        val MAIN = "Snowy"
        val DESCRIPTION = "..."
        val ICON_URL = "..."

        val _new = WeatherInfo(
                DATE,
                CITY,
                COUNTRY,
                MAIN,
                DESCRIPTION,
                dummyAmbientInfo,
                ICON_URL
        )

        with(_new){
            assertEquals(DATE, date)
            assertEquals(CITY, city)
            assertEquals(COUNTRY, country)
            assertEquals(MAIN, main)
            assertEquals(DESCRIPTION, description)
            assertEquals(dummyAmbientInfo, ambientInfo)
            assertEquals(ICON_URL, icon_url)
        }
    }

    @Test
    fun testParcel(){
        val parcel = Parcel.obtain()

        dummy?.writeToParcel(parcel, 0)
    }

    @Test
    fun testUnparcel(){
        val parcel = Parcel.obtain()
        dummy?.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        val createdFromParcel = WeatherInfo.CREATOR.createFromParcel(parcel)
        with(createdFromParcel){
            assertEquals(DUMMY_DATE, date)
            assertEquals(DUMMY_CITY, city)
            assertEquals(DUMMY_COUNTRY, country)
            assertEquals(DUMMY_MAIN, main)
            assertEquals(DUMMY_DESCRIPTION, description)

            // too lazy to implement AmbientInfo::equals
            with(ambientInfo!!){
                assertEquals(DUMMY_TEMP, temp)
                assertEquals(DUMMY_TEMP_MIN, tempMin)
                assertEquals(DUMMY_TEMP_MAX, tempMax)
                assertEquals(DUMMY_PRESSURE, pressure)
                assertEquals(DUMMY_HUMIDITY, humidity)
            }

            assertEquals(DUMMY_ICON_URL, icon_url)
        }
    }
}