package br.com.trelloapp.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.trelloapp.R
import br.com.trelloapp.adapter.TaskItemAdapter
import br.com.trelloapp.firebase.FirestoreClass
import br.com.trelloapp.model.BoardModel
import br.com.trelloapp.model.TaskModel
import br.com.trelloapp.utils.Constants.BOARDS_KEY_NAME
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {

    private lateinit var boardModel: BoardModel
    private lateinit var taskAdapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        if (intent.extras != null) {
            boardModel = intent.getParcelableExtra(BOARDS_KEY_NAME)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getBoardDetail(this, boardModel.documentId)

        setupActionBar()

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_task_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white)
            actionBar.title = boardModel.name
        }
    }

    fun boardDetails(board: BoardModel) {
        hideProgressDialog()

        val addTaskList = TaskModel(
            resources.getString(R.string.add_list)
        )

        board.taskList.add(addTaskList)

        rv_task_list.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_task_list.setHasFixedSize(true)

        taskAdapter = TaskItemAdapter(this, board.taskList)

        rv_task_list.adapter = taskAdapter

    }
}