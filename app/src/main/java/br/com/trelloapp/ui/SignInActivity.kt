package br.com.trelloapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import br.com.trelloapp.R
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.USER_KEY_MODEL
import br.com.trelloapp.utils.Constants.isNetworkAvailable
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity(), View.OnClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        firebaseAuth = FirebaseAuth.getInstance()

        setupActionBar()

        btn_sign_in.setOnClickListener(this)
    }


    private fun setupActionBar() {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
    }

    private fun signInRegisteredUser() {
        val email: String = et_signin_email.text.toString().trim { it <= ' ' }
        val password: String = et_signin_password.text.toString().trim { it <= ' ' }

        if (isNetworkAvailable(applicationContext)) {
            if (validadeForm(email, password)) {
                showProgressDialog(resources.getString(R.string.please_wait))
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirestoreClass().signInUser(this)
                        } else {
                            hideProgressDialog()
                            showErrorSnackBar("Something went wrong!")
                        }
                    }.addOnFailureListener { exception ->
                        showErrorSnackBar(exception.message.toString())
                    }
            }
        } else showErrorSnackBar("No internet connection")
    }

    private fun validadeForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please set an email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please set a password")
                false
            }


            else -> true
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_sign_in -> {
                signInRegisteredUser()
            }

        }
    }

    fun userSignInSuccess(user: UserModel?) {
        hideProgressDialog()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(USER_KEY_MODEL, user)
        startActivity(intent)
        finish()

    }


}