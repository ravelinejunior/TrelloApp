package br.com.trelloapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.trelloapp.R
import br.com.trelloapp.adapter.BoardItemsAdapter
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.NAME_USER_KEY
import br.com.trelloapp.utils.Constants.USER_KEY_MODEL
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val PROFILE_KEY: Int = 145
    }


    private var user: UserModel? = null
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mUserName: String

    private lateinit var boardItemsAdapter: BoardItemsAdapter
    private var listBoard = ArrayList<BoardModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()

        if (intent.extras != null) {
            user = intent.getParcelableExtra(USER_KEY_MODEL)
            if (user != null)
                showWelcomeSnabar("Hello ${user?.name}")
        }

        FirestoreClass().loadUserData(this)

        nav_view_id.setNavigationItemSelectedListener(this)

        fab_create_board.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(USER_KEY_MODEL, user)
            intent.putExtra(NAME_USER_KEY, mUserName)

            startActivity(intent)
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupActionBar() {
        firebaseAuth = FirebaseAuth.getInstance()
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            //toggle the drawer
            toggleDrawer()
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu)
        }
    }

    private fun toggleDrawer() {
        //if open,close it
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                val intent = Intent(this, MyProfileActivity::class.java)
                intent.putExtra(USER_KEY_MODEL, user)
                startActivityForResult(intent, PROFILE_KEY)
            }

            R.id.nav_sign_out -> {
                signOutUser()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_KEY && resultCode == Activity.RESULT_OK) {
            FirestoreClass().loadUserData(this)
        } else {
            Log.e(MainActivity::class.java.simpleName, "onActivityResult: Error")
        }
    }

    private fun signOutUser() {
        firebaseAuth.signOut()
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    fun updateNavigationUserDetails(loggedInUser: UserModel?) {

        mUserName = loggedInUser!!.name
        Glide.with(this).load(loggedInUser.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).into(iv_nav_user_image)

        tv_nav_username.text = loggedInUser.name

        user = loggedInUser

    }

     fun loadRecyclerView(listBoard:ArrayList<BoardModel>) {
        hideProgressDialog()

        if (listBoard.size > 0) {
            rv_content_boards_list.visibility = View.VISIBLE
            tv_content_no_boards_available.visibility = View.GONE

            boardItemsAdapter = BoardItemsAdapter(this, listBoard)

            val linearLayoutManager = LinearLayoutManager(this)

            rv_content_boards_list.setHasFixedSize(true)
            rv_content_boards_list.layoutManager = linearLayoutManager
            rv_content_boards_list.adapter = boardItemsAdapter




        } else {
            rv_content_boards_list.visibility = View.GONE
            tv_content_no_boards_available.visibility = View.VISIBLE

        }
    }


}