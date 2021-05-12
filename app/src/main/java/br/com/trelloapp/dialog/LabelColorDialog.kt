package br.com.trelloapp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.trelloapp.R
import br.com.trelloapp.adapter.LabelColorAdapter
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class LabelColorDialog(
    context: Context,
    private var colorList: ArrayList<String>,
    private var title: String = "",
    private var mSelectedColor: String = ""
) : Dialog(context) {
    private var adapter: LabelColorAdapter? = null


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
        adapter = LabelColorAdapter(context, colorList, mSelectedColor)
        view.rvList_dialog_list.adapter = adapter

        adapter!!.onItemClickListener = object: LabelColorAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {

                dismiss()

                onItemSelected(color)

            }

        }
    }

    abstract fun onItemSelected(color:String)
}