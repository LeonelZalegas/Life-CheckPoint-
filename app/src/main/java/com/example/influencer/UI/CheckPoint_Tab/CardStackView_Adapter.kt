package com.example.influencer.UI.CheckPoint_Tab

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.influencer.Core.Utils.ChipTextColor.isColorDark
import com.example.influencer.Core.Utils.DateTimeUtils
import com.example.influencer.UI.CheckPoint_Tab.Model.CardData
import com.example.influencer.databinding.CardLayoutBinding
import javax.inject.Inject

//funcion de extencion para modificar el Textview y asi poder agregar el color del background como tambien las puntas redondeadas/color del texto en base a color del fondo
fun View.setRoundedBackgroundColor(color: Int, cornerRadiusDp: Float) {
    val density = context.resources.displayMetrics.density
    // Convert dp to pixels
    val cornerRadiusPx = cornerRadiusDp * density
    val drawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(color)
        cornerRadius = cornerRadiusPx
    }
    background = drawable

    // Determine if the background color is dark or light
    val isDark = isColorDark(color)

    // If the view is a TextView (or subclass), set its text color based on the background color
    if (this is TextView) {
        this.setTextColor(if (isDark) Color.WHITE else Color.BLACK)
    }
}

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
                val colorString = if (cardData.post.categoryColor.isNullOrEmpty()) "#FFFFFF" else cardData.post.categoryColor // Default to white if null or empty
                val colorInt = Color.parseColor(colorString)
                MainCategory.text = cardData.post.selectedCategory
                MainCategory.setRoundedBackgroundColor(colorInt,50f)
                userName.text = cardData.user.username
                PostDatePublished.text = DateTimeUtils.getTimeAgo(cardData.post.creationDate)
                PostDatePublished.setRoundedBackgroundColor(colorInt,50f)
                CheckpointCategotyNumber.text = "Checkpoint N°${cardData.post.checkpointCategoryCounter}"
                CheckpointCategotyNumber.setRoundedBackgroundColor(colorInt,50f)
                PostAmountLikes.text = cardData.post.Likes.toString()
                SatisfactionLevelBar.progress = cardData.post.satisfaction_level_value.toFloat()
                SatisfactionLevelValue.text = cardData.post.satisfaction_level_value.toString()
                userAge.text = "${cardData.user.years_old} Years/ ${cardData.user.months_old} Months old" //TODO cambiar el Old y ponerlo en string.xml
                Glide.with(context).load(cardData.user.profilePictureUrl).into(profilePicture)
                val CountryCode = cardData.user.countryFlagCode
                val flagUrl = "https://flagsapi.com/$CountryCode/flat/64.png"
                Glide.with(context).load(flagUrl).into(CountryFlagIcon)
                CheckpointText.text = cardData.post.text_post
            }
        }
    }
}