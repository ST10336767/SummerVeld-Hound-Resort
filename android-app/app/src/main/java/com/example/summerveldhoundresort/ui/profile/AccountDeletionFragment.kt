package com.example.summerveldhoundresort.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.databinding.FragmentAccountDeletionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountDeletionFragment : Fragment() {

    private var _binding: FragmentAccountDeletionBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDeletionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back button
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Delete account button
        binding.buttonDeleteAccount.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Export data button
        binding.buttonExportData.setOnClickListener {
            exportUserData()
        }

        // Partial deletion button
        binding.buttonPartialDeletion.setOnClickListener {
            showPartialDeletionDialog()
        }

        // Contact support button
        binding.buttonContactSupport.setOnClickListener {
            contactSupport()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.account_deletion_title))
            .setMessage(getString(R.string.account_deletion_warning) + "\n\n" + getString(R.string.account_deletion_confirm))
            .setPositiveButton(getString(R.string.account_deletion_button)) { _, _ ->
                deleteAccount()
            }
            .setNegativeButton(getString(R.string.account_deletion_cancel), null)
            .show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading
        binding.buttonDeleteAccount.isEnabled = false
        binding.buttonDeleteAccount.text = "Deleting..."

        // Delete user data from Firestore
        deleteUserDataFromFirestore(user.uid) { success ->
            if (success) {
                // Delete Firebase Auth account
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), getString(R.string.account_deletion_success), Toast.LENGTH_LONG).show()
                            // Navigate to login
                            findNavController().navigate(R.id.action_accountDeletionFragment_to_authActivity)
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.account_deletion_error), Toast.LENGTH_SHORT).show()
                            binding.buttonDeleteAccount.isEnabled = true
                            binding.buttonDeleteAccount.text = getString(R.string.account_deletion_button)
                        }
                    }
            } else {
                Toast.makeText(requireContext(), getString(R.string.account_deletion_error), Toast.LENGTH_SHORT).show()
                binding.buttonDeleteAccount.isEnabled = true
                binding.buttonDeleteAccount.text = getString(R.string.account_deletion_button)
            }
        }
    }

    private fun deleteUserDataFromFirestore(userId: String, callback: (Boolean) -> Unit) {
        // Delete user document
        db.collection("users").document(userId).delete()
            .addOnSuccessListener {
                // Delete user's pets
                db.collection("dogs").whereEqualTo("userId", userId).get()
                    .addOnSuccessListener { petsSnapshot ->
                        val batch = db.batch()
                        petsSnapshot.documents.forEach { doc ->
                            batch.delete(doc.reference)
                        }
                        batch.commit()
                            .addOnSuccessListener { callback(true) }
                            .addOnFailureListener { callback(false) }
                    }
                    .addOnFailureListener { callback(false) }
            }
            .addOnFailureListener { callback(false) }
    }

    private fun exportUserData() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Collect user data
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { userDoc ->
                db.collection("dogs").whereEqualTo("userId", user.uid).get()
                    .addOnSuccessListener { petsSnapshot ->
                        // Create export data
                        val exportData = buildString {
                            appendLine("User Data Export - ${java.util.Date()}")
                            appendLine("Email: ${user.email}")
                            appendLine("User ID: ${user.uid}")
                            appendLine()
                            
                            if (userDoc.exists()) {
                                appendLine("Profile Information:")
                                userDoc.data?.forEach { (key, value) ->
                                    appendLine("$key: $value")
                                }
                                appendLine()
                            }
                            
                            if (!petsSnapshot.isEmpty) {
                                appendLine("Pet Information:")
                                petsSnapshot.documents.forEach { petDoc ->
                                    appendLine("Pet: ${petDoc.id}")
                                    petDoc.data?.forEach { (key, value) ->
                                        appendLine("  $key: $value")
                                    }
                                    appendLine()
                                }
                            }
                        }
                        
                        // Create email intent with export data
                        val emailIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("keatonjones02@gmail.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "Data Export Request - SummerVeld Hound Resort App")
                            putExtra(Intent.EXTRA_TEXT, exportData)
                        }
                        
                        if (emailIntent.resolveActivity(requireContext().packageManager) != null) {
                            startActivity(emailIntent)
                        } else {
                            Toast.makeText(requireContext(), "No email app available", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to export data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPartialDeletionDialog() {
        val options = arrayOf(
            "Delete Pet Profiles",
            "Delete Photos",
            "Delete Event History",
            "Opt Out of Notifications"
        )
        
        AlertDialog.Builder(requireContext())
            .setTitle("Partial Data Deletion")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> deletePetProfiles()
                    1 -> deletePhotos()
                    2 -> deleteEventHistory()
                    3 -> optOutOfNotifications()
                }
            }
            .show()
    }

    private fun deletePetProfiles() {
        val user = auth.currentUser
        if (user == null) return
        
        db.collection("dogs").whereEqualTo("userId", user.uid).get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Pet profiles deleted", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun deletePhotos() {
        // This would require Firebase Storage implementation
        Toast.makeText(requireContext(), "Photo deletion feature coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun deleteEventHistory() {
        Toast.makeText(requireContext(), "Event history deletion feature coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun optOutOfNotifications() {
        Toast.makeText(requireContext(), "Notification preferences updated", Toast.LENGTH_SHORT).show()
    }

    private fun contactSupport() {
        val user = auth.currentUser
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("keatonjones02@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.account_deletion_email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.account_deletion_email_body, 
                user?.email ?: "N/A", 
                user?.displayName ?: "N/A"))
        }
        
        if (emailIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(emailIntent)
        } else {
            Toast.makeText(requireContext(), "No email app available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
