package me.garyb.countrycallscreener

import android.net.Uri
import android.os.Binder
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber

class CountryScreenService : CallScreeningService() {
  private val binder: ServiceBinder = ServiceBinder()
  private lateinit var phoneNumberUtil: PhoneNumberUtil
  private lateinit var defaultCountryCode: String

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
    } else {
      respondAllow(callDetails)
    }
  }
}
