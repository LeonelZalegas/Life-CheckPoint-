package com.example.influencer.Core

import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.io.Serializable

//debido a que el safe Arguments no se lo puede utilizar en el navigation component (porque tenemos Activities/fragments en java, en especifico CheckpointThemeChoose_Fragment)
//tenemos que usar estos Serializables para pasar valores entre Activities/fragments
object Serializable {
    // Extension function to handle getSerializableExtra deprecation
//https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#405018c8e80444e091a91d15c9bd434d
    inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

    fun <T : Serializable?> Bundle.getSerializableCompat(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, clazz)
        } else {
            @Suppress("DEPRECATION") getSerializable(key) as? T
        }
    }
}