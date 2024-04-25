package com.example.influencer.Features.CheckPoint_Tab.Domain.Model

import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Update_Checkpoint.Domain.Model.CheckPoint_Update_Item

data class CardData(
    val user: _root_ide_package_.com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin,
    val post: Post,
    val updates: List<CheckPoint_Update_Item>
)
