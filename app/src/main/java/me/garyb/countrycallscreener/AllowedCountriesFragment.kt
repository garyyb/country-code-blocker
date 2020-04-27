package me.garyb.countrycallscreener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import me.garyb.countrycallscreener.countries.Countries

/**
 * Fragment which shows a list of allowed countries and provides options to block them.
 *
 * Use the [AllowedCountriesFragment.newInstance] factory method to create an instance of this
 * fragment.
 */
class AllowedCountriesFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_allowed_countries, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    view.findViewById<RecyclerView>(R.id.allowed_countries_recycler_view).apply {
      adapter =
        CountryListAdapter(Countries.getInstance(view.context).getLocalizedCountries(), false)
      addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }
  }

  companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AllowedCountriesFragment.
     */
    @JvmStatic
    fun newInstance() =
      AllowedCountriesFragment().apply {
        arguments = Bundle().apply {}
      }
  }
}
