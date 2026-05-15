package com.example.nammahomestay.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.nammahomestay.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import models.HomeStay
import utils.FirebaseDbManager
import java.util.concurrent.atomic.AtomicInteger

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
    private lateinit var llPhotosContainer: LinearLayout
    private lateinit var btnSave: MaterialButton

    private var selectedCoverUri: Uri? = null
    private val selectedRoomUris = mutableListOf<Uri>()
    private var cloudPhotoUrls = mutableListOf<String>()
    private var isSaving = false

    private val hostId = "my_homestay_id"

    private val pickCoverMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedCoverUri = uri
            Glide.with(this).load(uri).into(ivCover)
            Toast.makeText(requireContext(), "✅ Cover ready", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickRoomMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        if (uris.isNotEmpty()) {
            selectedRoomUris.clear()
            selectedRoomUris.addAll(uris)
            updatePhotosUI()
            Toast.makeText(requireContext(), "✅ ${uris.size} photos selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_home_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        
        FirebaseDbManager.getProfile(hostId) { profile ->
            // Safety: Don't refresh UI if we are currently saving/uploading to prevent "0 photos" bug
            if (isAdded && profile != null && !isSaving) {
                cloudPhotoUrls = profile.photoUrls.toMutableList()
                populateViews(profile)
            }
        }

        btnSave.setOnClickListener { if (!isSaving) startRobustUpload() }
        
        view.findViewById<MaterialButton>(R.id.btn_add_cover).setOnClickListener {
            pickCoverMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        
        view.findViewById<MaterialButton>(R.id.btn_add_photos).setOnClickListener {
            pickRoomMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun initViews(view: View) {
        etHostName = view.findViewById(R.id.et_host_name)
        etLocation = view.findViewById(R.id.et_location)
        etPhone = view.findViewById(R.id.et_phone)
        etRate = view.findViewById(R.id.et_rate)
        etDescription = view.findViewById(R.id.et_description)
        switchAvailable = view.findViewById(R.id.switch_available)
        cbCleanRoom = view.findViewById(R.id.cb_clean_room)
        cbCleanToilet = view.findViewById(R.id.cb_clean_toilet)
        cbMosquitoNet = view.findViewById(R.id.cb_mosquito_net)
        cbHotWater = view.findViewById(R.id.cb_hot_water)
        cbWifi = view.findViewById(R.id.cb_wifi)
        cbParking = view.findViewById(R.id.cb_parking)
        ivCover = view.findViewById(R.id.iv_cover_photo)
        llPhotosContainer = view.findViewById(R.id.ll_photos_container)
        btnSave = view.findViewById(R.id.btn_save_profile)
    }

    private fun populateViews(profile: HomeStay) {
        etHostName.setText(profile.hostName)
        etLocation.setText(profile.location)
        etPhone.setText(profile.phone)
        etRate.setText(profile.dailyRate.toString())
        etDescription.setText(profile.description)
        switchAvailable.isChecked = profile.isAvailable
        cbCleanRoom.isChecked = profile.cleanRoom
        cbCleanToilet.isChecked = profile.cleanToilet
        cbMosquitoNet.isChecked = profile.mosquitoNet
        cbHotWater.isChecked = profile.hotWater
        cbWifi.isChecked = profile.wifiAvailable
        cbParking.isChecked = profile.parkingAvailable
        
        if (profile.photoUrls.isNotEmpty()) {
            Glide.with(this).load(profile.photoUrls[0]).placeholder(R.color.primary_light).into(ivCover)
        }
        updatePhotosUI()
    }

    private fun updatePhotosUI() {
        llPhotosContainer.removeAllViews()
        // Show cloud room photos (skip index 0 cover)
        if (cloudPhotoUrls.size > 1) {
            for (i in 1 until cloudPhotoUrls.size) addPhotoToGallery(cloudPhotoUrls[i])
        }
        // Show local photos
        for (uri in selectedRoomUris) addPhotoToGallery(uri)
    }

    private fun addPhotoToGallery(source: Any) {
        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(300, 300).apply { setMargins(0, 0, 16, 0) }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        Glide.with(this).load(source).into(imageView)
        llPhotosContainer.addView(imageView)
    }

    private fun startRobustUpload() {
        isSaving = true
        btnSave.isEnabled = false
        btnSave.text = "Saving... ⏳"
        
        val newCoverUri = selectedCoverUri
        val roomUrisToUpload = selectedRoomUris.toList()
        
        var finalCoverUrl = if (cloudPhotoUrls.isNotEmpty()) cloudPhotoUrls[0] else ""
        val newlyUploadedRoomUrls = mutableListOf<String>()
        
        val totalToUpload = (if (newCoverUri != null) 1 else 0) + roomUrisToUpload.size
        
        if (totalToUpload == 0) {
            saveProfileFinal(cloudPhotoUrls)
            return
        }

        val completedCount = AtomicInteger(0)
        
        // Parallel Upload for Speed
        newCoverUri?.let {
            FirebaseDbManager.uploadImage("covers", it) { url, _ ->
                if (url != null) finalCoverUrl = url
                checkIfFinished(completedCount, totalToUpload, finalCoverUrl, newlyUploadedRoomUrls)
            }
        }

        for (uri in roomUrisToUpload) {
            FirebaseDbManager.uploadImage("rooms", uri) { url, _ ->
                if (url != null) synchronized(newlyUploadedRoomUrls) { newlyUploadedRoomUrls.add(url) }
                checkIfFinished(completedCount, totalToUpload, finalCoverUrl, newlyUploadedRoomUrls)
            }
        }
    }

    private fun checkIfFinished(counter: AtomicInteger, total: Int, coverUrl: String, newRooms: List<String>) {
        if (counter.incrementAndGet() == total) {
            val finalUrls = mutableListOf<String>()
            if (coverUrl.isNotEmpty()) finalUrls.add(coverUrl)
            
            // Merge existing cloud rooms with newly uploaded ones
            if (cloudPhotoUrls.size > 1) finalUrls.addAll(cloudPhotoUrls.drop(1))
            finalUrls.addAll(newRooms)
            
            saveProfileFinal(finalUrls)
        }
    }

    private fun saveProfileFinal(finalPhotoUrls: List<String>) {
        val profile = HomeStay(
            id = hostId,
            hostName = etHostName.text.toString().trim(),
            location = etLocation.text.toString().trim(),
            phone = etPhone.text.toString().trim(),
            dailyRate = etRate.text.toString().toDoubleOrNull() ?: 0.0,
            description = etDescription.text.toString().trim(),
            isAvailable = switchAvailable.isChecked,
            cleanRoom = cbCleanRoom.isChecked,
            cleanToilet = cbCleanToilet.isChecked,
            mosquitoNet = cbMosquitoNet.isChecked,
            hotWater = cbHotWater.isChecked,
            wifiAvailable = cbWifi.isChecked,
            parkingAvailable = cbParking.isChecked,
            photoUrls = finalPhotoUrls
        )

        FirebaseDbManager.saveProfile(hostId, profile) { success, error ->
            if (isAdded) {
                isSaving = false
                btnSave.isEnabled = true
                btnSave.text = "💾 Save My Home Profile"
                if (success) {
                    selectedCoverUri = null
                    selectedRoomUris.clear()
                    Toast.makeText(requireContext(), "✅ Saved ${finalPhotoUrls.size} Photos!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "❌ Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
