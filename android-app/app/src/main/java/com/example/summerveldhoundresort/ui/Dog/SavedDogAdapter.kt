package com.example.summerveldhoundresort.ui.saved

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.db.entities.Dog

class SavedDogAdapter(private val dogs: List<Dog>) :
    RecyclerView.Adapter<SavedDogAdapter.DogViewHolder>() {

    class DogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dogImage: ImageView = itemView.findViewById(R.id.imageDog)
        val kennelImage: ImageView = itemView.findViewById(R.id.imageKennel)
        val dogNameText: TextView = itemView.findViewById(R.id.textDogName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val dog = dogs[position]

        Glide.with(holder.itemView.context)
            .load(dog.imageUri)
            .placeholder(R.drawable.dog_placeholder)
            .centerCrop()
            .into(holder.dogImage)
        Log.d("SavedDogAdapter", "Loading dog image: ${dog.imageUri}")
        // Set dog name
        holder.dogNameText.text = dog.dogName

    }

    override fun getItemCount() = dogs.size
}
