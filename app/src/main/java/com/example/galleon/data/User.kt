package com.example.galleon.data

//User
// UID:
//  - username
//  - bio
//  - saves (List of list IDs)
//  - lists (List of list IDs)
//  - follows (List of UIDs)
// val lists : List<String>? = null, val saves : List<String>? = null

//Username
// -username (lowercase)


data class User(val username : String? = null, val bio : String? = null)
