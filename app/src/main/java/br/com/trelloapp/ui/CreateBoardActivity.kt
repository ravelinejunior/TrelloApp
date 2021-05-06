package br.com.trelloapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.trelloapp.R
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants
import br.com.trelloapp.utils.Constants.BOARD_IMAGE_REF
import br.com.trelloapp.utils.Constants.NAME_USER_KEY
import br.com.trelloapp.utils.Constants.USER_KEY_MODEL
import br.com.trelloapp.utils.Constants.getFileExtension
import br.com.trelloapp.utils.Constants.isNetworkAvailable
import br.com.trelloapp.utils.Constants.showImageChooser
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_board.*

class CreateBoardActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private val READ_STORAGE_PERMISSION_CODE: Int = 454
    }


    private var mSelectedImageFileUri: Uri? = null
    private var mUserName: String = ""
    private var mBoardImageUrl: String = ""
    private var mCurrentDate: String = ""

    private var userModel: UserModel? = null

    private var storageReference: StorageReference =
        FirebaseStorage.getInstance().reference.child(BOARD_IMAGE_REF)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        setupActionBar()

        if (intent.extras != null) {
            mUserName = intent.getStringExtra(NAME_USER_KEY).toString()
            userModel = intent.getParcelableExtra(USER_KEY_MODEL)
        }

        iv_create_board_image.setOnClickListener(this)

        btn_create_board_id.setOnClickListener(this)

    }


    private fun uploadBoardImage() {

        if (isNetworkAvailable(this)) {

            showProgressDialog(resources.getString(R.string.please_wait))

            storageReference = storageReference.child(getCurrentID())
                .child(

                    System.currentTimeMillis().toString() + "." +
                            getFileExtension(
                                mSelectedImageFileUri,
                                this
                            )
                )

            storageReference.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.i(
                    "TAGTaskSnapshot",
                    "Image Url Created Board ${taskSnapshot.metadata!!.reference!!.downloadUrl}"
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    mBoardImageUrl = uri.toString()
                    //update user data

                    createBoard()
                    hideProgressDialog()


                }.addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        } else {
            showErrorSnackBar("No Internet Connection")
            hideProgressDialog()
        }


    }

    private fun createBoard() {
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentID())

        val name: String = et_create_board_name.text.toString()
        mCurrentDate = getCurrentDate()

        val dateLong:Long = System.currentTimeMillis()

        val board = BoardModel(
            name, mBoardImageUrl, mUserName, mCurrentDate,dateLong, assignedUserArrayList
        )

        //creating board
        FirestoreClass().createBoard(this, board)
    }


    private fun setupActionBar() {
        setSupportActionBar(toolbar_create_board_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white)
            actionBar.title = resources.getString(R.string.create_board_title)
        }

        toolbar_create_board_activity.setNavigationOnClickListener {
            onBackPressed()
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

                showImageChooser(this)
            } else {
                Toast.makeText(this, "Oops, you denied the permissions!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data?.data != null) {
                    mSelectedImageFileUri = data.data!!

                    //case of image is successful, save on circle with glide
                    try {
                        Glide.with(this).load(mSelectedImageFileUri).centerCrop()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(iv_create_board_image)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_create_board_image -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    showImageChooser(this)
                } else {

                    //required the permissions
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_STORAGE_PERMISSION_CODE
                    )
                }
            }

            R.id.btn_create_board_id -> {

                if (!et_create_board_name.text.isNullOrEmpty()) {
                    if (mSelectedImageFileUri != null) {
                        uploadBoardImage()
                    } else {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        createBoard()
                    }
                } else {
                    showErrorSnackBar("Type something to add a board!")
                }

            }
        }
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }



}


