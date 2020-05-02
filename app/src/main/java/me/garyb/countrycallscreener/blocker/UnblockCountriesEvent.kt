package me.garyb.countrycallscreener.blocker

import me.garyb.countrycallscreener.countries.Country

data class UnblockCountriesEvent(val countriesToUnblock: List<Country>)