package me.garyb.countrycallscreener

import me.garyb.countrycallscreener.countries.Country

data class UnblockCountriesEvent(val countriesToUnblock: List<Country>)