package br.com.trelloapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.trelloapp.R
import kotlinx.android.synthetic.main.item_label_color.view.*

class LabelColorAdapter(
    private val context: Context,
    private val colorList: ArrayList<String>,
    private val mSelectedColor: String
) : RecyclerView.Adapter<LabelColorAdapter.MyViewHolder>() {

    var onItemClickListener:OnItemClickListener? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_label_color, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemColor = colorList[position]

        holder.itemView.view_main_item_label_color.setBackgroundColor(Color.parseColor(itemColor))

        //verify if is selected to show the image of check
        if (itemColor == mSelectedColor) {
            holder.itemView.iv_selected_color_item_label_color.visibility = View.VISIBLE
        } else {
            holder.itemView.iv_selected_color_item_label_color.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(position,itemColor)
        }
    }

    override fun getItemCount(): Int = colorList.size

    interface OnItemClickListener {
        fun onClick(position: Int, color: String)
    }
}