package isel.yawa

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import isel.yawa.connect.RequestManager
import isel.yawa.model.services.CurrentWeatherService
import isel.yawa.model.services.NotificationEmitter
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
        val action = Intent(this, CurrentWeatherService::class.java).putExtra("cities", cities)

        (getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager).setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                0,
                repeat_Time*60*1000,
                PendingIntent.getService(this, 1, action, PendingIntent.FLAG_UPDATE_CURRENT)
        )
    }

    /**
     * Schedules a notification to show the weather for the city passed as parameter
     */
    fun scheduleNotification(city: String, hour: Int, minute: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val action = Intent(this, NotificationEmitter::class.java).putExtra(CITY_KEY, city)

        val alarmManager = getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                PendingIntent.getService(this, 2, action, PendingIntent.FLAG_UPDATE_CURRENT)
        )
    }

    fun cancelNotificationAlarm(){
        val intent = Intent(this, NotificationEmitter::class.java)
        val pendingIntent = PendingIntent.getService(this, 2, intent, 0)

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