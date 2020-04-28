package me.garyb.countrycallscreener

import android.content.Context
import android.icu.text.MessageFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.garyb.countrycallscreener.countries.Country
import me.garyb.countrycallscreener.countries.CountryDataService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Fragment which shows a list of allowed countries and provides options to block them.
 *
 * Use the [AllowedCountriesFragment.newInstance] factory method to create an instance of this
 * fragment.
 */
class AllowedCountriesFragment : Fragment() {
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
    return inflater.inflate(R.layout.fragment_allowed_countries, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.allowedCountries.observe(viewLifecycleOwner, Observer { allowedCountries ->
      view.findViewById<RecyclerView>(R.id.allowed_countries_recycler_view).apply {
        adapter = CountryListAdapter(allowedCountries, false)
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
  fun onBlockCountriesEvent(blockCountriesEvent: BlockCountriesEvent) {
    blockCountriesEvent.countriesToBlock.forEach { country ->
      val countries: List<Country> =
        viewModel.allowedCountries.value!!.filter { it.phoneCountryCode == country.phoneCountryCode }

      val blockFn = {
        countryDataService.addBlockedCountries(countries)
        viewModel.addBlockedCountries(countries)
        viewModel.removeAllowedCountries(countries)
      }

      if (countries.size > 1) {
        MaterialAlertDialogBuilder(context)
          .setTitle(getString(R.string.multiple_block_dialog_title))
          .setMessage(
            MessageFormat.format(
              getString(R.string.multiple_block_dialog_description),
              mapOf(
                "selectedCountry" to country.displayName,
                "otherSelectedCountries" to countries.filterNot { it == country }.joinToString { it.displayName }
              )
            )
          )
          .setPositiveButton(getString(R.string.multiple_block_dialog_confirm)) { _, _ ->
            blockFn()
          }
          .setNegativeButton(getString(R.string.multiple_block_dialog_cancel)) { _, _ -> }
          .show()
      } else {
        blockFn()
      }
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
