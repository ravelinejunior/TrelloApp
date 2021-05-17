package br.com.trelloapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.trelloapp.R
import br.com.trelloapp.model.BoardModel
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

open class BoardItemsAdapter(
    private val context: Context,
    private var listBoard: List<BoardModel>
) :
    RecyclerView.Adapter<BoardItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var onLongClickListener: OnLongClickListener? = null


    open class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val boardImage: CircleImageView = itemView.findViewById(R.id.iv_adapter_board_image)
        val textName: TextView = itemView.findViewById(R.id.tv_board_adapter_name)
        val textDate: TextView = itemView.findViewById(R.id.tv_board_adapter_created_at)
        val textCreatedBy: TextView = itemView.findViewById(R.id.tv_board_adapter_created_by)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_board_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = listBoard.size


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val board = listBoard[position]

        Glide.with(context).load(board.image).centerCrop()
            .placeholder(R.drawable.ic_board_place_holder).into(holder.boardImage)

        holder.textName.text = board.name
        holder.textCreatedBy.text = "Created By ${board.createdBy}"
        holder.textDate.text = "Created At ${board.createdAt}"

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener?.onClick(position, board)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (onLongClickListener != null) {
                onLongClickListener?.onLongClickListener(position, board)
            }
            true
        }


    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener):Boolean {
        this.onLongClickListener = onLongClickListener
        return true
    }

    interface OnClickListener {
        fun onClick(position: Int, model: BoardModel)
    }

    interface OnLongClickListener {
        fun onLongClickListener(position: Int, model: BoardModel)
    }

}

