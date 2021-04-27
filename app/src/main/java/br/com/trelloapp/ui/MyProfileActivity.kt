package br.com.trelloapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.trelloapp.R
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.USER_KEY_MODEL
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_my_profile.*


class MyProfileActivity : AppCompatActivity(), View.OnClickListener {
    private var user: UserModel? = null
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mSelectedImageFileUri: Uri? = null

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 12510
        private const val PICK_IMAGE_REQUEST_CODE = 1615
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        if (intent.extras != null) {
            user = intent.getParcelableExtra(USER_KEY_MODEL)
            setUserFields(user!!)
        } else {
            FirestoreClass().loadUserData(this)
        }

        setupActionBar()

        iv_user_image_myProfile.setOnClickListener(this)


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
        Glide.with(this).load(user.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).into(iv_user_image_myProfile)

        et_email_myProfile.setText(user.email)
        et_name_myProfile.setText(user.name)
        et_mobile_myProfile.setText(user.mobile.toString())
    }

    fun updateNavigationUserDetails(loggedInUser: UserModel?) {
        Glide.with(this).load(loggedInUser?.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).into(iv_user_image_myProfile)

        et_email_myProfile.setText(loggedInUser?.email)
        et_name_myProfile.setText(loggedInUser?.name)
        et_mobile_myProfile.setText(loggedInUser?.mobile.toString())
    }


    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.iv_user_image_myProfile -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    showImageChooser()
                } else {

                    //required the permissions
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_STORAGE_PERMISSION_CODE
                    )
                }


            }

        }
        //TODO "ALTERAR CODIGO UTILIZANDO LIB DEXTER"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TODO show image dialog
                showImageChooser()
            } else {
                Toast.makeText(this, "Oops, you denied the permissions!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImageChooser() {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data?.data != null) {
                    mSelectedImageFileUri = data.data

                    //case of image is successful, save on circle with glide
                    try {
                        Glide.with(this).load(mSelectedImageFileUri).centerCrop()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(iv_user_image_myProfile)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

}