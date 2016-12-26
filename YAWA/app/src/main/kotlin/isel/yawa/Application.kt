package isel.yawa

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import isel.yawa.connect.RequestManager
import isel.yawa.model.services.CurrentWeatherService
import isel.yawa.broadcastReceivers.Notification
import java.util.*

class Application : android.app.Application() {

    companion object {
        const val CITY_KEY: String = "city"
    }

    override fun onCreate() {
        super.onCreate()
        RequestManager.setup(this)
    }

    fun scheduleUpdate(cities: ArrayList<String>?, repeat_Time: Long) {
        val action = Intent((applicationContext as Application), CurrentWeatherService::class.java)
                .putExtra("cities", cities)
        (getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager).setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                0,
                repeat_Time*60*1000,
                PendingIntent.getService(this, 1, action, PendingIntent.FLAG_UPDATE_CURRENT)
        )
    }

    fun scheduleNotification(city: String, hour: Int, minute: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        val TriggerMillis = cal.timeInMillis
        val action = Intent(this, Notification::class.java)
                .putExtra("city", city)
        (getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager).set(
                AlarmManager.RTC_WAKEUP,
                TriggerMillis,
                PendingIntent.getBroadcast(this, 2, action, PendingIntent.FLAG_UPDATE_CURRENT)
        )

    }

    fun cancelNotificationAlarm(){
        val intent = Intent(this, Notification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 2, intent, 0)
        val alarmManager = getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun cancelUpdateAlarm(){
        val intent = Intent(this, CurrentWeatherService::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
        val alarmManager = getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }



}