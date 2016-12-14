package isel.yawa.androidTest

import android.content.ContentValues
import android.net.Uri
import android.test.ProviderTestCase2
import org.junit.*
import isel.yawa.model.content.WeatherProvider;
import junit.framework.Assert
import java.sql.SQLException
import kotlin.test.assertNotEquals
import kotlin.test.expect

// @RunWith(JUnit4::class)
class WeatherProviderTests() : ProviderTestCase2<WeatherProvider>(WeatherProvider::class.java, WeatherProvider.AUTHORITY) {

    companion object{
        var dummyRowID : Int? = null

        val ALL_COLUMNS = with(WeatherProvider){
            arrayOf(COLUMN_CITY,
                    COLUMN_DATE,
                    COLUMN_TEMP,
                    COLUMN_TEMP_MIN,
                    COLUMN_TEMP_MAX,
                    COLUMN_TEMP_KF,
                    COLUMN_HUMIDITY,
                    COLUMN_MAIN,
                    COLUMN_DESCRIPTION,
                    COLUMN_ICON_URL)
        }

        private const val DUMMY_CITY = "Barcelos"
        private const val DUMMY_DATE : Long = 1234

        private const val DUMMY_CITY_B = "Barcelona"
        private const val DUMMY_DATE_B : Long = 5678
    }

    // Runs before each test
    override fun setUp() {
        super.setUp()

        dummyRowID = insertData(
                DUMMY_DATE,
                DUMMY_CITY,
                4.0,
                2.1,
                5.8,
                -1.23,
                95,
                "Cloudy",
                "not cool",
                "trash://not.a.real.domain.io/bullshit"
        )?.lastPathSegment?.toInt()
    }

    private fun insertData(date: Long, city: String?, temp: Double, tempMin: Double, tempMax: Double,
                           tempKf: Double, humidity: Int, main: String?, desc: String?, icon_url: String?): Uri? {
        val data = ContentValues().apply {
            put(WeatherProvider.COLUMN_DATE, date)
            if(city != null) put(WeatherProvider.COLUMN_CITY, city) else putNull(WeatherProvider.COLUMN_CITY);
            put(WeatherProvider.COLUMN_TEMP, temp)
            put(WeatherProvider.COLUMN_TEMP_MIN, tempMin)
            put(WeatherProvider.COLUMN_TEMP_MAX, tempMax)
            put(WeatherProvider.COLUMN_TEMP_KF, tempKf)
            put(WeatherProvider.COLUMN_HUMIDITY, humidity)
            if(city != null) put(WeatherProvider.COLUMN_MAIN, main) else putNull(WeatherProvider.COLUMN_MAIN);
            put(WeatherProvider.COLUMN_DESCRIPTION, desc)
            put(WeatherProvider.COLUMN_ICON_URL, icon_url)
        }

        return mockContentResolver.insert(WeatherProvider.WEATHER_CONTENT_URI, data)
    }

    // @Test(expected = IllegalArgumentException::class) // this doesn't work
    fun test_invalidUri() {
        val uri = Uri.parse("content://${WeatherProvider.AUTHORITY}/lalala")
        try {
            mockContentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
            ).close()
        }catch (e: IllegalArgumentException){
            assertEquals("Uri $uri is not supported", e.message)
            return
        }

        fail()
    }

    @Test
    fun test_validUri() {
        mockContentResolver.query(
                WeatherProvider.WEATHER_CONTENT_URI,
                null,
                null,
                null,
                null
        ).close()
    }

    @Test
    fun test_nullColumnInsertionOnNonNullableColumn() {
        try {
            insertData(
                    0,
                    null, // can not be null
                    1.0,
                    1.0,
                    1.0,
                    1.0,
                    1,
                    null, // can not be null
                    "test",
                    "test"
            )
        }catch(t: Throwable){
            return
        }

        fail()
    }

    @Test
    fun test_insertionOfWeatherInfo() {
        insertData(
                DUMMY_DATE_B,
                DUMMY_CITY_B,
                1.0,
                1.0,
                1.0,
                1.0,
                1,
                "Sunny",
                "cool weather",
                null
        )
    }

    @Test
    fun test_queryOfExistingWeatherInfo() {
        val cur = mockContentResolver.query(
                WeatherProvider.WEATHER_CONTENT_URI,
                arrayOf(WeatherProvider.COLUMN_CITY),
                "${WeatherProvider.COLUMN_CITY} = ?",
                arrayOf(DUMMY_CITY),
                null
        )

        try{
            assertNotNull(cur)
            assertTrue(cur.moveToNext())
            assertEquals(DUMMY_CITY, cur.getString(0))
        }finally {
            cur.close()
        }
    }

    @Test
    fun test_deleteWeatherInfo() {
        val affectedRows = mockContentResolver.delete(WeatherProvider.WEATHER_CONTENT_URI,
                "${WeatherProvider.COLUMN_CITY} = ? AND ${WeatherProvider.COLUMN_DATE} = ?",
                arrayOf(DUMMY_CITY, DUMMY_DATE.toString()))

        assertNotEquals(0, affectedRows)
    }

    @Test
    fun test_updateWeatherInfo() {
        val modifiedValue : String = "lol"
        val affectedRows = mockContentResolver.update(
                WeatherProvider.WEATHER_CONTENT_URI,
                ContentValues().apply { put(WeatherProvider.COLUMN_DESCRIPTION, modifiedValue) },
                "${WeatherProvider.COLUMN_CITY} = ?",
                arrayOf(DUMMY_CITY)
        )

        assertNotEquals(0, affectedRows)

        val cur = mockContentResolver.query(
                WeatherProvider.WEATHER_CONTENT_URI,
                arrayOf(WeatherProvider.COLUMN_DESCRIPTION),
                "${WeatherProvider.COLUMN_CITY} = ?",
                arrayOf(DUMMY_CITY),
                null
        )

        try {
            assertNotNull(cur)
            assertTrue(cur.moveToNext())
            assertEquals(modifiedValue, cur.getString(0))
        }finally {
            // close even if test fails
            cur.close()
        }
    }

    @Test
    fun test_updateWithInvalidValues() {
        try {
            mockContentResolver.update(
                    WeatherProvider.WEATHER_CONTENT_URI,
                    ContentValues().apply {
                        val  nullRef : String? = null
                        put(WeatherProvider.COLUMN_CITY, nullRef)
                    },
                    "${WeatherProvider.COLUMN_CITY} = ?",
                    arrayOf(DUMMY_CITY)
            )
        }catch (t: Throwable){
            return
        }

        fail()
    }

    override fun tearDown() {
        super.tearDown()
        mockContentResolver.delete(WeatherProvider.WEATHER_CONTENT_URI, "ROWID = ?", arrayOf("$dummyRowID"))

        mockContentResolver.delete(WeatherProvider.WEATHER_CONTENT_URI,
                "${WeatherProvider.COLUMN_CITY} = ? AND ${WeatherProvider.COLUMN_DATE} = ?",
                arrayOf(DUMMY_CITY, DUMMY_DATE.toString()))

        mockContentResolver.delete(WeatherProvider.WEATHER_CONTENT_URI,
                "${WeatherProvider.COLUMN_CITY} = ? AND ${WeatherProvider.COLUMN_DATE} = ?",
                arrayOf(DUMMY_CITY_B, DUMMY_DATE_B.toString()))
    }
}