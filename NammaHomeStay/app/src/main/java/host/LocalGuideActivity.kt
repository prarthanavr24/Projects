package host

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nammahomestay.R
import adapters.LocalSpotAdapter
import models.LocalSpot
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class LocalGuideActivity : AppCompatActivity() {

    private lateinit var rvSpots: RecyclerView
    private lateinit var adapter: LocalSpotAdapter
    private val spots = mutableListOf<LocalSpot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_guide)

        rvSpots = findViewById(R.id.rv_local_spots)
        rvSpots.layoutManager = LinearLayoutManager(this)
        adapter = LocalSpotAdapter(spots)
        rvSpots.adapter = adapter

        findViewById<ExtendedFloatingActionButton>(R.id.fab_add_spot)
            .setOnClickListener { showAddSpotDialog() }

        loadDummySpots()
    }

    private fun loadDummySpots() {
        spots.add(LocalSpot("Devimane Waterfalls", "Waterfall", 3.5).apply {
            description = "A hidden gem through the coffee estate trail."
            bestTime    = "Morning 6-8 AM"
        })
        spots.add(LocalSpot("Sunset Viewpoint", "Viewpoint", 1.2).apply {
            description = "Amazing 180° view of the Western Ghats."
            bestTime    = "Evening 5-6 PM"
        })
        spots.add(LocalSpot("Organic Spice Farm", "Farm", 0.5).apply {
            description = "Walk through cardamom, pepper and vanilla plants."
            bestTime    = "Anytime"
        })
        adapter.notifyDataSetChanged()
    }

    private fun showAddSpotDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val etName = EditText(this).apply { hint = "Spot Name (e.g., Devimane Falls)" }
        val etType = EditText(this).apply { hint = "Type (Waterfall/Viewpoint/Temple/Farm)" }
        val etDistance = EditText(this).apply {
            hint = "Distance in km (e.g., 3.5)"
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val etDescription = EditText(this).apply {
            hint = "Short description for guests"
            minLines = 2
        }
        val etBestTime = EditText(this).apply {
            hint = "Best time (e.g., Morning 6-8 AM)"
        }

        layout.addView(etName)
        layout.addView(etType)
        layout.addView(etDistance)
        layout.addView(etDescription)
        layout.addView(etBestTime)

        AlertDialog.Builder(this)
            .setTitle("🗺️ Add Secret Spot")
            .setView(layout)
            .setPositiveButton("Add Spot") { _, _ ->
                val name    = etName.text.toString().trim()
                val type    = etType.text.toString().trim()
                val distStr = etDistance.text.toString().trim()

                if (name.isEmpty() || type.isEmpty() || distStr.isEmpty()) {
                    Toast.makeText(this,
                        "Please fill required fields",
                        Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val spot = LocalSpot(name, type, distStr.toDouble()).apply {
                    description = etDescription.text.toString().trim()
                    bestTime    = etBestTime.text.toString().trim()
                }
                spots.add(spot)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Spot added! 🌟", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}