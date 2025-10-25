package com.summerveldhoundresort.app.ui.Dog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.db.entities.Dog

class ManageDogsAdapter(
    private val dogs: List<Dog>,
    private val onEditClick: (Dog) -> Unit
) : RecyclerView.Adapter<ManageDogsAdapter.DogViewHolder>() {

    class DogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dogImage: ImageView = itemView.findViewById(R.id.imageDog)
        val dogName: TextView = itemView.findViewById(R.id.textDogName)
        val editButton: Button = itemView.findViewById(R.id.buttonEditDog)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val dog = dogs[position]
        holder.dogName.text = dog.dogName

        Glide.with(holder.itemView.context)
            .load(dog.imageUri)
            .placeholder(R.drawable.dog_placeholder)
            .error(R.drawable.dog_placeholder)
            .centerCrop()
            .into(holder.dogImage)

        holder.editButton.setOnClickListener {
            onEditClick(dog)
        }
    }

    override fun getItemCount() = dogs.size
}
