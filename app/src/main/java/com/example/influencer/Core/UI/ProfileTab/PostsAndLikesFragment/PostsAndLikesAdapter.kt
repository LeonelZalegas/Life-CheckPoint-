package com.example.influencer.Core.UI.ProfileTab.PostsAndLikesFragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setChipTextColor
import com.example.influencer.Core.Utils.DateTimeUtils
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.example.influencer.databinding.ItemProfileCheckpointsBinding

class PostsAndLikesAdapter(
    private val isCheckpoints: Boolean
) : RecyclerView.Adapter<PostsAndLikesAdapter.ViewHolder>() {

    private var items: List<Post> = emptyList()

    inner class ViewHolder(private val binding: ItemProfileCheckpointsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Post) {
            val colorString = if (item.categoryColor.isNullOrEmpty()) "#FFFFFF" else item.categoryColor // Default to white if null or empty
            val colorInt = Color.parseColor(colorString)

            binding.Date.text = DateTimeUtils.getTimeAgo(item.creationDate)
            binding.CheckpointText.text = item.text_post
            binding.Date.chipBackgroundColor = ColorStateList.valueOf(colorInt)
            binding.Date.setChipTextColor(colorInt)
            binding.UpdateChipText.chipBackgroundColor = ColorStateList.valueOf(colorInt)
            binding.UpdateChipText.setChipTextColor(colorInt)


            if (isCheckpoints) {
                binding.LikeButton.visibility = View.GONE
                binding.deleteCheckppint.visibility = View.VISIBLE
            } else {
                binding.LikeButton.visibility = View.VISIBLE
                binding.deleteCheckppint.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProfileCheckpointsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<Post>) {
        items = newItems
        notifyDataSetChanged()
    }
}
