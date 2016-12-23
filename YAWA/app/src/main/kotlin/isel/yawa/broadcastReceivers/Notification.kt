package isel.yawa.broadcastReceivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.support.v7.app.NotificationCompat
import isel.yawa.R

class Notification : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val city = intent!!.getStringExtra("city")

        val builder =  NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.weather_img2)
                .setContentTitle(city)
                .setContentText("This is a test notification");

        val notificationIntent = Intent(context, Notification::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(contentIntent)
        // Add as notification
        val manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())
    }
}

