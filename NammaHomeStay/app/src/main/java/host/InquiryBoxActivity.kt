package host

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nammahomestay.R
import adapters.InquiryAdapter
import models.Inquiry

class InquiryBoxActivity : AppCompatActivity() {

    private lateinit var rvInquiries: RecyclerView
    private lateinit var adapter: InquiryAdapter
    private lateinit var tvTotal: TextView
    private lateinit var tvUnread: TextView
    private val inquiries = mutableListOf<Inquiry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquiry_box)

        tvTotal     = findViewById(R.id.tv_total_inquiries)
        tvUnread    = findViewById(R.id.tv_unread_inquiries)
        rvInquiries = findViewById(R.id.rv_inquiries)

        rvInquiries.layoutManager = LinearLayoutManager(this)
        adapter = InquiryAdapter(inquiries, this)
        rvInquiries.adapter = adapter

        loadDummyInquiries()
    }

    private fun loadDummyInquiries() {
        inquiries.add(Inquiry().apply {
            travelerName  = "Prarthana"
            travelerPhone = "9008124215"
            message       = "Interested in a 2-night stay with family. Is the room available?"
            checkIn       = "Jun 15"
            checkOut      = "Jun 17"
            guests        = 3
            isRead        = false
        })

        inquiries.add(Inquiry().apply {
            travelerName  = "Prathiksha"
            travelerPhone = "6363843718"
            message       = "Looking for a peaceful weekend stay. Do you serve home cooked food?"
            checkIn       = "Jun 20"
            checkOut      = "Jun 21"
            guests        = 2
            isRead        = true
        })

        inquiries.add(Inquiry().apply {
            travelerName  = "Appu"
            travelerPhone = "9632114219"
            message       = "Planning a solo trip. Can I get a single room?"
            checkIn       = "Jul 01"
            checkOut      = "Jul 03"
            guests        = 1
            isRead        = false
        })

        val unread = inquiries.count { !it.isRead }
        tvTotal.text  = inquiries.size.toString()
        tvUnread.text = unread.toString()
        adapter.notifyDataSetChanged()
    }

    fun markAsRead(inquiryId: String) {
        inquiries.find { it.id == inquiryId }?.isRead = true
        adapter.notifyDataSetChanged()
    }
}