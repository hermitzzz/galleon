package com.example.galleon.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateViewModel : ViewModel() {
    val profileUsername: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

}