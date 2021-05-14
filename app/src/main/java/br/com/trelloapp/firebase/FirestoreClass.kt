package br.com.trelloapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.ui.*
import br.com.trelloapp.utils.Constants.ASSIGNED_TO_KEY
import br.com.trelloapp.utils.Constants.BOARDS_KEY_NAME_COLLECTION
import br.com.trelloapp.utils.Constants.EMAIL_USER_KEY
import br.com.trelloapp.utils.Constants.TASK_LIST
import br.com.trelloapp.utils.Constants.USER_COLLECTION_NAME
import br.com.trelloapp.utils.Constants.USER_MEMBER_ID
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

    private fun loadBoardFromServer(activity: MainActivity) {
        mFirestore.collection(BOARDS_KEY_NAME_COLLECTION)
            .whereArrayContains(ASSIGNED_TO_KEY, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val boardList: ArrayList<BoardModel> = ArrayList()
                for (newBoard in document.documents) {

                    val board: BoardModel = newBoard.toObject(BoardModel::class.java)!!
                    board.documentId = newBoard.id
                    boardList.add(board)

                    Log.i("TAGFirestore", boardList.toString())
                }
                boardList.sortBy { boardModel -> boardModel.createdAtDate }
                boardList.reverse()
                activity.loadRecyclerView(boardList)

            }

            .addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error loading boards:${exception.printStackTrace()}"
                )

            }
        activity.showProgressDialog("Please wait ... ")

    }


    fun getBoardDetail(activity: TaskListActivity, documentId: String) {
        mFirestore.collection(BOARDS_KEY_NAME_COLLECTION)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                val board = document.toObject(BoardModel::class.java)!!
                board.documentId = documentId
                activity.boardDetails(board)

            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error loading boards:${exception.printStackTrace()}"
                )

            }


    }

    fun addUpdateTaskList(activity: Activity, board: BoardModel, isUpdate: Boolean = false) {

        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[TASK_LIST] = board.taskList

        mFirestore.collection(BOARDS_KEY_NAME_COLLECTION)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {

                if (activity is TaskListActivity) {
                    activity.addUpdateTaskListBoard()
                } else if (activity is CardDetailsActivity) {
                    activity.addUpdateTaskList(activity)
                }

            }.addOnFailureListener { exception ->
                if (activity is TaskListActivity)
                    activity.hideProgressDialog()
                else if (activity is CardDetailsActivity)
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
            mFirestore.collection(BOARDS_KEY_NAME_COLLECTION)
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

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(USER_COLLECTION_NAME)
            .document(getCurrentUserId())
            .update(userHashMap).addOnSuccessListener {
                Log.i("TAGTaskSnapshot", "updateUserProfileData: Success updating user")
                Toast.makeText(activity, "Updated with success!", Toast.LENGTH_SHORT).show()

                when (activity) {
                    is MainActivity -> {
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                        activity.finish()
                    }
                }
            }.addOnFailureListener { e ->

                when (activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }


            }
    }

    fun getAssignedMembersDetails(activity: Activity, assignedTo: ArrayList<String>) {
        mFirestore.collection(USER_COLLECTION_NAME)
            .whereIn(USER_MEMBER_ID, assignedTo)
            .get()
            .addOnSuccessListener { document ->

                val usersList: ArrayList<UserModel> = ArrayList()

                for (userFor in document.documents) {
                    val user = userFor.toObject(UserModel::class.java)
                    usersList.add(user!!)
                }

                if (activity is MembersActivity)
                    activity.setupMembersList(usersList)
                else if (activity is TaskListActivity) {
                    activity.boardMembersDetailsList(usersList)
                }


            }.addOnFailureListener { exception ->
                exception.printStackTrace()

                if (activity is MembersActivity)
                    activity.hideProgressDialog()
                else if (activity is MembersActivity) {
                    activity.hideProgressDialog()
                }
            }
    }

    fun getRequestMemberDetail(activity: MembersActivity, email: String) {
        mFirestore.collection(USER_COLLECTION_NAME)
            .whereEqualTo(EMAIL_USER_KEY, email)
            .get()
            .addOnSuccessListener { document ->
                if (document.size() > 0) {
                    val user = document.documents[0].toObject(UserModel::class.java)
                    activity.getMemberDetails(user!!)
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No member founded")
                }
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
                activity.hideProgressDialog()
            }
    }

    fun postRequestAssignMemberToBoard(
        activity: MembersActivity,
        board: BoardModel,
        userInfo: UserModel
    ) {
        val assignedToHashMap: HashMap<String, Any> = HashMap()
        assignedToHashMap[ASSIGNED_TO_KEY] = board.assignedTo

        mFirestore.collection(BOARDS_KEY_NAME_COLLECTION)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {

                activity.memberAssignedSuccess(userInfo)

            }.addOnFailureListener { exception ->
                exception.printStackTrace()
                activity.hideProgressDialog()
            }

    }


    fun getCurrentUserId(): String {
        val currentUser = mFirebaseAuth.currentUser
        var currentID = ""

        if (currentUser != null) {
            currentID = currentUser.uid
        }
        return currentID
    }

}