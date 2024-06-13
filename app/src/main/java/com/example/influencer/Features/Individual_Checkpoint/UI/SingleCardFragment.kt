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

    private fun setupNestedRecyclerView() {
        binding.UpdatesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = updatesAdapter
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CardLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = arguments?.getString("userId") ?: throw IllegalArgumentException("UserId required")
        val postId = arguments?.getString("postId") ?: throw IllegalArgumentException("PostId required")

        // Use the same ViewModel as CheckpointTabFragment
        val viewModel: CheckpointTabViewModel by viewModels()
        viewModel.loadSingleCardData(userId, postId)

        viewModel.checkIfPostIsLiked(postId)

        setupNestedRecyclerView()

        viewModel.singleCardData.observe(viewLifecycleOwner) { cardData ->
            bindCardData(cardData)
        }

        viewModel.likeUpdate.observe(viewLifecycleOwner) { (postId, newLikes) ->
            currentLikes = newLikes
        }

        viewModel.isPostLiked.observe(viewLifecycleOwner) { isLiked ->
            binding.LikeButtom.setLiked(isLiked)
        }

        binding.LikeButtom.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                viewModel.likePost(postId)
            }
            override fun unLiked(likeButton: LikeButton) {
                viewModel.unlikePost(postId)
            }
        })

        binding.apply {
            profilePicture.setOnClickListener {
                val intent = Intent(requireContext(), UserProfileActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }

            userName.setOnClickListener {
                val intent = Intent(requireContext(), UserProfileActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }
        }
    }

    private fun bindCardData(cardData: CardData) {
        val colorString = if (cardData.post.categoryColor.isNullOrEmpty()) "#FFFFFF" else cardData.post.categoryColor // Default to white if null or empty
        val colorInt = Color.parseColor(colorString)

        setupCardData(cardData,colorInt)
        setupLikes(cardData)
        setupImages(cardData)
        loadUpdates(cardData,colorInt)
    }

    private fun setupCardData(cardData: CardData, colorInt:Int) {
        binding.apply {
            MainCategory.text = cardData.post.selectedCategory
            MainCategory.setRoundedBackgroundColor(colorInt, 50f)
            userName.text = cardData.user.username
            PostDatePublished.text = DateTimeUtils.getTimeAgo(cardData.post.creationDate)
            PostDatePublished.setRoundedBackgroundColor(colorInt, 50f)
            CheckpointCategoryNumber.text = "Checkpoint N°${cardData.post.checkpointCategoryCounter}"
            CheckpointCategoryNumber.setRoundedBackgroundColor(colorInt, 50f)
            PostAmountLikes.text = cardData.post.likes.toString()
            SatisfactionLevelBar.progress = cardData.post.satisfaction_level_value.toFloat()
            SatisfactionLevelValue.text = cardData.post.satisfaction_level_value.toString()
            userAge.text = "${cardData.user.years_old} Years/ ${cardData.user.months_old} Months old" //TODO cambiar el Old y ponerlo en string.xml
            Glide.with(profilePicture.context).load(cardData.user.profilePictureUrl).into(profilePicture)
            CountryFlagIcon.loadFlag(cardData.user.countryFlagCode)
            CheckpointText.text = cardData.post.text_post
        }
    }

    private fun setupLikes(cardData: CardData) {
        binding.apply {
            currentLikes = currentLikes ?: cardData.post.likes
            PostAmountLikes.text = currentLikes.toString()
        }
    }

    private fun setupImages(cardData: CardData) {
        with(cardData.post) {
            setImageVisibility(image_1, binding.PostPhoto1)
            setImageVisibility(image_2, binding.PostPhoto2) // Assuming you meant PostPhoto2 for image_2
        }
    }
    private fun setImageVisibility(imageUrl: String?, imageView: ImageView) {
        imageUrl?.let { imageUrl->
            imageView.apply {
                visibility = View.VISIBLE
                Glide.with(context).load(imageUrl).into(this)
            }
        } ?: run {
            imageView.visibility = View.GONE
        }
    }

    private fun loadUpdates(cardData: CardData, colorInt: Int) {
        with(binding) {
            val colorStateList = ColorStateList.valueOf(colorInt)
            DailyCheckpointUpdatesTitle.chipBackgroundColor = colorStateList
            DailyCheckpointUpdatesTitle.setChipTextColor(colorInt)

            var updatesList = cardData.updates

            UpdatesRecyclerView.visibility = View.GONE
            textViewNoUpdates.visibility = View.GONE
            if (updatesList.isNullOrEmpty()) {
                textViewNoUpdates.visibility = View.VISIBLE
            } else {
                UpdatesRecyclerView.visibility = View.VISIBLE
                updatesAdapter.setUpdates(updatesList, colorInt)
            }
        }
    }

    private fun ImageView.loadFlag(countryCode: String?) {
        val flagUrl = "https://flagsapi.com/$countryCode/flat/64.png"
        Glide.with(context).load(flagUrl).into(this)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}