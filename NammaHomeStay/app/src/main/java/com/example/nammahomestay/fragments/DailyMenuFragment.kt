package com.example.nammahomestay.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
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

class DailyMenuFragment : Fragment() {

    private lateinit var rvMenu: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: MenuAdapter
    private val menuItems = mutableListOf<MenuItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_daily_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<TextView>(R.id.tv_date).text = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date())
        rvMenu = view.findViewById(R.id.rv_menu)
        emptyState = view.findViewById(R.id.empty_state)

        rvMenu.layoutManager = LinearLayoutManager(requireContext())
        adapter = MenuAdapter(menuItems, { item -> showAddDishDialog(item) }, { item -> deleteDish(item) })
        rvMenu.adapter = adapter

        view.findViewById<ExtendedFloatingActionButton>(R.id.fab_add_dish).setOnClickListener { showAddDishDialog(null) }

        FirebaseDbManager.listenToMenu { items ->
            if (isAdded) {
                menuItems.clear()
                if (items.isEmpty()) {
                    loadDefaultMenu()
                } else {
                    menuItems.addAll(items)
                }
                updateEmptyState()
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadDefaultMenu() {
        // Adding the Pizza you requested as a default if the menu is empty
        menuItems.add(MenuItem(
            dishName = "Fresh Oven Pizza",
            category = "Special",
            price = 299.0,
            isVeg = true,
            description = "Oven-fresh pizza made with a crispy crust, rich mozzarella cheese, savory sauce, and fresh toppings for a perfect cheesy delight."
        ))
    }

    private fun showAddDishDialog(existingItem: MenuItem?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(if (existingItem == null) "🍽️ Add Dish" else "✏️ Edit Dish")

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_dish, null)
        builder.setView(dialogView)

        val etDishName = dialogView.findViewById<EditText>(R.id.et_dish_name_dialog)
        val etDesc = dialogView.findViewById<EditText>(R.id.et_dish_desc_dialog)
        val etPrice = dialogView.findViewById<EditText>(R.id.et_dish_price_dialog)
        val etImageUrl = dialogView.findViewById<EditText>(R.id.et_dish_image_url_dialog)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_category)
        val switchVeg = dialogView.findViewById<SwitchMaterial>(R.id.switch_veg)

        val categories = arrayOf("Breakfast", "Lunch", "Dinner", "Snacks", "Special")
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

        existingItem?.let {
            etDishName.setText(it.dishName)
            etDesc.setText(it.description)
            etPrice.setText(it.price.toString())
            etImageUrl.setText(it.imageUrl)
            switchVeg.isChecked = it.isVeg
            val pos = categories.indexOf(it.category)
            if (pos != -1) spinner.setSelection(pos)
        }

        builder.setPositiveButton("Save") { _, _ ->
            val name = etDishName.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()
            
            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(requireContext(), "Name and Price required", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val item = existingItem?.copy() ?: MenuItem()
            item.dishName = name
            item.description = etDesc.text.toString().trim()
            item.price = priceStr.toDoubleOrNull() ?: 0.0
            item.imageUrl = etImageUrl.text.toString().trim()
            item.category = spinner.selectedItem.toString()
            item.isVeg = switchVeg.isChecked

            FirebaseDbManager.saveMenuItem(item) { success ->
                if (isAdded && success) Toast.makeText(requireContext(), "Dish Saved!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null).show()
    }

    private fun deleteDish(item: MenuItem) {
        if (item.id.isEmpty()) {
            menuItems.remove(item)
            adapter.notifyDataSetChanged()
            updateEmptyState()
            return
        }
        FirebaseDbManager.deleteMenuItem(item.id) { success ->
            if (isAdded && success) Toast.makeText(requireContext(), "Removed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEmptyState() {
        val isEmpty = menuItems.isEmpty()
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        rvMenu.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}
