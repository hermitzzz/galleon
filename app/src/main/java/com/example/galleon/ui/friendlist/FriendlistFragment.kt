package com.example.galleon.ui.friendlist

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.galleon.R
import com.example.galleon.data.FeedLists
import com.example.galleon.data.User
import com.example.galleon.databinding.FragmentFriendlistBinding
import com.example.galleon.databinding.FragmentSearchBinding
import com.example.galleon.ui.profile.ProfileViewModel
import com.example.galleon.ui.search.MyAdapter
import com.example.galleon.ui.search.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class FriendlistFragment : Fragment() {


    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersArrayList2: ArrayList<User>
    private var _binding: FragmentFriendlistBinding? = null
    private lateinit var adapter: MyAdapterFriend
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var selectedProfilePicture: Uri
    private lateinit var useridRef: String
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(FriendlistViewModel::class.java)

        _binding = FragmentFriendlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.recycler_friendlist)
        firebaseAuth = FirebaseAuth.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(this@FriendlistFragment.context)
        recyclerView.setHasFixedSize(true)


        usersArrayList2 = ArrayList()
        adapter = MyAdapterFriend(usersArrayList2)
        recyclerView.adapter = adapter

        val currentUser = firebaseAuth.uid
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(currentUser.toString())
                .child("follows")

        databaseReference.orderByKey().limitToLast(10).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(postSnapshot.key.toString()).get()
                            .addOnSuccessListener { userSnap ->
                                val user = User(
                                    userSnap.child("username").value.toString(),
                                    userSnap.child("bio").value.toString()
                                )
                                usersArrayList2.add(user!!)
                                adapter.notifyDataSetChanged()

                            }


                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
