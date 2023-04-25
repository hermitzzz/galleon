package com.example.galleon.ui.profile.lists

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.galleon.data.FeedLists
import com.example.galleon.R
import com.example.galleon.databinding.FragmentProfileListsBinding
import com.example.galleon.ui.recyclers.PostRecyclerAdapter
import com.google.firebase.database.*

class ProfileListsFragment(private val userid : String) : Fragment() {

    private lateinit var adapter: PostRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listsArrayList: ArrayList<FeedLists>
    private lateinit var databaseReference: DatabaseReference

    private var _binding: FragmentProfileListsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileListsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dataInitialize()
        val layoutManager =
            StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        recyclerView = root.findViewById(R.id.profile_lists_recycler)
        recyclerView.layoutManager = layoutManager
        adapter = PostRecyclerAdapter(listsArrayList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : PostRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, type: PostRecyclerAdapter.ClickType) {
                when(type){
                    PostRecyclerAdapter.ClickType.USERNAME -> {}
                    PostRecyclerAdapter.ClickType.PHOTO -> {showPost(position)}
                }
            }

            private fun showPost(position: Int){
                // TODO Implement Post View
                //      position is a variable that stores item id in listArrayList
                //      so you can pull data from listArrayList[position]
            }
        })

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dataInitialize(){
        listsArrayList = arrayListOf()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("posts")

        databaseReference.orderByKey().limitToLast(10).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    FirebaseDatabase.getInstance().getReference("Users").child(userid).child("username").get().addOnSuccessListener{ userSnap ->
                        for (postSnapshot in snapshot.children){
                            FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postSnapshot.key.toString()).get().addOnSuccessListener{ postsSnap ->
                                    val post = FeedLists(postsSnap.child("title").value.toString(),
                                        userSnap.value.toString(),
                                        postsSnap.child("uri").value.toString())
                                    listsArrayList.remove(post)
                                    listsArrayList.add(post)
                                    listsArrayList.reverse()
                                    adapter.notifyDataSetChanged()
                                }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                error("ERROR! That user doesn't exist! $error")
            }
        })
    }
}