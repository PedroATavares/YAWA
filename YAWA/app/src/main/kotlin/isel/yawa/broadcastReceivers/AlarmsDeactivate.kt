package isel.yawa.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import isel.yawa.Application
import isel.yawa.R

class AlarmsDeactivate : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val app =  (context!!).applicationContext as Application
        val sharedPref = app.getSharedPreferences(app.resources.getString(R.string.UserPreferences), Context.MODE_PRIVATE)
        if(sharedPref.contains(app.resources.getString(R.string.time_notify_hour))){
            app.cancelNotificationAlarm()
        }

        if(sharedPref.contains(app.resources.getString(R.string.period_refresh))){
            app.cancelUpdateAlarm()
        }
    }

}