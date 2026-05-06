package adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nammahomestay.R
import models.LocalSpot

class LocalSpotAdapter(
    private val spots: MutableList<LocalSpot>
) : RecyclerView.Adapter<LocalSpotAdapter.SpotViewHolder>() {

    class SpotViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvSpotName: TextView        = itemView.findViewById(R.id.tv_spot_name)
        val tvSpotType: TextView        = itemView.findViewById(R.id.tv_spot_type)
        val tvSpotDescription: TextView = itemView.findViewById(R.id.tv_spot_description)
        val tvSpotDistance: TextView    = itemView.findViewById(R.id.tv_spot_distance)
        val tvBestTime: TextView        = itemView.findViewById(R.id.tv_best_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            SpotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_local_spot, parent, false)
        return SpotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpotViewHolder, position: Int) {
        val spot = spots[position]

        val emoji = getEmojiForType(spot.type)
        holder.tvSpotName.text        = "$emoji ${spot.name}"
        holder.tvSpotType.text        = spot.type
        holder.tvSpotDescription.text = spot.description
        holder.tvSpotDistance.text    = "${spot.distanceKm} km away"
        holder.tvBestTime.text        = "⏰ Best: ${spot.bestTime}"

        // Open Google Maps on click
        holder.itemView.setOnClickListener {
            if (spot.mapsLink.isNotEmpty()) {
                val mapIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse(spot.mapsLink))
                holder.itemView.context.startActivity(mapIntent)
            }
        }
    }

    private fun getEmojiForType(type: String): String {
        return when (type.lowercase()) {
            "waterfall" -> "💧"
            "viewpoint" -> "🏔️"
            "temple"    -> "🛕"
            "farm"      -> "🌾"
            "market"    -> "🏪"
            else        -> "📍"
        }
    }

    override fun getItemCount() = spots.size
}