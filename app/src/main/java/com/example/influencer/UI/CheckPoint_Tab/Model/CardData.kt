package com.example.influencer.UI.CheckPoint_Tab.Model

import com.example.influencer.UI.SignIn.Model.UsuarioSignin
import com.example.influencer.UI.Upload_New_Checkpoint.Model.Post
import com.example.influencer.UI.Upload_New_Update_Checkpoint.Model.CheckPoint_Update_Item

data class CardData(
    val user: UsuarioSignin,
    val post: Post,
    val updates: List<CheckPoint_Update_Item>
)
