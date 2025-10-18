package com.example.summerveldhoundresort.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.summerveldhoundresort.EditDogActivity
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.db.entities.Dog
import com.example.summerveldhoundresort.ui.admin.CreateDogActivity
import com.google.firebase.firestore.FirebaseFirestore

class ManageDogsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ManageDogsAdapter
    private lateinit var backButton: Button
    private val firestore = FirebaseFirestore.getInstance()
    private val dogList = mutableListOf<Dog>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_manage_dogs, container, false)

        recyclerView = view.findViewById(R.id.recyclerManageDogs)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val backButton: Button = view.findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            try {
                // Try to finish the activity safely
                if (isAdded && !requireActivity().isFinishing) {
                    requireActivity().finish()
                }
            } catch (e: Exception) {
                // If there's an error, try alternative navigation
                try {
                    requireActivity().onBackPressed()
                } catch (e2: Exception) {
                    // Last resort - just log the error
                    android.util.Log.e("ManageDogsFragment", "Error handling back button", e2)
                }
            }
        }

        // Set up Add Dog button click listener
        val addDogButton: Button = view.findViewById(R.id.buttonAddDog)
        addDogButton.setOnClickListener {
            try {
                if (isAdded && !requireActivity().isFinishing) {
                    val intent = Intent(requireContext(), CreateDogActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                android.util.Log.e("ManageDogsFragment", "Error starting CreateDogActivity", e)
            }
        }



        adapter = ManageDogsAdapter(dogList) { dog ->
            try {
                if (isAdded && !requireActivity().isFinishing) {
                    val intent = Intent(requireContext(), EditDogActivity::class.java)
                    intent.putExtra("dog", dog) // Pass the full Dog object
                    startActivity(intent)
                }
            } catch (e: Exception) {
                android.util.Log.e("ManageDogsFragment", "Error starting EditDogActivity", e)
            }
        }
        recyclerView.adapter = adapter

        loadDogs()
        return view
    }

    private fun loadDogs() {
        firestore.collection("dogs")
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    dogList.clear()
                    for (doc in snapshot.documents) {
                        val dog = doc.toObject(Dog::class.java)
                        if (dog != null) {
                            dog.dogID = doc.id
                            dogList.add(dog)
                        }
                    }
                    if (::adapter.isInitialized) {
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ManageDogsFragment", "Error processing dogs data", e)
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("ManageDogsFragment", "Error loading dogs", e)
            }
    }
}
