package isel.yawa.present


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import isel.yawa.R
import kotlinx.android.synthetic.main.activity_settings.*
import java.text.SimpleDateFormat
import java.util.*


const val SHARED_PREFERENCES_KEY ="userprefs"
const val SHARED_PREFERENCES_CITIES ="cities"

class SettingsActivity : AppCompatActivity() {


    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    var listItems:ArrayList<String>? = null


    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val sharedPref = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        val editor = sharedPref.edit()
        if(!sharedPref.contains(SHARED_PREFERENCES_CITIES)) {
            val set = HashSet<String>()
            editor.putStringSet(SHARED_PREFERENCES_CITIES, set)
            editor.apply()
        }

        listItems = ArrayList<String>(sharedPref.getStringSet(SHARED_PREFERENCES_CITIES,HashSet<String>()))
        if(sharedPref.contains(resources.getString(R.string.time_notify_hour))){
            notif_toggle.isChecked=true
        }

        adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                listItems)

        listView.adapter=adapter

        save_button.setOnClickListener {
            checkNotifyToggle(editor)
            if(!period_to_updData.text.isEmpty()){
                val period = time_notify.text.toString().toLong()
                isel.yawa.Application().scheduleUpdate(listItems,period)
                editor.putLong("period",period)
            }
            editor.commit()
            finish()
        }

        addBtn.setOnClickListener {
            if(!editCity.text.isEmpty()) {
                listItems!!.add(editCity.text.toString())
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
        }

        listView.setOnItemClickListener { adapterView, view, idx,
                                      l ->
            val builder1 = AlertDialog.Builder(this)
            builder1.setTitle("Delete Entry")
            builder1.setMessage("Do you want to delete entry?")
            builder1.setCancelable(true)
            builder1.setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialogInterface, i ->
                listItems!!.removeAt(idx)
                editor.putStringSet(SHARED_PREFERENCES_CITIES, listItems!!.toSet())
                editor.commit()
                adapter!!.notifyDataSetChanged()
            })
            builder1.setNegativeButton(android.R.string.no,DialogInterface.OnClickListener { dialogInterface, i ->  })
            builder1.show()
        }


    }

    private fun checkNotifyToggle(editor: SharedPreferences.Editor) {
        val hour_notify = resources.getString(R.string.time_notify_hour)!!
        val minute_notify = resources.getString(R.string.time_notify_minute)!!
        if (notif_toggle.isChecked) {
            if (!time_notify.text.isEmpty()) {
                val str = time_notify.text.toString()
                val formatter = SimpleDateFormat("hh:mm")
                val date = formatter.parse(str)
                val hour = date.hours
                val minute = date.minutes
                editor.putInt(minute_notify, minute)
                editor.putInt(hour_notify, hour)
                isel.yawa.Application().scheduleNotification("Lisbon", hour, minute)
            }
        } else {
            editor.remove(minute_notify)
            editor.remove(hour_notify)
        }
    }

}
