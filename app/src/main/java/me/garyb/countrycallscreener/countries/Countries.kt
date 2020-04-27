package me.garyb.countrycallscreener.countries

import android.content.Context
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import me.garyb.countrycallscreener.util.SingletonHolder
import java.util.*

class Countries private constructor(context: Context) {
  private val locale: Locale = context.resources.configuration.locales[0]
  private val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.createInstance(context)

  companion object : SingletonHolder<Countries, Context>(::Countries)

  fun getLocalizedCountries(): List<Country> =
    Locale.getISOCountries().map { isoCountryCode ->
      Country(
        isoCountryCode = isoCountryCode,
        phoneCountryCode = phoneNumberUtil.getCountryCodeForRegion(isoCountryCode),
        displayName = Locale.getAvailableLocales().firstOrNull { it.country == isoCountryCode }?.getDisplayCountry(
          locale
        ) ?: ""
      )
    }.filter { country ->
      country.displayName.isNotEmpty()
    }.sortedBy { country ->
      country.displayName
    }
}