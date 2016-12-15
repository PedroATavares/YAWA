package isel.yawa.androidTest

import android.os.Parcel
import isel.yawa.model.AmbientInfo
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AmbientInfoTests {

    companion object {
        const val DUMMY_TEMP = 2.0
        const val DUMMY_TEMP_MIN = 1.0
        const val DUMMY_TEMP_MAX = 3.0
        const val DUMMY_PRESSURE = 29
        const val DUMMY_HUMIDITY = 92

        var dummy: AmbientInfo? = null

    }
    @Before
    fun setUp(){
        dummy = AmbientInfo(
                DUMMY_TEMP,
                DUMMY_TEMP_MIN,
                DUMMY_TEMP_MAX,
                DUMMY_PRESSURE,
                DUMMY_HUMIDITY
        )
    }

    @After
    fun tearDown(){
        dummy = null
    }

    @Test
    fun testCorrectInstantiation(){
        val TEMP = 2.0
        val TEMP_MIN = 1.0
        val TEMP_MAX = 3.0
        val PRESSURE = 29
        val HUMIDITY = 92

        val _new = AmbientInfo(
                TEMP,
                TEMP_MIN,
                TEMP_MAX,
                PRESSURE,
                HUMIDITY
        )

        with(_new){
            assertEquals(TEMP, temp)
            assertEquals(TEMP_MIN, tempMin)
            assertEquals(TEMP_MAX, tempMax)
            assertEquals(PRESSURE, pressure)
            assertEquals(HUMIDITY, humidity)
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

        val createdFromParcel = AmbientInfo.CREATOR.createFromParcel(parcel)
        with(createdFromParcel){
            assertEquals(DUMMY_TEMP, temp)
            assertEquals(DUMMY_TEMP_MIN, tempMin)
            assertEquals(DUMMY_TEMP_MAX, tempMax)
            assertEquals(DUMMY_PRESSURE, pressure)
            assertEquals(DUMMY_HUMIDITY, humidity)
        }
    }
}