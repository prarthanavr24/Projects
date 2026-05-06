package com.example.nammahomestay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import adapters.ListingAdapter
import models.HomeStay

class HomeStayListActivity : AppCompatActivity() {

    private lateinit var rvListings: RecyclerView
    private lateinit var adapter: ListingAdapter
    private val listings = mutableListOf<HomeStay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homestay_list)

        rvListings = findViewById(R.id.rv_homestay_listings)
        rvListings.layoutManager = LinearLayoutManager(this)
        
        adapter = ListingAdapter(listings) { homestay ->
            // Handle click
        }
        rvListings.adapter = adapter

        loadDummyData()
    }

    private fun loadDummyData() {
        listings.add(HomeStay(
            hostName = "Malnad Heritage Home",
            location = "Sakleshpur",
            dailyRate = 2500.0,
            description = "Experience authentic Malnad hospitality with home-cooked food.",
            isAvailable = true
        ))
        listings.add(HomeStay(
            hostName = "Coorg Coffee Estate Stay",
            location = "Madikeri",
            dailyRate = 3200.0,
            description = "Stay amidst lush coffee plantations with a private waterfall nearby.",
            isAvailable = true
        ))
        listings.add(HomeStay(
            hostName = "River Side Retreat",
            location = "Chikmagalur",
            dailyRate = 1800.0,
            description = "Budget friendly stay right next to the Hemavathi river.",
            isAvailable = false
        ))
        adapter.notifyDataSetChanged()
    }
}
