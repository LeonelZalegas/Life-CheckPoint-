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
            val colorString =
                if (item.categoryColor.isNullOrEmpty()) "#FFFFFF" else item.categoryColor // Default to white if null or empty
            val colorInt = Color.parseColor(colorString)

            binding.apply {
                Date.text = DateTimeUtils.getTimeAgo(item.creationDate)
                CheckpointText.text = item.text_post
                Date.chipBackgroundColor = ColorStateList.valueOf(colorInt)
                Date.setChipTextColor(colorInt)
                UpdateChipText.chipBackgroundColor = ColorStateList.valueOf(colorInt)
                UpdateChipText.setChipTextColor(colorInt)
                UpdateChipNumber.chipBackgroundColor = ColorStateList.valueOf(colorInt)
                UpdateChipNumber.setChipTextColor(colorInt)
                UpdateChipNumber.text = item.UpdatesAmount.toString()
            }


            if (isCheckpoints) {
                binding.LikeButton.visibility = View.GONE
                binding.deleteCheckppint.visibility = View.VISIBLE
            } else {
                binding.LikeButton.visibility = View.VISIBLE
                binding.deleteCheckppint.visibility = View.GONE
            }

            // Determine visibility of the line and point views
            binding.TopLine.visibility =
                if (bindingAdapterPosition == 0) View.GONE else View.VISIBLE
            binding.TopPoint.visibility =
                if (bindingAdapterPosition == 0) View.GONE else View.VISIBLE
            binding.BottomLine.visibility =
                if (bindingAdapterPosition == items.size - 1) View.GONE else View.VISIBLE
            binding.BottomPoint.visibility =
                if (bindingAdapterPosition == items.size - 1) View.GONE else View.VISIBLE

            // Special case for a single item
            if (items.size == 1) {
                binding.TopLine.visibility = View.GONE
                binding.TopPoint.visibility = View.GONE
                binding.BottomLine.visibility = View.GONE
                binding.BottomPoint.visibility = View.GONE
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
