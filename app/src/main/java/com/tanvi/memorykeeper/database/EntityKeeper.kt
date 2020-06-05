package com.tanvi.memorykeeper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tanvi.memorykeeper.dataclass.Note


@Entity(tableName = "MemoryKeeper")
data class EntityKeeper(
    @ColumnInfo(name = "profile")
    val profile: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "contact")
    val contact: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "sociallink")
    val social: String ,

    @TypeConverters(Convert::class)
    @ColumnInfo(name = "notelist")
    val notelist:ArrayList<Note>

) {
    @PrimaryKey(autoGenerate = true)
    var Id: Int = 0
}

