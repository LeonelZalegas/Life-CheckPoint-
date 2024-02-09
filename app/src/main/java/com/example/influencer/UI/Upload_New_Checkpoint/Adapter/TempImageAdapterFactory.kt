package com.example.influencer.UI.Upload_New_Checkpoint.Adapter

import dagger.assisted.AssistedFactory

@AssistedFactory
interface TempImageAdapterFactory {
    fun create(onDelete: (Int) -> Unit): TempImageAdapter
}
