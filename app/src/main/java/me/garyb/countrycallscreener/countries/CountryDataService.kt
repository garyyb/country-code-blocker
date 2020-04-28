package me.garyb.countrycallscreener.countries

import android.content.Context
import android.content.SharedPreferences
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import me.garyb.countrycallscreener.BlockedCountriesChangedEvent
import me.garyb.countrycallscreener.util.SingletonHolder
import org.greenrobot.eventbus.EventBus
import java.util.*

class CountryDataService private constructor(context: Context) {
  private val locale: Locale = context.resources.configuration.locales[0]
  private val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.createInstance(context)
  private val blockedCountriesSharedPreferences: SharedPreferences = context.getSharedPreferences(
    BLOCKED_COUNTRIES_SHARED_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE
  )

  companion object : SingletonHolder<CountryDataService, Context>(::CountryDataService) {
    private val BLOCKED_COUNTRIES_SHARED_PREFERENCES_FILE_KEY =
      "BLOCKED_COUNTRIES_SHARED_PREFERENCES_FILE_KEY"
  }

  private fun notifyBlockedCountriesChanged() {
    EventBus.getDefault().post(BlockedCountriesChangedEvent)
  }

  fun getAllCountries(): List<Country> =
    Locale.getISOCountries().map { isoCountryCode ->
      Country(
        isoCountryCode = isoCountryCode,
        phoneCountryCode = phoneNumberUtil.getCountryCodeForRegion(isoCountryCode),
        displayName = Locale.getAvailableLocales().firstOrNull { it.country == isoCountryCode }?.getDisplayCountry(
          locale
        ) ?: ""
      )
    }.filter { country ->
      country.displayName.isNotEmpty() && country.phoneCountryCode != 0
    }.sortedBy { country ->
      country.displayName
    }

  fun getBlockedCountries(): List<Country> =
    getAllCountries().filter { country ->
      blockedCountriesSharedPreferences.contains(country.isoCountryCode)
    }

  fun getAllowedCountries(): List<Country> =
    getAllCountries().filterNot { country ->
      blockedCountriesSharedPreferences.contains(country.isoCountryCode)
    }

  fun addBlockedCountry(country: Country): Boolean =
    with(blockedCountriesSharedPreferences.edit()) {
      putBoolean(country.isoCountryCode, true)
      commit()
    }.also { success ->
      if (success) notifyBlockedCountriesChanged()
    }

  fun addBlockedCountries(countries: List<Country>): Boolean =
    with(blockedCountriesSharedPreferences.edit()) {
      countries.forEach { country -> putBoolean(country.isoCountryCode, true) }
      commit()
    }.also { success ->
      if (success) notifyBlockedCountriesChanged()
    }

  fun removeBlockedCountry(country: Country): Boolean =
    with(blockedCountriesSharedPreferences.edit()) {
      remove(country.isoCountryCode)
      commit()
    }.also { success ->
      if (success) notifyBlockedCountriesChanged()
    }

  fun removeBlockedCountries(countries: List<Country>): Boolean =
    with(blockedCountriesSharedPreferences.edit()) {
      countries.forEach { country -> remove(country.isoCountryCode) }
      commit()
    }.also { success ->
      if (success) notifyBlockedCountriesChanged()
    }
}