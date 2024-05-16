package com.example.influencer.Features.Upload_New_Update_Checkpoint.UI.Fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setChipTextColor
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setRoundedBackgroundColor
import com.example.influencer.Core.Utils.DateTimeUtils
import com.example.influencer.Features.CheckPoint_Tab.UI.Updates_Adapter
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.Model.CheckPoint_Update_Item
import com.example.influencer.databinding.FragmentUpdateCheckpointCardInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Update_checkpoint_CardInfo : Fragment() {

    private var _binding: FragmentUpdateCheckpointCardInfoBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewmodel by activityViewModels()
    private val updatesAdapter = Updates_Adapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentUpdateCheckpointCardInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        sharedViewModel.loadPostAndUpdates()

        sharedViewModel.postAndUpdates.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                val (post, updates) = result.getOrNull()!!
                updateUI(post, updates)
            } else {
                // Handle error TODO
                Toast.makeText(context, "Failed to load post and updates", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.UpdatesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = updatesAdapter
        }
    }

    private fun updateUI(post: Post, updates: List<CheckPoint_Update_Item>) {
        binding.apply {
            val colorString = if (post.categoryColor.isNullOrEmpty()) "#FFFFFF" else post.categoryColor // Default to white if null or empty
            val colorInt = Color.parseColor(colorString)

            PostDatePublished.text = DateTimeUtils.getTimeAgo(post.creationDate)
            PostDatePublished.setRoundedBackgroundColor(colorInt, 50f)
            CheckpointCategoryNumber.text = "Checkpoint N°${post.checkpointCategoryCounter}" //TODO
            CheckpointCategoryNumber.setRoundedBackgroundColor(colorInt, 50f)
            SatisfactionLevelBar.progress = post.satisfaction_level_value.toFloat()
            SatisfactionLevelValue.text = post.satisfaction_level_value.toString()

            CheckpointText.text = post.text_post
            with(post) {
                setImageVisibility(image_1, binding.PostPhoto1)
                setImageVisibility(image_2, binding.PostPhoto2) // Assuming you meant PostPhoto2 for image_2
            }
            with(binding) {
                val colorStateList = ColorStateList.valueOf(colorInt)
                DailyCheckpointUpdatesTitle.chipBackgroundColor = colorStateList
                DailyCheckpointUpdatesTitle.setChipTextColor(colorInt)

                var updatesList = updates

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}