package isel.yawa

import isel.yawa.connect.RequestManager

class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        RequestManager.setup(this)
    }
}