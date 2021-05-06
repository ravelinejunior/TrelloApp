package br.com.trelloapp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import br.com.trelloapp.R
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.utils.Constants.BOARD_DETAIL
import kotlinx.android.synthetic.main.activity_members.*

class MembersActivity : BaseActivity() {

    private lateinit var mBoard: BoardModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(BOARD_DETAIL)) {
            mBoard = intent.getParcelableExtra(BOARD_DETAIL)!!
        }

        setupActionBar()

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_members_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white)
            actionBar.title = resources.getString(R.string.members)
        }
        toolbar_members_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member_menu_id -> {
                Toast.makeText(this, mBoard.name, Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}