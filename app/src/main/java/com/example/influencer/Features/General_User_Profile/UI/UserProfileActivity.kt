package com.example.influencer.Features.General_User_Profile.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.example.influencer.Features.ProfileTab.UI.ProfileTabFragment
import com.example.influencer.R
import com.example.influencer.databinding.ActivityUserProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("userId")
        val fragment = ProfileTabFragment.newInstance(userId)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container_view, fragment)
        }

        setupGoingBack()
    }
    private fun setupGoingBack() {
        binding.GoingBack.setOnClickListener {
            finish()
        }
    }
}
