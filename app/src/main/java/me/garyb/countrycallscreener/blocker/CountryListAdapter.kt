package me.garyb.countrycallscreener.blocker

import android.icu.text.MessageFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import me.garyb.countrycallscreener.R
import me.garyb.countrycallscreener.countries.Country
import org.greenrobot.eventbus.EventBus

class CountryListAdapter(private val countryList: List<Country>, private val blocked: Boolean) :
  RecyclerView.Adapter<CountryListAdapter.ListItemViewHolder>() {

  class ListItemViewHolder(private val view: View) :
    RecyclerView.ViewHolder(view) {

    fun bind(country: Country, blocked: Boolean) {
      view.findViewById<TextView>(R.id.country_name).apply {
        text = country.displayName
      }

      view.findViewById<TextView>(R.id.country_phone_code).apply {
        text = MessageFormat.format(
          view.context.resources.getString(R.string.phone_country_code),
          mapOf<String, String>(
            "phoneCountryCode" to country.phoneCountryCode.toString()
          )
        )
      }

      view.findViewById<MaterialButton>(R.id.toggle_block_button).apply {
        text = context.getString(if (blocked) R.string.unblock_button else R.string.block_button)
        icon =
          context.getDrawable(if (blocked) R.drawable.round_check_24 else R.drawable.round_block_24)
        setOnClickListener {
          EventBus.getDefault().post(
            if (blocked) UnblockCountriesEvent(listOf(country))
            else BlockCountriesEvent(listOf(country))
          )
        }
      }
    }

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
    val listItem: View = LayoutInflater.from(parent.context)
      .inflate(R.layout.country_list_item, parent, false)

    return ListItemViewHolder(listItem)
  }

  override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
    holder.bind(countryList[position], blocked)
  }

  override fun getItemCount(): Int = countryList.size
}