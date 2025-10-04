package com.example.summerveldhoundresort.db.implementations

import android.content.ContentValues.TAG
import android.util.Log
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.db.entities.User
import com.example.summerveldhoundresort.db.repos.UsersRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


import kotlinx.coroutines.tasks.await

class firebaseUsersImpl : UsersRepo {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        creationDate: String
    ): AppResult<Unit> {
      return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            /*
            Elvis Operator, if result.user is not null, return it, if it is, return what
            is after the operator ?:
            */
            val user = result.user ?: return AppResult.Error(Exception("User registration failed"))

            val userProfile = User(
                userID = user.uid,
                username = name,
                email = email,
                phoneNumber = phoneNumber,
                creationDate = creationDate,
                role = "user"
            )
            //Adding the extra user information to a user profile under their ID
          //sets display name to name user entered
            val profile= UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profile).await()

            firestore.collection("users").document(user.uid).set(userProfile).await()
             AppResult.Success(Unit)
        } catch (e: Exception) {
          Log.e(TAG, "Registration failed", e)
             AppResult.Error(e)
        }
    }

    override suspend fun login(identifier: String, password: String): AppResult<User> {
        return try{
            val result = auth.signInWithEmailAndPassword(identifier, password).await()
            val fbUser = result.user ?: return AppResult.Error(Exception("User not found."))

            Log.d(TAG, "signInWithEmail:success")

            //gets user doc from firestore then converts to user entity
            val documentSnapShot = firestore.collection("users").document(fbUser.uid).get().await()
            val user = documentSnapShot.toObject(User::class.java)
                ?: return AppResult.Error(Exception("Failed to parse user profile"))
            AppResult.Success(user)
        }catch (e:Exception){
            Log.e(TAG, "Login failed", e)
            AppResult.Error(e)
        }
    }

    override  fun logout() {
       FirebaseAuth.getInstance().signOut()
    }

    override suspend fun getCurrentUser(): AppResult<User?> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            try {
                //gets user doc from firestore then converts to user entity
                val documentSnapShot = firestore.collection("users").document(currentUser.uid).get().await()
                
                if (documentSnapShot.exists()) {
                    val user = documentSnapShot.toObject(User::class.java)
                        ?: return AppResult.Error(Exception("Failed to parse user profile"))
                    return AppResult.Success(user)
                } else {
                    // If user profile doesn't exist, check if it's a Google user and create profile
                    val signInMethod = currentUser.getIdToken(false).await()?.signInProvider
                    if (signInMethod == "google.com") {
                        Log.d(TAG, "Creating missing Google user profile for user: ${currentUser.uid}")
                        
                        // Create user profile for Google Sign-In
                        val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                        val userProfile = User(
                            userID = currentUser.uid,
                            username = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "User",
                            email = currentUser.email ?: "",
                            phoneNumber = "",
                            creationDate = currentDate,
                            role = "user"
                        )
                        
                        // Save to Firestore
                        firestore.collection("users").document(currentUser.uid).set(userProfile).await()
                        return AppResult.Success(userProfile)
                    } else {
                        return AppResult.Error(Exception("User profile not found and is not a Google user"))
                    }
                }
            }catch (e: Exception){
                Log.e(TAG, "get current user method failed", e)
              return  AppResult.Error(e)
            }
        }
       return  AppResult.Success(null)
    }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserBy(identifier: String): AppResult<User?> {
        TODO("Not yet implemented")
    }
}