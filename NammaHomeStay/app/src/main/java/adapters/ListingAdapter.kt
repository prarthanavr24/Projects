package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nammahomestay.R
import models.HomeStay

class ListingAdapter(
    private val listings: List<HomeStay>,
    private val onItemClick: (HomeStay) -> Unit
) : RecyclerView.Adapter<ListingAdapter.ListingViewHolder>() {

    class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView      = itemView.findViewById(R.id.iv_homestay_image)
        val tvName: TextView        = itemView.findViewById(R.id.tv_homestay_name)
        val tvPrice: TextView       = itemView.findViewById(R.id.tv_price)
        val tvLocation: TextView    = itemView.findViewById(R.id.tv_location)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvAvailability: TextView = itemView.findViewById(R.id.tv_availability)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_homestay_listing, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val homestay = listings[position]
        
        holder.tvName.text = homestay.hostName
        holder.tvPrice.text = "₹ ${homestay.dailyRate.toInt()}/day"
        holder.tvLocation.text = "📍 ${homestay.location}"
        holder.tvDescription.text = homestay.description
        
        if (homestay.isAvailable) {
            holder.tvAvailability.text = "🟢 Available Now"
            holder.tvAvailability.setTextColor(holder.itemView.context.getColor(R.color.green_badge))
        } else {
            holder.tvAvailability.text = "🔴 Fully Booked"
            holder.tvAvailability.setTextColor(holder.itemView.context.getColor(R.color.red_badge))
        }

        if (homestay.photoUrls.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(homestay.photoUrls[0])
                .placeholder(R.color.primary_light)
                .into(holder.ivImage)
        } else {
            holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.itemView.setOnClickListener { onItemClick(homestay) }
    }

    override fun getItemCount() = listings.size
}
