package com.example.galleon.ui.create

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.galleon.R
import com.example.galleon.data.Post
import com.example.galleon.databinding.FragmentCreateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.DateFormat
import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var binding:FragmentCreateBinding
    var imageURL: String? = null
    var uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            if(result.resultCode == RESULT_OK){
                val data = result.data
                uri = data!!.data
                binding.imageView.setImageURI(uri)
            } else {
                Toast.makeText(this@UploadActivity, "No image", Toast.LENGTH_SHORT).show()
            }
        }
        binding.chooseImg.setOnClickListener{
            val photoPicker = Intent(Intent.ACTION_PICK)
            val filename = UUID.randomUUID().toString()
            photoPicker.type= "image/$filename"
            activityResultLauncher.launch((photoPicker))
        }
        binding.buttonCreate.setOnClickListener{
                saveData()
        }

    }
    private fun saveData(){
        val storageReference = FirebaseStorage.getInstance().reference.child("Posts")
            .child(uri!!.lastPathSegment!!)
        val builder = AlertDialog.Builder(this@UploadActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.fragment_progress)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result
            imageURL = urlImage.toString()
            uploadData()
            dialog.dismiss()
        }.addOnFailureListener{
            dialog.dismiss()
        }
    }

    // TODO Jak to teraz testowałam, zauważyłam, że nie da się wziąć zdjęcia spoza innego folderu niż galeria.
    //      Byłoby miło jaky Ci się udało to zrobić by otwierał się ten sam panel co przy
    //      uploadowaniu zdjęcia profilowego (tj. odpala się widok na pliki, nie na gallery)

    private fun uploadData() {
        val title = binding.editTextTextMultiLine2.text.toString()
        val firebaseAuth  = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.uid
        val dataClass = Post(currentUser, imageURL, title)
        val currentData=DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        if (currentUser != null){
            FirebaseDatabase.getInstance().getReference("Users").child(currentUser).child("posts").child(currentData)
                .setValue("").addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this@UploadActivity, "Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }.addOnFailureListener{ e ->
                    Toast.makeText(this@UploadActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            FirebaseDatabase.getInstance().getReference("Posts").child(currentData)
                .setValue(dataClass).addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this@UploadActivity, "Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }.addOnFailureListener{ e ->
                    Toast.makeText(this@UploadActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }


    }

}