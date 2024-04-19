package com.example.influencer.UI.CheckPoint_Tab

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setChipTextColor
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setRoundedBackgroundColor
import com.example.influencer.Core.Utils.DateTimeUtils
import com.example.influencer.UI.CheckPoint_Tab.Model.CardData
import com.example.influencer.databinding.CardLayoutBinding
import com.like.LikeButton
import com.like.OnLikeListener
import java.util.*
import javax.inject.Inject


class CardStackView_Adapter @Inject constructor(
    private val context: Context
): RecyclerView.Adapter<CardStackView_Adapter.ViewHolder>() {

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
        holder.bind(cardData,context)
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
        ) : RecyclerView.ViewHolder(binding.root){

        var currentLikes : Int = 0

        private val updatesAdapter = Updates_Adapter()

        init {
            binding.UpdatesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = updatesAdapter
            }
        }

        fun bind(cardData: CardData, context: Context){
            with(binding){

                val colorString = if (cardData.post.categoryColor.isNullOrEmpty()) "#FFFFFF" else cardData.post.categoryColor // Default to white if null or empty
                val colorInt = Color.parseColor(colorString)
                MainCategory.text = cardData.post.selectedCategory
                MainCategory.setRoundedBackgroundColor(colorInt,50f)
                userName.text = cardData.user.username
                PostDatePublished.text = DateTimeUtils.getTimeAgo(cardData.post.creationDate)
                PostDatePublished.setRoundedBackgroundColor(colorInt,50f)
                CheckpointCategotyNumber.text = "Checkpoint N°${cardData.post.checkpointCategoryCounter}"
                CheckpointCategotyNumber.setRoundedBackgroundColor(colorInt,50f)
                PostAmountLikes.text = cardData.post.likes.toString()
                SatisfactionLevelBar.progress = cardData.post.satisfaction_level_value.toFloat()
                SatisfactionLevelValue.text = cardData.post.satisfaction_level_value.toString()
                userAge.text = "${cardData.user.years_old} Years/ ${cardData.user.months_old} Months old" //TODO cambiar el Old y ponerlo en string.xml
                Glide.with(context).load(cardData.user.profilePictureUrl).into(profilePicture)
                val CountryCode = cardData.user.countryFlagCode
                val flagUrl = "https://flagsapi.com/$CountryCode/flat/64.png"
                Glide.with(context).load(flagUrl).into(CountryFlagIcon)
                CheckpointText.text = cardData.post.text_post

                currentLikes = currentLikesMap[cardData.post.id] ?: cardData.post.likes
                PostAmountLikes.text = currentLikes.toString()

                listener?.checkPostLiked(cardData.post.id) { isLiked ->
                    LikeButtom.setLiked(isLiked)
                }

                LikeButtom.setOnLikeListener(object : OnLikeListener {
                    override fun liked(likeButton: LikeButton) {
                        listener?.onLikeClicked(cardData.post.id,cardData.user.id, currentLikes)
                    }

                    override fun unLiked(likeButton: LikeButton) {
                        listener?.onUnlikeClicked(cardData.post.id,cardData.user.id, currentLikes)
                    }
                })

                // cargar imagenes
                cardData.post.image_1?.let {
                    PostPhoto1.visibility = View.VISIBLE
                    Glide.with(context).load(it).into(PostPhoto1)

                }?: run { PostPhoto1.visibility = View.GONE }

                cardData.post.image_2?.let {
                    PostPhoto2.visibility = View.VISIBLE
                    Glide.with(context).load(it).into(PostPhoto2)
                }?: run { PostPhoto2.visibility = View.GONE }


                //cargar los Updates de la card que se esta mostrando actualmente
                val colorStateList = ColorStateList.valueOf(colorInt)
                DailyCheckpointUpdatesTitle.chipBackgroundColor = colorStateList
                DailyCheckpointUpdatesTitle.setChipTextColor(colorInt)
                listener?.requestUpdates(cardData.post.id,cardData.user.id){updatesList ->
                    updatesList?.let {
                        updatesAdapter.setUpdates(it,colorInt)
                    }
                }

            }
          }
        }

       interface CardActionsListener {
        fun onLikeClicked(postId: String,postOwnerId: String,currentLikes: Int)
        fun onUnlikeClicked(postId: String,postOwnerId: String,currentLikes: Int)
        fun checkPostLiked(postId: String, callback: (Boolean) -> Unit)
        fun requestUpdates(postId: String,postOwnerId: String, callback: (SortedMap<Int, String>?) -> Unit)
       }

    }



