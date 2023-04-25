package com.example.galleon.ui.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.example.galleon.R
import com.example.galleon.data.FeedLists
import kotlinx.android.synthetic.main.post_card_view_layout.view.*
import com.bumptech.glide.request.target.Target

class PostRecyclerAdapter(private val listsList: ArrayList<FeedLists>) :
    RecyclerView.Adapter<PostRecyclerAdapter.PostRecyclerViewHolder>() {

    enum class ClickType{USERNAME, PHOTO}

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int, type: ClickType)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostRecyclerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_card_view_layout, parent, false)
        return PostRecyclerViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: PostRecyclerViewHolder, pos: Int) {
        val currentItem = listsList[pos]
        holder.title.text = currentItem.title
        holder.username.text = currentItem.username
        Glide.with(holder.image.context).load(currentItem.uri.toUri())
            .apply(RequestOptions()
                .fitCenter()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL))
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return  listsList.size
    }

    class PostRecyclerViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView)
    {
        val title : TextView = itemView.findViewById(R.id.post_list_title)
        val username : TextView = itemView.findViewById(R.id.post_list_username)
        val image : ImageView = itemView.findViewById(R.id.post_list_image)

        init{
            itemView.post_list_username.setOnClickListener {
                listener.onItemClick(adapterPosition, ClickType.USERNAME)
            }
            itemView.post_list_image.setOnClickListener {
                listener.onItemClick(adapterPosition, ClickType.PHOTO)
            }
        }
    }

}