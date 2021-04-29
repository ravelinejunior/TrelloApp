package br.com.trelloapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import br.com.trelloapp.R
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.ui.*
import br.com.trelloapp.utils.Constants.ASSIGNED_TO_KEY
import br.com.trelloapp.utils.Constants.BOARDS_KEY_NAME
import br.com.trelloapp.utils.Constants.USER_COLLECTION_NAME
import br.com.trelloapp.utils.Constants.isNetworkAvailable
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

    fun loadBoardFromServer(activity: MainActivity) {
activity.showProgressDialog("Please wait ... ")
        mFirestore.collection(BOARDS_KEY_NAME)
            .whereArrayContains(ASSIGNED_TO_KEY, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val boardList: ArrayList<BoardModel> = ArrayList()
                for (newBoard in document.documents) {

                    val board = newBoard.toObject(BoardModel::class.java)
                    board?.documentId = newBoard.id
                    boardList.add(board!!)

                    Log.i("TAGFirestore", boardList.toString())
                }

                activity.loadRecyclerView(boardList)

            }

            .addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error loading boards:${exception.printStackTrace()}"
                )

            }

    }

    fun loadUserData(activity: Activity, readBoardList: Boolean = false) {
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
                        loadBoardFromServer(activity)
                    }
                    is MyProfileActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }

                }

            }.addOnFailureListener {

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

    fun createBoard(activity: CreateBoardActivity, boardModel: BoardModel) {
        if (isNetworkAvailable(activity)) {
            mFirestore.collection(BOARDS_KEY_NAME)
                .document()
                .set(boardModel, SetOptions.merge())
                .addOnSuccessListener {
                    Log.i("TAGTaskSnapshot", "Board: Success board create")
                    Toast.makeText(activity, "Board created successfully!", Toast.LENGTH_SHORT)
                        .show()
                    activity.boardCreatedSuccessfully()
                }.addOnFailureListener { exception ->
                    Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                    activity.hideProgressDialog()
                }
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
            }.addOnFailureListener { e ->
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