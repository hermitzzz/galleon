package com.example.galleon.ui.profile.saves

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.galleon.data.FeedLists
import com.example.galleon.R
import com.example.galleon.databinding.FragmentProfileSavesBinding
import com.example.galleon.ui.profile.ProfileFragment
import com.example.galleon.ui.recyclers.PostRecyclerAdapter
import com.google.firebase.database.*


class ProfileSavesFragment(private val userid : String) : Fragment() {

    private lateinit var adapter: PostRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listsArrayList: ArrayList<FeedLists>
    private lateinit var databaseReference: DatabaseReference

    private var _binding: FragmentProfileSavesBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileSavesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dataInitialize()
        val layoutManager =
            StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        recyclerView = root.findViewById(R.id.profile_saves_recycler)
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
                        error("ERROR! Couldn't fetch user data! $it")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dataInitialize(){
        listsArrayList = arrayListOf()

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("saves")

        databaseReference.orderByKey().limitToLast(10).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                        for (postSnapshot in snapshot.children){
                            FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postSnapshot.key.toString()).get().addOnSuccessListener{ postsSnap ->
                                    val post = FeedLists(postsSnap.child("title").value.toString(),
                                        postsSnap.child("uid").value.toString(),
                                        postsSnap.child("uri").value.toString())
                                    listsArrayList.remove(post)
                                    listsArrayList.add(post)
                                    listsArrayList.reverse()
                                    adapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error("ERROR! Database error. $error")
            }
        })

    }
}