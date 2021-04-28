package br.com.trelloapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.trelloapp.R
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.IMAGE_REFERENCE_DOCUMENT
import br.com.trelloapp.utils.Constants.IMAGE_USER_KEY
import br.com.trelloapp.utils.Constants.MOBILE_USER_KEY
import br.com.trelloapp.utils.Constants.NAME_USER_KEY
import br.com.trelloapp.utils.Constants.USER_KEY_MODEL
import br.com.trelloapp.utils.Constants.isNetworkAvailable
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*


class MyProfileActivity : BaseActivity(), View.OnClickListener {
    private var user: UserModel? = null

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var storageRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("Profile_Images")

    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl: String = ""

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
        btn_update_myProfile.setOnClickListener(this)
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

            R.id.btn_update_myProfile -> {

                if (isNetworkAvailable(this)) {
                    if (mSelectedImageFileUri != null) {
                        uploadUserImage()
                    } else {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        updateUserProfileData(
                            et_name_myProfile.text.toString(),
                            et_mobile_myProfile.text.toString().toLong()
                        )
                    }
                } else {
                    showErrorSnackBar("No Internet Connection!")
                }

            }

        }
        //TODO "ALTERAR CODIGO UTILIZANDO LIB DEXTER"
    }

    private fun updateUserProfileData(name: String, phone: Long) {
        if (isNetworkAvailable(this)) {

            val userHashMap: HashMap<String, Any> = HashMap()
            var anyChanges = false

            if (mProfileImageUrl.isNotEmpty() && mProfileImageUrl != user?.image) {
                userHashMap[IMAGE_USER_KEY] = mProfileImageUrl
                anyChanges = true
            }
            if (name.isNotEmpty() && name != user?.name) {
                userHashMap[NAME_USER_KEY] = name
                anyChanges = true
            }
            if (phone != 0L && phone != user?.mobile) {
                userHashMap[MOBILE_USER_KEY] = phone
                anyChanges = true
            }

            if (anyChanges) {
                FirestoreClass().updateUserProfileData(this, userHashMap)
                setResult(Activity.RESULT_OK)

            } else {
                showErrorSnackBar("None changes has been detected!")
                hideProgressDialog()
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {
            //storage file
            storageRef = storageRef.child(user!!.id).child(
                IMAGE_REFERENCE_DOCUMENT  + "." +
                        getFileExtension(mSelectedImageFileUri)
            )

            storageRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.i(
                    "TAGTaskSnapshot",
                    "Image Url ${taskSnapshot.metadata!!.reference!!.downloadUrl}"
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    mProfileImageUrl = uri.toString()
                    //update user data
                    updateUserProfileData(
                        et_name_myProfile.text.toString(),
                        et_mobile_myProfile.text.toString().toLong()
                    )

                    hideProgressDialog()


                }.addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }

        } else {
            hideProgressDialog()
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

}