package br.com.trelloapp.ui

import android.os.Bundle
import br.com.trelloapp.R
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.utils.Constants.BOARDS_KEY_NAME_COLLECTION
import kotlinx.android.synthetic.main.activity_card_details.*

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardModel: BoardModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        if (intent.hasExtra(BOARDS_KEY_NAME_COLLECTION)) {
            mBoardModel = intent.getParcelableExtra(BOARDS_KEY_NAME_COLLECTION)!!
        }
        setupActionBar()
        initFields()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_card_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white)
            actionBar.title = mBoardModel.name
        }

        toolbar_card_details_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initFields(){
        et_name_card_details.setText(mBoardModel.name)
        tv_select_due_date_card_details.text = mBoardModel.createdAt
    }
}