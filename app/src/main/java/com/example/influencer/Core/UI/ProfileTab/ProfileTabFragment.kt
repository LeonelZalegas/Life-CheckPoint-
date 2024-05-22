package com.example.influencer.Core.UI.ProfileTab

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.influencer.R
import com.example.influencer.databinding.FragmentProfileTabBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //a traves de este codigo obtenemos el argument pasado ya sea a traves de newInstance() o de navig component
        arguments?.let {
            userId = it.getString(ARG_USER_ID)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
