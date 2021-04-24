package br.com.trelloapp.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import br.com.trelloapp.R
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.isNetworkAvailable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity(), View.OnClickListener {

    private lateinit var firebaseInstance: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initFirebaseInstance()

        setupActionBar()

        btn_sign_up.setOnClickListener(this)

    }

    private fun initFirebaseInstance() {
        firebaseInstance = FirebaseAuth.getInstance()
    }

    private fun setupActionBar() {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
    }

    private fun registerUser() {
        val name: String = et_signup_name.text.toString().trim { it <= ' ' }
        val email: String = et_signup_email.text.toString().trim { it <= ' ' }
        val password: String = et_signup_password.text.toString().trim { it <= ' ' }

        if (isNetworkAvailable(applicationContext)) {
            if (validadeForm(name, email, password)) {
                showProgressDialog(resources.getString(R.string.please_wait))
                firebaseInstance
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user = UserModel(firebaseUser.uid, name, email)

                            FirestoreClass().registerUser(this, user)

                            hideProgressDialog()


                        } else {
                            showErrorSnackBar("Something went wrong during your signup! ${task.exception}")
                            hideProgressDialog()
                        }
                    }.addOnFailureListener { exception ->
                        showErrorSnackBar(exception.message.toString())
                        hideProgressDialog()
                    }
            }
        } else showErrorSnackBar("No Internet Connection")


    }

    private fun validadeForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please set a name")
                false
            }
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
            R.id.btn_sign_up -> {
                registerUser()
            }
        }

    }

    fun userRegisteredSuccess() {
        firebaseInstance.signOut()
        showWelcomeSnabar("Registered with success!")
        finish()
    }
}