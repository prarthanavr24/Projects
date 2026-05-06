package com.example.nammahomestay

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import host.DailyMenuActivity
import host.HomeProfileActivity
import host.InquiryBoxActivity
import host.LocalGuideActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // Open Home Profile by default
        openActivity(HomeProfileActivity::class.java)
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_profile   -> openActivity(HomeProfileActivity::class.java)
                R.id.nav_menu      -> openActivity(DailyMenuActivity::class.java)
                R.id.nav_inquiries -> openActivity(InquiryBoxActivity::class.java)
                R.id.nav_guide     -> openActivity(LocalGuideActivity::class.java)
                R.id.nav_listings  -> openActivity(HomeStayListActivity::class.java)
            }
            true
        }
    }

    private fun openActivity(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }
}