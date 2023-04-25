package com.example.galleon.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.galleon.PreferencesSettings
import com.example.galleon.R
import com.example.galleon.databinding.FragmentProfileBinding
import com.example.galleon.ui.profile.comments.ProfileCommentsFragment
import com.example.galleon.ui.profile.lists.ProfileListsFragment
import com.example.galleon.ui.profile.saves.ProfileSavesFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment(private val userid : String? = null): Fragment(), View.OnClickListener {


    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var selectedProfilePicture : Uri
    private lateinit var useridRef: String

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //userprofile user
        profileViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        useridRef = userid ?: firebaseAuth.uid!!

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val usernameView: TextView = binding.profileUsername
        val bioView: TextView = binding.profileBio
        val pictureView: CircleImageView = binding.profileProfilePicture

        profileViewModel.profileUsername.observe(viewLifecycleOwner) {
            usernameView.text = it.toString()
        }
        profileViewModel.profileBio.observe(viewLifecycleOwner){
            bioView.text = it.toString()
        }
        profileViewModel.profilePicture.observe(viewLifecycleOwner){
            Picasso.get().load(it.toUri()).into(pictureView)
        }
        getUserData()

        childFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_fragment_profile, ProfileListsFragment(useridRef))
                .addToBackStack(null)
                .commit()
        }

        //settings button
        val prefButton : Button = root.findViewById(R.id.btn_profile_popup)

        //userprofile navigation bar buttons
        val listsButton : Button = root.findViewById(R.id.btn_profile_nav_lists)
        val commentsButton : Button = root.findViewById(R.id.btn_profile_nav_comments)
        val savesButton : Button = root.findViewById(R.id.btn_profile_nav_saves)

        //button listeners
        prefButton.setOnClickListener{onClick(prefButton)}
        listsButton.setOnClickListener{onClick(listsButton)}
        commentsButton.setOnClickListener{onClick(commentsButton)}
        savesButton.setOnClickListener{onClick(savesButton)}

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //handling button listeners
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_profile_popup -> {
                val popupMenu = PopupMenu(context, requireView().findViewById(R.id.btn_profile_popup))
                popupMenu.menuInflater.inflate(R.menu.popup_profile_menu, popupMenu.menu)

                if (useridRef == FirebaseAuth.getInstance().currentUser!!.uid) {
                    // TODO follow-unfollow user
                    popupMenu.menu.findItem(R.id.item1).isVisible = false
                    popupMenu.menu.findItem(R.id.item1).isVisible = false
                    popupMenu.menu.findItem(R.id.item4).isVisible = false
                }else{
                    popupMenu.menu.findItem(R.id.item2).isVisible = false
                    popupMenu.menu.findItem(R.id.item3).isVisible = false
                    popupMenu.menu.findItem(R.id.item5).isVisible = false
                }

                popupMenu.setOnMenuItemClickListener{ menuItem ->
                    when (menuItem.itemId) {
                        //follow
                        R.id.item1 -> { followUser() }
                        //change userprofile pic
                        R.id.item2 -> { changeProfilePic() }
                        //change your bio
                        R.id.item3 -> { changeBio() }
                        //report
                        R.id.item4 -> { reportUser() }
                        //settings
                        R.id.item5 -> {
                            val pref = PreferencesSettings()
                            requireActivity().supportFragmentManager.beginTransaction().apply {
                                replace(R.id.main_frame_layout, pref)
                                    .addToBackStack(null)
                                    .commit()
                            }
                            }
                    }
                    true
                }
                popupMenu.show()
            }
            R.id.btn_profile_nav_lists -> {
                //This code creates a nested ProfileLists fragment
                val profileLists = ProfileListsFragment(useridRef)

                childFragmentManager.beginTransaction().apply {
                    replace(R.id.nav_host_fragment_fragment_profile, profileLists)
                        .addToBackStack(null)
                        .commit()
                }
            }
            R.id.btn_profile_nav_comments ->{
                //This code creates a nested ProfileComments fragment
                val profileComments = ProfileCommentsFragment(useridRef)

                childFragmentManager.beginTransaction().apply {
                    replace(R.id.nav_host_fragment_fragment_profile, profileComments)
                        .addToBackStack(null)
                        .commit()
                }
            }
            R.id.btn_profile_nav_saves ->{
                // This code creates a nested ProfileSaves fragment
                val profileSaves = ProfileSavesFragment(useridRef)

                childFragmentManager.beginTransaction().apply {
                    replace(R.id.nav_host_fragment_fragment_profile, profileSaves)
                        .addToBackStack(null)
                        .commit()
                }
            }
            else -> {
                error("Button doesn't exist")
            }
        }
    }
    // get User Data on Fragment creation
    private fun getUserData(){
        storage = FirebaseStorage.getInstance()

        val currentUser = useridRef

        database = FirebaseDatabase.getInstance().getReference("Users")

        storage.reference.child("Users").child(currentUser).downloadUrl.addOnSuccessListener{ uri ->
            profileViewModel.profilePicture.value = uri.toString()
        }

        database.child(currentUser).get().addOnSuccessListener{
            val bio = it.child("bio").value
            val username = it.child("username").value

            profileViewModel.profileUsername.value = username.toString()
            profileViewModel.profileBio.value = bio.toString()

        }
            .addOnFailureListener{
                Toast.makeText(activity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
            }

    }
    // follow User
    private fun followUser(){
        val currentUser = firebaseAuth.uid
        if (currentUser != null && userid != null) {
                database = FirebaseDatabase.getInstance().getReference("Users").child(currentUser).child("follows")
                database.child(userid).setValue("").addOnSuccessListener{
                    Toast.makeText(activity, getString(R.string.followed_user), Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(activity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                }
        }
    }
    // change Bio
    private fun changeBio(){
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_box_edit_bio, null)
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)
            .setTitle(getString(R.string.edit_bio))
        val editBio = dialogView.findViewById<EditText>(R.id.dialog_editBio)
        val tmpBio : String = if(profileViewModel.profileBio.value != null){
            profileViewModel.profileBio.value!!
        } else{
            ""
        }
        editBio.setText(tmpBio)

        with(builder){
            setPositiveButton(getString(R.string.choice_accept)){ _, _ ->
                //change bio
                database = FirebaseDatabase.getInstance().getReference("Users")

                val currentUser = firebaseAuth.uid
                if (currentUser != null) {
                    database.child(currentUser).child("bio").setValue(editBio.text.toString())
                        .addOnSuccessListener{
                            getUserData()
                        }.addOnFailureListener {
                            Toast.makeText(activity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                        }
                }
            }
            setNegativeButton(getString(R.string.choice_cancel)){ _, _ ->
            }
        }
        builder.show()
    }
    // change Profile Picture
    private fun changeProfilePic() {
        val builder = AlertDialog.Builder(activity)
            .setTitle(getString(R.string.edit_profilePictureStart))

        with(builder){
            setPositiveButton(getString(R.string.choice_chooseFile)){_,_ ->

                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(intent, 1)
                }
            }
            setNegativeButton(getString(R.string.choice_cancel)){ _, _ ->
            }
        }
        builder.show()
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null && data.data != null) {
            selectedProfilePicture = data.data!!

            val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_box_edit_profile_picture, null)
            val builder = AlertDialog.Builder(activity).create()

            builder.setView(dialogView)
            builder.setTitle(getString(R.string.edit_profilePicture))

            val editProfilePicture = dialogView.findViewById<CircleImageView>(R.id.dialog_editProfilePicture)

            editProfilePicture.setImageURI(selectedProfilePicture)

            editProfilePicture.setOnClickListener{
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    builder.dismiss()
                    startActivityForResult(intent, 1)
                }
            }

            builder.setButton(getString(R.string.choice_cancel)) { _, _ ->
            }
            builder.setButton2(getString(R.string.choice_accept)) { _, _ ->
                uploadData()
            }

            builder.show()
        }
    }
    private fun uploadData() {
        val currentUser = firebaseAuth.uid
        if (currentUser != null) {
            storage = FirebaseStorage.getInstance()
            val storageReference = storage.reference.child("Users").child(currentUser)
            storageReference.putFile(selectedProfilePicture).addOnCompleteListener{
                if(it.isSuccessful){
                    storageReference.downloadUrl.addOnSuccessListener{
                        getUserData()
                    }
                }
            }
        }
    }
    // report User
    private fun reportUser(){
        val currentUser = firebaseAuth.uid
        if (currentUser != null && userid != null) {
            FirebaseDatabase.getInstance().getReference("Reported").child(userid).child("reportedBy")
                .child(currentUser).setValue("")
                .addOnSuccessListener {
                    Toast.makeText(activity, getString(R.string.user_reported), Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(activity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                }
        }
    }

}
