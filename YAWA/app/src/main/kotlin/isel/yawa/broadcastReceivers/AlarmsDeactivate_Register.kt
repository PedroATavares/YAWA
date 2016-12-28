package isel.yawa.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import isel.yawa.Application
import isel.yawa.R
import java.util.*

class AlarmsDeactivate_Register : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val app =  (context!!).applicationContext as Application
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(app)
        when(intent?.action){
            Intent.ACTION_BATTERY_LOW -> deactivateAlarms(app,sharedPref)
            Intent.ACTION_BATTERY_OKAY -> alarmsRegister(app,sharedPref)
            Intent.ACTION_REBOOT -> alarmsRegister(app,sharedPref)
        }

    }


    fun deactivateAlarms(app: Application, sharedPref: SharedPreferences) {
        if(sharedPref.contains(app.resources.getString(R.string.time_notify_hour))){
            app.cancelNotificationAlarm()
        }

        if(sharedPref.contains(app.resources.getString(R.string.period_refresh))){
            app.cancelUpdateCurrentWeather()
            app.cancelUpdateForecast()
        }
    }

    fun alarmsRegister (app: Application, sharedPref: SharedPreferences){
        if(sharedPref.contains(app.resources.getString(R.string.time_notify_hour))){
            val city = sharedPref.getString(app.resources.getString(R.string.favourite_city),null)
            val hour = sharedPref.getInt(app.resources.getString(R.string.time_notify_hour),0)
            val minute = sharedPref.getInt(app.resources.getString(R.string.time_notify_minute),0)
            app.scheduleNotification(city, hour, minute)
        }

        if(sharedPref.contains(app.resources.getString(R.string.period_refresh))){
            val cities = sharedPref.getStringSet(app.resources.getString(R.string.cities_to_Follow),null)
            var listItems =  ArrayList<String>(cities)
            val period = sharedPref.getString(app.resources.getString(R.string.period_refresh),null).toLong()
            app.scheduleUpdateCurrentWeather(listItems,period)
            app.scheduleUpdateForecast(listItems,period)
        }
    }

}