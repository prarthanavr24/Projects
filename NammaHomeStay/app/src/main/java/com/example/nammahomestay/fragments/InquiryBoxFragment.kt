package com.example.nammahomestay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nammahomestay.R
import adapters.InquiryAdapter
import models.Inquiry
import utils.FirebaseDbManager

// Singleton to keep data alive during the app session
object InquiryRepo {
    val inquiries = mutableListOf<Inquiry>()

    init {
        inquiries.add(Inquiry("d1", "Prarthana", "9008124215", "Interested in a 2-night stay with family.", "Jun 15", "Jun 17", 3, false))
        inquiries.add(Inquiry("d2", "Prathiksha", "6363843718", "Looking for a peaceful weekend stay.", "Jun 20", "Jun 21", 2, true))
        inquiries.add(Inquiry("d3", "Ravi Kumar", "9876543210", "Planning a trip next month. Any discount?", "Jul 10", "Jul 17", 1, false))
        inquiries.add(Inquiry("d4", "Ananya", "8884445555", "Is there WiFi available for remote work?", "Aug 05", "Aug 08", 2, false))
        inquiries.add(Inquiry("d5", "Sandeep", "9900112233", "Do you have space for 5 people this weekend?", "Jun 25", "Jun 27", 5, false))
        inquiries.add(Inquiry("d6", "Meghana", "7766554433", "Is breakfast included in the daily rate?", "Jul 02", "Jul 04", 2, false))
    }
}

class InquiryBoxFragment : Fragment() {

    private lateinit var rvInquiries: RecyclerView
    private lateinit var adapter: InquiryAdapter
    private lateinit var tvTotal: TextView
    private lateinit var tvUnread: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_inquiry_box, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTotal     = view.findViewById(R.id.tv_total_inquiries)
        tvUnread    = view.findViewById(R.id.tv_unread_inquiries)
        rvInquiries = view.findViewById(R.id.rv_inquiries)

        rvInquiries.layoutManager = LinearLayoutManager(requireContext())

        adapter = InquiryAdapter(InquiryRepo.inquiries, requireContext()) { clickedInquiry ->
            // Update locally for instant count change
            if (!clickedInquiry.isRead) {
                clickedInquiry.isRead = true
                updateCounts()
                adapter.notifyDataSetChanged()

                // If it's a real Firebase inquiry, update cloud
                if (!clickedInquiry.id.startsWith("d")) {
                    FirebaseDbManager.markInquiryAsRead(clickedInquiry.id)
                }
            }
        }
        rvInquiries.adapter = adapter

        // Sync with real Firebase inquiries
        FirebaseDbManager.listenToInquiries { remoteInquiries ->
            if (isAdded) {
                remoteInquiries.forEach { remote ->
                    val existing = InquiryRepo.inquiries.find { it.id == remote.id }
                    if (existing != null) {
                        existing.isRead = remote.isRead
                    } else {
                        InquiryRepo.inquiries.add(remote)
                    }
                }
                updateCounts()
                adapter.notifyDataSetChanged()
            }
        }

        updateCounts()
    }

    private fun updateCounts() {
        tvTotal.text = InquiryRepo.inquiries.size.toString()
        tvUnread.text = InquiryRepo.inquiries.count { !it.isRead }.toString()
    }
}
