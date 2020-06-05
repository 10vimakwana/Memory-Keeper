package com.tanvi.memorykeeper.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    val title: String,
    val note: String,
    val image: String,
    val date:String
) {
    @PrimaryKey(autoGenerate = true)
    var Id: Int = 0
}