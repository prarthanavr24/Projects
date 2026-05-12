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

class HomeStayDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_homestay_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get data from arguments
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
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.color.primary_light)
                .into(ivImage)
        } else {
            ivImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        btnInquire.setOnClickListener {
            Toast.makeText(requireContext(), "Inquiry sent to $name! 🙏", Toast.LENGTH_LONG).show()
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
