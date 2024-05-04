package com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model

import com.google.firebase.firestore.DocumentId

data class Post(
    var text_post: String = "",
    var satisfaction_level_value: Int = 0,
    var image_1: String? = null,
    var image_2: String? = null,
    var selectedCategory: String = "",
    var categoryColor: String = "",
    var creationDate: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(), // Automatically captures the creation timestamp
    var likes:Int = 0,
    var checkpointCategoryCounter:Int = 0,
    @DocumentId
    val id: String = "",  // Automatically populated with Firestore document ID
    var userId: String = "",  // ID of the user who owns the post
)
