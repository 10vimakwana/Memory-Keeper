package com.tanvi.memorykeeper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.tanvi.memorykeeper.R
import com.tanvi.memorykeeper.dataclass.Note
import com.tanvi.memorykeeper.dialog.DisplayNoteDialog


class NoteAdapter(private val context: Context,private val arraylist: ArrayList<Note>,private val onListener: onNoteDeleteListener) :
    RecyclerView.Adapter<NoteAdapterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NoteAdapterViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }

    override fun onBindViewHolder(holder: NoteAdapterViewHolder, position: Int) {
        val note: Note = arraylist[position]
        holder.bind(note)

        holder.mCancel?.setOnClickListener {
            onListener.onNoteDelete(position)
        }
        holder.mImage?.let {
            Glide.with(context).load(note.image).centerCrop().transform(CircleCrop()).error(context.resources.getDrawable(R.drawable.user_default))
                .into(it)
        }
        holder.itemView.setOnClickListener {
            val dialogFragment = DisplayNoteDialog(note.title,note.note,note.image,note.date,position);
//        val bundle = Bundle()
//        bundle.putBoolean("notAlertDialog", true)
//        dialogFragment.arguments = bundle

            val manager: FragmentManager =
                (context as AppCompatActivity).supportFragmentManager

            val ft = manager.beginTransaction()
            ft.addToBackStack(null)
            dialogFragment.show(ft, "dialog") }
    }
}

class NoteAdapterViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_note, parent, false)) {
    private var mTitleView: TextView? = null
    private var mNote: TextView? = null
    public var mCancel: ImageView? = null
    public var mImage: ImageView? = null


    init {
        mTitleView = itemView.findViewById(R.id.txt_note_title)
        mNote = itemView.findViewById(R.id.txt_note)
        mCancel = itemView.findViewById(R.id.iv_cancel)
        mImage = itemView.findViewById(R.id.iv_note_image)
    }

    fun bind (note: Note) {
        mTitleView?.text = note.title
        mNote?.text = note.note
    }


}
interface onNoteDeleteListener{
    fun onNoteDelete(position: Int)
}


