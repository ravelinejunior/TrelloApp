package br.com.trelloapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import br.com.trelloapp.R
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.USER_KEY_MODEL
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var user: UserModel? = null
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()

        if (intent.extras != null) {
            user = intent.getParcelableExtra(USER_KEY_MODEL)
            if (user != null)
                showWelcomeSnabar("Hello ${user?.name}")
        }

        nav_view_id.setNavigationItemSelectedListener(this)
    }

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
                Toast.makeText(this, "User", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_sign_out -> {
                signOutUser()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }

    private fun signOutUser() {
        firebaseAuth.signOut()
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}