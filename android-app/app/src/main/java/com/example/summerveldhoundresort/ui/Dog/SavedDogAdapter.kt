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

class SavedDogAdapter(
    private val dogs: List<Dog>,
    private val onDogClick: (Dog) -> Unit
) : RecyclerView.Adapter<SavedDogAdapter.DogViewHolder>() {

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

        Log.d("SavedDogAdapter", "Binding dog at position $position: ${dog.dogName}")
        Log.d("SavedDogAdapter", "Dog image URI: ${dog.imageUri}")
        Log.d("SavedDogAdapter", "Dog breed: ${dog.breed}")
        
        // Set dog name
        holder.dogNameText.text = dog.dogName

        // Load dog image using Glide with Supabase URL support
        if (dog.imageUri.isNotEmpty()) {
            Log.d("SavedDogAdapter", "=== IMAGE LOADING DEBUG ===")
            Log.d("SavedDogAdapter", "Dog: ${dog.dogName}")
            Log.d("SavedDogAdapter", "Raw imageUri: '${dog.imageUri}'")
            Log.d("SavedDogAdapter", "URI length: ${dog.imageUri.length}")
            Log.d("SavedDogAdapter", "Starts with http: ${dog.imageUri.startsWith("http")}")
            Log.d("SavedDogAdapter", "Contains supabase: ${dog.imageUri.contains("supabase", ignoreCase = true)}")
            
            // Check if it's a valid URL and convert signed URLs to public URLs
            val imageUrl = if (dog.imageUri.startsWith("http")) {
                // Convert signed URL to public URL if needed
                val publicUrl = if (dog.imageUri.contains("/sign/")) {
                    dog.imageUri.replace("/sign/", "/public/").split("?")[0] // Remove token parameter
                } else {
                    dog.imageUri
                }
                Log.d("SavedDogAdapter", "Original URL: $dog.imageUri")
                Log.d("SavedDogAdapter", "Using public URL: $publicUrl")
                publicUrl
            } else {
                // If it's not a full URL, log it for debugging
                Log.w("SavedDogAdapter", "Image URI is not a full URL: ${dog.imageUri}")
                Log.w("SavedDogAdapter", "This might be a Supabase path that needs to be converted to a full URL")
                dog.imageUri
            }
            
            Log.d("SavedDogAdapter", "Final imageUrl being loaded: '$imageUrl'")
            
            // Check if this is a Supabase signed URL (has token parameter)
            val isSignedUrl = imageUrl.contains("token=")
            Log.d("SavedDogAdapter", "Is signed URL: $isSignedUrl")
            
            if (isSignedUrl) {
                Log.d("SavedDogAdapter", "Loading Supabase signed URL")
            } else {
                Log.d("SavedDogAdapter", "Loading regular URL")
            }
            
            // Try loading with better error handling and timeout
            Glide.with(holder.itemView.context)
                .load(imageUrl) // Use the actual Supabase image URL
                .placeholder(R.drawable.dog_placeholder)
                .error(R.drawable.dog_placeholder)
                .timeout(10000) // 10 second timeout
                .centerCrop()
                .into(holder.dogImage)
        } else {
            Log.w("SavedDogAdapter", "Empty image URI for dog: ${dog.dogName}")
            holder.dogImage.setImageResource(R.drawable.dog_placeholder)
        }

        // Set click listener on the entire item
        holder.itemView.setOnClickListener {
            Log.d("SavedDogAdapter", "Dog clicked: ${dog.dogName}")
            onDogClick(dog)
        }
    }

    override fun getItemCount() = dogs.size
}
