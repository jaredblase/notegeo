package com.mobdeve.s15.group5.notegeo.models

import androidx.room.*

@Dao
interface LabelDao {
    @Query("SELECT * FROM label")
    fun getAll(): List<Label>

    @Query("SELECT * FROM label WHERE _id IN (:labelIds)")
    fun loadAllByIds(labelIds: IntArray): List<Label>

    @Query("SELECT * FROM label WHERE label LIKE :labelName")
    fun findByLabelName(labelName: String): Label

    @Insert
    fun insertAll(vararg labels: Label)

    @Update
    fun updateLabel(label: Label)

    @Delete
    fun deleteLabel(label: Label)
}