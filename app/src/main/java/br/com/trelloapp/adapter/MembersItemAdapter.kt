package br.com.trelloapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.trelloapp.R
import br.com.trelloapp.model.UserModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_member_adapter.view.*

open class MembersItemAdapter(
    private val context: Context,
    private val memberList: ArrayList<UserModel>
) : RecyclerView.Adapter<MembersItemAdapter.MyViewHolder>() {

    private lateinit var onClickListener: OnClickLister

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_member_adapter, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: UserModel = memberList[position]

        Glide.with(context).load(model.image).centerCrop()
            .placeholder(context.resources.getDrawable(R.drawable.ic_user_place_holder))
            .into(holder.itemView.iv_member_image)

        holder.itemView.tv_member_name.text = model.name
        holder.itemView.tv_member_email.text = model.email
        holder.itemView.tv_member_mobile_id.text = model.mobile.toString()
    }

    override fun getItemCount(): Int = memberList.size

    interface OnClickLister {
        fun onClick(position: Int, user: UserModel)
    }

    fun setOnClickListener(onClickLister: OnClickLister) {
        this.onClickListener = onClickLister
    }
}