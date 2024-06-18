package com.example.influencer.Features.General_FollowersFollowing_showing.UI

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.example.influencer.R
import com.example.influencer.databinding.ItemFollowersFollowingBinding

class FollowersFollowing_Adapter constructor (
    private val ownerUserId: String,
    private var listener: OnItemInteractionListener
) : RecyclerView.Adapter<FollowersFollowing_Adapter.ViewHolder>() {

    private var users: List<UsuarioSignin> = emptyList()
    private var followLoading: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowersFollowing_Adapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFollowersFollowingBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(private val binding: ItemFollowersFollowingBinding) : RecyclerView.ViewHolder(binding.root) {

        val context = itemView.context

        fun bind(user: UsuarioSignin) {
            checkItemisCurrentUser(user)
            if (ownerUserId != user.id) {  //tenemos q hacer eso xq si ejecutamos esas fun y es el mismo usuario entonces se muestra igual el boton de follow
                handlingFollowButton(user)
                handlingFollowLoading()
            }
            setUpItemData(user)
        }

        private fun checkItemisCurrentUser(user:UsuarioSignin) {
            if (ownerUserId == user.id){
                binding.FollowButton.visibility = View.GONE
            }else{
                //si el item no es el mismo usuario entonces mostramos el boton de follow y actualizamos el UI en base a si le seguimos o no
                listener.checkIfFollowing(user.id){ isTargetUserFollowing->
                    if (isTargetUserFollowing) FollowingButtonUIState() else NotFollowingButtonUIState()
                }
            }
        }

        private fun FollowingButtonUIState(){
            val chipColor = ContextCompat.getColor(context, R.color.verde_seekBar)
            binding.apply {
                FollowButton.isChecked = true
                FollowButton.text = context.getString(R.string.Following)
                FollowButton.chipIcon = ContextCompat.getDrawable(context, R.drawable.vector_asset_check)
                FollowButton.chipBackgroundColor = ColorStateList.valueOf(chipColor)
            }
        }

        private fun NotFollowingButtonUIState(){
            binding.apply {
                FollowButton.isChecked = false
            FollowButton.text = context.getString(R.string.Follow)
            FollowButton.chipIcon = ContextCompat.getDrawable(context, R.drawable.vector_asset_add)
            FollowButton.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            }
        }

        private fun handlingFollowButton(user:UsuarioSignin) {
            binding.apply {
                FollowButton.isCheckable = true
                FollowButton.isClickable = true

                FollowButton.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView.isPressed) {
                        val position = bindingAdapterPosition
                        if (position == RecyclerView.NO_POSITION) return@setOnCheckedChangeListener

                        if (isChecked) {
                            listener.followUser(user.id,position)
                            FollowingButtonUIState()
                        } else {
                            listener.unfollowUser(user.id,position)
                            NotFollowingButtonUIState()
                        }
                    }
                }
            }
        }

        private fun setUpItemData(user:UsuarioSignin) {
            binding.apply {
                userName.text = user.username
                userAge.text = "${user.years_old} Years/ ${user.months_old} Months old" //TODO cambiar el Old y ponerlo en string.xml
                city.text = user.countryName
                val flagUrl = "https://flagsapi.com/${user.countryFlagCode}/flat/64.png"
                Glide.with(context)
                    .load(flagUrl)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            city.chipIcon = resource
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                            // No needed
                        }
                    })
                CheckpointsCreatedNumber.text = user.postCount.toString()
                Glide.with(profilePicture.context).load(user.profilePictureUrl).into(profilePicture)
            }
        }

        private fun handlingFollowLoading() {
            if (followLoading) {
                binding.FollowProgress.visibility = View.VISIBLE
                binding.FollowButton.visibility = View.GONE
            }else{
                binding.FollowProgress.visibility = View.GONE
                binding.FollowButton.visibility = View.VISIBLE
            }
        }
    }

    fun uploadUsersList(newItems: List<UsuarioSignin>) {
        users = newItems
        notifyDataSetChanged()
    }

    fun followLoading(isLoading:Boolean,position: Int){
        followLoading = isLoading
        notifyItemChanged(position)
    }


    interface OnItemInteractionListener {
        fun followUser(UserId: String?,position: Int)
        fun unfollowUser(UserId: String?,position: Int)
        fun checkIfFollowing(targetUserId:String, callback: (Boolean) -> Unit )
    }
}