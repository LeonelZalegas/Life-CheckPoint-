package com.example.influencer.Features.ProfileTab.UI.PostsAndLikesFragment

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setChipTextColor
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Domain.Model.CheckpointThemeItem
import com.example.influencer.databinding.ItemChipHorizontalListBinding

class CategoriesAdapter(
    private val categories: List<CheckpointThemeItem>,
    private val listener: OnCategoryClickListener
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    // Track the currently selected position
    private var selectedPosition = 0

    inner class ViewHolder(private val binding: ItemChipHorizontalListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CheckpointThemeItem, position: Int) {
           var categoryColor = ContextCompat.getColor(binding.categoryChip.context, category.color)

            binding.categoryChip.text = category.text
            binding.categoryChip.chipBackgroundColor = ColorStateList.valueOf(categoryColor)
            binding.categoryChip.setChipTextColor(categoryColor)

            if (position == selectedPosition) {
                binding.categoryChip.typeface = Typeface.create(binding.categoryChip.typeface, Typeface.BOLD)
            }else{
                binding.categoryChip.typeface = Typeface.create(binding.categoryChip.typeface, Typeface.NORMAL)}

            // Update chip appearance based on selection
            binding.categoryChip.isChecked = position == selectedPosition

            binding.categoryChip.setOnClickListener {
                listener.onCategoryClick(category.text)
                if (selectedPosition != position) {
                    // Update the selected position
                    val previousPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChipHorizontalListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position],position)
    }

    override fun getItemCount(): Int = categories.size

    interface OnCategoryClickListener {
        fun onCategoryClick(category: String)
    }

}