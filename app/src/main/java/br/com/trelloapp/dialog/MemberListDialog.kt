package br.com.trelloapp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.trelloapp.R
import br.com.trelloapp.adapter.MembersItemAdapter
import br.com.trelloapp.model.UserModel
import kotlinx.android.synthetic.main.dialog_list.view.*


abstract class MemberListDialog(
    context: Context,
    private var title: String = "",
    private var memberList: ArrayList<UserModel>

) : Dialog(context) {
    private var adapter: MembersItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null, false)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)

        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        view.tvTitle_dialog_list.text = title
        view.rvList_dialog_list.layoutManager = LinearLayoutManager(context)
        adapter = MembersItemAdapter(context, memberList)
        view.rvList_dialog_list.adapter = adapter

        adapter!!.setOnClickListener(object : MembersItemAdapter.OnClickListener {
            override fun onClick(position: Int, user: UserModel, action: String) {
                dismiss()
                onItemSelected(user, action)
            }

        })
    }

    abstract fun onItemSelected(user: UserModel, action: String)
}