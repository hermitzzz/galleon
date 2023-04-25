package com.example.galleon.ui.profile.comments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.galleon.data.FeedComments
import com.example.galleon.R
import com.example.galleon.databinding.FragmentProfileCommentsBinding
import com.example.galleon.ui.recyclers.PostRecyclerAdapter
import com.example.galleon.ui.recyclers.ProfileCommentsRecyclerAdapter
import com.google.firebase.database.*

class ProfileCommentsFragment(private val userid : String) : Fragment() {

    private lateinit var adapter: ProfileCommentsRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listsArrayList: ArrayList<FeedComments>
    private lateinit var databaseReference: DatabaseReference

    lateinit var comment : Array<String>
    lateinit var username : Array<String>
    lateinit var title : Array<String>
    private var _binding: FragmentProfileCommentsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileCommentsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dataInitialize()
        val layoutManager =
            StaggeredGridLayoutManager(
                1,
                StaggeredGridLayoutManager.VERTICAL
            )
        recyclerView = root.findViewById(R.id.profile_comments_recycler)
        recyclerView.layoutManager = layoutManager
        adapter = ProfileCommentsRecyclerAdapter(listsArrayList)
        recyclerView.adapter = adapter
        // TODO("Implement click listener in ProfileCommentsRecyclerAdapter")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dataInitialize(){
        listsArrayList = arrayListOf()

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("comments")
        databaseReference.orderByKey().limitToLast(10).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(userid).child("username").get().addOnSuccessListener{ userSnap ->
                            for (postSnapshot in snapshot.children){
                                FirebaseDatabase.getInstance().getReference("Comments")
                                    .child(postSnapshot.key.toString()).get()
                                    .addOnSuccessListener{ postsSnap ->
                                        val list = FeedComments(
                                                postsSnap.child("text").value.toString(),
                                                userSnap.value.toString(),
                                                postsSnap.child("comment").value.toString())
                                        listsArrayList.remove(list)
                                        listsArrayList.add(list)
                                        listsArrayList.reverse()
                                        adapter.notifyDataSetChanged() } } } } }
            override fun onCancelled(error: DatabaseError) {
                Log.w("warning","ERROR! $error")}
        })
    }
}