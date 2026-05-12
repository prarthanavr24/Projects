package com.example.nammahomestay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nammahomestay.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // Set default fragment on first launch
        if (savedInstanceState == null) {
            loadFragment(HomeProfileFragment())
            bottomNav.selectedItemId = R.id.nav_profile
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_profile   -> HomeProfileFragment()
                R.id.nav_menu      -> DailyMenuFragment()
                R.id.nav_inquiries -> InquiryBoxFragment()
                R.id.nav_guide     -> LocalGuideFragment()
                R.id.nav_listings  -> HomeStayListFragment()
                else -> HomeProfileFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
