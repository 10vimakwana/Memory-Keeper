package com.tanvi.memorykeeper.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.tanvi.memorykeeper.R
import com.tanvi.memorykeeper.databinding.LayoutAddnoteBinding
import com.tanvi.memorykeeper.databinding.LayoutDisplaynoteBinding

class DisplayNoteDialog(
    private val title: String,
    private val note: String,
    private val image: String,
    private val date:String,
    private val pos: Int
) : DialogFragment() {

    private lateinit var binding: LayoutDisplaynoteBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<LayoutDisplaynoteBinding>(
            inflater,
            R.layout.layout_displaynote,
            container,
            false
        )

        binding.txtDisplaynoteTitle.setText(title)
        binding.txtDisplaynoteNote.setText(note)
        activity?.let {
            Glide.with(it).load(image).centerCrop()
                .error(activity?.resources?.getDrawable(R.drawable.user_default))
                .into(binding.ivDisplaynoteImage)
        }


        binding.btnDisplaynoteEdit.setOnClickListener {
            dismissAllowingStateLoss()
            val dialogFragment = AddNoteDialog(title, note, image,date, pos);
            val manager: FragmentManager =
                (context as AppCompatActivity).supportFragmentManager
            val ft = manager.beginTransaction()
            ft.addToBackStack(null)
            dialogFragment.show(ft, "dialog")
        }

        binding.btnDisplaynoteCancel.setOnClickListener{
            dismissAllowingStateLoss()
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

}