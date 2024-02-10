package com.example.influencer.UI.Upload_New_Checkpoint.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.influencer.databinding.ItemPhototakenBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// el onDelete es un metodo Lamda que pasamos como parametro al crear el Adapter (en el constructor) cuya implementacion estara
//desarollada en la Activity, cuando se cree el Adapter
class TempImageAdapter @AssistedInject constructor( @Assisted private val onDelete: (Int) -> Unit)
    :RecyclerView.Adapter<TempImageAdapter.ViewHolder>() {

    private var imageUris: MutableList<Uri?> = mutableListOf()

    fun updateUriList(newImageUris: List<Uri?>){
        imageUris.clear()
        imageUris.addAll(newImageUris)
        notifyDataSetChanged()
    }

    fun deleteItem(position:Int){
        imageUris.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhototakenBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding,onDelete)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val uri = imageUris[position]
        holder.bind(uri)
    }

    override fun getItemCount() = imageUris.size

    class ViewHolder(private val binding: ItemPhototakenBinding, private val onDelete: (Int) -> Unit)
        :RecyclerView.ViewHolder(binding.root){
        fun bind(uri:Uri?){
            if (uri != null){
                binding.UploadedTemporalImage.setImageURI(uri)
            }
            binding.deleteButton.setOnClickListener(){
                onDelete(absoluteAdapterPosition)
            }
        }
    }

}