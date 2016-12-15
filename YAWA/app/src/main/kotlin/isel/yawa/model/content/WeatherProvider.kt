package isel.yawa.model.content

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.support.annotation.MainThread

/**
 * The provider for the local replica containing the weather information.
 * Sustained on an SQLite database with only one table.
 */
class WeatherProvider : ContentProvider(){

    companion object {
        private const val DATABASE_NAME: String = "WEATHER_INFO_DATABASE"
        private const val WEATHER_TABLE_NAME = "WeatherInfo"
        private const val WEATHER_TABLE_CODE: Int = 1
        private const val ROW_CONTENT_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/movie"

        // Public interface
        const val WEATHER_TABLE_PATH: String = "weather"
        const val AUTHORITY: String = "isel.yawa"
        val WEATHER_CONTENT_URI : Uri = Uri.parse("content://$AUTHORITY/$WEATHER_TABLE_PATH")

        // Column definitions (name, index)
        val COLUMN_DATE         = "DATE"
        val COLUMN_CITY         = "CITY"
        val COLUMN_COUNTRY      = "COUNTRY"
        val COLUMN_TEMP         = "T"
        val COLUMN_TEMP_MIN     = "T_MIN"
        val COLUMN_TEMP_MAX     = "T_MAX"
        val COLUMN_PRESSURE     = "PRESSURE"
        val COLUMN_HUMIDITY     = "HUMIDITY"
        val COLUMN_MAIN         = "MAIN"
        val COLUMN_DESCRIPTION  = "DESCRIPTION"
        val COLUMN_ICON_URL     = "ICON_URL"

        val COLUMN_DATE_IDX        = 0
        val COLUMN_CITY_IDX        = 1
        val COLUMN_COUNTRY_IDX     = 2
        val COLUMN_TEMP_IDX        = 3
        val COLUMN_TEMP_MIN_IDX    = 4
        val COLUMN_TEMP_MAX_IDX    = 5
        val COLUMN_PRESSURE_IDX    = 6
        val COLUMN_HUMIDITY_IDX    = 7
        val COLUMN_MAIN_IDX        = 8
        val COLUMN_DESCRIPTION_IDX = 9
        val COLUMN_ICON_URL_IDX    = 10
    }

    // Helper instance for easier manipulation of the actual database
    @Volatile private lateinit var dbHelper: WeatherInfoDbHelper

    // Matcher instance for matching inbound URI's
    @Volatile private lateinit var uriMatcher: UriMatcher

    /**
     * The associated helper for DB accesses and migration.
     */
    private inner class WeatherInfoDbHelper(version: Int = 1, dbName: String = DATABASE_NAME) :
            SQLiteOpenHelper(this.context, dbName, null, version) {

        private fun createTable(db: SQLiteDatabase?) {
            val CREATE_CMD = "CREATE TABLE $WEATHER_TABLE_NAME ( " +
                    "$COLUMN_DATE INTEGER NOT NULL , " + // Unix time stamp
                    "$COLUMN_CITY TEXT NOT NULL , "+
                    "$COLUMN_COUNTRY COUNTRY , "+
                    "$COLUMN_TEMP REAL , "+
                    "$COLUMN_TEMP_MIN REAL , "+
                    "$COLUMN_TEMP_MAX REAL , "+
                    "$COLUMN_PRESSURE INTEGER , "+
                    "$COLUMN_HUMIDITY INTEGER , "+
                    "$COLUMN_MAIN TEXT NOT NULL , "+
                    "$COLUMN_DESCRIPTION TEXT NOT NULL , "+
                    "$COLUMN_ICON_URL TEXT ," +
                    "PRIMARY KEY ($COLUMN_DATE, $COLUMN_CITY) " +
                    ");"


            db?.execSQL(CREATE_CMD)
        }

        private fun dropTable(db: SQLiteDatabase?) {
            val DROP_CMD = "DROP TABLE IF EXISTS $WEATHER_TABLE_NAME"

            db?.execSQL(DROP_CMD)
        }

        override fun onCreate(db: SQLiteDatabase?) {
            createTable(db)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            /**
             * I'm not sure how to proceed here so i will just go with this
             * Shouldn't be a big problem because the data stored in this db is not of vital importance
             */
            dropTable(db)
            createTable(db)
        }
    }

    @MainThread
    override fun onCreate(): Boolean {
        dbHelper = WeatherInfoDbHelper()
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        with(uriMatcher){
            addURI(AUTHORITY, WEATHER_TABLE_PATH, WEATHER_TABLE_CODE)
        }

        return true
    }

    override fun getType(uri: Uri): String? = when (uriMatcher.match(uri)) {
        WEATHER_TABLE_CODE -> ROW_CONTENT_TYPE
        else -> throw IllegalArgumentException("Uri $uri not supported")
    }

    private fun resolveTableFromUri(uri: Uri) : String{
        if(uriMatcher.match(uri) == WEATHER_TABLE_CODE)
            return WEATHER_TABLE_NAME

        throw IllegalArgumentException("Uri $uri is not supported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val table = resolveTableFromUri(uri)

        return dbHelper.writableDatabase.use {
            it.delete(table, selection, selectionArgs)
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        validateContentValues(values)

        val table = resolveTableFromUri(uri)
        return dbHelper.writableDatabase.use {
            val id = it.insert(table, null, values)

            if (id < 0)
                throw SQLException("Insertion in $uri failed")

            context.contentResolver.notifyChange(uri, null)
            // don't think this is relevant for this provider's use case
            Uri.parse("content://$AUTHORITY/$WEATHER_TABLE_NAME/$id")
        }
    }

    private fun validateContentValues(values: ContentValues?) {
        values?.apply {
            if (!containsKey(COLUMN_CITY) || !containsKey(COLUMN_DATE))
                throw IllegalArgumentException("Must provide both city and date parameter")
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val table = resolveTableFromUri(uri)

        val db = dbHelper.readableDatabase
        return try {
            // ProxyCursor(cursor) // overrides close and closes db aswell as cursor
            db.query(table, projection, selection, selectionArgs, null, null, sortOrder)
        }
        finally {
            // TODO: figure out a way to safely expose cursor while ensuring close() of readableDatabase
            // db.close() // this damn line made me waste almost 2 days of work
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selArgs: Array<String>?): Int {
        val table = resolveTableFromUri(uri)

        return dbHelper.readableDatabase.use{
            it.update(table, values, selection, selArgs)
        }
    }
}