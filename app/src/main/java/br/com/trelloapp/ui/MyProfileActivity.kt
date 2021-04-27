package br.com.trelloapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.trelloapp.R
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.USER_KEY_MODEL
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity() {
    private var user: UserModel? = null
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        if (intent.extras != null) {
            user = intent.getParcelableExtra(USER_KEY_MODEL)
            setUserFields(user!!)
        }else{
            FirestoreClass().loadUserData(this)
        }

        setupActionBar()


    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white)
            actionBar.title = user?.name
        }
    }

    private fun setUserFields(user: UserModel) {
        if (user != null) {
            Glide.with(this).load(user.image).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder).into(iv_user_image_myProfile)

            et_email_myProfile.setText(user.email)
            et_name_myProfile.setText(user.name)
            et_mobile_myProfile.setText(user.mobile.toString())
        }
    }

    fun updateNavigationUserDetails(loggedInUser: UserModel?) {
        Glide.with(this).load(loggedInUser?.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).into(iv_user_image_myProfile)

        et_email_myProfile.setText(loggedInUser?.email)
        et_name_myProfile.setText(loggedInUser?.name)
        et_mobile_myProfile.setText(loggedInUser?.mobile.toString())
    }
}