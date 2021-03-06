package me.garyb.countrycallscreener.blocker

import android.content.Intent
import android.icu.text.MessageFormat
import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.garyb.countrycallscreener.R
import me.garyb.countrycallscreener.countries.Country
import me.garyb.countrycallscreener.countries.CountryDataService
import me.garyb.countrycallscreener.history.database.CallHistoryDatabase
import me.garyb.countrycallscreener.history.database.CallHistoryDatabaseProvider
import me.garyb.countrycallscreener.history.model.CallHistoryEntity

class CountryScreenService : CallScreeningService() {
  private lateinit var phoneNumberUtil: PhoneNumberUtil
  private lateinit var defaultCountryCode: String
  private lateinit var blockedCountries: List<Country>
  private lateinit var callHistoryDatabase: CallHistoryDatabase

  private var notificationId = 0

  private fun respondAllow(callDetails: Call.Details) {
    respondToCall(
      callDetails, CallResponse.Builder()
        .setDisallowCall(false)
        .setRejectCall(false)
        .setSilenceCall(false)
        .setSkipCallLog(false)
        .setSkipNotification(false)
        .build()
    )
  }

  private fun respondDisallow(callDetails: Call.Details) {
    respondToCall(
      callDetails, CallResponse.Builder()
        .setDisallowCall(true)
        .setRejectCall(false)
        .setSilenceCall(true)
        .setSkipCallLog(true)
        .setSkipNotification(true)
        .build()
    )
  }

  private fun showBlockedNotification(phoneNumber: Phonenumber.PhoneNumber) {
    val builder: NotificationCompat.Builder =
      NotificationCompat.Builder(
        this,
        BlockerActivity.BLOCKED_CALLS_NOTIFICATION_CHANNEL
      )
        .setSmallIcon(R.drawable.ic_blocked_call_notification)
        .setContentTitle(getString(R.string.blocked_call_notification_title))
        .setContentText(
          MessageFormat.format(
            getString(R.string.blocked_call_notification_text),
            mapOf<String, String>(
              "phoneNumber" to phoneNumberUtil.format(
                phoneNumber,
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
              )
            )
          )
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // ignored for API Level 26+.
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(this)) {
      notify(notificationId++, builder.build())
    }
  }

  private fun storeBlockedNumber(callDetails: Call.Details) {
    GlobalScope.launch {
      callHistoryDatabase.callHistoryDao().insertAll(
        CallHistoryEntity(
          phoneUri = Uri.decode(callDetails.handle.toString()),
          creationTimeMs = callDetails.creationTimeMillis
        )
      )
    }
  }

  override fun onCreate() {
    Log.i("CountryCallScreener", "CountryScreenService created")
    phoneNumberUtil = PhoneNumberUtil.createInstance(this)
    defaultCountryCode = resources.configuration.locales[0].country
    blockedCountries = CountryDataService.getInstance(this).getBlockedCountries()
    callHistoryDatabase = CallHistoryDatabaseProvider.getInstance(applicationContext).db
  }

  // We can't pass data through binding because CallScreeningService::onBind already returns its
  // own binder. So we use onStartCommand as a 'signal' to refetch the data. Main thread code can
  // then just call startService() to notify the service to re-fetch data.
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    blockedCountries = CountryDataService.getInstance(this).getBlockedCountries()
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onScreenCall(callDetails: Call.Details) {
    Log.i(
      "CountryCallScreener",
      "Screening call from " + Uri.decode(callDetails.handle.toString())
    )

    val number: Phonenumber.PhoneNumber =
      phoneNumberUtil.parse(Uri.decode(callDetails.handle.toString()), defaultCountryCode)

    if (number.hasCountryCode() &&
      blockedCountries.find { it.phoneCountryCode == number.countryCode } != null
    ) {
      respondDisallow(callDetails)
      showBlockedNotification(number)
      storeBlockedNumber(callDetails)
    } else {
      respondAllow(callDetails)
    }
  }
}
