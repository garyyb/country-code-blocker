package me.garyb.countrycallscreener.history.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CallHistoryEntity(
  @PrimaryKey(autoGenerate = true) val uid: Int? = null,
  @ColumnInfo(name = "phone_uri") val phoneUri: String,
  @ColumnInfo(name = "creation_time_ms") val creationTimeMs: Long
)