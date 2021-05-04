package br.com.trelloapp.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.com.trelloapp.R
import br.com.trelloapp.model.TaskModel
import br.com.trelloapp.ui.TaskListActivity
import kotlinx.android.synthetic.main.item_task.view.*

class TaskItemAdapter(private val context: Context, private var listTask: ArrayList<TaskModel>) :
    RecyclerView.Adapter<TaskItemAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            parent.width,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((5.toDP()).toPX(), 0, (5.toDP()).toPX(), 0)

        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = listTask.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = listTask[position]

        if (position == listTask.size - 1) {
            holder.itemView.tv_add_item_task_list.visibility = View.VISIBLE
            holder.itemView.ll_task_item.visibility = View.GONE
        } else {
            holder.itemView.tv_add_item_task_list.visibility = View.GONE
            holder.itemView.ll_task_item.visibility = View.VISIBLE
        }

        holder.itemView.tv_item_task_list_title.text = model.title
        holder.itemView.tv_add_item_task_list.setOnClickListener {
            holder.itemView.tv_add_item_task_list.visibility = View.GONE
            holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE
        }

        holder.itemView.ib_task_list_close_card_name.setOnClickListener {
            holder.itemView.tv_add_item_task_list.visibility = View.VISIBLE
            holder.itemView.cv_add_task_list_name.visibility = View.GONE
        }

        holder.itemView.ib_task_list_done_list_name.setOnClickListener {
            //save and display board
            val listName: String = holder.itemView.et__item_task_list_name.text.toString()

            if (listName.isNotEmpty()) {
                if (context is TaskListActivity) {
                    context.createTaskList(listName,position)
                }
            } else {
                Toast.makeText(context, "Type the title.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun Int.toDP(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPX(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}