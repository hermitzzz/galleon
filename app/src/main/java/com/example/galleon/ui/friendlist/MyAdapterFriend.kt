package com.example.galleon.ui.friendlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.galleon.R
import com.example.galleon.data.User
import com.example.galleon.ui.search.MyAdapter

class MyAdapterFriend (private var userses:List<User> ): RecyclerView.Adapter<MyAdapterFriend.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.friendlist_card_view_layout, parent, false)
        return MyAdapterFriend.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userses[position]
        holder.tvName.text=currentItem.username
        holder.tvBio.text=currentItem.bio
    }

    override fun getItemCount(): Int {
        return userses.size
    }

    class MyViewHolder (itemView:android.view.View):RecyclerView.ViewHolder(itemView) {
        var tvName:TextView=itemView.findViewById((R.id.friend_username_list1))
        var tvBio:TextView=itemView.findViewById((R.id.friend_bio_list1))
    }

}