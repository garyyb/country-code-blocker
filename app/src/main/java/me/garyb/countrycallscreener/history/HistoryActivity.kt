package me.garyb.countrycallscreener.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.garyb.countrycallscreener.R

class HistoryActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history)

    findViewById<BottomNavigationView>(R.id.bottom_nav_view).apply {
      selectedItemId = R.id.call_log
      setOnNavigationItemSelectedListener { menuItem ->
        when {
          menuItem.itemId == R.id.blocker -> {
            findNavController(R.id.history_nav_host_fragment).navigate(R.id.history_fragment_to_blocker_activity)
            finish()
            true
          }
          menuItem.itemId == R.id.call_log -> {
            true
          }
          else -> false
        }
      }
    }
  }
}
