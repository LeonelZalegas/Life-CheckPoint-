package com.example.influencer.Features.Individual_Checkpoint.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.example.influencer.R
import com.example.influencer.databinding.ActivityIndividualCheckpointBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IndividualCheckpointActivity : AppCompatActivity() {
    private lateinit var binding:ActivityIndividualCheckpointBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndividualCheckpointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("userId")
        val postId = intent.getStringExtra("postId")
        if (userId != null && postId != null ){
            val fragment = SingleCardFragment.newInstance(userId, postId)

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.SingleCardFragment, fragment)
            }
        }
    }
}