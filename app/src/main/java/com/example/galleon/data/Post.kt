package com.example.galleon.data

//Post node structure:
// PID:
// - title
// - UID (User that posted this)
// - pictures
//   - PicID
// - description
// - comments
//   - UID (Users that commented)

data class Post(val uid: String? = null, val uri: String? = null, val title: String?)
