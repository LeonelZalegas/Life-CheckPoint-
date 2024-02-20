package com.example.influencer.UI.Upload_New_Update_Checkpoint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.influencer.databinding.ActivityUploadNewCheckpointBinding
import com.example.influencer.databinding.ActivityUploadNewUpdateCheckpointBinding

class Upload_New_Update_CheckpointActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadNewUpdateCheckpointBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadNewUpdateCheckpointBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}