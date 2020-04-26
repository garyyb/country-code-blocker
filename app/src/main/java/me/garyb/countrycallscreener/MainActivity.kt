package me.garyb.countrycallscreener

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telecom.CallScreeningService
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
  private val CALL_SCREEN_REQUEST_CODE: Int = 1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    requestCallScreeningRole()
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
