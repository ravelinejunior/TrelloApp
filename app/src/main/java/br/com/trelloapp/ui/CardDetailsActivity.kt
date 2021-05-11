package br.com.trelloapp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import br.com.trelloapp.R
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.model.CardModel
import br.com.trelloapp.model.TaskModel
import br.com.trelloapp.utils.Constants.BOARDS_KEY_NAME_COLLECTION
import br.com.trelloapp.utils.Constants.CARD_LIST_ITEM_POSITION
import br.com.trelloapp.utils.Constants.TASK_LIST_ITEM_POSITION
import kotlinx.android.synthetic.main.activity_card_details.*

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardModel: BoardModel
    private var mCardPosition: Int = -1
    private var mTaskItemPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        if (intent.hasExtra(BOARDS_KEY_NAME_COLLECTION)) {
            mBoardModel = intent.getParcelableExtra(BOARDS_KEY_NAME_COLLECTION)!!
            mCardPosition = intent.getIntExtra(CARD_LIST_ITEM_POSITION, -1)
            mTaskItemPosition = intent.getIntExtra(TASK_LIST_ITEM_POSITION, -1)
        }
        setupActionBar()
        initFields()

        btn_update_card_details.setOnClickListener{
            val name = et_name_card_details.text.toString()
            if (name.isNotEmpty()) {
                updateCardDetails()
            } else {
                showErrorSnackBar("Name can´t be empty!")
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_card_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white)
            actionBar.title =
                mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].name
        }

        toolbar_card_details_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initFields() {
        et_name_card_details.setText(mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)
        tv_select_due_date_card_details.text =
            mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].createAt
    }


    fun addUpdateTaskList(activity: CardDetailsActivity) {
        hideProgressDialog()

        setResult(RESULT_OK)
        finish()
    }

    private fun updateCardDetails() {
        val name = et_name_card_details.text.toString()
        val card = CardModel(
            name,
            mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].createdBy,
            mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].createAt,
            mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].assignedTo

        )

        mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardModel)

    }

    private fun deleteCard() {
        //get my current cards of this task

        val cardList: ArrayList<CardModel> = mBoardModel.taskList[mTaskItemPosition].cards

        cardList.removeAt(mCardPosition)

        val taskList: ArrayList<TaskModel> = mBoardModel.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskItemPosition].cards = cardList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardModel)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete_card_item_id) {
            alertDialogDeleletedList(mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].name)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun alertDialogDeleletedList(title: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()
            deleteCard()
        }
        builder.setNegativeButton("No") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        val dialog: Dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }



}