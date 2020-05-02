package me.garyb.countrycallscreener.blocker

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.garyb.countrycallscreener.R
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class BlockerActivity : AppCompatActivity() {
  companion object {
    const val BLOCKED_CALLS_NOTIFICATION_CHANNEL: String = "BLOCKED_CALLS"
    private const val CALL_SCREEN_REQUEST_CODE: Int = 1
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestCallScreeningRole()
    registerNotificationChannel()

    setContentView(R.layout.activity_blocker)

    findViewById<BottomNavigationView>(R.id.bottom_nav_view).apply {
      selectedItemId = R.id.blocker
      setOnNavigationItemSelectedListener { menuItem ->
        when {
          menuItem.itemId == R.id.blocker -> true
          menuItem.itemId == R.id.call_log -> {
            findNavController(R.id.nav_host_fragment).navigate(R.id.blocker_fragment_to_history_activity)
            true
          }
          else -> false
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    EventBus.getDefault().register(this)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    findViewById<FragmentContainerView>(R.id.nav_host_fragment).apply {
      updatePadding(top = window.decorView.rootWindowInsets.stableInsetTop)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == CALL_SCREEN_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        Log.i("CountryCallScreener", "Call Screen Role Request OK")
        startCallScreenService()
      } else {
        // Your app is not the call screening app, display an error message to the user.
      }
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun onStop() {
    EventBus.getDefault().unregister(this)
    super.onStop()
  }

  @Subscribe
  fun onBlockedCountriesChangedEvent(event: BlockedCountriesChangedEvent) {
    startCallScreenService()
  }

  private fun registerNotificationChannel() {
    val notificationManager: NotificationManager =
      getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel: NotificationChannel =
      NotificationChannel(
        BLOCKED_CALLS_NOTIFICATION_CHANNEL,
        getString(R.string.blocked_calls_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = getString(R.string.blocked_calls_notification_channel_description)
      }
    notificationManager.createNotificationChannel(channel)
  }

  @SuppressLint("WrongConstant")
  private fun requestCallScreeningRole() {
    val roleManager: RoleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
    val intent: Intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
    startActivityForResult(
      intent,
      CALL_SCREEN_REQUEST_CODE
    )
  }

  private fun startCallScreenService() {
    Intent(this, CountryScreenService::class.java).also { intent ->
      startService(intent)
    }
  }
}
