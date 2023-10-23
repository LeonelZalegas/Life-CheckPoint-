package com.example.influencer.UI.Upload_New_Checkpoint

import android.animation.ArgbEvaluator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.example.influencer.R
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model.CheckpointThemeItem
import com.example.influencer.databinding.ActivityUploadNewCheckpointBinding
import java.io.Serializable

// Extension function to handle getSerializableExtra deprecation
//https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#405018c8e80444e091a91d15c9bd434d
inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

class UploadNewCheckpoint : AppCompatActivity() {

    private lateinit var binding: ActivityUploadNewCheckpointBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadNewCheckpointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedCategory = intent.getSerializableExtraCompat<CheckpointThemeItem>("SELECTED_CATEGORY")
        val startColor = Color.RED
        val endColor = ContextCompat.getColor(this, R.color.verde_seekBar)
        val colorEvaluator = ArgbEvaluator()

        //https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#4636dc4be413463f92462daa45fa3048
        selectedCategory?.let {
            with(binding.chip) {
                text = it.text
                setChipBackgroundColorResource(it.color)
            }
        }

        // Initialize the SeekBar and set the listener
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Calculate the color
                val color = colorEvaluator.evaluate(progress / 100f, startColor, endColor) as Int

                // Update the TextView with the current progress and color
                binding.percentageBar.apply {
                    text = progress.toString()
                    setTextColor(color)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Optional: Respond to the user starting to drag the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Optional: Respond to the user finishing dragging the SeekBar
            }
        })

    }
}