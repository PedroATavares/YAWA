package isel.yawa.present


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import isel.yawa.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*


const val SHARED_PREFERENCES_KEY ="userprefs"
const val SHARED_PREFERENCES_CITIES ="cities"

class SettingsActivity : AppCompatActivity() {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    var listItems:ArrayList<String>? = null

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    var adapter: ArrayAdapter<String>? = null

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    var clickCounter = 0

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

        adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                listItems)

        list.adapter=adapter;

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

        list.setOnItemClickListener { adapterView, view, idx,
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
}
