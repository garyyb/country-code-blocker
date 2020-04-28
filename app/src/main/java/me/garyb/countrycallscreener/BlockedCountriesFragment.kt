package me.garyb.countrycallscreener

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import me.garyb.countrycallscreener.countries.Country
import me.garyb.countrycallscreener.countries.CountryDataService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Fragment which shows a list of blocked countries and provides options to allow them.
 *
 * Use the [BlockedCountriesFragment.newInstance] factory method to create an instance of this
 * fragment.
 */
class BlockedCountriesFragment : Fragment() {
  private lateinit var countryDataService: CountryDataService
  private lateinit var viewModel: CountriesViewModel

  override fun onAttach(context: Context) {
    super.onAttach(context)
    countryDataService = CountryDataService.getInstance(context)
    val model: CountriesViewModel by activityViewModels {
      CountriesViewModel.Factory(
        countryDataService.getAllowedCountries(), countryDataService.getBlockedCountries()
      )
    }
    viewModel = model
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_blocked_countries, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel.blockedCountries.observe(viewLifecycleOwner, Observer { blockedCountries ->
      view.findViewById<RecyclerView>(R.id.blocked_countries_recycler_view).apply {
        adapter = CountryListAdapter(blockedCountries, true)
        if (itemDecorationCount == 0) {
          addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
      }
    })
  }

  override fun onStart() {
    super.onStart()
    EventBus.getDefault().register(this)
  }

  override fun onStop() {
    EventBus.getDefault().unregister(this)
    super.onStop()
  }

  @Subscribe
  fun onUnblockCountriesEvent(unblockCountriesEvent: UnblockCountriesEvent) {
    unblockCountriesEvent.countriesToUnblock.forEach { country ->
      val countries: List<Country> =
        viewModel.blockedCountries.value!!.filter { it.phoneCountryCode == country.phoneCountryCode }
      countryDataService.removeBlockedCountries(countries)
      viewModel.addAllowedCountries(countries)
      viewModel.removeBlockedCountries(countries)
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
      BlockedCountriesFragment().apply {
        arguments = Bundle().apply {}
      }
  }
}
