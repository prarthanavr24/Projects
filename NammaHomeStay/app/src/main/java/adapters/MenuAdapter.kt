package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nammahomestay.R
import models.MenuItem

class MenuAdapter(
    private val items: MutableList<MenuItem>,
    private val onEdit: (MenuItem) -> Unit,
    private val onDelete: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvDishName: TextView    = itemView.findViewById(R.id.tv_dish_name)
        val tvCategory: TextView    = itemView.findViewById(R.id.tv_category)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvPrice: TextView       = itemView.findViewById(R.id.tv_price)
        val tvVegBadge: TextView    = itemView.findViewById(R.id.tv_veg_badge)
        val ivDishImage: ImageView  = itemView.findViewById(R.id.iv_dish_image)
        val btnEdit: ImageButton    = itemView.findViewById(R.id.btn_edit_dish)
        val btnDelete: ImageButton  = itemView.findViewById(R.id.btn_delete_dish)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = items[position]

        holder.tvDishName.text    = item.dishName
        holder.tvCategory.text    = item.category
        holder.tvDescription.text = item.description
        holder.tvPrice.text       = "₹ ${item.price.toInt()}"

        // Load image with Glide
        if (item.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.color.primary_light)
                .into(holder.ivDishImage)
        } else {
            holder.ivDishImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Veg / Non-veg badge
        if (item.isVeg) {
            holder.tvVegBadge.text = "🟢 VEG"
            holder.tvVegBadge.setBackgroundResource(R.color.green_badge)
        } else {
            holder.tvVegBadge.text = "🔴 NON-VEG"
            holder.tvVegBadge.setBackgroundResource(R.color.red_badge)
        }

        holder.btnEdit.setOnClickListener   { onEdit(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = items.size
}