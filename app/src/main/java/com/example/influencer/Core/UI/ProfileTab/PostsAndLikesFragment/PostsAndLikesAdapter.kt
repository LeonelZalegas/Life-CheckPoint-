package com.example.influencer.Core.UI.ProfileTab.PostsAndLikesFragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setChipTextColor
import com.example.influencer.Core.Utils.DateTimeUtils
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.example.influencer.databinding.ItemProfileCheckpointsBinding
import com.like.LikeButton
import com.like.OnLikeListener

class PostsAndLikesAdapter(
    private val isOwnProfile: Boolean,
    private val isCheckpoints: Boolean,
    private var listener: OnPostInteractionListener
) : RecyclerView.Adapter<PostsAndLikesAdapter.ViewHolder>() {

    private var posts: List<Post> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProfileCheckpointsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class ViewHolder(private val binding: ItemProfileCheckpointsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            val colorString =
                if (post.categoryColor.isNullOrEmpty()) "#FFFFFF" else post.categoryColor // Default to white if null or empty
            val colorInt = Color.parseColor(colorString)

            binding.apply {
                Date.text = DateTimeUtils.getTimeAgo(post.creationDate)
                CheckpointText.text = post.text_post
                Date.chipBackgroundColor = ColorStateList.valueOf(colorInt)
                Date.setChipTextColor(colorInt)
                UpdateChipText.chipBackgroundColor = ColorStateList.valueOf(colorInt)
                UpdateChipText.setChipTextColor(colorInt)
                UpdateChipNumber.chipBackgroundColor = ColorStateList.valueOf(colorInt)
                UpdateChipNumber.setChipTextColor(colorInt)
                UpdateChipNumber.text = post.UpdatesAmount.toString()
            }
            if (isOwnProfile) {
                if (isCheckpoints) {
                    binding.LikeButton.visibility = View.GONE
                    binding.deleteCheckpoint.visibility = View.VISIBLE

                } else {
                    binding.LikeButton.visibility = View.VISIBLE
                    binding.deleteCheckpoint.visibility = View.GONE
                }
            }else{
                //hiding trash and like icons because we are in someone else profile
                binding.LikeButton.visibility = View.GONE
                binding.deleteCheckpoint.visibility = View.GONE
            }

            binding.LikeButton.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {
                    Log.w("dadoLike", "se detecto que se dio like ")
                    listener.onLikeClicked(post.id)
                }

                override fun unLiked(likeButton: LikeButton) {
                    Log.w("dadoLike", "se detecto que se dio unlike ")
                    listener.onUnlikeClicked(post.id)
                }
            })

            binding.deleteCheckpoint.setOnClickListener {
                listener.onDeleteClicked(post.id)
            }

            // Determine visibility of the line and point views
            binding.TopLine.visibility =
                if (bindingAdapterPosition == 0) View.GONE else View.VISIBLE
            binding.TopPoint.visibility =
                if (bindingAdapterPosition == 0) View.GONE else View.VISIBLE
            binding.BottomLine.visibility =
                if (bindingAdapterPosition == posts.size - 1) View.GONE else View.VISIBLE
            binding.BottomPoint.visibility =
                if (bindingAdapterPosition == posts.size - 1) View.GONE else View.VISIBLE

            // Special case for a single item
            if (posts.size == 1) {
                binding.TopLine.visibility = View.GONE
                binding.TopPoint.visibility = View.GONE
                binding.BottomLine.visibility = View.GONE
                binding.BottomPoint.visibility = View.GONE
            }

        }

    }

    fun submitList(newItems: List<Post>) {
        posts = newItems
        notifyDataSetChanged()
    }


    interface OnPostInteractionListener {
        fun onLikeClicked(postId: String)
        fun onUnlikeClicked(postId: String)
        fun onDeleteClicked(postId: String)
    }
}
