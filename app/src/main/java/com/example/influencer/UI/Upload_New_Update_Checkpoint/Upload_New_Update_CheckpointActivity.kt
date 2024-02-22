package com.example.influencer.UI.Upload_New_Update_Checkpoint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.influencer.Core.Serializable.getSerializableExtraCompat
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model.CheckpointThemeItem
import com.example.influencer.UI.Upload_New_Update_Checkpoint.Fragments.Update_checkpoint_TextBox
import com.example.influencer.databinding.ActivityUploadNewCheckpointBinding
import com.example.influencer.databinding.ActivityUploadNewUpdateCheckpointBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Upload_New_Update_CheckpointActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadNewUpdateCheckpointBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadNewUpdateCheckpointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //obtenemos la categoria seleccionada
        val selectedCategory = intent.getSerializableExtraCompat<CheckpointThemeItem>("SELECTED_CATEGORY")

        val fragment = Update_checkpoint_TextBox().apply {
            arguments = Bundle().apply {
                putSerializable("SELECTED_CATEGORY", selectedCategory) //enviamos al fragment de Update_checkpoint_TextBox la categoria seleccionada
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentUpdateTextBox.id, fragment)
            .commit()
    }
}