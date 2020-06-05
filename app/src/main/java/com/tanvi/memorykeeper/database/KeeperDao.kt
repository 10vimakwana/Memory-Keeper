package com.tanvi.memorykeeper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tanvi.memorykeeper.dataclass.Note


@Dao
interface KeeperDao {
    @Insert
    fun addMemery(entityKeeper: EntityKeeper)

    @Query("Select * from MemoryKeeper")
    fun getalldata(): List<EntityKeeper>

    @Query("Select * from MemoryKeeper WHERE id = :id")
    fun getDatabyid(id: String): List<EntityKeeper>

    @Query("UPDATE MemoryKeeper SET profile = :profile,name = :name,contact=:contact,email=:email,sociallink=:social,notelist=:arrayList WHERE id = :id ")
    fun updateMemory(
        id: String,
        profile: String?,
        name: String?,
        contact: String?,
        email: String?,
        social: String?,
        arrayList: ArrayList<Note>
    )

    @Query("UPDATE MemoryKeeper SET name = :name,contact=:contact,email=:email,sociallink=:social,notelist=:arrayList WHERE id = :id ")
    fun updateMemorywithoutimage(
        id: String,
        name: String?,
        contact: String?,
        email: String?,
        social: String?,
        arrayList: ArrayList<Note>
    )

    @Query("DELETE FROM memorykeeper WHERE id = :id")
    fun getDeletebyid(id: String)

}