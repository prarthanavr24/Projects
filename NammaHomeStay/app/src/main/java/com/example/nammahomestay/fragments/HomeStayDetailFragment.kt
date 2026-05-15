package com.example.nammahomestay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.nammahomestay.R
import com.google.android.material.button.MaterialButton
import models.Inquiry
import utils.FirebaseDbManager

class HomeStayDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_homestay_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name") ?: "HomeStay"
        val location = arguments?.getString("location") ?: "Unknown"
        val price = arguments?.getDouble("price", 0.0) ?: 0.0
        val description = arguments?.getString("description") ?: ""
        val imageUrl = arguments?.getString("imageUrl") ?: ""

        val ivImage = view.findViewById<ImageView>(R.id.iv_detail_image)
        val tvName = view.findViewById<TextView>(R.id.tv_detail_name)
        val tvLocation = view.findViewById<TextView>(R.id.tv_detail_location)
        val tvPrice = view.findViewById<TextView>(R.id.tv_detail_price)
        val tvDescription = view.findViewById<TextView>(R.id.tv_detail_description)
        val btnInquire = view.findViewById<MaterialButton>(R.id.btn_inquire_now)
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.detail_toolbar)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tvName.text = name
        tvLocation.text = "📍 $location"
        tvPrice.text = "₹ ${price.toInt()}/day"
        tvDescription.text = description

        if (imageUrl.isNotEmpty()) {
            Glide.with(this).load(imageUrl).placeholder(R.color.primary_light).into(ivImage)
        }

        btnInquire.setOnClickListener {
            // SEND REAL INQUIRY TO FIREBASE
            val newInquiry = Inquiry().apply {
                travelerName = "Guest User"
                message = "I am interested in booking $name. Please contact me."
                checkIn = "Jul 01"
                checkOut = "Jul 05"
                guests = 2
                isRead = false
            }

            btnInquire.isEnabled = false
            btnInquire.text = "Sending..."

            FirebaseDbManager.sendInquiry(newInquiry) { success ->
                if (isAdded) {
                    btnInquire.isEnabled = true
                    btnInquire.text = "Inquire Now"
                    if (success) {
                        Toast.makeText(requireContext(), "Inquiry sent to $name! 🙏", Toast.LENGTH_LONG).show()
                        parentFragmentManager.popBackStack() // Go back to list
                    } else {
                        Toast.makeText(requireContext(), "Failed to send inquiry", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(name: String, location: String, price: Double, description: String, imageUrl: String): HomeStayDetailFragment {
            val fragment = HomeStayDetailFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("location", location)
            args.putDouble("price", price)
            args.putString("description", description)
            args.putString("imageUrl", imageUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
