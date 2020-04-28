package me.garyb.countrycallscreener

import me.garyb.countrycallscreener.countries.Country

data class BlockCountriesEvent(val countriesToBlock: List<Country>)