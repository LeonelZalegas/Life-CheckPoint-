package com.example.influencer.Core.UI.ProfileTab

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.influencer.R
import com.example.influencer.databinding.FragmentProfileTabBinding
import com.google.firebase.database.collection.LLRBNode.Color
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileTabFragment : Fragment() {

    companion object {
        private const val ARG_USER_ID = "userId"
        //vamos a usar este aproach para pasar como argumento el userId pero solo cuando no estamos operando en el HOME activity, lo otro usamos Nav component nomas
        //es decir cuando al estar en el checkpoint_card y el user clickea la foto de un post, se lo redirije a otra activity para mostrar perfil
        fun newInstance(userId: String?): ProfileTabFragment {
            val fragment = ProfileTabFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    private var userId: String? = null
    private val viewModel: UserProfileViewModel by viewModels()
    private var _binding: FragmentProfileTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var carga: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //a traves de este codigo obtenemos el argument pasado ya sea a traves de newInstance() o de navig component
        arguments?.let {
            userId = it.getString(ARG_USER_ID)  //el userId del usuario que vamos a mostrar a continuacion en el profile Fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Use the userId to load the user data
        viewModel.loadUser(userId)

        initLoading()
        setUpUI()
        handlingFollowButton()
    }

    private fun initLoading() {
        carga = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        carga.getProgressHelper().setBarColor(android.graphics.Color.parseColor("#F57E00"))
        carga.setTitleText(R.string.Loading)
        carga.setCancelable(false)
    }

    private fun setUpUI() {
        // Observe the user data and update the UI
        viewModel.user.observe(viewLifecycleOwner) { result ->
            result.onSuccess { user ->
                binding.apply {
                    Glide.with(profilePicture.context).load(user.profilePictureUrl).into(profilePicture)
                    userName.text = user.username

                    //https://www.notion.so/ProfileTabFragment-26cfd73f6c2c4576b680f713ad431eaf
                    val flagUrl = "https://flagsapi.com/${user.countryFlagCode}/flat/64.png"
                    city.text = user.countryName
                    Glide.with(requireContext())
                        .load(flagUrl)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                city.chipIcon = resource
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {
                                // No needed
                            }
                        })

                    userAge.text = "${user.years_old} Years/ ${user.months_old} Months old" //TODO cambiar el Old y ponerlo en string.xml
                    NumCheckpointsCreated.text = user.postCount.toString()
                    NumFollowers.text = user.followersCount.toString()
                    NumFollowing.text = user.followingCount.toString()
                }

            }.onFailure {
                // Handle the error
            }
        }
        //hacemos distincion de que mostrar en la UI en base a si el profile user es el dueno usando la app o es el perfil de otro usuario
        viewModel.isCurrentUser.observe(viewLifecycleOwner) { isCurrentUser ->
            binding.Configurations.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
            binding.FollowButton.visibility = if (isCurrentUser) View.GONE else View.VISIBLE
        }
        //checkeamos al abrir un perfil de algun usuario, si este esta siendo seguido o no por l current user/owner user
        viewModel.isFollowing.observe(viewLifecycleOwner) { isFollowing ->
           if (isFollowing) FollowingButtonState()
        }

        viewModel.loading.observe(viewLifecycleOwner){isloading ->
            if (isloading) {
                carga.show()
            } else {
                carga.dismiss()
            }
        }
    }

    private fun handlingFollowButton() {
        binding.apply {
            FollowButton.isCheckable = true
            FollowButton.isClickable = true

            FollowButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    if (isChecked) {
                        FollowingButtonState()
                        viewModel.followUser(userId)
                    } else {
                        FollowButton.text = getString(R.string.Follow)
                        FollowButton.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.vector_asset_add)
                        FollowButton.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                        viewModel.unfollowUser(userId)
                    }
                }
            }
        }
    }

    private fun FollowingButtonState(){
        val chipColor = ContextCompat.getColor(requireContext(), R.color.verde_seekBar)
        binding.apply {
            FollowButton.isChecked = true
            FollowButton.text = getString(R.string.Following)
            FollowButton.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.vector_asset_check)
            FollowButton.chipBackgroundColor = ColorStateList.valueOf(chipColor)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
