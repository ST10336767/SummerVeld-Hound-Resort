package com.summerveldhoundresort.app.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.summerveldhoundresort.app.EditDogActivity
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.db.entities.Dog
import com.summerveldhoundresort.app.ui.admin.CreateDogActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ManageDogsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ManageDogsAdapter
    private lateinit var backButton: Button
    private val firestore = FirebaseFirestore.getInstance()
    private val dogList = mutableListOf<Dog>()
    private var realTimeListener: ListenerRegistration? = null

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

        // Load dogs and set up real-time listener
        loadDogs()
        setupRealTimeListener()
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

    private fun setupRealTimeListener() {
        // Set up real-time listener for dogs collection
        realTimeListener = firestore.collection("dogs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("ManageDogsFragment", "Real-time listener error", error)
                    return@addSnapshotListener
                }

                snapshot?.let { docs ->
                    try {
                        dogList.clear()
                        for (doc in docs.documents) {
                            val dog = doc.toObject(Dog::class.java)
                            if (dog != null) {
                                dog.dogID = doc.id
                                dogList.add(dog)
                            }
                        }
                        if (::adapter.isInitialized) {
                            adapter.notifyDataSetChanged()
                        }
                        android.util.Log.d("ManageDogsFragment", "Real-time update: ${dogList.size} dogs loaded")
                    } catch (e: Exception) {
                        android.util.Log.e("ManageDogsFragment", "Error processing real-time dogs data", e)
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the real-time listener
        realTimeListener?.remove()
        realTimeListener = null
    }
}
