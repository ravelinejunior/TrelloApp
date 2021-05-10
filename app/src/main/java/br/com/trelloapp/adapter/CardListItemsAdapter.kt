package br.com.trelloapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.trelloapp.R
import br.com.trelloapp.model.CardModel
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

        holder.itemView.tv_card_name.text = model.name

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(position,model)
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