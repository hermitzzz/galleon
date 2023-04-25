package com.example.galleon.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment

import com.google.firebase.database.*

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.galleon.R
import com.example.galleon.data.User
import com.example.galleon.databinding.FragmentSearchBinding
import java.util.*
import kotlin.collections.ArrayList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView;
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.galleon.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


class SearchFragment(private val userid : String? = null) : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersArrayList: ArrayList<User>
    private var _binding: FragmentSearchBinding? = null
    private lateinit var searchView: SearchView
    private lateinit var adapter: MyAdapter
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var selectedProfilePicture : Uri
    private lateinit var useridRef: String
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = root.findViewById(R.id.recycler)
        searchView = root.findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this@SearchFragment.context)
        recyclerView.setHasFixedSize(true)


        usersArrayList = ArrayList()
        adapter=MyAdapter(usersArrayList)
        recyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue((User::class.java))
                        usersArrayList.add(user!!)
                        adapter.notifyDataSetChanged()
                    }

                }



            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                filterList(newText)

                return true
            }

        })
     /*   adapter.setOnItemClickListener(object : MyAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                showUser(position)

            }
            private fun showUser(position: Int) {
                val user = usersArrayList[position].username!!.lowercase()

                val database = FirebaseDatabase.getInstance().getReference("Usernames")
                database.child(user).get().addOnSuccessListener{
                    requireActivity().supportFragmentManager.beginTransaction().apply {
                        val userProfile = SearchFragment(it.value.toString())
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

        },*/



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun filterList(query: String) {
        val filteredList = ArrayList<User>()
        for (i in usersArrayList) {
            if (i.username?.lowercase()?.contains(query.lowercase())==true) {
                filteredList.add(i)
            }
        }
        adapter.searchDataList(filteredList)
    }


    /*  override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          //requireActivity().setContentView(R.layout.fragment_search)

          recyclerView = requireActivity().findViewById(R.id.recycler)
          searchView = requireActivity().findViewById(R.id.searchView)

          recyclerView.layoutManager = LinearLayoutManager(this@SearchFragment.context)
          recyclerView.setHasFixedSize(true)


          usersArrayList = ArrayList()
          adapter=MyAdapter(usersArrayList)
          recyclerView.adapter = adapter
          databaseReference = FirebaseDatabase.getInstance().getReference("Users")

          databaseReference.addValueEventListener(object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {
                  if (snapshot.exists()) {
                      for (userSnapshot in snapshot.children) {
                          val user = userSnapshot.getValue((User::class.java))
                          usersArrayList.add(user!!)
                          adapter.notifyDataSetChanged()
                      }

                  }



              }

              override fun onCancelled(error: DatabaseError) {
                  TODO("Not yet implemented")
              }
          })
          searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
              override fun onQueryTextSubmit(query: String?): Boolean {
                  return false
              }

              override fun onQueryTextChange(newText: String): Boolean {

                  filterList(newText)

                  return true
              }

          })


      }
      fun filterList(query: String) {
              val filteredList = ArrayList<User>()
              for (i in usersArrayList) {
                  if (i.username?.lowercase()?.contains(query.lowercase())==true) {
                      filteredList.add(i)
                  }
          }
          adapter.searchDataList(filteredList)
      }
  */

}


