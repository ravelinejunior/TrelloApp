package br.com.trelloapp.firebase

import android.util.Log
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.ui.SignInActivity
import br.com.trelloapp.ui.SignUpActivity
import br.com.trelloapp.utils.Constants.USER_COLLECTION_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: UserModel) {
        mFirestore.collection(USER_COLLECTION_NAME)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {

                activity.userRegisteredSuccess()

            }.addOnFailureListener { it ->
                Log.e(activity.javaClass.simpleName, "Error saving user:${it.printStackTrace()}")
            }
    }

    fun signInUser(activity: SignInActivity) {
        mFirestore.collection(USER_COLLECTION_NAME)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->

                val loggedInUser = document.toObject(UserModel::class.java)

                activity.userSignInSuccess(loggedInUser)
            }.addOnFailureListener { it ->
                Log.e(activity.javaClass.simpleName, "Error saving user:${it.printStackTrace()}")
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = mFirebaseAuth.currentUser
        var currentID = ""

        if (currentUser != null) {
            currentID = currentUser.uid
        }
        return currentID
    }
}