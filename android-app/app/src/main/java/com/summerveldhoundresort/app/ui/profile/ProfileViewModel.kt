package com.summerveldhoundresort.app.ui.profile

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.summerveldhoundresort.app.db.AppResult
import com.summerveldhoundresort.app.db.entities.User
import com.summerveldhoundresort.app.db.implementations.firebaseUsersImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepo: firebaseUsersImpl) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private  var fbUser : FirebaseUser? = null
    private val firestore = Firebase.firestore

    init{
        loadUsername()
    }
    
    /**
     * Load username from Firestore, fallback to Firebase Auth displayName if not found
     */
    fun loadUsername() {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
                    if (userDoc.exists()) {
                        val username = userDoc.getString("username") ?: userDoc.getString("name")
                        if (username != null) {
                            _username.postValue(username)
                            return@launch
                        }
                    }
                }
                // Fallback to Firebase Auth displayName
                _username.postValue(currentUser?.displayName)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading username", e)
                // Fallback to Firebase Auth displayName
                _username.postValue(FirebaseAuth.getInstance().currentUser?.displayName)
            }
        }
    }

    fun updateUserInfo(name: String?, email: String?, phoneNum: String?) {
        Log.d(TAG, "updateUserInfo method called")

        if (name != null){
            updateUserName(name)
        }
        if (email != null){
            updateEmail(email)
        }
        if (phoneNum != null){
            updatePhoneNum(phoneNum)
        }

    }
    private fun updateUserName(name: String) {
        fbUser = FirebaseAuth.getInstance().currentUser
        if (fbUser == null) {
            Log.e(TAG, "No current user found")
            return
        }
        
        viewModelScope.launch {
            try {
                val userRef = firestore.collection("users").document(fbUser!!.uid)
                
                // Update both username and name fields if they exist
                val updates = hashMapOf<String, Any>()
                updates["username"] = name
                updates["name"] = name
                
                // Wait for Firestore update to complete
                userRef.update(updates).await()
                
                // Also update Firebase Auth display name
                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                fbUser?.updateProfile(profile)?.await()
                
                // Update the LiveData username after successful update
                _username.postValue(name)
                Log.d(TAG, "Username updated successfully: $name")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating username", e)
            }
        }
    }

    private fun updateEmail(email: String) {
        fbUser = FirebaseAuth.getInstance().currentUser
        //Will send email to new address
        fbUser?.verifyBeforeUpdateEmail(email)
    }

    private fun updatePhoneNum(phoneNum: String) {
        fbUser = FirebaseAuth.getInstance().currentUser
        firestore.collection("users").document(fbUser!!.uid).update("phoneNumber", phoneNum)
    }

    private fun changeUserPassword(password: String) {

    }

     fun logout(){
        userRepo.logout()
    }

    suspend fun getCurrentUser(): AppResult<User?> {
        return userRepo.getCurrentUser()
    }


}
