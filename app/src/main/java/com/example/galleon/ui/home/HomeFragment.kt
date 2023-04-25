package com.example.galleon.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.galleon.data.FeedLists
import com.example.galleon.R
import com.example.galleon.databinding.FragmentHomeBinding
import com.example.galleon.ui.profile.ProfileFragment
import com.example.galleon.ui.recyclers.PostRecyclerAdapter
import com.google.firebase.database.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var adapter: PostRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listsArrayList: ArrayList<FeedLists>
    private lateinit var databaseReference: DatabaseReference

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dataInitialize()
        val layoutManager =
            StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        recyclerView = root.findViewById(R.id.home_recycler)
        recyclerView.layoutManager = layoutManager
        adapter = PostRecyclerAdapter(listsArrayList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : PostRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, type: PostRecyclerAdapter.ClickType) {
                when(type){
                    PostRecyclerAdapter.ClickType.USERNAME -> {showUser(position)}
                    PostRecyclerAdapter.ClickType.PHOTO -> {showPost(position)}
                }
            }

            private fun showUser(position: Int) {
                val user = listsArrayList[position].username.lowercase()

                val database = FirebaseDatabase.getInstance().getReference("Usernames")
                database.child(user).get().addOnSuccessListener{
                    requireActivity().supportFragmentManager.beginTransaction().apply {
                        val userProfile = ProfileFragment(it.value.toString())
                        replace(R.id.nav_host_fragment_activity_main, userProfile)
                            .disallowAddToBackStack()
                            .commit()
                    }
                }.addOnFailureListener{
                    error("ERROR! That user doesn't exist! $it")
                }
                    .addOnFailureListener{
                        Toast.makeText(activity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts")

        databaseReference.orderByKey().limitToLast(10).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (postSnapshot in snapshot.children){
                        FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(postSnapshot.child("uid").value.toString())
                            .child("username").get().addOnSuccessListener{
                                val username = it.value.toString()
                                val post = FeedLists(postSnapshot.child("title").value.toString(),
                                    username,
                                    postSnapshot.child("uri").value.toString())
                                listsArrayList.remove(post)
                                listsArrayList.add(post)
                                listsArrayList.reverse()
                                adapter.notifyDataSetChanged()
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("warning","ERROR! That user doesn't exist! $error")
            }
        })
    }

}