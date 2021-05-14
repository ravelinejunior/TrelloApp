package br.com.trelloapp.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.trelloapp.R
import br.com.trelloapp.model.CardModel
import br.com.trelloapp.model.TaskModel
import br.com.trelloapp.ui.TaskListActivity
import kotlinx.android.synthetic.main.item_task_adapter.view.*
import java.util.*

class TaskItemAdapter(private val context: Context, private var listTask: ArrayList<TaskModel>) :
    RecyclerView.Adapter<TaskItemAdapter.MyViewHolder>() {

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_task_adapter, parent, false)
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

        //Add Card List
        holder.itemView.tv_item_task_list_title.text = model.title
        holder.itemView.tv_add_item_task_list.setOnClickListener {
            holder.itemView.tv_add_item_task_list.visibility = View.GONE
            holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE
        }

        holder.itemView.ib_task_list_close_list_name.setOnClickListener {
            holder.itemView.tv_add_item_task_list.visibility = View.VISIBLE
            holder.itemView.cv_add_task_list_name.visibility = View.GONE
        }

        holder.itemView.ib_task_list_done_list_name.setOnClickListener {
            //save and display board
            val listName: String = holder.itemView.et__item_task_list_name.text.toString()

            if (listName.isNotEmpty()) {
                if (context is TaskListActivity) {
                    context.createTaskList(listName, position)
                }
            } else {
                Toast.makeText(context, "Type the title.", Toast.LENGTH_SHORT).show()
            }
        }

//Edit card list
        holder.itemView.ib_task_list_edit_list_name.setOnClickListener {
            holder.itemView.et_item_edit_task_list_name.setText(model.title)
            holder.itemView.ll_title_view.visibility = View.GONE
            holder.itemView.cv_edit_task_list_name.visibility = View.VISIBLE
        }

        holder.itemView.ib_task_list_close_editable_view.setOnClickListener {
            holder.itemView.ll_title_view.visibility = View.VISIBLE
            holder.itemView.cv_edit_task_list_name.visibility = View.GONE
        }

        holder.itemView.ib_task_list_done_edit_list_name.setOnClickListener {
            // done editing and saving on firebase
            val listName: String = holder.itemView.et_item_edit_task_list_name.text.toString()

            if (listName.isNotEmpty()) {
                if (context is TaskListActivity) {
                    context.updateTaskList(position, listName, model)
                }
            } else {
                Toast.makeText(context, "Type the title.", Toast.LENGTH_SHORT).show()
            }

        }

        //delete card list
        holder.itemView.ib_task_list_delete_list.setOnClickListener {
            //delete
            alertDialogDeleletedList(position, model.title)
        }


        //add card name
        holder.itemView.tv_item_add_card.setOnClickListener {
            holder.itemView.tv_item_add_card.visibility = View.GONE
            holder.itemView.cv_add_card.visibility = View.VISIBLE
        }

        holder.itemView.ib_task_list_close_card_name.setOnClickListener {
            holder.itemView.tv_item_add_card.visibility = View.VISIBLE
            holder.itemView.cv_add_card.visibility = View.GONE
        }

        holder.itemView.ib_task_list_done_card_name.setOnClickListener {
            //save and display board
            val cardName: String = holder.itemView.et_item_card_name.text.toString()

            if (cardName.isNotEmpty()) {
                if (context is TaskListActivity) {
                    context.addCardToTaskList(cardName, position)
                }
            } else {
                Toast.makeText(context, "Type the title.", Toast.LENGTH_SHORT).show()
            }
        }

        //setting adapter inside an adapter

        holder.itemView.rv_item_card_list.layoutManager = LinearLayoutManager(context)
        holder.itemView.rv_item_card_list.setHasFixedSize(true)

        val adapter = CardListItemsAdapter(context, model.cards)
        holder.itemView.rv_item_card_list.adapter = adapter

        //card list with details
        adapter.setOnClickListener(object : CardListItemsAdapter.OnClickListener {
            override fun onClick(cardPosition: Int, card: CardModel) {
                if (context is TaskListActivity) {
                    context.cardDetails(position, cardPosition)
                }
            }

        })

        //creating the drag and drop function
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        holder.itemView.rv_item_card_list.addItemDecoration(dividerItemDecoration)

        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                //get the dragged position
                val draggedPosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition

                if (mPositionDraggedFrom == -1) {
                    mPositionDraggedFrom = draggedPosition
                }

                mPositionDraggedTo = targetPosition

                Collections.swap(listTask[position].cards, draggedPosition, targetPosition)
                adapter.notifyItemMoved(draggedPosition, targetPosition)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }


            //call it when user stops to drag and drop and then save the thing
            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if (mPositionDraggedFrom != -1 &&
                    mPositionDraggedTo != -1 &&
                    mPositionDraggedFrom != mPositionDraggedTo
                ) {
                    (context as TaskListActivity).updateCardsTaskList(
                        position,
                        listTask[position].cards
                    )

                    //reset the position
                    mPositionDraggedFrom = -1
                    mPositionDraggedTo = -1
                }
            }
        }
        )

        //call the helper
        helper.attachToRecyclerView(holder.itemView.rv_item_card_list)
    }

    private fun Int.toDP(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPX(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun alertDialogDeleletedList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialog, which ->

            dialog.dismiss()

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        val dialog: Dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}