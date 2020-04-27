package me.garyb.countrycallscreener

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
  companion object {
    const val BLOCKED_CALLS_NOTIFICATION_CHANNEL: String = "BLOCKED_CALLS"
    private const val CALL_SCREEN_REQUEST_CODE: Int = 1
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestCallScreeningRole()
    registerNotificationChannel()

    setContentView(R.layout.activity_main)

    val viewPager: ViewPager = findViewById(R.id.main_view_pager)
    viewPager.adapter = MainViewPagerAdapter(supportFragmentManager)

    val tabLayout: TabLayout = findViewById(R.id.main_tab_layout)
    tabLayout.setupWithViewPager(viewPager)

    tabLayout.getTabAt(0)!!.text = getString(R.string.allowed_countries_tab_title)
    tabLayout.getTabAt(0)!!.icon = getDrawable(R.drawable.baseline_phone_forwarded_24)

    tabLayout.getTabAt(1)!!.text = getString(R.string.blocked_countries_tab_title)
    tabLayout.getTabAt(1)!!.icon = getDrawable(R.drawable.baseline_phone_missed_24)
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

  private fun requestCallScreeningRole() {
    val roleManager: RoleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
    val intent: Intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
    startActivityForResult(intent, CALL_SCREEN_REQUEST_CODE)
  }

  private fun startCallScreenService() {
    Intent(this, CountryScreenService::class.java).also { intent ->
      startService(intent)
    }
  }
}
