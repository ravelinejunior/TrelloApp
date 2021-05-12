package br.com.trelloapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.trelloapp.R
import br.com.trelloapp.model.SelectedMembersModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_card_selected_member.view.*

open class CardMemberListAdapter(
    private val context: Context,
    private val list: ArrayList<SelectedMembersModel>

) : RecyclerView.Adapter<CardMemberListAdapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_card_selected_member, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        if (position == list.size - 1) {
            holder.itemView.iv_item_card_selected_add_member.visibility = View.VISIBLE
            holder.itemView.iv_item_card_selected_member_image.visibility = View.GONE
        } else {
            holder.itemView.iv_item_card_selected_add_member.visibility = View.GONE
            holder.itemView.iv_item_card_selected_member_image.visibility = View.VISIBLE

            Glide.with(context).load(model.image).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_item_card_selected_member_image)
        }

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick()
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick()
    }
}