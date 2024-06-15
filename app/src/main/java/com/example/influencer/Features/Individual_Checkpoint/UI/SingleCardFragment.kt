package com.example.influencer.Features.Individual_Checkpoint.UI

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.influencer.Core.UI.Updates_Adapter
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setChipTextColor
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setRoundedBackgroundColor
import com.example.influencer.Core.Utils.DateTimeUtils
import com.example.influencer.Features.CheckPoint_Tab.Domain.Model.CardData
import com.example.influencer.Features.CheckPoint_Tab.UI.CheckpointTabViewModel
import com.example.influencer.Features.User_Profile.UI.UserProfileActivity
import com.example.influencer.R
import com.example.influencer.databinding.CardLayoutBinding
import com.like.LikeButton
import com.like.OnLikeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleCardFragment : Fragment() {
    private var _binding: CardLayoutBinding? = null
    private val binding get() = _binding!!

    private var currentLikes : Int? = null
    private val updatesAdapter = Updates_Adapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CardLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = arguments?.getString("userId") ?: throw IllegalArgumentException("UserId required")
        val postId = arguments?.getString("postId") ?: throw IllegalArgumentException("PostId required")

        initializeViewModel(userId, postId)
        setupNestedRecyclerView()
        setupProfileClickListeners(userId)
    }

    private fun initializeViewModel(userId: String, postId: String) {
        val viewModel: CheckpointTabViewModel by viewModels()
        viewModel.loadSingleCardData(userId, postId)
        viewModel.checkIfPostIsLiked(postId)
        observeViewModel(viewModel)
        setupLikeButtonListener(viewModel, postId)
    }

    private fun observeViewModel(viewModel: CheckpointTabViewModel) {
        viewModel.singleCardData.observe(viewLifecycleOwner) { cardData ->
            bindCardData(cardData)
        }

        viewModel.likeUpdate.observe(viewLifecycleOwner) { (_, newLikes) ->
            currentLikes = newLikes
        }

        viewModel.isPostLiked.observe(viewLifecycleOwner) { isLiked ->
            binding.LikeButtom.setLiked(isLiked)
        }
    }

    private fun setupLikeButtonListener(viewModel: CheckpointTabViewModel, postId: String) {
        binding.LikeButtom.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                viewModel.likePost(postId)
            }
            override fun unLiked(likeButton: LikeButton) {
                viewModel.unlikePost(postId)
            }
        })
    }

    private fun setupProfileClickListeners(userId: String) {
        binding.profilePicture.setOnClickListener {
            navigateToUserProfile(userId)
        }

        binding.userName.setOnClickListener {
            navigateToUserProfile(userId)
        }
    }

    private fun navigateToUserProfile(userId: String) {
        val intent = Intent(requireContext(), UserProfileActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun setupNestedRecyclerView() {
        binding.UpdatesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = updatesAdapter
        }
    }

    private fun bindCardData(cardData: CardData) {
        val colorInt = Color.parseColor(cardData.post.categoryColor ?: "#FFFFFF")
        setupCardData(cardData, colorInt)
        setupLikes(cardData)
        setupImages(cardData)
        loadUpdates(cardData, colorInt)
    }

    private fun setupCardData(cardData: CardData, colorInt: Int) {
        val colorStateList = ColorStateList.valueOf(colorInt)
        binding.apply {
            MainCategory.text = cardData.post.selectedCategory
            MainCategory.setRoundedBackgroundColor(colorInt, 50f)
            userName.text = cardData.user.username
            PostDatePublished.text = DateTimeUtils.getTimeAgo(cardData.post.creationDate)
            PostDatePublished.setRoundedBackgroundColor(colorInt, 50f)
            CheckpointCategoryNumber.text = "Checkpoint N°${cardData.post.checkpointCategoryCounter}"
            CheckpointCategoryNumber.setRoundedBackgroundColor(colorInt, 50f)
            DailyCheckpointUpdatesTitle.chipBackgroundColor = colorStateList
            DailyCheckpointUpdatesTitle.setChipTextColor(colorInt)
            PostAmountLikes.text = cardData.post.likes.toString()
            SatisfactionLevelBar.progress = cardData.post.satisfaction_level_value.toFloat()
            SatisfactionLevelValue.text = cardData.post.satisfaction_level_value.toString()
            userAge.text = "${cardData.user.years_old} Years/ ${cardData.user.months_old} Months old"
            Glide.with(profilePicture.context).load(cardData.user.profilePictureUrl).into(profilePicture)
            CountryFlagIcon.loadFlag(cardData.user.countryFlagCode)
            CheckpointText.text = cardData.post.text_post
        }
    }

    private fun setupLikes(cardData: CardData) {
        binding.PostAmountLikes.text = (currentLikes ?: cardData.post.likes).toString()
    }

    private fun setupImages(cardData: CardData) {
        setImageVisibility(cardData.post.image_1, binding.PostPhoto1)
        setImageVisibility(cardData.post.image_2, binding.PostPhoto2)
    }

    private fun setImageVisibility(imageUrl: String?, imageView: ImageView) {
        if (imageUrl != null) {
            imageView.visibility = View.VISIBLE
            Glide.with(imageView.context).load(imageUrl).into(imageView)
        } else {
            imageView.visibility = View.GONE
        }
    }

    private fun loadUpdates(cardData: CardData, colorInt: Int) {
        val updatesList = cardData.updates
        if (updatesList.isNullOrEmpty()) {
            binding.textViewNoUpdates.visibility = View.VISIBLE
            binding.UpdatesRecyclerView.visibility = View.GONE
        } else {
            binding.UpdatesRecyclerView.visibility = View.VISIBLE
            binding.textViewNoUpdates.visibility = View.GONE
            updatesAdapter.setUpdates(updatesList, colorInt)
        }
    }

    private fun ImageView.loadFlag(countryCode: String?) {
        countryCode?.let {
            val flagUrl = "https://flagsapi.com/$countryCode/flat/64.png"
            Glide.with(this).load(flagUrl).into(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(userId: String, postId: String): SingleCardFragment {
            val fragment = SingleCardFragment()
            val args = Bundle()
            args.putString("userId", userId)
            args.putString("postId", postId)
            fragment.arguments = args
            return fragment
        }
    }
}
