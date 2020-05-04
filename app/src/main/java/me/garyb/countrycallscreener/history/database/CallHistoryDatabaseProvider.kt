package me.garyb.countrycallscreener.history.database

import android.content.Context
import androidx.room.Room
import me.garyb.countrycallscreener.util.SingletonHolder

class CallHistoryDatabaseProvider private constructor(applicationContext: Context) {
  val db =
    Room.databaseBuilder(applicationContext, CallHistoryDatabase::class.java, "call-history.db")
      .build()

  companion object :
    SingletonHolder<CallHistoryDatabaseProvider, Context>(::CallHistoryDatabaseProvider)
}