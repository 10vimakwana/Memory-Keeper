package com.tanvi.memorykeeper.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.snackbar.Snackbar
import com.tanvi.memorykeeper.R
import com.tanvi.memorykeeper.adapter.NoteAdapter
import com.tanvi.memorykeeper.adapter.onNoteDeleteListener
import com.tanvi.memorykeeper.database.DatabaseKeeper
import com.tanvi.memorykeeper.database.EntityKeeper
import com.tanvi.memorykeeper.databinding.ActivityAddContactBinding
import com.tanvi.memorykeeper.dataclass.Note
import com.tanvi.memorykeeper.dialog.AddNoteDialog
import com.tanvi.memorykeeper.dialog.AddNoteDialog.onDataSave
import com.tanvi.memorykeeper.helper.ReadWritePermisstion
import com.tanvi.memorykeeper.helper.onPermissionListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddContactActivity : AppCompatActivity(), onDataSave, onNoteDeleteListener {
    val arraylist = ArrayList<Note>()
    var noteAdapter: NoteAdapter? = null
    var id: String = ""


    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_GALLARY_SELECT = 2
    var mCurrentPhotoPath: String? = null;
    val REQUEST_PERMISSION_SETTING = 9898
    var selectedImageUri: String? = null

    private lateinit var binding: ActivityAddContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_add_contact
        )
        id = intent.getStringExtra("id")

        if (!id.equals("")) {
            getdatabyId(id)
        }
        binding.ryNote.layoutManager = GridLayoutManager(this, 2)
        noteAdapter = NoteAdapter(this, arraylist, this)
        binding.ryNote.adapter = noteAdapter

        binding.ivImageProfile.setOnClickListener { onImageProfile() }

    }


    fun onImageProfile() {

        var permisstion = ReadWritePermisstion(this, object : onPermissionListener {
            override fun onPermissionGrand() {
                val dialog = Dialog(this@AddContactActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.layout_gallarycamera)
                dialog.window?.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val btncamera = dialog.findViewById(R.id.btn_camera) as TextView
                val btngallary = dialog.findViewById(R.id.btn_gallary) as TextView
                btncamera.setOnClickListener {
                    val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val file: File = createFile()

                    val uri: Uri = FileProvider.getUriForFile(
                        this@AddContactActivity,
                        "com.tanvi.iremeberyou",
                        file
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                    dialog.dismiss()


                }
                btngallary.setOnClickListener {
                    val intent = Intent("android.intent.action.GET_CONTENT")
                    intent.type = "image/*"
                    startActivityForResult(intent, REQUEST_GALLARY_SELECT)
                    dialog.dismiss()
                }
                dialog.show()
            }

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


    }

    @Throws(IOException::class)
    private fun createFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    fun showview() {
        /*    binding.edInsta.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.edTwitter.visibility = View.VISIBLE
                }
            })*/
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            //To get the File for further usage
            val auxFile = File(mCurrentPhotoPath)
            var bitmap: Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            Glide.with(this).load(bitmap).centerCrop().transform(CircleCrop())
                .into(binding.ivImageProfile)

            selectedImageUri = null
        }
        if (requestCode == REQUEST_GALLARY_SELECT && resultCode == Activity.RESULT_OK) {

            selectedImageUri = data!!.data.toString()
            Glide.with(this).load(selectedImageUri).centerCrop().transform(CircleCrop())
                .into(binding.ivImageProfile)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (selectedImageUri != null) {
                    val data = EntityKeeper(
                        selectedImageUri.toString(),
                        binding.edName.text.toString(),
                        binding.edContact.text.toString(),
                        binding.edEmail.text.toString(),
                        binding.edSocial.text.toString(),
                        arraylist
                    )
                    addMemory(data)
                } else {
                    val data = EntityKeeper(
                        mCurrentPhotoPath.toString(),
                        binding.edName.text.toString(),
                        binding.edContact.text.toString(),
                        binding.edEmail.text.toString(),
                        binding.edSocial.text.toString(),
                        arraylist
                    )
                    addMemory(data)
                }


                true
            }
            R.id.action_delete -> {
                getDeletebyId(id)
                true
            }
            R.id.action_add -> {
                val dialogFragment = AddNoteDialog("", "", "", "", 0);
//        val bundle = Bundle()
//        bundle.putBoolean("notAlertDialog", true)
//        dialogFragment.arguments = bundle


                val ft = supportFragmentManager.beginTransaction()
                ft.addToBackStack(null)
                dialogFragment.show(ft, "dialog")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addMemory(entityKeeper: EntityKeeper) {

        if (id.equals("")) {
            if (binding.edName.text.toString().equals("")) {
                binding.edName.setError("Enter Name")
            }else  if (!binding.edContact.text.toString().equals("") && binding.edContact.text.length<10) {
                binding.edContact.setError("Enter Contact")
            }else  if (!binding.edEmail.text.toString().equals("") && !binding.edEmail.text.toString().isValidEmail()) {
                binding.edEmail.setError("Enter Email")
            } else {
                class save : AsyncTask<Void, Void, Void>() {
                    override fun doInBackground(vararg params: Void?): Void? {
                        DatabaseKeeper(this@AddContactActivity).getDao().addMemery(entityKeeper)
                        return null
                    }

                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        Toast.makeText(
                            this@AddContactActivity,
                            "Add Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        onBackPressed()

                    }

                }
                save().execute()
            }
        } else {
            if (binding.edName.text.toString().equals("")) {
                binding.edName.setError("Enter Name")
            }else  if (!binding.edContact.text.toString().equals("") && binding.edContact.text.length<10) {
                binding.edContact.setError("Enter Contact")
            }else  if (!binding.edEmail.text.toString().equals("") && !binding.edEmail.text.toString().isValidEmail()) {
                binding.edEmail.setError("Enter Email")
            } else {
            class update : AsyncTask<Void, Void, Void>() {
                @SuppressLint("WrongThread")
                override fun doInBackground(vararg params: Void?): Void? {
                    if (selectedImageUri != null) {
                        DatabaseKeeper(this@AddContactActivity).getDao().updateMemory(
                            id,
                            selectedImageUri,
                            binding.edName.text.toString(),
                            binding.edContact.text.toString(),
                            binding.edEmail.text.toString(),
                            binding.edSocial.text.toString(),
                            arraylist
                        )
                    } else {
                        if (mCurrentPhotoPath != null) {
                            DatabaseKeeper(this@AddContactActivity).getDao().updateMemory(
                                id,
                                mCurrentPhotoPath,
                                binding.edName.text.toString(),
                                binding.edContact.text.toString(),
                                binding.edEmail.text.toString(),
                                binding.edSocial.text.toString(),
                                arraylist
                            )
                        } else {
                            DatabaseKeeper(this@AddContactActivity).getDao()
                                .updateMemorywithoutimage(
                                    id,
                                    binding.edName.text.toString(),
                                    binding.edContact.text.toString(),
                                    binding.edEmail.text.toString(),
                                    binding.edSocial.text.toString(),
                                    arraylist
                                )
                        }

                    }
                    return null
                }

                override fun onPostExecute(result: Void?) {
                    super.onPostExecute(result)

                    Toast.makeText(
                        this@AddContactActivity,
                        "Update Successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    onBackPressed()
                }

            }
            update().execute()
            }
        }

    }

    private fun getdatabyId(id: String) {
        class get : AsyncTask<Void, Void, List<EntityKeeper>>() {
            override fun doInBackground(vararg params: Void?): List<EntityKeeper> {
                DatabaseKeeper(this@AddContactActivity).getDao().getDatabyid(id)
                return DatabaseKeeper(this@AddContactActivity).getDao().getDatabyid(id)
            }

            override fun onPostExecute(result: List<EntityKeeper>?) {
                super.onPostExecute(result)
                binding.edName.setText(result?.get(0)?.name)
                binding.edEmail.setText(result?.get(0)?.email)
                binding.edContact.setText(result?.get(0)?.contact)
                binding.edSocial.setText(result?.get(0)?.social)
                result?.get(0)?.notelist?.let { arraylist.addAll(it) }
                noteAdapter?.notifyDataSetChanged()
                Glide.with(this@AddContactActivity).load(result?.get(0)?.profile).centerCrop()
                    .error(resources.getDrawable(R.drawable.user_default)).transform(CircleCrop())
                    .into(binding.ivImageProfile)

            }

        }
        get().execute()

    }


    private fun getDeletebyId(id: String) {
        if (!id.equals("")) {
            class delete : AsyncTask<Void, Void, Void>() {
                override fun doInBackground(vararg params: Void?): Void? {
                    DatabaseKeeper(this@AddContactActivity).getDao().getDeletebyid(id)
                    return null
                }

                override fun onPostExecute(result: Void?) {
                    super.onPostExecute(result)
                    Toast.makeText(
                        this@AddContactActivity,
                        "Delete Successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    onBackPressed()

                }

            }
            delete().execute()
        } else {
            onBackPressed()
        }
    }

    override fun onNoteDelete(position: Int) {
        arraylist.removeAt(position)
        noteAdapter?.notifyDataSetChanged()
    }

    override fun ondatasave(title: String, note: String, image: String, date: String) {
        arraylist.add(Note(title, note, image, date))
        noteAdapter?.notifyDataSetChanged()
    }

    override fun ondataupdate(
        title: String,
        note: String,
        image: String,
        date: String,
        position: Int
    ) {
        arraylist.set(position, Note(title, note, image, date))
        noteAdapter?.notifyDataSetChanged()
    }

    fun String.isValidEmail(): Boolean
            = this.isNotEmpty() &&
            Patterns.EMAIL_ADDRESS.matcher(this).matches()
}


