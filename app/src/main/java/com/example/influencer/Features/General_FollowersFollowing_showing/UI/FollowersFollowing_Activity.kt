package com.example.influencer.Features.General_FollowersFollowing_showing.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.influencer.Core.Data.Network.NetworkConnectivity.NetworkActivity
import com.example.influencer.Features.General_User_Profile.UI.UserProfileActivity
import com.example.influencer.R
import com.example.influencer.databinding.ActivityFollowersFollowingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowersFollowing_Activity : NetworkActivity(),FollowersFollowing_Adapter.OnItemInteractionListener {

    private lateinit var binding: ActivityFollowersFollowingBinding
    private var FollowingOptionSelected : Boolean = true
    private lateinit var currentUserId : String
    private val viewModel: FollowersFollowing_Viewmodel by viewModels()
    lateinit var followersfollowingAdapter: FollowersFollowing_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowersFollowingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getOwnerUserId()

        currentUserId = intent.getStringExtra("userId").toString() //userId del perfil del cual estamos haciendo click el textoiew de follower o following
        FollowingOptionSelected = intent.getBooleanExtra("FollowingOptionSelected",true)

        viewModel.ownerUserId.observe(this) { ownerUserId ->
            followersfollowingAdapter = FollowersFollowing_Adapter(ownerUserId, this)
            setupRecyclerView()
        }

        loadAndObserveUsers()
        setupTitle()
        setupGoingBack()
    }

    private fun setupRecyclerView() {
        binding.UsersRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.UsersRecyclerView.adapter = followersfollowingAdapter
    }

    private fun loadAndObserveUsers() {
       if(FollowingOptionSelected) viewModel.getFollowingUsers(currentUserId) else viewModel.getFollowersUsers(currentUserId)

        viewModel.usersList.observe(this){ usersList->
            if (!usersList.isNullOrEmpty())
            followersfollowingAdapter.uploadUsersList(usersList)
            else{
                if(FollowingOptionSelected)
                    binding.noFollowingUsers.visibility = View.VISIBLE
                else
                    binding.noFollowersUsers.visibility = View.VISIBLE
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.progressLoading.observe(this){ loadingInfo ->
            followersfollowingAdapter.followLoading(loadingInfo.first,  loadingInfo.second)
        }
    }

    private fun setupTitle() {
        if (FollowingOptionSelected)
            binding.FollowersFollowingTitle.text = getString(R.string.Following)
        else
            binding.FollowersFollowingTitle.text = getString(R.string.Followers)

    }

    private fun setupGoingBack() {
        binding.GoingBack.setOnClickListener {
            finish()
        }
    }

    override fun followUser(UserId: String?,position: Int) {
        viewModel.followUser(UserId,position)
    }

    override fun unfollowUser(UserId: String?,position: Int) {
        viewModel.unfollowUser(UserId,position)
    }

    override fun checkIfFollowing(targetUserId:String, callback: (Boolean) -> Unit) {
        viewModel.checkIfFollowing(currentUserId,targetUserId,callback)
    }

    override fun goToSelectedProfile(userId: String) {
        val intent = Intent(this, UserProfileActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }


}