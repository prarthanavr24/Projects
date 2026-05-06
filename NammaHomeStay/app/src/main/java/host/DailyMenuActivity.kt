package host

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nammahomestay.R
import adapters.MenuAdapter
import models.MenuItem
import utils.FirebaseDbManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyMenuActivity : AppCompatActivity() {

    private lateinit var rvMenu: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: MenuAdapter
    private val menuItems = mutableListOf<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_menu)

        // Set today's date
        findViewById<TextView>(R.id.tv_date).text =
            SimpleDateFormat("EEE, dd MMM yyyy",
                Locale.getDefault()).format(Date())

        rvMenu     = findViewById(R.id.rv_menu)
        emptyState = findViewById(R.id.empty_state)

        rvMenu.layoutManager = LinearLayoutManager(this)
        adapter = MenuAdapter(
            menuItems,
            { item -> showAddDishDialog(item) },
            { item -> deleteMenuItem(item) }
        )
        rvMenu.adapter = adapter

        findViewById<ExtendedFloatingActionButton>(R.id.fab_add_dish)
            .setOnClickListener { showAddDishDialog(null) }

        // listen to real-time updates from Firebase
        FirebaseDbManager.listenToMenu { items ->
            menuItems.clear()
            menuItems.addAll(items)
            updateEmptyState()
            adapter.notifyDataSetChanged()
        }
    }


    private fun showAddDishDialog(existingItem: MenuItem?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (existingItem == null) "🍽️ Add Dish" else "✏️ Edit Dish")

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_dish, null)
        builder.setView(dialogView)

        val etDishName    = dialogView.findViewById<EditText>(R.id.et_dish_name_dialog)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_dish_desc_dialog)
        val etPrice       = dialogView.findViewById<EditText>(R.id.et_dish_price_dialog)
        val etImageUrl    = dialogView.findViewById<EditText>(R.id.et_dish_image_url_dialog)
        val spinner       = dialogView.findViewById<Spinner>(R.id.spinner_category)
        val switchVeg     = dialogView.findViewById<SwitchMaterial>(R.id.switch_veg)

        val categories = arrayOf("Breakfast", "Lunch", "Dinner", "Snacks", "Special")
        spinner.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, categories)

        existingItem?.let {
            etDishName.setText(it.dishName)
            etDescription.setText(it.description)
            etPrice.setText(it.price.toString())
            etImageUrl.setText(it.imageUrl)
            switchVeg.isChecked = it.isVeg
        }

        builder.setPositiveButton("Save") { _, _ ->
            val name     = etDishName.text.toString().trim()
            val desc     = etDescription.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()
            val imageUrl = etImageUrl.text.toString().trim()
            val category = spinner.selectedItem.toString()
            val isVeg    = switchVeg.isChecked

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this,
                    "Dish name and price are required",
                    Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (existingItem != null) {
                existingItem.dishName    = name
                existingItem.description = desc
                existingItem.price       = priceStr.toDouble()
                existingItem.imageUrl    = imageUrl
                existingItem.category    = category
                existingItem.isVeg       = isVeg
                FirebaseDbManager.saveMenuItem(existingItem) { success ->
                    if (success) Toast.makeText(this, "Dish updated! ✅", Toast.LENGTH_SHORT).show()
                }
            } else {
                val newItem = MenuItem(name, category, priceStr.toDouble(), isVeg)
                newItem.description = desc
                newItem.imageUrl = imageUrl
                FirebaseDbManager.saveMenuItem(newItem) { success ->
                    if (success) Toast.makeText(this, "Dish added! 🍛", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun deleteMenuItem(item: MenuItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete Dish?")
            .setMessage("Remove \"${item.dishName}\" from today's menu?")
            .setPositiveButton("Delete") { _, _ ->
                FirebaseDbManager.deleteMenuItem(item.id) { success ->
                    if (success) Toast.makeText(this, "Dish removed", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateEmptyState() {
        if (menuItems.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            rvMenu.visibility     = View.GONE
        } else {
            emptyState.visibility = View.GONE
            rvMenu.visibility     = View.VISIBLE
        }
    }
}