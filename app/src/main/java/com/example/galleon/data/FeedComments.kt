package com.example.galleon.data

//Comment node structure:
// CID:
// - UID (User that commented)
// - comment

data class FeedComments(var title : String, var username : String, var comment : String)