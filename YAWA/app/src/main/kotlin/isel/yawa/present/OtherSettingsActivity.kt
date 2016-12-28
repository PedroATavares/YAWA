package isel.yawa.present


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.ArrayAdapter
import android.widget.Toast
import isel.yawa.Application
import isel.yawa.Application.Companion.CITY_KEY
import isel.yawa.R
import isel.yawa.model.services.NotificationEmitter
import kotlinx.android.synthetic.main.activity_settings.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class OtherSettingsActivity : AppCompatActivity() {
    companion object {
        const val SHARED_PREFERENCES_CITIES ="cities"
    }

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    var listItems:ArrayList<String>? = null


    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        if(!sharedPref.contains(SHARED_PREFERENCES_CITIES)) {
            val set = HashSet<String>()
            editor.putStringSet(SHARED_PREFERENCES_CITIES, set)
            editor.apply()
        }

        listItems = ArrayList<String>(sharedPref.getStringSet(SHARED_PREFERENCES_CITIES, HashSet<String>()))
        if(sharedPref.contains(resources.getString(R.string.time_notify_hour))){
            notif_toggle.isChecked=true
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        save_button.setOnClickListener {
            checkNotifyToggle(sharedPref,editor)
            editor.commit()
            finish()
        }

        addBtn.setOnClickListener {
            if(!editCity.text.isEmpty()) {
                listItems!!.add(editCity.text.toString())
                val period = sharedPref.getString(resources.getString(R.string.period_refresh),180.toString()).toLong()
                (application as Application).scheduleUpdateCurrentWeather(listItems, period)
                //(application as Application).scheduleUpdateForecast(listItems,period)
                editCity.text.clear()
                editor.putStringSet(SHARED_PREFERENCES_CITIES, listItems!!.toSet())
                editor.commit()
                adapter!!.notifyDataSetChanged()
            }
        }

        clearBtn.setOnClickListener {
            editor.clear()
            editor.commit()
            listItems!!.clear()
            adapter!!.notifyDataSetChanged()
            (application as Application).cancelUpdateCurrentWeather()
            (application as Application).cancelUpdateForecast()
        }

        listView.setOnItemClickListener { adapterView, view, idx, l ->
            with(AlertDialog.Builder(this)){
                setTitle(getString(R.string.settings_popup_title))
                setMessage(getString(R.string.settings_popup_confirm))
                setCancelable(true)

                setPositiveButton(android.R.string.yes, { dialogInterface, i ->
                    listItems!!.removeAt(idx)
                    editor.putStringSet(SHARED_PREFERENCES_CITIES, listItems!!.toSet())
                    editor.commit()
                    adapter!!.notifyDataSetChanged()
                })
                setNegativeButton(android.R.string.no, { dialogInterface, i ->  })

                setNeutralButton(getString(R.string.set_favourite),{dialogInterfase, i ->
                    editor.putString("favourite_city",listItems!!.get(idx))
                    editor.commit()
                })

                show()
            }
        }
    }

    private fun checkNotifyToggle(sharedPref: SharedPreferences,editor: SharedPreferences.Editor) {
        val hour_notify = resources.getString(R.string.time_notify_hour)
        val minute_notify = resources.getString(R.string.time_notify_minute)

        if (notif_toggle.isChecked ) {
            if(!time_notify.text.isEmpty()){
                val str = time_notify.text.toString()
                val formatter = SimpleDateFormat("hh:mm")
                val date = formatter.parse(str)
                val hour = date.hours
                val minute = date.minutes
                val city = sharedPref.getString(resources.getString(R.string.favourite_city),null)
                System.out.println(city)
                if(city != null){
                    editor.putInt(minute_notify, minute)
                    editor.putInt(hour_notify, hour)
                    (application as Application).scheduleNotification(city, hour, minute)
                }else{
                    Toast.makeText(this,"Favourite City Not Defined",Toast.LENGTH_LONG).show()
                }
            }
            return
        }
        editor.remove(minute_notify)
        editor.remove(hour_notify)
        (application as Application).cancelNotificationAlarm()
    }

}
