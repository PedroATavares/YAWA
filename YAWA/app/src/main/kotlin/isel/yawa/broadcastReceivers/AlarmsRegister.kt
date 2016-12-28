package isel.yawa.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import isel.yawa.Application
import isel.yawa.R
import java.util.*


class AlarmsRegister : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val app =  (context!!).applicationContext as Application
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(app)

        if(sharedPref.contains(app.resources.getString(R.string.time_notify_hour))){
            val city = sharedPref.getString(app.resources.getString(R.string.favourite_city),null)
            val hour = sharedPref.getInt(app.resources.getString(R.string.time_notify_hour),0)
            val minute = sharedPref.getInt(app.resources.getString(R.string.time_notify_minute),0)
            app.scheduleNotification(city, hour, minute)
        }

        if(sharedPref.contains(app.resources.getString(R.string.period_refresh))){
            val cities = sharedPref.getStringSet(app.resources.getString(R.string.cities_to_Follow),null) as ArrayList<String>
            val period = sharedPref.getLong(app.resources.getString(R.string.period_refresh),0)
            app.scheduleUpdate(cities,period)
        }
    }
}