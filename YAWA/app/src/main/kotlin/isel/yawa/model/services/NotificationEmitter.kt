package isel.yawa.model.services

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.NotificationCompat
import isel.yawa.Application.Companion.CITY_KEY
import isel.yawa.R
import isel.yawa.model.WeatherInfo
import isel.yawa.model.content.WeatherProvider
import isel.yawa.model.content.toWeatherInfo
import isel.yawa.present.WeatherActivity

/**
 * Service whose role is to fetch the lastest weather information for the given city
 * and produce a notification with that info
 */
class NotificationEmitter : IntentService("notification-emitter") {
    override fun onHandleIntent(intent: Intent) {
        if(!intent.hasExtra(CITY_KEY))
            return

        val city = intent.getStringExtra(CITY_KEY)
        val wInfo = fetchLatestWeatherInfoForCity(city) ?: return

        produceNotification(wInfo)
    }

    private fun fetchLatestWeatherInfoForCity(cityName: String) : WeatherInfo? {
        var  wInfo : WeatherInfo? = null
        with(WeatherProvider){
            contentResolver.query(WEATHER_CONTENT_URI, null,
                    "$COLUMN_CITY = ?",
                    arrayOf(cityName),
                    "$COLUMN_DATE desc")
        }.use {
            if(it.moveToNext())
                wInfo = it.toWeatherInfo()
        }

        return wInfo;
    }

    private fun  produceNotification(wInfo: WeatherInfo) {
        val builder =  NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.weather_img2)
                .setContentTitle(wInfo.city)
                .setContentText(buildNotificationText(wInfo))
                .setContentIntent(buildContentIntent(wInfo))

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())
    }

    private fun  buildNotificationText(wInfo: WeatherInfo): String {
        // TODO: enrich this text
        return "${wInfo.description} ${wInfo.ambientInfo?.temp}"
    }

    private fun  buildContentIntent(wInfo: WeatherInfo): PendingIntent {
        val detailActivity = Intent(this, WeatherActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(WeatherActivity.WEATHER_KEY, wInfo)
        }

        return PendingIntent.getActivity(this, 0, detailActivity, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
