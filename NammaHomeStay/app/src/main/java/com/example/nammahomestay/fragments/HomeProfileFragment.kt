package com.example.nammahomestay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.nammahomestay.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import utils.FirebaseDbManager

class HomeProfileFragment : Fragment() {

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

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Glide.with(this).load(uri).into(ivCover)
            Toast.makeText(requireContext(), "Photo updated locally!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_home_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        view.findViewById<MaterialButton>(R.id.btn_save_profile)
            .setOnClickListener { saveProfile() }

        view.findViewById<MaterialButton>(R.id.btn_add_cover)
            .setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

        view.findViewById<MaterialButton>(R.id.btn_add_photos)
            .setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

        switchAvailable.setOnCheckedChangeListener { _, isChecked ->
            FirebaseDbManager.updateAvailability("my_homestay_id", isChecked) { success ->
                if (success) {
                    val status = if (isChecked) "Available" else "Booked"
                    Toast.makeText(requireContext(), "Status: $status", Toast.LENGTH_SHORT).show()
                }
            }
        }

        FirebaseDbManager.listenToAvailability("my_homestay_id") { isAvailable ->
            if (isAdded && switchAvailable.isChecked != isAvailable) {
                switchAvailable.isChecked = isAvailable
            }
        }
    }

    private fun initViews(view: View) {
        etHostName      = view.findViewById(R.id.et_host_name)
        etLocation      = view.findViewById(R.id.et_location)
        etPhone         = view.findViewById(R.id.et_phone)
        etRate          = view.findViewById(R.id.et_rate)
        etDescription   = view.findViewById(R.id.et_description)
        switchAvailable = view.findViewById(R.id.switch_available)
        cbCleanRoom     = view.findViewById(R.id.cb_clean_room)
        cbCleanToilet   = view.findViewById(R.id.cb_clean_toilet)
        cbMosquitoNet   = view.findViewById(R.id.cb_mosquito_net)
        cbHotWater      = view.findViewById(R.id.cb_hot_water)
        cbWifi          = view.findViewById(R.id.cb_wifi)
        cbParking       = view.findViewById(R.id.cb_parking)
        ivCover         = view.findViewById(R.id.iv_cover_photo)
    }

    private fun saveProfile() {
        val name     = etHostName.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val phone    = etPhone.text.toString().trim()
        val rate     = etRate.text.toString().trim()

        if (name.isEmpty() || location.isEmpty()
            || phone.isEmpty() || rate.isEmpty()) {
            Toast.makeText(requireContext(),
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

        Toast.makeText(requireContext(),
            "✅ Profile saved! $checkCount/6 checklist items done.",
            Toast.LENGTH_LONG).show()
    }
}
