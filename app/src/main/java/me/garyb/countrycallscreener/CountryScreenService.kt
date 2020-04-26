package me.garyb.countrycallscreener

import android.icu.text.MessageFormat
import android.net.Uri
import android.os.Binder
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber

class CountryScreenService : CallScreeningService() {
  private val binder: ServiceBinder = ServiceBinder()
  private lateinit var phoneNumberUtil: PhoneNumberUtil
  private lateinit var defaultCountryCode: String

  private var notification_id = 0

  inner class ServiceBinder : Binder() {
    fun getService(): CountryScreenService = this@CountryScreenService
  }

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
      NotificationCompat.Builder(this, MainActivity.BLOCKED_CALLS_NOTIFICATION_CHANNEL)
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
      notify(notification_id++, builder.build())
    }
  }

  override fun onCreate() {
    Log.i("CountryCallScreener", "CountryScreenService created")
    phoneNumberUtil = PhoneNumberUtil.createInstance(this)
    defaultCountryCode = resources.configuration.locales[0].country
  }

  override fun onScreenCall(callDetails: Call.Details) {
    Log.i(
      "CountryCallScreener",
      "Screening call from " + Uri.decode(callDetails.handle.toString())
    )

    val number: Phonenumber.PhoneNumber =
      phoneNumberUtil.parse(Uri.decode(callDetails.handle.toString()), defaultCountryCode)

    if (number.hasCountryCode() &&
      number.countryCode == phoneNumberUtil.getCountryCodeForRegion("CN")
    ) {
      respondDisallow(callDetails)
      showBlockedNotification(number)
    } else {
      respondAllow(callDetails)
    }
  }
}
