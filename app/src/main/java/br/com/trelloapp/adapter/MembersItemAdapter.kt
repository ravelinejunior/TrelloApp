package br.com.trelloapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.trelloapp.R
import br.com.trelloapp.model.UserModel
import br.com.trelloapp.utils.Constants.SELECT
import br.com.trelloapp.utils.Constants.UNSELECT
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_member_adapter.view.*

open class MembersItemAdapter(
    private val context: Context,
    private val memberList: ArrayList<UserModel>
) : RecyclerView.Adapter<MembersItemAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

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

        if(model.selected){
            holder.itemView.iv_selected_item_member.visibility = View.VISIBLE
        }else{
            holder.itemView.iv_selected_item_member.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                if(model.selected){
                    onClickListener?.onClick(position,model,UNSELECT)
                }else{
                    onClickListener?.onClick(position,model,SELECT)
                }
            }
        }
    }

    override fun getItemCount(): Int = memberList.size

    interface OnClickListener {
        fun onClick(position: Int, user: UserModel,action:String)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}