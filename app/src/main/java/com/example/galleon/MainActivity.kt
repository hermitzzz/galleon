package com.example.galleon

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.example.galleon.databinding.ActivityMainBinding
import com.example.galleon.ui.create.UploadActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(),OnSharedPreferenceChangeListener{

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.menu.getItem(2).isEnabled = false //disable placeholder

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.fab.setOnClickListener{
            val intent = Intent(this, UploadActivity::class.java)
            startActivity((intent))
        }

        val listener = OnSharedPreferenceChangeListener { prefs, key ->
            onSharedPreferenceChanged(prefs, key) }

        PreferenceManager
            .getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(listener)

        navView.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navigation_home      -> {
                    navController.navigate(R.id.navigation_home)
                    true }
                R.id.navigation_search    -> {
                    navController.navigate(R.id.navigation_search)
                    true }
                R.id.navigation_friendlist-> {
                    navController.navigate(R.id.navigation_friendlist)
                    true }
                R.id.navigation_profile   -> {
                    navController.navigate(R.id.navigation_profile)
                    true }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        //preferenceManager.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    // TODO Listener duplicates everytime prefs is changed...?
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key){
            "pref_theme"-> { //change theme
                val theme = sharedPreferences?.getBoolean(key, false)
                if (theme != true) {
                    AppCompatDelegate.MODE_NIGHT_NO
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                else {
                    AppCompatDelegate.MODE_NIGHT_YES
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            "pref_language" ->{ //change language
                val lang = sharedPreferences?.getString(key, "en-US")!!
                val localLang = Locale.forLanguageTag(lang)
                resources.configuration.setLocale(localLang)
            }
            "pref_nick" -> { //change nickname
                firebaseDatabase = FirebaseDatabase.getInstance()
                firebaseAuth = FirebaseAuth.getInstance()
                var username : String

                firebaseAuth.uid?.let { firebaseDatabase.getReference("Users").child(it).child("username").get().addOnSuccessListener{ name ->
                    username = name.value.toString()

                    val newNick = sharedPreferences?.getString(key, username)!!

                    firebaseDatabase.getReference("Usernames").child(newNick.lowercase()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists())
                                Toast.makeText(this@MainActivity, getString(R.string.error_username_is_taken), Toast.LENGTH_SHORT).show()
                            else{
                                Log.d("t", newNick)
                                Log.d("t", username)
                                firebaseDatabase.getReference("Usernames").child(username.lowercase()).removeValue()
                                firebaseDatabase.getReference("Usernames").child(newNick.lowercase()).setValue(it)
                                firebaseDatabase.getReference("Users").child(it).child("username").setValue(newNick)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@MainActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                        }
                    })
                } }
            }
            "pref_password" -> { //change password
                val user = Firebase.auth.currentUser
                val newPass = sharedPreferences?.getString(key, "")!!

                user!!.updatePassword(newPass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@MainActivity, getString(R.string.toast_password_changed), Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this@MainActivity, getString(R.string.error_couldnt_change_password), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            "pref_deletion" -> {
                firebaseDatabase = FirebaseDatabase.getInstance()
                val user = Firebase.auth.currentUser
                var username : String
                firebaseDatabase.getReference("Users").child(user!!.uid).child("username").get().addOnSuccessListener {
                    firebaseDatabase
                        .getReference("Posts")
                        .orderByChild("uid")
                        .equalTo(user.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (data : DataSnapshot in snapshot.children){
                                    data.ref.removeValue()
                                }
                                username = it.value.toString()
                                firebaseDatabase.getReference("Usernames").child(username.lowercase())
                                    .removeValue()
                                firebaseDatabase.getReference("Users").child(user.uid).removeValue()
                                user.delete()
                                finish()
                                exitProcess(0)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@MainActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                            }

                        })
                }
            }
        }
        finish()
        startActivity(intent)
    }

    private fun updateLanguage(context : Context, selectedLanguage: String?){
        val lang : String = if(selectedLanguage.isNullOrEmpty()){
            "en_"
        }else
            selectedLanguage

        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        context.resources.updateConfiguration(config, null)

    }
}