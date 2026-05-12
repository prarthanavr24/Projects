package com.example.nammahomestay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nammahomestay.R
import adapters.ListingAdapter
import models.HomeStay

class HomeStayListFragment : Fragment() {

    private lateinit var rvListings: RecyclerView
    private lateinit var adapter: ListingAdapter
    private val listings = mutableListOf<HomeStay>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_homestay_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvListings = view.findViewById(R.id.rv_homestay_listings)
        rvListings.layoutManager = LinearLayoutManager(requireContext())

        adapter = ListingAdapter(listings) { homestay ->
            // Correctly passing data to the Detail Fragment using the updated newInstance
            val detailFragment = HomeStayDetailFragment.newInstance(
                name = homestay.hostName,
                location = homestay.location,
                price = homestay.dailyRate,
                description = homestay.description,
                imageUrl = if (homestay.photoUrls.isNotEmpty()) homestay.photoUrls[0] else ""
            )

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        rvListings.adapter = adapter

        if (listings.isEmpty()) {
            loadDummyData()
        }
    }

    private fun loadDummyData() {
        listings.add(HomeStay(
            hostName = "Malnad Heritage Home",
            location = "Sakleshpur",
            dailyRate = 2500.0,
            description = "Experience authentic Malnad hospitality with home-cooked food. This traditional home offers a unique glimpse into the local culture and architecture.",
            isAvailable = true,
            photoUrls = listOf("https://images.unsplash.com/photo-1588668214407-6ea9a6d8c272?q=80&w=1000&auto=format&fit=crop")
        ))
        listings.add(HomeStay(
            hostName = "Coorg Coffee Estate Stay",
            location = "Madikeri",
            dailyRate = 3200.0,
            description = "Stay amidst lush coffee plantations with a private waterfall nearby. Wake up to the aroma of fresh coffee and enjoy estate walks.",
            isAvailable = true,
            photoUrls = listOf("https://images.unsplash.com/photo-1501333194171-aa639a67a80a?q=80&w=1000&auto=format&fit=crop")
        ))
        listings.add(HomeStay(
            hostName = "River Side Retreat",
            location = "Chikmagalur",
            dailyRate = 1800.0,
            description = "Budget friendly stay right next to the Hemavathi river. Perfect for groups looking for adventure and nature.",
            isAvailable = false,
            photoUrls = listOf("https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?q=80&w=1000&auto=format&fit=crop")
        ))
        adapter.notifyDataSetChanged()
    }
}
