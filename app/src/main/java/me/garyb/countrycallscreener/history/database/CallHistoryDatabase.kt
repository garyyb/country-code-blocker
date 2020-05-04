package me.garyb.countrycallscreener.history.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.garyb.countrycallscreener.history.dao.CallHistoryDao
import me.garyb.countrycallscreener.history.model.CallHistoryEntity

@Database(entities = [CallHistoryEntity::class], version = 1)
abstract class CallHistoryDatabase : RoomDatabase() {
  abstract fun callHistoryDao(): CallHistoryDao
}