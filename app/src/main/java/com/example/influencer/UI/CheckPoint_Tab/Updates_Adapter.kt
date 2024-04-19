package com.example.influencer.UI.CheckPoint_Tab

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.influencer.Core.Utils.BackgroundAndTextColors.setChipTextColor
import com.example.influencer.databinding.ItemUpdateCheckpointBinding
import java.util.SortedMap

class Updates_Adapter: RecyclerView.Adapter<Updates_Adapter.ViewHolder>() {

    private var updatesMap: SortedMap<Int,String> = sortedMapOf()
    private var categoryColor:Int = 0

    fun setUpdates(updates: SortedMap<Int, String>,categoryColor:Int) {
        this.updatesMap = updates
        this.categoryColor = categoryColor
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUpdateCheckpointBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder, position:Int){
        val entry = updatesMap.entries.toList()[position]
        holder.bind(entry.key,entry.value,categoryColor)
    }

    override fun getItemCount(): Int = updatesMap.size

    class ViewHolder(private val binding: ItemUpdateCheckpointBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(updateNumber: Int, updateText: String, categoryColor: Int){
            //tuve que hacer esto aproach porque setColorBackground() no funcionaba x alguna razon
            val colorStateList = ColorStateList.valueOf(categoryColor)
            binding.chipNumberUpdate.chipBackgroundColor = colorStateList
            //
            binding.chipNumberUpdate.setChipTextColor(categoryColor)
            binding.chipNumberUpdate.text = updateNumber.toString()
            binding.UpdateText.text = updateText
        }
    }
}