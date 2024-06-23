package com.example.influencer.Features.Upload_New_Update_Checkpoint.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.influencer.Core.Data.Network.NetworkConnectivity.NetworkActivity
import com.example.influencer.Core.Utils.Serializable.getSerializableExtraCompat
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Domain.Model.CheckpointThemeItem
import com.example.influencer.Features.Upload_New_Update_Checkpoint.UI.Fragments.Update_checkpoint_TextBox
import com.example.influencer.Features.Upload_New_Update_Checkpoint.UI.Fragments.SharedViewmodel
import com.example.influencer.Features.Upload_New_Update_Checkpoint.UI.Fragments.Update_checkpoint_CardInfo
import com.example.influencer.databinding.ActivityUploadNewUpdateCheckpointBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Upload_New_Update_CheckpointActivity : NetworkActivity() {
    private lateinit var binding: ActivityUploadNewUpdateCheckpointBinding
    private val sharedViewmodel: SharedViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadNewUpdateCheckpointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //obtenemos la categoria seleccionada y enviamos para que luego los fragments de esta Activity las puedan leer
        val selectedCategory = intent.getSerializableExtraCompat<CheckpointThemeItem>("SELECTED_CATEGORY")
        sharedViewmodel.setSelectedCategory(selectedCategory!!)

        // Initialize fragments
        val fragment1 = Update_checkpoint_TextBox()
        val fragment2 = Update_checkpoint_CardInfo()

        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentUpdateTextBox.id, fragment1)
            .replace(binding.FragmentUpdateCardInfo.id, fragment2) // Replace with your actual fragment container ID
            .commit()
    }
}