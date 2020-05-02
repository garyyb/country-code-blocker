package me.garyb.countrycallscreener.blocker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import me.garyb.countrycallscreener.R

/**
 * Use the [BlockerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlockerFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_blocker, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val viewPager = view.findViewById<ViewPager>(R.id.main_view_pager).apply {
      adapter = BlockerViewPagerAdapter(childFragmentManager)
    }

    view.findViewById<TabLayout>(R.id.main_tab_layout).apply {
      setupWithViewPager(viewPager)
      getTabAt(0)!!.text = getString(R.string.allowed_countries_tab_title)
      getTabAt(0)!!.icon = resources.getDrawable(R.drawable.baseline_phone_forwarded_24, null)

      getTabAt(1)!!.text = getString(R.string.blocked_countries_tab_title)
      getTabAt(1)!!.icon = resources.getDrawable(R.drawable.baseline_phone_missed_24, null)
    }

    super.onViewCreated(view, savedInstanceState)
  }

  companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment BlockerFragment.
     */
    @JvmStatic
    fun newInstance() =
      BlockerFragment().apply {
        arguments = Bundle().apply {
        }
      }
  }
}
