package me.garyb.countrycallscreener

import android.icu.text.MessageFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import me.garyb.countrycallscreener.countries.Country

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

      view.findViewById<SwitchMaterial>(R.id.blocked_switch).apply {
        isChecked = blocked
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