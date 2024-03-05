package com.example.influencer.UI.CheckPoint_Tab

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.influencer.Core.Utils.DateTimeUtils
import com.example.influencer.UI.CheckPoint_Tab.Model.CardData
import com.example.influencer.databinding.CardLayoutBinding
import com.yuyakaido.android.cardstackview.CardStackView
import javax.inject.Inject

class CardStackView_Adapter @Inject constructor(
    private val context: Context
): RecyclerView.Adapter<CardStackView_Adapter.ViewHolder>() {

    private var cardDataList: List<CardData> = emptyList()

    fun setCards(cards: List<CardData>) {
        cardDataList = cards
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardData = cardDataList[position]
        holder.bind(cardData,context)
    }

    override fun getItemCount(): Int = cardDataList.size

    class ViewHolder(private val binding: CardLayoutBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(cardData: CardData,context: Context){
            with(binding){
                MainCategory.text = cardData.post.selectedCategory
                MainCategory.setChipBackgroundColorResource(cardData.post.categoryColor)
                userName.text = cardData.user.username
                PostDatePublished.text = DateTimeUtils.getTimeAgo(cardData.post.creationDate)
                PostDatePublished.setChipBackgroundColorResource(cardData.post.categoryColor)
                CheckpointCategotyNumber.text = "Checkpoint N°${cardData.post.checkpointCategoryCounter}"
                CheckpointCategotyNumber.setChipBackgroundColorResource(cardData.post.categoryColor)
                PostAmountLikes.text = cardData.post.Likes.toString()
                SatisfactionLevelBar.progress = cardData.post.satisfaction_level_value.toFloat()
                SatisfactionLevelValue.text = cardData.post.satisfaction_level_value.toString()
                userAge.text = "${cardData.user.years_old} Years/ ${cardData.user.months_old} Months old" //TODO cambiar el Old y ponerlo en string.xml
                Glide.with(context).load(cardData.user.profilePictureUrl).into(profilePicture)
                CountryFlagIcon.setImageResource(cardData.user.countryFlagResourceId)
                CheckpointText.text = cardData.post.text_post
            }
        }
    }
}