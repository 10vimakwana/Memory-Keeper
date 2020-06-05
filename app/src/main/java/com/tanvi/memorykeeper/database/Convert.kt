package com.tanvi.memorykeeper.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tanvi.memorykeeper.dataclass.Note
import java.util.*

class Convert {
    @TypeConverter
    fun convertMapArr(list: ArrayList<Note?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
    @TypeConverter
    fun toMappedItem(value: String?): ArrayList<Note?>? {
        val listType = object : TypeToken<ArrayList<Note?>?>() {}.type
        return Gson().fromJson(
            value,
            listType
        )
    }

}