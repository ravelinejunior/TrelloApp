package br.com.trelloapp.ui

import android.app.Dialog
import android.os.AsyncTask
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
import br.com.trelloapp.utils.Constants.FCM_AUTHORIZATION
import br.com.trelloapp.utils.Constants.FCM_BASE_URL
import br.com.trelloapp.utils.Constants.FCM_KEY
import br.com.trelloapp.utils.Constants.FCM_KEY_DATA
import br.com.trelloapp.utils.Constants.FCM_KEY_MESSAGE
import br.com.trelloapp.utils.Constants.FCM_KEY_TITLE
import br.com.trelloapp.utils.Constants.FCM_KEY_TO
import br.com.trelloapp.utils.Constants.FCM_SERVER_KEY
import br.com.trelloapp.utils.Constants.isNetworkAvailable
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_search_member.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

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

        SendNotificationToUserAsyncTask(mBoard.name,user.fcmToken).execute()
    }

    private inner class SendNotificationToUserAsyncTask(val boardName: String, val token: String) :
        AsyncTask<Any, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(resources.getString(R.string.please_wait))
        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null
            try {
                val url = URL(FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"


                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(FCM_AUTHORIZATION, "${FCM_KEY}=${FCM_SERVER_KEY}")

                connection.useCaches = false

                val dataWriter = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()

                dataObject.put(FCM_KEY_TITLE, "You have a new task on board $boardName")
                dataObject.put(
                    FCM_KEY_MESSAGE,
                    "You've a new task assigned to you. It was added by ${mListMembers[0].name}"
                )

                jsonRequest.put(FCM_KEY_DATA, dataObject)
                jsonRequest.put(FCM_KEY_TO, token)

                dataWriter.writeBytes(jsonRequest.toString())
                dataWriter.flush()
                dataWriter.close()

                val httpResult: Int = connection.responseCode

                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val stringBuilder = StringBuilder()
                    var line: String?
                    try {
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    result = stringBuilder.toString()
                } else {
                    result = connection.responseMessage
                }


            } catch (httpException: SocketTimeoutException) {
                result = "Connection timeout"
            } catch (e: Exception) {
                result = "Error: ${e.printStackTrace()}"
            } finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.i("onPostExecuteJSON", "json: $result")
            hideProgressDialog()
        }

    }

    override fun onBackPressed() {
        if (anyChangesMade) {
            setResult(RESULT_OK)
        }
        super.onBackPressed()


    }


}