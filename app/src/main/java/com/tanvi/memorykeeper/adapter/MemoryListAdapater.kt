package com.tanvi.memorykeeper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.tanvi.memorykeeper.R
import com.tanvi.memorykeeper.database.EntityKeeper
import java.util.*
import kotlin.collections.ArrayList

class MemoryListAdapater(private val context: Context, private val arraylist: ArrayList<EntityKeeper>, private val onlistener: onMemoryListListener) :
    RecyclerView.Adapter<MemoryViewHolder>(), Filterable {

    var FilterList = ArrayList<EntityKeeper>()

    init {
        FilterList = arraylist
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MemoryViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return FilterList.size
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val entityKeeper:EntityKeeper = FilterList[position]
        holder.bind(entityKeeper)
        holder.itemView.setOnClickListener {
            onlistener.onClickMemoryList(entityKeeper.Id.toString())
        }
        holder.mImage?.let {
            Glide.with(context).load(entityKeeper.profile).centerCrop().transform(CircleCrop()).error(context.resources.getDrawable(R.drawable.user_default))
                .into(it)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    FilterList = arraylist
                } else {
                    val resultList = ArrayList<EntityKeeper>()
                    for (row in arraylist) {
                        if (row.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    FilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = FilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                FilterList = results?.values as ArrayList<EntityKeeper>
                notifyDataSetChanged()
            }

        }
    }

}
class MemoryViewHolder (inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_memorylist, parent, false)){
    private var mNameView: TextView? = null
    private var mContactView: TextView? = null
    public var mImage: ImageView? = null


    init {
        mNameView = itemView.findViewById(R.id.txt_memory_name)
        mContactView = itemView.findViewById(R.id.txt_memory_contact)
        mImage = itemView.findViewById(R.id.iv_main_image)
    }

    fun bind(entityKeeper: EntityKeeper) {
        mNameView?.text = entityKeeper.name
        mContactView?.text = entityKeeper.contact
    }

}

interface onMemoryListListener {
    fun onClickMemoryList(id: String)
}