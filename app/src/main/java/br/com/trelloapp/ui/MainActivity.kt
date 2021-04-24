package br.com.trelloapp.ui

import android.os.Bundle
import android.view.WindowManager
import br.com.trelloapp.R
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.USER_KEY_MODEL
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()

        if (intent.extras != null) {
            user = intent.getParcelableExtra(USER_KEY_MODEL)
            if (user != null)
                showWelcomeSnabar("Hello ${user?.name}")
        }
    }

    private fun setupActionBar() {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setSupportActionBar(toolbar_main_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
    }
}