package utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import models.MenuItem

object FirebaseDbManager {
    private val db = FirebaseFirestore.getInstance()
    private val menuCollection = db.collection("daily_menu")
    private val profileCollection = db.collection("profiles")

    // --- Menu Operations ---
    fun saveMenuItem(item: MenuItem, onComplete: (Boolean) -> Unit) {
        val docRef = if (item.id.isEmpty()) {
            menuCollection.document()
        } else {
            menuCollection.document(item.id)
        }
        
        item.id = docRef.id
        docRef.set(item)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteMenuItem(itemId: String, onComplete: (Boolean) -> Unit) {
        menuCollection.document(itemId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun listenToMenu(onUpdate: (List<MenuItem>) -> Unit) {
        menuCollection.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null) return@addSnapshotListener
            
            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject<MenuItem>()?.apply { id = doc.id }
            }
            onUpdate(items)
        }
    }

    // --- Availability Operations ---
    fun updateAvailability(hostId: String, isAvailable: Boolean, onComplete: (Boolean) -> Unit) {
        profileCollection.document(hostId).update("isAvailable", isAvailable)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { 
                // If document doesn't exist, create it
                profileCollection.document(hostId).set(mapOf("isAvailable" to isAvailable))
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }
    }

    fun listenToAvailability(hostId: String, onUpdate: (Boolean) -> Unit) {
        profileCollection.document(hostId).addSnapshotListener { snapshot, _ ->
            val available = snapshot?.getBoolean("isAvailable") ?: true
            onUpdate(available)
        }
    }
}
