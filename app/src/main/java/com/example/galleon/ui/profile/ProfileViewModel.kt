package com.example.galleon.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    //username
    val profileUsername: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    //bio
    val profileBio: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    //user image URL
    val profilePicture: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

    //user lists list
    val profileUserLists: MutableLiveData<ArrayList<String>> = MutableLiveData()
    init {
        profileUserLists.value = arrayListOf()
    }

    //user saves list
    val profileUserSaves: MutableLiveData<ArrayList<String>> = MutableLiveData()
    init {
        profileUserSaves.value = arrayListOf()
    }

    //user comments lists
    val profileUserComments: MutableLiveData<ArrayList<String>> = MutableLiveData()
    init {
        profileUserComments.value = arrayListOf()
    }
}