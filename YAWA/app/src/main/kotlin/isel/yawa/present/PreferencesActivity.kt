package isel.yawa.present

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import isel.yawa.Application
import isel.yawa.R
import java.util.*


class PreferencesActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(isel.yawa.R.xml.user_settings)
        val preference = findPreference(resources.getString(R.string.period_refresh))

        preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            val cities = sharedPref.getStringSet(resources.getString(R.string.cities_to_Follow),null) as ArrayList<String>
            (application as Application).scheduleUpdate(cities, newValue as Long)
            true
        }


    }
}