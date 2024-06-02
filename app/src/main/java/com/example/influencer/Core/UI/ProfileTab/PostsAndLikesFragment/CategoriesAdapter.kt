package com.example.influencer.Core.UI.ProfileTab.PostsAndLikesFragment

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Domain.Model.CheckpointThemeItem
import com.example.influencer.databinding.ItemChipHorizontalListBinding

class CategoriesAdapter(
    private val categories: List<CheckpointThemeItem>,
    private val listener: OnCategoryClickListener
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemChipHorizontalListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CheckpointThemeItem) {
            binding.categoryChip.text = category.text
            binding.categoryChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(binding.categoryChip.context, category.color))
            binding.categoryChip.setOnClickListener {
                listener.onCategoryClick(category.text)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChipHorizontalListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    interface OnCategoryClickListener {
        fun onCategoryClick(category: String)
    }

}