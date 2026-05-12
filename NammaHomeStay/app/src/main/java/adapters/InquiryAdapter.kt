package adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.nammahomestay.R
import models.Inquiry
import com.google.android.material.button.MaterialButton

class InquiryAdapter(
    private val inquiries: MutableList<Inquiry>,
    private val context: Context,
    private val onInquiryInteracted: (Inquiry) -> Unit
) : RecyclerView.Adapter<InquiryAdapter.InquiryViewHolder>() {

    class InquiryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val cardView: CardView          = itemView as CardView
        val tvTravelerName: TextView    = itemView.findViewById(R.id.tv_traveler_name)
        val tvMessage: TextView         = itemView.findViewById(R.id.tv_message)
        val tvDates: TextView           = itemView.findViewById(R.id.tv_dates)
        val tvGuests: TextView          = itemView.findViewById(R.id.tv_guests)
        val tvNewBadge: TextView        = itemView.findViewById(R.id.tv_new_badge)
        val btnCall: MaterialButton     = itemView.findViewById(R.id.btn_call_traveler)
        val btnWhatsApp: MaterialButton = itemView.findViewById(R.id.btn_whatsapp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            InquiryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inquiry, parent, false)
        return InquiryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InquiryViewHolder, position: Int) {
        val inquiry = inquiries[position]

        holder.tvTravelerName.text = inquiry.travelerName
        holder.tvMessage.text      = inquiry.message
        holder.tvDates.text        = "📅 ${inquiry.checkIn} – ${inquiry.checkOut}"
        holder.tvGuests.text       = "👥 ${inquiry.guests} guests"

        // Mark as read when clicking the card
        holder.cardView.setOnClickListener {
            onInquiryInteracted(inquiry)
        }

        // Appearance based on read status
        if (inquiry.isRead) {
            holder.tvNewBadge.visibility = View.GONE
            holder.cardView.setCardBackgroundColor(0xFFFFFFFF.toInt()) // White
        } else {
            holder.tvNewBadge.visibility = View.VISIBLE
            holder.cardView.setCardBackgroundColor(0xFFFFF3E0.toInt()) // Light Orange
        }

        // 📞 Call button
        holder.btnCall.setOnClickListener {
            onInquiryInteracted(inquiry) // Also mark as read
            val phone = inquiry.travelerPhone
            if (phone.isNotEmpty()) {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                context.startActivity(callIntent)
            }
        }

        // 💬 WhatsApp button
        holder.btnWhatsApp.setOnClickListener {
            onInquiryInteracted(inquiry) // Also mark as read
            val phone = inquiry.travelerPhone
            val msg = "Namaste ${inquiry.travelerName}! Received your inquiry for ${inquiry.checkIn}."
            try {
                val waIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://wa.me/91$phone?text=${Uri.encode(msg)}"))
                context.startActivity(waIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = inquiries.size
}
