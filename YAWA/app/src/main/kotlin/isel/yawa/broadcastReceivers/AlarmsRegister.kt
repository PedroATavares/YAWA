package isel.yawa.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import isel.yawa.Application
import isel.yawa.R
import java.util.*


class AlarmsRegister : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val app =  (context!!).applicationContext as Application
        val sharedPref = app.getSharedPreferences(app.resources.getString(R.string.UserPreferences), Context.MODE_PRIVATE)

        if(sharedPref.contains(app.resources.getString(R.string.time_notify_hour))){
            val city = app.resources.getString(R.string.favourite_city)
            val hour = sharedPref.getInt(app.resources.getString(R.string.time_notify_hour),0)
            val minute = sharedPref.getInt(app.resources.getString(R.string.time_notify_minute),0)
            isel.yawa.Application().scheduleNotification(city, hour, minute)
        }

        if(sharedPref.contains(app.resources.getString(R.string.period_refresh))){
            val cities = sharedPref.getStringSet(app.resources.getString(R.string.cities_to_Follow),null) as ArrayList<String>
            val period = sharedPref.getLong(app.resources.getString(R.string.period_refresh),0)
            isel.yawa.Application().scheduleUpdate(cities,period)
        }
    }
}