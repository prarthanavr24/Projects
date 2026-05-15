package utils

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import models.HomeStay
import models.Inquiry
import models.MenuItem
import models.LocalSpot

object FirebaseDbManager {
    private const val TAG = "FirebaseDbManager"
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    private val menuCollection = db.collection("daily_menu")
    private val profileCollection = db.collection("profiles")
    private val listingsCollection = db.collection("listings")
    private val inquiriesCollection = db.collection("inquiries")
    private val spotsCollection = db.collection("local_spots")

    // --- Profile Operations ---
    fun saveProfile(hostId: String, profile: HomeStay, onComplete: (Boolean, String?) -> Unit) {
        val batch = db.batch()
        val profileRef = profileCollection.document(hostId)
        val listingRef = listingsCollection.document(hostId)
        
        batch.set(profileRef, profile)
        batch.set(listingRef, profile)
        
        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(true, null)
            } else {
                onComplete(false, task.exception?.message ?: "Firestore error")
            }
        }
    }

    fun getProfile(hostId: String, onUpdate: (HomeStay?) -> Unit) {
        profileCollection.document(hostId).addSnapshotListener { snapshot, _ ->
            onUpdate(snapshot?.toObject<HomeStay>())
        }
    }

    // --- Image Upload ---
    fun uploadImage(folderName: String, fileUri: Uri, onComplete: (String?, String?) -> Unit) {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val storageRef = storage.reference.child("$folderName/$fileName")
        
        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString(), null)
                }.addOnFailureListener { 
                    onComplete(null, "Failed to get download URL")
                }
            }
            .addOnFailureListener {
                onComplete(null, it.message ?: "Upload failed")
            }
    }

    // --- Inquiries Operations ---
    fun sendInquiry(inquiry: Inquiry, onComplete: (Boolean) -> Unit) {
        val docRef = inquiriesCollection.document()
        inquiry.id = docRef.id
        docRef.set(inquiry).addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun listenToInquiries(onUpdate: (List<Inquiry>) -> Unit) {
        inquiriesCollection.addSnapshotListener { snapshot, _ ->
            val items = snapshot?.documents?.mapNotNull { it.toObject<Inquiry>()?.apply { id = it.id } } ?: emptyList()
            onUpdate(items)
        }
    }

    fun markInquiryAsRead(inquiryId: String) {
        if (inquiryId.isNotEmpty() && !inquiryId.startsWith("dummy")) {
            inquiriesCollection.document(inquiryId).update("isRead", true)
        }
    }

    // --- Menu Operations ---
    fun saveMenuItem(item: MenuItem, onComplete: (Boolean) -> Unit) {
        val docRef = if (item.id.isEmpty()) menuCollection.document() else menuCollection.document(item.id)
        item.id = docRef.id
        docRef.set(item).addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun deleteMenuItem(itemId: String, onComplete: (Boolean) -> Unit) {
        if (itemId.isEmpty()) return
        menuCollection.document(itemId).delete().addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun listenToMenu(onUpdate: (List<MenuItem>) -> Unit) {
        menuCollection.addSnapshotListener { snapshot, _ ->
            val items = snapshot?.documents?.mapNotNull { it.toObject<MenuItem>()?.apply { id = it.id } } ?: emptyList()
            onUpdate(items)
        }
    }

    // --- Listings Operations ---
    fun listenToListings(onUpdate: (List<HomeStay>) -> Unit) {
        listingsCollection.addSnapshotListener { snapshot, _ ->
            val items = snapshot?.documents?.mapNotNull { it.toObject<HomeStay>()?.apply { id = it.id } } ?: emptyList()
            onUpdate(items)
        }
    }

    // --- Local Spots ---
    fun listenToSpots(onUpdate: (List<LocalSpot>) -> Unit) {
        spotsCollection.addSnapshotListener { snapshot, _ ->
            val items = snapshot?.documents?.mapNotNull { it.toObject<LocalSpot>() } ?: emptyList()
            onUpdate(items)
        }
    }

    fun saveSpot(spot: LocalSpot, onComplete: (Boolean) -> Unit) {
        spotsCollection.document().set(spot).addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    // --- Availability ---
    fun updateAvailability(hostId: String, isAvailable: Boolean, onComplete: (Boolean) -> Unit = {}) {
        profileCollection.document(hostId).update("isAvailable", isAvailable)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
        listingsCollection.document(hostId).update("isAvailable", isAvailable)
    }

    fun listenToAvailability(hostId: String, onUpdate: (Boolean) -> Unit) {
        profileCollection.document(hostId).addSnapshotListener { snapshot, _ ->
            onUpdate(snapshot?.getBoolean("isAvailable") ?: true)
        }
    }
}
