package com.example.influencer.Features.General_FollowersFollowing_showing.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.influencer.databinding.ActivityFollowersFollowingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowersFollowing_Activity : AppCompatActivity(),FollowersFollowing_Adapter.OnItemInteractionListener {

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
            followersfollowingAdapter = FollowersFollowing_Adapter(FollowingOptionSelected, ownerUserId, this)
            setupRecyclerView()
        }

        loadAndObserveUsers()
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
            else
                Log.w("ListaVaciaFollows", "por alguna la lista es vacia" )
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun followUser(UserId: String?) {
        viewModel.followUser(UserId)
    }

    override fun unfollowUser(UserId: String?) {
        viewModel.unfollowUser(UserId)
    }

    override fun checkIfFollowing(targetUserId:String, callback: (Boolean) -> Unit) {
        viewModel.checkIfFollowing(currentUserId,targetUserId,callback)
    }


}