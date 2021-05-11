package br.com.trelloapp.ui

import android.os.Bundle
import br.com.trelloapp.R
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.utils.Constants.BOARDS_KEY_NAME_COLLECTION
import br.com.trelloapp.utils.Constants.CARD_LIST_ITEM_POSITION
import br.com.trelloapp.utils.Constants.TASK_LIST_ITEM_POSITION
import kotlinx.android.synthetic.main.activity_card_details.*

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardModel: BoardModel
    private var mCardPosition: Int = -1
    private var mTaskItemPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        if (intent.hasExtra(BOARDS_KEY_NAME_COLLECTION)) {
            mBoardModel = intent.getParcelableExtra(BOARDS_KEY_NAME_COLLECTION)!!
            mCardPosition = intent.getIntExtra(CARD_LIST_ITEM_POSITION, -1)
            mTaskItemPosition = intent.getIntExtra(TASK_LIST_ITEM_POSITION, -1)
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
            actionBar.title =
                mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].name
        }

        toolbar_card_details_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initFields() {
        et_name_card_details.setText(mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].name)
        tv_select_due_date_card_details.text =mBoardModel.taskList[mTaskItemPosition].cards[mCardPosition].createAt
    }
}