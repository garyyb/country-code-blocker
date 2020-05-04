package me.garyb.countrycallscreener.history.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.garyb.countrycallscreener.history.model.CallHistoryEntity

@Dao
interface CallHistoryDao {
  @Query("SELECT * FROM CallHistoryEntity ORDER BY creation_time_ms DESC")
  fun getAll(): LiveData<List<CallHistoryEntity>>

  @Insert
  suspend fun insertAll(vararg callHistoryEntities: CallHistoryEntity)

  @Delete
  suspend fun delete(callHistoryEntity: CallHistoryEntity)
}