package com.example.nammahomestay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nammahomestay.R
import adapters.InquiryAdapter
import models.Inquiry

// Temporary global storage so data doesn't reset when switching tabs
object InquiryDataRepo {
    val inquiries = mutableListOf<Inquiry>()

    init {
        inquiries.add(Inquiry("1", "Prarthana", "9008124215", "Interested in a 2-night stay. Is it available?", "Jun 15", "Jun 17", 3, false))
        inquiries.add(Inquiry("2", "Prathiksha", "6363843718", "Do you serve home cooked food?", "Jun 20", "Jun 21", 2, true))
        inquiries.add(Inquiry("3", "Appu", "9632114219", "Planning a solo trip. Single room?", "Jul 01", "Jul 03", 1, false))
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

        adapter = InquiryAdapter(InquiryDataRepo.inquiries, requireContext()) { clickedInquiry ->
            markAsRead(clickedInquiry.id)
        }
        rvInquiries.adapter = adapter

        updateCounts()
    }

    private fun updateCounts() {
        val total = InquiryDataRepo.inquiries.size
        val unread = InquiryDataRepo.inquiries.count { !it.isRead }
        tvTotal.text = total.toString()
        tvUnread.text = unread.toString()
    }

    private fun markAsRead(inquiryId: String) {
        val inquiry = InquiryDataRepo.inquiries.find { it.id == inquiryId }
        if (inquiry != null && !inquiry.isRead) {
            inquiry.isRead = true
            updateCounts()
            adapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Marked as read", Toast.LENGTH_SHORT).show()
        }
    }
}
