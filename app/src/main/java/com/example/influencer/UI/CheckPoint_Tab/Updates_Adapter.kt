package com.example.influencer.UI.CheckPoint_Tab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.influencer.databinding.ItemUpdateCheckpointBinding
import java.util.SortedMap

class Updates_Adapter: RecyclerView.Adapter<Updates_Adapter.ViewHolder>() {

    private var updatesMap: SortedMap<Int,String> = sortedMapOf()

    fun setUpdates(updates: SortedMap<Int, String>) {
        this.updatesMap = updates
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUpdateCheckpointBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder, position:Int){
        val entry = updatesMap.entries.toList()[position]
        holder.bind(entry.key,entry.value)
    }

    override fun getItemCount(): Int = updatesMap.size

    class ViewHolder(private val binding: ItemUpdateCheckpointBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(updateNumber:Int,updateText:String){
            binding.chipNumberUpdate.text = updateNumber.toString()
            binding.UpdateText.text = updateText
        }
    }
}