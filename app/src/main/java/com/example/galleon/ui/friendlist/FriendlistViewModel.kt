package com.example.galleon.ui.friendlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendlistViewModel : ViewModel() {

    val profileUserLists: MutableLiveData<ArrayList<String>> = MutableLiveData()
    init {
        profileUserLists.value = arrayListOf()
    }
}