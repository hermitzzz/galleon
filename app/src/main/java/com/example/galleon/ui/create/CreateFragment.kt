package com.example.galleon.ui.create

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.galleon.databinding.FragmentCreateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.*

class CreateFragment : Fragment() {
    private lateinit var createViewModel: CreateViewModel // <-ViewModel profilu
    private lateinit var firebaseAuth: FirebaseAuth // <- zmienna przechowująca instancę bazy danych
    private lateinit var database: DatabaseReference // <- zmienna przechowująca instancę auth managera
    private var _binding: FragmentCreateBinding? = null

    //private lateinit var imageView: ImageView
    //private lateinit var btnGallery:Button
    //private lateinit var btnUpload : Button

    private lateinit var binding: FragmentCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requireActivity().setContentView(R.layout.fragment_create)
        binding=FragmentCreateBinding.inflate(layoutInflater,container, false)
        requireActivity().setContentView(binding.root)

        binding.chooseImg.setOnClickListener{
            val intent = Intent(this@CreateFragment.context, UploadActivity::class.java)
            startActivity((intent))
        }


    }

}


