package com.tanvi.memorykeeper.activity

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tanvi.memorykeeper.R
import com.tanvi.memorykeeper.adapter.MemoryListAdapater
import com.tanvi.memorykeeper.adapter.onMemoryListListener
import com.tanvi.memorykeeper.database.DatabaseKeeper
import com.tanvi.memorykeeper.database.EntityKeeper
import com.tanvi.memorykeeper.databinding.ActivityMainBinding
import java.util.ArrayList

class MainActivity : AppCompatActivity(), onMemoryListListener {
    var adapter: MemoryListAdapater? = null
    val arraylist = ArrayList<EntityKeeper>()
    val REQUEST_PERMISSION_SETTING = 9898

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )


        binding.ryMemory.layoutManager = LinearLayoutManager(this@MainActivity)
        arraylist.clear()
        adapter = MemoryListAdapater(this, arraylist, this)
        binding.ryMemory.adapter = adapter


        binding.btnfloatAdd.setOnClickListener { btnfloatintent() }


        binding.edSearch.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
             }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter?.filter?.filter(s.toString())
            }

        })
    }

    private fun get() {
        class get : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                arraylist.addAll(DatabaseKeeper(this@MainActivity).getDao().getalldata())
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                if(arraylist.size<1){
                    binding.txtNodata.visibility = View.VISIBLE
                }else{
                    binding.txtNodata.visibility = View.GONE
                }
                adapter?.notifyDataSetChanged()
            }

        }
        get().execute()

    }

    public fun btnfloatintent() {
        val intent = Intent(this, AddContactActivity::class.java)
        intent.putExtra("id", "")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        arraylist.clear()
        adapter?.notifyDataSetChanged()
       /*  val permisstion = ReadWritePermisstion(this, object : onPermissionListener {
             override fun onPermissionGrand() {

                */ get()
       /*  }

         override fun onPermissionNotGrand() {
             val snackbar = Snackbar
                 .make(
                     binding.lyMain,
                     "Storange needed permission. allow it",
                     Snackbar.LENGTH_LONG
                 )
                 .setAction("Settings") {
                     val intent =
                         Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                     val uri =
                         Uri.fromParts("package", getPackageName(), null)
                     intent.data = uri
                     startActivityForResult(
                         intent,
                         REQUEST_PERMISSION_SETTING
                     )
                 }
             snackbar.show()
         }
     })
     permisstion.requestPermission()
*/

    }


    override fun onClickMemoryList(id: String) {
        val intent = Intent(this, AddContactActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }




}
