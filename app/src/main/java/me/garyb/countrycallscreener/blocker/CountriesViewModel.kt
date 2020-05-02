package me.garyb.countrycallscreener.blocker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.garyb.countrycallscreener.countries.Country

class CountriesViewModel(allowedCountries: List<Country>, blockedCountries: List<Country>) :
  ViewModel() {
  class Factory(
    private val allowedCountries: List<Country>,
    private val blockedCountries: List<Country>
  ) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
      CountriesViewModel(allowedCountries, blockedCountries) as T
  }

  val allowedCountries: MutableLiveData<List<Country>> =
    MutableLiveData<List<Country>>().apply {
      value = allowedCountries
    }

  val blockedCountries: MutableLiveData<List<Country>> =
    MutableLiveData<List<Country>>().apply {
      value = blockedCountries
    }

  fun addBlockedCountry(country: Country) {
    blockedCountries.value = blockedCountries.value!!.plus(country).sortedBy { it.displayName }
  }

  fun addBlockedCountries(countries: List<Country>) {
    blockedCountries.value = blockedCountries.value!!.plus(countries).sortedBy { it.displayName }
  }

  fun addAllowedCountry(country: Country) {
    allowedCountries.value = allowedCountries.value!!.plus(country).sortedBy { it.displayName }
  }

  fun addAllowedCountries(countries: List<Country>) {
    allowedCountries.value = allowedCountries.value!!.plus(countries).sortedBy { it.displayName }
  }

  fun removeBlockedCountry(country: Country) {
    blockedCountries.value = blockedCountries.value!!.minus(country).sortedBy { it.displayName }
  }

  fun removeBlockedCountries(countries: List<Country>) {
    blockedCountries.value = blockedCountries.value!!.minus(countries).sortedBy { it.displayName }
  }

  fun removeAllowedCountry(country: Country) {
    allowedCountries.value = allowedCountries.value!!.minus(country).sortedBy { it.displayName }
  }

  fun removeAllowedCountries(countries: List<Country>) {
    allowedCountries.value = allowedCountries.value!!.minus(countries).sortedBy { it.displayName }
  }
}