package me.garyb.countrycallscreener.history

import android.icu.text.MessageFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.garyb.countrycallscreener.R
import me.garyb.countrycallscreener.history.model.CallHistoryDetails
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class HistoryListAdapter(private val callHistoryDetailsList: List<CallHistoryDetails>) :
  RecyclerView.Adapter<HistoryListAdapter.ListItemViewHolder>() {

  class ListItemViewHolder(private val view: View) :
    RecyclerView.ViewHolder(view) {

    fun bind(callHistoryDetails: CallHistoryDetails) {
      view.findViewById<TextView>(R.id.call_history_phone_number).apply {
        text = callHistoryDetails.formattedPhoneNumber
      }

      view.findViewById<TextView>(R.id.call_history_country_name).apply {
        text = callHistoryDetails.countryDisplayName
      }

      val creationInstant: Instant = Instant.ofEpochMilli(callHistoryDetails.creationTimeMs)
      val todayInstant: Instant = Instant.now() // TODO: Inject clock using DI for testing.
      view.findViewById<TextView>(R.id.call_history_creation_time).apply {
        // TODO: Use ICU Date Formatting.
        text =
          when {
            todayInstant.truncatedTo(ChronoUnit.DAYS) == creationInstant.truncatedTo(ChronoUnit.DAYS) -> SimpleDateFormat(
              "h:mm a",
              resources.configuration.locales[0]
            ).format(Date.from(creationInstant))
            todayInstant.truncatedTo(ChronoUnit.YEARS) == creationInstant.truncatedTo(ChronoUnit.YEARS) -> SimpleDateFormat(
              "MMM d",
              resources.configuration.locales[0]
            ).format(Date.from(creationInstant))
            else -> SimpleDateFormat(
              "MMM d yyyy",
              resources.configuration.locales[0]
            ).format(Date.from(creationInstant))
          }
      }
    }

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
    val listItem: View = LayoutInflater.from(parent.context)
      .inflate(R.layout.call_history_list_item, parent, false)

    return ListItemViewHolder(listItem)
  }

  override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
    holder.bind(callHistoryDetailsList[position])
  }

  override fun getItemCount(): Int = callHistoryDetailsList.size

}