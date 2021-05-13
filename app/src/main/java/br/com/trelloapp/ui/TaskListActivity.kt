package br.com.trelloapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.trelloapp.R
import br.com.trelloapp.adapter.TaskItemAdapter
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.model.CardModel
import br.com.trelloapp.model.TaskModel
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.BOARDS_KEY_NAME_COLLECTION
import br.com.trelloapp.utils.Constants.BOARD_DETAIL
import br.com.trelloapp.utils.Constants.BOARD_MEMBERS_LIST
import br.com.trelloapp.utils.Constants.CARD_LIST_ITEM_POSITION
import br.com.trelloapp.utils.Constants.TASK_LIST_ITEM_POSITION
import br.com.trelloapp.utils.Constants.isNetworkAvailable
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardModel: BoardModel
    private lateinit var taskAdapter: TaskItemAdapter
    private var cardModelList: ArrayList<CardModel>? = null
    lateinit var mAssignedMemberList: ArrayList<UserModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        if (intent.extras != null) {
            mBoardModel = intent.getParcelableExtra(BOARDS_KEY_NAME_COLLECTION)!!
        }

        if (isNetworkAvailable(this)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetail(this, mBoardModel.documentId)
        } else {
            showProgressDialog(resources.getString(R.string.please_wait))
            boardDetails(mBoardModel)
            showErrorSnackBar("No Internet Connection")
        }

        setupActionBar()

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_task_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white)
            actionBar.title = mBoardModel.name
        }

        toolbar_task_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board: BoardModel) {
        mBoardModel = board
        hideProgressDialog()


        //call the function to get members of a card
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersDetails(this, mBoardModel.assignedTo)

    }

    fun addUpdateTaskListBoard() {

        if (isNetworkAvailable(this)) {
            hideProgressDialog()

            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetail(this, mBoardModel.documentId)
        }else{
            showErrorSnackBar("No Internet Connection!")
        }

    }

    fun createTaskList(taskListName: String, position: Int) {
        if (isNetworkAvailable(this)) {
            val task =
                TaskModel(taskListName, FirestoreClass().getCurrentUserId(), getCurrentDate())

            mBoardModel.taskList.add(0, task)
            mBoardModel.taskList.removeAt(mBoardModel.taskList.size - 1)
            // mBoardModel.taskList.add(position, task)

            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().addUpdateTaskList(this, mBoardModel)
        }else{
            showErrorSnackBar("No Internet Connection!")
        }

    }

    fun updateTaskList(position: Int, listName: String, model: TaskModel) {
        if (isNetworkAvailable(this)) {
            val task = TaskModel(listName, model.createdBy, model.createdAt, model.cards)

            mBoardModel.taskList[position] = task
            mBoardModel.taskList.removeAt(mBoardModel.taskList.size - 1)

            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().addUpdateTaskList(this, mBoardModel, true)
        }else{
            showErrorSnackBar("No Internet Connection!")
        }

    }

    fun deleteTaskList(position: Int) {
        if (isNetworkAvailable(this)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            mBoardModel.taskList.removeAt(position)
            mBoardModel.taskList.removeAt(mBoardModel.taskList.size - 1)

            hideProgressDialog()
            FirestoreClass().addUpdateTaskList(this, mBoardModel)
        }else{
            hideProgressDialog()
            showErrorSnackBar("No Internet Connection!")
        }
    }

    fun addCardToTaskList(cardName: String, position: Int) {
        if (isNetworkAvailable(this)) {

            showProgressDialog(resources.getString(R.string.please_wait))
            mBoardModel.taskList.removeAt(mBoardModel.taskList.size - 1)

            val cardAssignedUsersList: ArrayList<String> = ArrayList()

            cardAssignedUsersList.add(FirestoreClass().getCurrentUserId())

            val card = CardModel(
                cardName,
                FirestoreClass().getCurrentUserId(),
                getCurrentDate(),
                cardAssignedUsersList
            )

            val cardList = mBoardModel.taskList[position].cards
            cardList.add(card)

            val task =
                TaskModel(
                    mBoardModel.taskList[position].title,
                    mBoardModel.taskList[position].createdBy,
                    mBoardModel.taskList[position].createdAt,
                    cardList
                )

            cardModelList = cardList

            mBoardModel.taskList[position] = task

            FirestoreClass().addUpdateTaskList(this, mBoardModel)

        }else{
            showErrorSnackBar("No Internet Connection!")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.members_menu_item_id -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(BOARD_DETAIL, mBoardModel)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE)
            && resultCode == RESULT_OK
        ) {
            if (isNetworkAvailable(this)) {
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getBoardDetail(this, mBoardModel.documentId)
            }else{
                showErrorSnackBar("No Internet Connection!")
            }
        } else {
            Log.i("TAGTASKLIST", "nothing changed!")
        }
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {

        val intent = Intent(this@TaskListActivity, CardDetailsActivity::class.java)

        intent.putExtra(BOARDS_KEY_NAME_COLLECTION, mBoardModel)
        intent.putExtra(TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(BOARD_MEMBERS_LIST, mAssignedMemberList)

        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun boardMembersDetailsList(list: ArrayList<UserModel>) {
        mAssignedMemberList = list

        hideProgressDialog()


        val addTaskList = TaskModel(
            resources.getString(R.string.add_list)
        )

        mBoardModel.taskList.add(addTaskList)

        rv_task_list.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_task_list.setHasFixedSize(true)

        taskAdapter = TaskItemAdapter(this, mBoardModel.taskList)

        rv_task_list.adapter = taskAdapter
    }

    companion object {
        const val MEMBERS_REQUEST_CODE: Int = 151
        const val CARD_DETAILS_REQUEST_CODE: Int = 56153
    }
}
