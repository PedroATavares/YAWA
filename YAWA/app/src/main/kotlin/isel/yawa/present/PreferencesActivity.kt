package isel.yawa.present

import android.os.Bundle
import android.preference.PreferenceActivity

class PreferencesActivity : PreferenceActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(isel.yawa.R.xml.user_settings)
    }
}