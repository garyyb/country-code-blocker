package me.garyb.countrycallscreener.blocker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BlockerViewPagerAdapter(fm: FragmentManager) :
  FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

  companion object {
    private const val NUM_TABS: Int = 2
  }

  override fun getCount(): Int =
    NUM_TABS

  override fun getItem(position: Int): Fragment =
    if (position == 0) AllowedCountriesFragment.newInstance()
    else BlockedCountriesFragment.newInstance()
}