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
import utils.FirebaseDbManager

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

        // Real-time Firebase connection
        FirebaseDbManager.listenToListings { remoteListings ->
            if (isAdded) {
                listings.clear()
                if (remoteListings.isEmpty()) {
                    loadDummyData()
                } else {
                    listings.addAll(remoteListings)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadDummyData() {
        listings.add(HomeStay("1", "Malnad Heritage Home", "Sakleshpur", "Experience authentic Malnad hospitality in a traditional ancestral home.", "", 2500.0, true, listOf("https://images.unsplash.com/photo-1588668214407-6ea9a6d8c272?w=800")))
        listings.add(HomeStay("2", "Coorg Coffee Estate", "Madikeri", "Stay amidst lush coffee plantations with a private waterfall nearby.", "", 3200.0, true, listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800")))
        listings.add(HomeStay("3", "River Side Retreat", "Chikmagalur", "Perfect for nature lovers. Relax right next to the Hemavathi river bank.", "", 1800.0, true, listOf("https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=800")))
        listings.add(HomeStay("4", "Western Ghats Eco Stay", "Sirsi", "An eco-friendly stay deep in the forest for a true wilderness experience.", "", 2200.0, true, listOf("https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=800")))
        listings.add(HomeStay("5", "Coastal Palms Homestay", "Gokarna", "Relax by the pristine beaches with tropical palm tree views and home-cooked seafood.", "", 1500.0, true, listOf("https://images.unsplash.com/photo-1540202404-a2f29016bb5d?w=800")))
        listings.add(HomeStay("6", "Forest View Haven", "Kodagu", "Wake up to stunning mountain views and deep forest trails. Perfect for bird watching.", "", 2800.0, true, listOf("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800")))
        listings.add(HomeStay("7", "Sunrise Beach Villa", "Mangalore", "A luxurious beach-front villa where you can watch the sunrise from your balcony.", "", 3500.0, true, listOf("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800")))
        listings.add(HomeStay("8", "Green Valley Farm Stay", "Hassan", "Get hands-on experience with organic farming and enjoy garden-fresh vegetables.", "", 2000.0, true, listOf("https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800")))
        
        // Added 3 more listings to make a total of 11
        listings.add(HomeStay("9", "Mountain Peak Lodge", "Kudremukh", "Enjoy the mist and clouds at this hilltop lodge. Trekker's paradise with breathtaking valley views.", "", 2600.0, true, listOf("https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0?w=800")))
        listings.add(HomeStay("10", "Ancient Heritage Mansion", "Hampi", "Stay in a restored 19th-century mansion close to the ancient ruins of Hampi. Architecture meets history.", "", 3000.0, true, listOf("https://images.unsplash.com/photo-1524230507669-5ff9e996bb5e?w=800")))
        listings.add(HomeStay("11", "Quiet Backwater Cottage", "Udupi", "A serene cottage facing the calm backwaters. Experience coastal silence and enjoy fresh coconut water.", "", 1900.0, true, listOf("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800")))

        adapter.notifyDataSetChanged()
    }
}
