package isel.yawa.present

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import isel.yawa.R
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

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
        val sharedPref = getSharedPreferences("userprefs", Context.MODE_PRIVATE);
        val editor = sharedPref.edit()
        if(!sharedPref.contains("cities")) {
            val set = HashSet<String>()
            editor.putStringSet("cities", set)
            editor.commit()
        }

        listItems = ArrayList<String>(sharedPref.getStringSet("cities",HashSet<String>()))

        adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                listItems)

        list.adapter=adapter;

        addBtn.setOnClickListener {

            listItems!!.add("Clicked : "+clickCounter++)
            editor.putStringSet("cities", listItems!!.toSet())
            editor.commit()
            listItems= ArrayList<String>(sharedPref.getStringSet("cities",HashSet<String>()))
            adapter!!.notifyDataSetChanged()
        }
    }
}
