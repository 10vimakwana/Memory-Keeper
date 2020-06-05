package com.tanvi.memorykeeper.dialog

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.snackbar.Snackbar
import com.tanvi.memorykeeper.R
import com.tanvi.memorykeeper.databinding.LayoutAddnoteBinding
import com.tanvi.memorykeeper.helper.ReadWritePermisstion
import com.tanvi.memorykeeper.helper.onPermissionListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddNoteDialog(
    private val title: String,
    private val note: String,
    private val image: String,
    private val date:String,
    private val pos: Int
) : DialogFragment() {


    var mListener: onDataSave? = null
    private lateinit var binding: LayoutAddnoteBinding

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_GALLARY_SELECT = 2
    var mCurrentPhotoPath: String? = null;
    val REQUEST_PERMISSION_SETTING = 9898
    var selectedImageUri: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as onDataSave

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<LayoutAddnoteBinding>(
            inflater,
            R.layout.layout_addnote,
            container,
            false
        )
        if (!title.equals("")) {
            binding.edDialogTitle.setText(title)
        }
        if (!note.equals("")) {
            binding.edDialogNote.setText(note)
        }
        if (!date.equals("")) {
            binding.edDate.setText(date)
        }
        if (!image.equals("")) {
            Glide.with(this).load(image).centerCrop().transform(CircleCrop())
                .error(activity?.resources?.getDrawable(R.drawable.user_default))
                .into(binding.ivDialogImage)
        }



        binding.btnDialogCancel.setOnClickListener { dialog?.dismiss() }
        binding.btnDialogSubmit.setOnClickListener {
            if (title.equals("")) {
                save()
            } else {
                update()
            }
        }
        binding.ivDialogImage.setOnClickListener { onImage() }
        binding.ivCalender.setOnClickListener { onCalender() }
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    interface onDataSave {
         fun ondatasave(title: String, note: String, image: String,date:String)
         fun ondataupdate(title: String, note: String, image: String,date: String, position: Int)
    }

    fun save() {
        if (binding.edDialogTitle.text.toString().equals("")) {
            binding.edDialogTitle.setError("Enter Title")
        } else if (binding.edDialogNote.text.toString().equals("")) {
            binding.edDialogNote.setError("Enter Note")
        } else if (binding.edDate.text.toString().equals("")) {
            binding.edDialogNote.setError("Enter Date")
        } else {
            if (selectedImageUri != null) {
                mListener!!.ondatasave(
                    binding.edDialogTitle.text.toString(),
                    binding.edDialogNote.text.toString(),
                    selectedImageUri.toString(),
                    binding.edDate.toString()
                )
            } else {
                mListener!!.ondatasave(
                    binding.edDialogTitle.text.toString(),
                    binding.edDialogNote.text.toString(),
                    mCurrentPhotoPath.toString(),
                            binding.edDate.toString()
                )
            }
            dialog?.dismiss()
        }
    }

    fun update() {
        if (binding.edDialogTitle.text.toString().equals("")) {
            binding.edDialogTitle.setError("Enter Title")
        } else if (binding.edDialogNote.text.toString().equals("")) {
            binding.edDialogNote.setError("Enter Note")
        } else {
            if (selectedImageUri != null) {
                mListener!!.ondataupdate(
                    binding.edDialogTitle.text.toString(),
                    binding.edDialogNote.text.toString(),
                    selectedImageUri.toString(),
                    binding.edDate.toString(),
                    pos
                )
            } else {
                if (mCurrentPhotoPath != null) {
                    mListener!!.ondataupdate(
                        binding.edDialogTitle.text.toString(),
                        binding.edDialogNote.text.toString(),
                        mCurrentPhotoPath.toString(),
                        binding.edDate.toString()
                        , pos
                    )
                } else {
                    mListener!!.ondataupdate(
                        binding.edDialogTitle.text.toString(),
                        binding.edDialogNote.text.toString(),
                        binding.edDate.toString(),
                        image, pos
                    )
                }
            }
            dialog?.dismiss()
        }
    }

    fun onImage() {
        var permisstion = activity?.let {
            ReadWritePermisstion(it, object : onPermissionListener {
                override fun onPermissionGrand() {
                    val dialog = activity?.let { Dialog(it) }
                    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog?.setCancelable(true)
                    dialog?.setContentView(R.layout.layout_gallarycamera)
                    dialog?.window?.setLayout(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    val btncamera = dialog?.findViewById(R.id.btn_camera) as TextView
                    val btngallary = dialog?.findViewById(R.id.btn_gallary) as TextView
                    btncamera.setOnClickListener {
                        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        val file: File = createFile()

                        val uri: Uri = FileProvider.getUriForFile(
                            activity!!,
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
                            binding.lyMainDialog,
                            "Storange needed permission. allow it",
                            Snackbar.LENGTH_LONG
                        )
                        .setAction("Settings") {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri =
                                Uri.fromParts("package", activity!!.getPackageName(), null)
                            intent.data = uri
                            startActivityForResult(
                                intent,
                                REQUEST_PERMISSION_SETTING
                            )
                        }
                    snackbar.show()
                }
            })
        }
        permisstion?.requestPermission()
    }

    @Throws(IOException::class)
    private fun createFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            mCurrentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            Glide.with(this).load(bitmap).centerCrop().transform(CircleCrop())
                .into(binding.ivDialogImage)

            selectedImageUri = null
        }
        if (requestCode == REQUEST_GALLARY_SELECT && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data!!.data.toString()
            Glide.with(this).load(selectedImageUri).centerCrop().transform(CircleCrop())
                .into(binding.ivDialogImage)
        }
    }
    fun onCalender() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = activity?.let {
            DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val month = monthOfYear + 1
                    binding.edDate.setText(dayOfMonth.toString() + "/" + month + "/" + year.toString())
                },
                year,
                month,
                day
            )
        }
        dpd?.show()
    }

}