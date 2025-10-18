package com.example.summerveldhoundresort.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.db.entities.Dog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedDogsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dogList = mutableListOf<Dog>()
    private lateinit var adapter: SavedDogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_saved_dogs, container, false)
        recyclerView = view.findViewById(R.id.recyclerSavedDogs)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        
        // Initialize adapter with click listener
        adapter = SavedDogAdapter(dogList) { dog ->
            onDogClicked(dog)
        }
        recyclerView.adapter = adapter
        loadSavedDogs()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up back button click listener
        view.findViewById<View>(R.id.buttonBack)?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadSavedDogs() {
        android.util.Log.d("SavedDogsFragment", "Loading dogs from Firestore...")
        firestore.collection("dogs")
            .get()
            .addOnSuccessListener { snapshot ->
                android.util.Log.d("SavedDogsFragment", "Successfully loaded ${snapshot.documents.size} dogs")
                dogList.clear()
                for (doc in snapshot.documents) {
                    val dog = doc.toObject(Dog::class.java)
                    if (dog != null) {
                        dog.dogID = doc.id
                        android.util.Log.d("SavedDogsFragment", "Loaded dog: ${dog.dogName}")
                        android.util.Log.d("SavedDogsFragment", "Image URI: '${dog.imageUri}'")
                        android.util.Log.d("SavedDogsFragment", "Image URI length: ${dog.imageUri.length}")
                        android.util.Log.d("SavedDogsFragment", "Is HTTP URL: ${dog.imageUri.startsWith("http")}")
                        dogList.add(dog)
                    }
                }
                adapter.notifyDataSetChanged()
                android.util.Log.d("SavedDogsFragment", "Adapter updated with ${dogList.size} dogs")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("SavedDogsFragment", "Error loading dogs", e)
            }
    }

    private fun onDogClicked(dog: Dog) {
        // Navigate to dog detail fragment with dog data
        val bundle = DogDetailFragment.createBundle(dog)
        findNavController().navigate(R.id.action_savedDogsFragment_to_dogDetailFragment, bundle)
    }

}
