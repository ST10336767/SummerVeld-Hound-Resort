package com.example.summerveldhoundresort.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        adapter = SavedDogAdapter(dogList)
        recyclerView.adapter = adapter
        loadSavedDogs()
        return view
    }

    private fun loadSavedDogs() {
        firestore.collection("dogs")
            .get()
            .addOnSuccessListener { snapshot ->
                dogList.clear()
                for (doc in snapshot.documents) {
                    val dog = doc.toObject(Dog::class.java)
                    if (dog != null) {
                        dog.dogID = doc.id
                        dogList.add(dog)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("SavedDogsFragment", "Error loading dogs", e)
            }
    }

}
