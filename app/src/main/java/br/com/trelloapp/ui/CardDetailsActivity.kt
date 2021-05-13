package br.com.trelloapp.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import br.com.trelloapp.R
import br.com.trelloapp.adapter.CardMemberListAdapter
import br.com.trelloapp.dialog.LabelColorDialog
import br.com.trelloapp.dialog.MemberListDialog
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.*
import br.com.trelloapp.utils.Constants.BOARDS_KEY_NAME_COLLECTION
import br.com.trelloapp.utils.Constants.BOARD_MEMBERS_LIST
import br.com.trelloapp.utils.Constants.CARD_LIST_ITEM_POSITION
import br.com.trelloapp.utils.Constants.SELECT
import br.com.trelloapp.utils.Constants.TASK_LIST_ITEM_POSITION
import br.com.trelloapp.utils.Constants.isNetworkAvailable
import kotlinx.android.synthetic.main.activity_card_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private var mSelectedColor: String = ""
    private lateinit var mBoardModel: BoardModel
    private var mCardPosition: Int = -1
    private var mTaskItemPosition: Int = -1
    private lateinit var mListMembers: ArrayList<UserModel>
    private var mSelectedDueDateMilliSec: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        if (intent.hasExtra(BOARDS_KEY_NAME_COLLECTION)) {
            mBoardModel = intent.getParcelableExtra(BOARDS_KEY_NAME_COLLECTION)!!
            mCardPosition = intent.getIntExtra(CARD_LIST_ITEM_POSITION, -1)
            mTaskItemPosition = intent.getIntExtra(TASK_LIST_ITEM_POSITION, -1)
            mListMembers = intent.getParcelableArrayListExtra(BOARD_MEMBERS_LIST)!!
        }
        setupActionBar()


        btn_update_card_details.setOnClickListener {
            if (isNetworkAvailable(this)) {
                val name = et_name_card_details.text.toString()
                if (name.isNotEmpty()) {
                    updateCardDetails()
                } else {
                    showErrorSnackBar("Name can´t be empty!")
                }
            } else {
                showErrorSnackBar("No Internet Connection")
            }
        }

        tv_select_label_color_card_details.setOnClickListener {
            labelColorsDialog()
        }

        tv_select_members_card_details.setOnClickListener {
            memberListDialog()
        }

        tv_select_due_date_card_details.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                showDataPicker()
            }
        }

        mSelectedColor = mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].labelColor
        mSelectedDueDateMilliSec =
            mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].dueDate

        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }
        initFields()
        setUpSelectedMembersList()
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

        if (mSelectedDueDateMilliSec > 0L) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = simpleDateFormat.format(mSelectedDueDateMilliSec)

            tv_select_due_date_card_details.text = selectedDate
        }
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
            mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSec

        )

        mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition] = card

        val taskList: ArrayList<TaskModel> = mBoardModel.taskList
        taskList.removeAt(taskList.size - 1)

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

    private fun memberListDialog() {
        val cardAssignedMemberList =
            mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].assignedTo

        if (cardAssignedMemberList.size > 0) {
            for (i in mListMembers.indices) {
                for (j in cardAssignedMemberList) {
                    if (mListMembers[i].id == j) {
                        mListMembers[i].selected = true
                    }
                }
            }
        } else {
            for (i in mListMembers.indices) {
                mListMembers[i].selected = false
            }
        }

        val listDialog = object :
            MemberListDialog(this, resources.getString(R.string.str_select_member), mListMembers) {
            override fun onItemSelected(user: UserModel, action: String) {
                //verify if the action is select or unselect
                if (action == SELECT) {
                    //if the user is already added, do something , else add the user to the card
                    if (!mBoardModel.taskList[mTaskItemPosition]
                            .cards[mCardPosition]
                            .assignedTo.contains(user.id)
                    ) {
                        //add the user
                        mBoardModel.taskList[mTaskItemPosition]
                            .cards[mCardPosition]
                            .assignedTo.add(user.id)
                    }

                } else {
                    //remove from the list
                    mBoardModel.taskList[mTaskItemPosition]
                        .cards[mCardPosition]
                        .assignedTo.remove(user.id)

                    //go through all the users and unselect the user
                    for (i in mListMembers.indices) {
                        mListMembers[i].selected = mListMembers[i].id != user.id
                    }
                }

                //refresh the screen
                setUpSelectedMembersList()
            }
        }

        listDialog.show()


    }

    private fun setUpSelectedMembersList() {
        val cardAssignedMembersList =
            mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembersModel> = ArrayList()

        for (i in mListMembers.indices) {
            for (j in cardAssignedMembersList) {
                if (mListMembers[i].id == j) {
                    val selectedMember = SelectedMembersModel(
                        mListMembers[i].id,
                        mListMembers[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembersModel("", ""))
            tv_select_members_card_details.visibility = View.GONE
            rv_selected_members_list_id.visibility = View.VISIBLE

            rv_selected_members_list_id.layoutManager = GridLayoutManager(this, 6)
            rv_selected_members_list_id.setHasFixedSize(true)

            val adapter = CardMemberListAdapter(this, selectedMembersList, true)

            rv_selected_members_list_id.adapter = adapter

            adapter.setOnClickListener(object : CardMemberListAdapter.OnClickListener {
                override fun onClick() {
                    memberListDialog()
                }

            })
        } else {
            tv_select_members_card_details.visibility = View.VISIBLE
            rv_selected_members_list_id.visibility = View.GONE
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDataPicker() {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                    val sMonthOfYear = if ((month + 1) < 10) "0${month + 1}" else "${month + 1}"

                    val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                    tv_select_due_date_card_details.text = selectedDate

                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val finalDate = simpleDateFormat.parse(selectedDate)
                    mSelectedDueDateMilliSec = finalDate!!.time
                },
                year, month, day
            )

        datePickerDialog.show()

    }

    private fun colorsList(): ArrayList<String> {
        val colorsList: ArrayList<String> = ArrayList()

        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor() {
        tv_select_label_color_card_details.text = ""
        tv_select_label_color_card_details.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsDialog() {
        val colorList: ArrayList<String> = colorsList()

        val listDialog = object : LabelColorDialog(
            this,
            colorList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }

        listDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete_card_item_id) {
            if (isNetworkAvailable(this)) {
                alertDialogDeleletedList(mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].name)
                return true
            } else {
                showErrorSnackBar("No Internet Connection!")
            }
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