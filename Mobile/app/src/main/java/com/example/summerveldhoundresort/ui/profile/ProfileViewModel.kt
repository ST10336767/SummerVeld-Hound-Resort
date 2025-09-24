package com.example.summerveldhoundresort.ui.profile

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.db.entities.User
import com.example.summerveldhoundresort.db.implementations.firebaseUsersImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileViewModel(private val userRepo: firebaseUsersImpl) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private  var fbUser : FirebaseUser? = null
    private val firestore = Firebase.firestore

    init{
        _username.value = FirebaseAuth.getInstance().currentUser?.displayName
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
        firestore.collection("users").document(fbUser!!.uid).update("name", name)
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