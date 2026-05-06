package host

import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.nammahomestay.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import utils.FirebaseDbManager

class HomeProfileActivity : AppCompatActivity() {

    private lateinit var etHostName: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etRate: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var switchAvailable: SwitchMaterial
    private lateinit var cbCleanRoom: CheckBox
    private lateinit var cbCleanToilet: CheckBox
    private lateinit var cbMosquitoNet: CheckBox
    private lateinit var cbHotWater: CheckBox
    private lateinit var cbWifi: CheckBox
    private lateinit var cbParking: CheckBox
    private lateinit var ivCover: ImageView

    // Register the photo picker activity launcher
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the photo picker.
        if (uri != null) {
            Glide.with(this).load(uri).into(ivCover)
            Toast.makeText(this, "Photo updated locally!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_profile)

        initViews()

        findViewById<MaterialButton>(R.id.btn_save_profile)
            .setOnClickListener { saveProfile() }

        findViewById<MaterialButton>(R.id.btn_add_cover)
            .setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

        findViewById<MaterialButton>(R.id.btn_add_photos)
            .setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

        // Real-time availability update
        switchAvailable.setOnCheckedChangeListener { _, isChecked ->
            FirebaseDbManager.updateAvailability("my_homestay_id", isChecked) { success ->
                if (success) {
                    val status = if (isChecked) "Available" else "Booked"
                    Toast.makeText(this, "Status: $status", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Listen for remote changes
        FirebaseDbManager.listenToAvailability("my_homestay_id") { isAvailable ->
            if (switchAvailable.isChecked != isAvailable) {
                switchAvailable.isChecked = isAvailable
            }
        }
    }

    private fun initViews() {
        etHostName      = findViewById(R.id.et_host_name)
        etLocation      = findViewById(R.id.et_location)
        etPhone         = findViewById(R.id.et_phone)
        etRate          = findViewById(R.id.et_rate)
        etDescription   = findViewById(R.id.et_description)
        switchAvailable = findViewById(R.id.switch_available)
        cbCleanRoom     = findViewById(R.id.cb_clean_room)
        cbCleanToilet   = findViewById(R.id.cb_clean_toilet)
        cbMosquitoNet   = findViewById(R.id.cb_mosquito_net)
        cbHotWater      = findViewById(R.id.cb_hot_water)
        cbWifi          = findViewById(R.id.cb_wifi)
        cbParking       = findViewById(R.id.cb_parking)
        ivCover         = findViewById(R.id.iv_cover_photo)
    }

    private fun saveProfile() {
        val name     = etHostName.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val phone    = etPhone.text.toString().trim()
        val rate     = etRate.text.toString().trim()

        if (name.isEmpty() || location.isEmpty()
            || phone.isEmpty() || rate.isEmpty()) {
            Toast.makeText(this,
                "Please fill all required fields",
                Toast.LENGTH_SHORT).show()
            return
        }

        var checkCount = 0
        if (cbCleanRoom.isChecked)   checkCount++
        if (cbCleanToilet.isChecked) checkCount++
        if (cbMosquitoNet.isChecked) checkCount++
        if (cbHotWater.isChecked)    checkCount++
        if (cbWifi.isChecked)        checkCount++
        if (cbParking.isChecked)     checkCount++

        Toast.makeText(this,
            "✅ Profile saved! $checkCount/6 checklist items done.",
            Toast.LENGTH_LONG).show()
    }
}