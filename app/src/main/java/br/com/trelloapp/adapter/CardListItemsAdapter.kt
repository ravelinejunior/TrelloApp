package br.com.trelloapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.trelloapp.R
import br.com.trelloapp.model.CardModel
import br.com.trelloapp.model.SelectedMembersModel
import br.com.trelloapp.ui.TaskListActivity
import kotlinx.android.synthetic.main.item_card_adapter.view.*

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<CardModel>
) : RecyclerView.Adapter<CardListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_card_adapter, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: CardModel = list[position]

        if (model.labelColor.isNotEmpty()) {
            holder.itemView.view_label_color.visibility = View.VISIBLE
            holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(model.labelColor))

        } else {
            holder.itemView.view_label_color.visibility = View.GONE
        }

        holder.itemView.tv_card_name.text = model.name

        //verify if the mAssignedMemberList is bigger than 0 on TaskListActivity
        if ((context as TaskListActivity).mAssignedMemberList.size > 0) {
            val selectedMembersList: ArrayList<SelectedMembersModel> = ArrayList()

            for (i in context.mAssignedMemberList.indices) {
                for (j in model.assignedTo) {
                    if (context.mAssignedMemberList[i].id == j) {
                        val selectedMembers = SelectedMembersModel(
                            context.mAssignedMemberList[i].id,
                            context.mAssignedMemberList[i].image
                        )

                        selectedMembersList.add(selectedMembers)
                    }
                }
            }

            //verify if there is members added to that task
            if (selectedMembersList.size > 0) {
                if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                    holder.itemView.rv_item_card_adapter_selected_id.visibility = View.GONE
                } else {
                    holder.itemView.rv_item_card_adapter_selected_id.visibility = View.VISIBLE

                    holder.itemView.rv_item_card_adapter_selected_id.layoutManager =
                        GridLayoutManager(context, 6)

                    val adapter = CardMemberListAdapter(context, selectedMembersList, false)
                    holder.itemView.rv_item_card_adapter_selected_id.adapter = adapter

                    adapter.setOnClickListener(object : CardMemberListAdapter.OnClickListener {
                        override fun onClick() {
                            if (onClickListener != null) {
                                onClickListener!!.onClick(position, model)
                            }
                        }

                    })
                }
            } else {
                holder.itemView.rv_item_card_adapter_selected_id.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, model)
            }
        }

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, card: CardModel)
    }

    override fun getItemCount(): Int = list.size

}