package com.example.galleon.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import com.example.galleon.R
import com.example.galleon.data.User
import com.example.galleon.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        binding.textSignIn.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener{
            //variables we are sending to the database
            val username = binding.userET.text.toString()
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            //if no fields are empty
            if(username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty())
            {
                //if passwords match and email is valid
                if(pass == confirmPass && email.isEmailValid())
                {
                    firebaseAuth.createUserWithEmailAndPassword(email,pass)
                        .addOnSuccessListener{
                            val uid = firebaseAuth.currentUser!!.uid

                            val user = User(username, "")
                                val databaseReferenceUsers = firebaseDatabase.getReference("Users")

                                databaseReferenceUsers.child(uid).setValue(user)
                                    .addOnSuccessListener {
                                        val databaseReferenceUsernames = firebaseDatabase.getReference("Usernames")
                                        // lowercase so e.g. "Anna" and "anna" will be considered as the same username.
                                        // Lowercase only in map node (Users can have uppercase letters in visible nicks)
                                        databaseReferenceUsernames.child(username.lowercase()).setValue(uid)
                                            .addOnSuccessListener {
                                                val intent = Intent(this, SignInActivity::class.java)
                                                startActivity(intent)
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                                    }
                        }
                        .addOnFailureListener{
                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                        }
                } else{
                    Toast.makeText(this, getString(R.string.error_signup_passwords_dont_match), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.error_signup_empty_field), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

}