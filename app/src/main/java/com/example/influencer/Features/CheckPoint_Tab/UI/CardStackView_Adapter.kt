package com.example.influencer.Features.CheckPoint_Tab.UI


import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.influencer.Core.UI.Updates_Adapter
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setChipTextColor
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setRoundedBackgroundColor
import com.example.influencer.Core.Utils.DateTimeUtils
import com.example.influencer.Features.CheckPoint_Tab.Domain.Model.CardData
import com.example.influencer.databinding.CardLayoutBinding
import com.like.LikeButton
import com.like.OnLikeListener
import javax.inject.Inject

class CardStackView_Adapter @Inject constructor(
) : RecyclerView.Adapter<CardStackView_Adapter.ViewHolder>() {

    private var cardDataList: List<CardData> = emptyList()
    var listener: CardActionsListener? = null

    private var currentLikesMap: MutableMap<String, Int> = mutableMapOf()

    fun setCards(cards: List<CardData>) {
        cardDataList = cards
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding,listener,currentLikesMap)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardData = cardDataList[position]
        holder.bind(cardData)
    }

    override fun getItemCount(): Int = cardDataList.size

    fun updateLikes(postId: String, newLikes: Int) {
        val position = cardDataList.indexOfFirst { it.post.id == postId }
        if (position != -1) {
            currentLikesMap[postId] = newLikes
            notifyItemChanged(position, newLikes)
        }
    }

    class ViewHolder(
        private val binding: CardLayoutBinding,
        private val listener: CardActionsListener?,
        private val currentLikesMap: MutableMap<String, Int>
    ) : RecyclerView.ViewHolder(binding.root) {

        var currentLikes : Int = 0
        private val updatesAdapter = Updates_Adapter()

        init {
            setupNestedRecyclerView()
        }

        private fun setupNestedRecyclerView() {
            binding.UpdatesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = updatesAdapter
            }
        }

        fun bind(cardData: CardData) {
            val colorString = if (cardData.post.categoryColor.isNullOrEmpty()) "#FFFFFF" else cardData.post.categoryColor // Default to white if null or empty
            val colorInt = Color.parseColor(colorString)
            setupCardData(cardData,colorInt)
            setupLikes(cardData)
            setupProfileClick(cardData)
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
                currentLikes = currentLikesMap[cardData.post.id] ?: cardData.post.likes
                PostAmountLikes.text = currentLikes.toString()

                listener?.checkPostLiked(cardData.post.id) { isLiked ->
                    LikeButtom.setLiked(isLiked)
                }

                LikeButtom.setOnLikeListener(object : OnLikeListener {
                    override fun liked(likeButton: LikeButton) {
                        listener?.onLikeClicked(cardData.post.id)
                    }

                    override fun unLiked(likeButton: LikeButton) {
                        listener?.onUnlikeClicked(cardData.post.id)
                    }
                })
            }
        }

        private fun setupProfileClick(cardData: CardData) {
            binding.apply {
                profilePicture.setOnClickListener {
                    listener?.onProfilePictureClicked(cardData.user.id)
                }

                userName.setOnClickListener {
                    listener?.onProfilePictureClicked(cardData.user.id)
                }
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
    }

    interface CardActionsListener {
        fun onLikeClicked(postId: String)
        fun onUnlikeClicked(postId: String)
        fun checkPostLiked(postId: String, callback: (Boolean) -> Unit)
        fun onProfilePictureClicked(userId: String)
    }
}




