package com.example.galleon.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.galleon.R
import com.example.galleon.data.User
import kotlinx.android.synthetic.main.post_card_view_layout.view.*

class MyAdapter(private var users:List<User> ):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

  /*  private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener, nothing: Nothing){
        mListener = listener
    }
*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.search_cardview_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = users[position]
        holder.tvName.text=currentItem.username
        holder.tvBio.text=currentItem.bio
    }

    override fun getItemCount(): Int {
        return users.size
    }
    fun searchDataList(searchList: List<User>){
        users = searchList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView:android.view.View):RecyclerView.ViewHolder(itemView){
        var tvName:TextView=itemView.findViewById((R.id.search_username_list))
        var tvBio:TextView=itemView.findViewById((R.id.search_bio_list))
        /*init{

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }*/
    }
}