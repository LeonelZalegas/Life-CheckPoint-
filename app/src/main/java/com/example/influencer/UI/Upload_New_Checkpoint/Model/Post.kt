package com.example.influencer.UI.Upload_New_Checkpoint.Model

data class Post(
    var text_post: String = "",
    var satisfaction_level_value: Int = 0,
    var image_1: String? = null,
    var image_2: String? = null,
    var selectedCategory: String = "",
    var categoryColor: Int = 0, //Resource Id
    var creationDate: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(), // Automatically captures the creation timestamp
    var Likes:Int = 0
)
