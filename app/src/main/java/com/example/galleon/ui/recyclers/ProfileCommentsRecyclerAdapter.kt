package com.example.galleon.ui.recyclers

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.galleon.data.FeedComments
import com.example.galleon.R

class ProfileCommentsRecyclerAdapter(private val listsList: ArrayList<FeedComments>) : RecyclerView.Adapter<ProfileCommentsRecyclerAdapter.ProfileCommentsRecyclerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileCommentsRecyclerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.comments_cardview_layout, parent, false)
        return ProfileCommentsRecyclerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileCommentsRecyclerViewHolder, pos: Int) {
        val currentItem = listsList[pos]
        holder.title.text = currentItem.title
        holder.username.text = currentItem.username
        holder.comment.text = currentItem.comment
    }

    override fun getItemCount(): Int {
        return listsList.size
    }

    class ProfileCommentsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.comments_list_title)
        val username: TextView = itemView.findViewById(R.id.comments_list_username)
        val comment: TextView = itemView.findViewById(R.id.comments_list_comment)
    }
}