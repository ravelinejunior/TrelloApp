package br.com.trelloapp.firebase

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.ui.MainActivity
import br.com.trelloapp.ui.MyProfileActivity
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

    fun loadUserData(activity: Activity) {
        mFirestore.collection(USER_COLLECTION_NAME)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->

                val loggedInUser = document.toObject(UserModel::class.java)

                when (activity) {
                    is SignInActivity -> {
                        activity.userSignInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }

                }


            }.addOnFailureListener { it ->

                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                }

                Log.e(activity.javaClass.simpleName, "Error saving user:${it.printStackTrace()}")
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(USER_COLLECTION_NAME)
            .document(getCurrentUserId())
            .update(userHashMap).addOnSuccessListener {
                Log.i("TAGTaskSnapshot", "updateUserProfileData: Success updating user")
                Toast.makeText(activity, "Updated with success!", Toast.LENGTH_SHORT).show()
                activity.hideProgressDialog()
                activity.finish()
                //activity.startActivity(Intent(activity,MainActivity::class.java))
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
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