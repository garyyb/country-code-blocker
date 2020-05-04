package me.garyb.countrycallscreener.history

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber

import me.garyb.countrycallscreener.R
import me.garyb.countrycallscreener.countries.CountryDataService
import me.garyb.countrycallscreener.history.database.CallHistoryDatabaseProvider
import me.garyb.countrycallscreener.history.model.CallHistoryDetails

/**
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
  private lateinit var countryDataService: CountryDataService
  private lateinit var phoneNumberUtil: PhoneNumberUtil

  override fun onAttach(context: Context) {
    super.onAttach(context)
    countryDataService = CountryDataService.getInstance(context)
    phoneNumberUtil = PhoneNumberUtil.createInstance(context)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_history, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    CallHistoryDatabaseProvider.getInstance(requireContext()).db.callHistoryDao().getAll().observe(
      viewLifecycleOwner, Observer { callHistoryEntities ->
        view.findViewById<RecyclerView>(R.id.call_history_recycler_view).apply {
          adapter = HistoryListAdapter(callHistoryEntities.map {
            val defaultRegion: String = resources.configuration.locales[0].country
            val phoneNumber: Phonenumber.PhoneNumber =
              phoneNumberUtil.parse(it.phoneUri, defaultRegion)
            CallHistoryDetails(
              formattedPhoneNumber = phoneNumberUtil.format(
                phoneNumber,
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
              ),
              countryDisplayName = countryDataService.getAllCountries().find { country ->
                country.phoneCountryCode == phoneNumber.countryCode
              }!!.displayName,
              creationTimeMs = it.creationTimeMs
            )
          })

          if (itemDecorationCount == 0) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
          }
        }
      })
  }

  companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    @JvmStatic
    fun newInstance() =
      HistoryFragment().apply {
        arguments = Bundle().apply {
        }
      }
  }
}
