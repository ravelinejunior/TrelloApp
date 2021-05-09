package br.com.trelloapp.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.trelloapp.R
import br.com.trelloapp.adapter.MembersItemAdapter
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.BOARD_DETAIL
import br.com.trelloapp.utils.Constants.isNetworkAvailable
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class MembersActivity : BaseActivity() {

    private lateinit var mBoard: BoardModel
    private lateinit var mListMembers: ArrayList<UserModel>
    private var anyChangesMade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.extras != null) {

            mBoard = intent.getParcelableExtra(BOARD_DETAIL)!!

        }

        if (isNetworkAvailable(this)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedMembersDetails(this, mBoard.assignedTo)
        } else {
            showErrorSnackBar("No Internet Connection!")
        }

        setupActionBar()


    }

    fun setupMembersList(list: ArrayList<UserModel>) {

        mListMembers = list

        hideProgressDialog()

        rv_members_list.layoutManager = LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)

        val adapter = MembersItemAdapter(this, list)
        rv_members_list.adapter = adapter
    }

    fun getMemberDetails(user: UserModel) {
        mBoard.assignedTo.add(user.id)
        FirestoreClass().postRequestAssignMemberToBoard(this@MembersActivity, mBoard, user)

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_members_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white)
            actionBar.title = resources.getString(R.string.members)
        }
        toolbar_members_activity.setNavigationOnClickListener {
            Log.i("TAGMembers", "Board $mBoard")
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member_menu_id -> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener {
            val email: String = dialog.et_email_search_member.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this@MembersActivity, "Email can´t be empty.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                var isAlreadyMember = false
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))

                if (isNetworkAvailable(this@MembersActivity)) {
                    for (i in mListMembers) {
                        if (email == i.email) {
                            isAlreadyMember = true
                        }
                    }

                    if (isAlreadyMember) {
                        hideProgressDialog()
                        showErrorSnackBar("User already make part of this membership!")
                    } else {
                        FirestoreClass().getRequestMemberDetail(this@MembersActivity, email)
                    }


                } else {
                    hideProgressDialog()
                    showErrorSnackBar("No Internet Connection!")
                }

            }

        }

        dialog.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun memberAssignedSuccess(user: UserModel) {
        hideProgressDialog()
        mListMembers.add(user)

        anyChangesMade = true

        setupMembersList(mListMembers)
    }

    override fun onBackPressed() {
        if (anyChangesMade) {
            setResult(RESULT_OK)
        }
        super.onBackPressed()


    }


}