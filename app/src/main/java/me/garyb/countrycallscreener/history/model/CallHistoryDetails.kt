package me.garyb.countrycallscreener.history.model

data class CallHistoryDetails(
  val formattedPhoneNumber: String,
  val countryDisplayName: String,
  val creationTimeMs: Long)