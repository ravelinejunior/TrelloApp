package br.com.trelloapp.ui

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.trelloapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_progress.*
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text: String, time: Int? = 1500) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.tv_progress_text.text = text
        mProgressDialog.setCancelable(true)
        mProgressDialog.show()

        if (time != 1500) {
            Handler().postDelayed(
                {
                    hideProgressDialog()
                }, 2000
            )
        }
    }

    fun hideProgressDialog() {
        mProgressDialog.hide()
    }

    fun getCurrentID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click again to exit!", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    fun showErrorSnackBar(message: String) {
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))
        snackbar.show()
    }

    fun showWelcomeSnackBar(message: String) {
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackbar.show()
    }

    fun getCurrentDate(): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss", Locale.getDefault())
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        return currentDateAndTime
    }
}