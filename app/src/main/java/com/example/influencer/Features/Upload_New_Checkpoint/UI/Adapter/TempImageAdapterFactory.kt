package com.example.influencer.Features.Upload_New_Checkpoint.UI.Adapter

import dagger.assisted.AssistedFactory

@AssistedFactory
interface TempImageAdapterFactory {
    fun create(onDelete: (Int) -> Unit): TempImageAdapter
}
