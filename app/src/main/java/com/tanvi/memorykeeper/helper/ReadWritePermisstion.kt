package com.tanvi.memorykeeper.helper

import android.Manifest
import android.app.Activity
import android.content.Context
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class ReadWritePermisstion( private val context: Context,private val onListener: onPermissionListener) {

     fun requestPermission() {
        val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        Dexter.withActivity(context as Activity?)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted()!!) {
                        onListener?.onPermissionGrand()
                    } else {
                        onListener?.onPermissionNotGrand()

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            })
            .check()
    }


}

interface onPermissionListener {
    fun onPermissionGrand()
    fun onPermissionNotGrand()
}