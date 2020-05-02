package me.garyb.countrycallscreener.blocker

import me.garyb.countrycallscreener.countries.Country

data class BlockCountriesEvent(val countriesToBlock: List<Country>)