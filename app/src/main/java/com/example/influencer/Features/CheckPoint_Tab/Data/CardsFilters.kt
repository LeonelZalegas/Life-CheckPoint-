package com.example.influencer.Features.CheckPoint_Tab.Data

import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post

interface CardsFilters {
   suspend fun getRandomPost():Result<Post>
}