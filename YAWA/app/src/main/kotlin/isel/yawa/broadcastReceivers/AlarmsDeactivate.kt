package isel.yawa.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import isel.yawa.Application
import isel.yawa.R
import kotlinx.android.synthetic.main.activity_settings.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmsDeactivate : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val app =  (context!!).applicationContext as Application
        val sharedPref = app.getSharedPreferences(app.resources.getString(R.string.UserPreferences), Context.MODE_PRIVATE)

        if(sharedPref.contains(app.resources.getString(R.string.time_notify_hour))){
            isel.yawa.Application().cancelNotificationAlarm()
        }

        if(sharedPref.contains(app.resources.getString(R.string.period_refresh))){
            isel.yawa.Application().cancelUpdateAlarm()
        }
    }

}