package me.garyb.countrycallscreener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Fragment which shows a list of blocked countries and provides options to allow them.
 *
 * Use the [BlockedCountriesFragment.newInstance] factory method to create an instance of this
 * fragment.
 */
class BlockedCountriesFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_blocked_countries, container, false)
  }

  companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AllowedCountriesFragment.
     */
    @JvmStatic
    fun newInstance() =
      BlockedCountriesFragment().apply {
        arguments = Bundle().apply {}
      }
  }
}
