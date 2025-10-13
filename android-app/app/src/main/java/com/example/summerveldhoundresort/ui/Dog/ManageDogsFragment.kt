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
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val backButton: Button = view.findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            requireActivity().finish() // finish ManageDogsActivity, go back to AdminActivity
        }



        adapter = ManageDogsAdapter(dogList) { dog ->
            val intent = Intent(requireContext(), EditDogActivity::class.java)
            intent.putExtra("dog", dog) // Pass the full Dog object
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        loadDogs()
        return view
    }

    private fun loadDogs() {
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
    }
}
